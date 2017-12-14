package me.theyinspire.pandora.raft.impl;

import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.core.datastore.*;
import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommandDispatcher;
import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommands;
import me.theyinspire.pandora.core.server.ServerConfiguration;
import me.theyinspire.pandora.raft.LogEntry;
import me.theyinspire.pandora.raft.LogReference;
import me.theyinspire.pandora.raft.ServerMode;
import me.theyinspire.pandora.raft.cmd.*;
import me.theyinspire.pandora.raft.cmd.impl.ImmutableLogEntry;
import me.theyinspire.pandora.raft.cmd.impl.ImmutableLogReference;
import me.theyinspire.pandora.raft.cmd.impl.RaftCommands;
import me.theyinspire.pandora.replica.Replica;
import me.theyinspire.pandora.replica.ReplicaRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/12/17, 6:20 PM)
 */
public class RaftDataStore implements LockingDataStore, CommandReceiver, InitializingDataStore, DestroyableDataStore {

    private static final Log LOG = LogFactory.getLog("pandora.server.raft");
    private static final int HALFLIFE = 10000;
    private final LockingDataStore delegate;
    private final ReplicaRegistry replicaRegistry;
    private final Random random;
    private final HeartbeatTransmitter heartbeatTransmitter;
    private final ElectionWatcher electionWatcher;
    private final LogCommitter logCommitter;
    private List<LogEntry> entries;
    private int term;
    private String votedFor;
    private ServerMode mode;
    private long timestamp;
    private int applied;
    private int committed;
    private String leader;
    private long timeout;
    private Map<String, Integer> knownIndex;
    private Map<String, Integer> matchIndex;

    public RaftDataStore(final LockingDataStore delegate,
                         final ReplicaRegistry replicaRegistry) {
        this.delegate = delegate;
        this.replicaRegistry = replicaRegistry;
        this.random = new Random();
        LOG.info("Starting RAFT node: " + delegate.getSignature());
        entries = new CopyOnWriteArrayList<>();
        term = 0;
        votedFor = null;
        mode = ServerMode.FOLLOWER;
        timestamp = 0;
        applied = 0;
        committed = 0;
        leader = null;
        timeout = HALFLIFE + random.nextInt(HALFLIFE);
        heartbeatTransmitter = new HeartbeatTransmitter(this);
        electionWatcher = new ElectionWatcher(this);
        logCommitter = new LogCommitter(this);
    }

    @Override
    public String getUri(final ServerConfiguration configuration) {
        return delegate.getUri(configuration);
    }

    @Override
    public String lock(final String key) {
        return delegate.lock(key);
    }

    @Override
    public void restore(final String key, final String lock) {
        delegate.restore(key, lock);
    }

    @Override
    public void unlock(final String key, final String lock) {
        delegate.unlock(key, lock);
    }

    @Override
    public boolean store(final String key, final Serializable value, final String lock) {
        return delegate.store(key, value, lock);
    }

    @Override
    public boolean delete(final String key, final String lock) {
        return delegate.delete(key, lock);
    }

    @Override
    public Serializable get(final String key, final String lock) {
        return delegate.get(key, lock);
    }

    @Override
    public boolean locked(final String key) {
        return delegate.locked(key);
    }

    @Override
    public String getSignature() {
        return delegate.getSignature();
    }

    @Override
    public long size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    private void waitThroughCandidacy() {
        if (mode == ServerMode.CANDIDATE) {
            LOG.info("Waiting for candidacy to be over");
            while (mode == ServerMode.CANDIDATE) {
                waitQuietly(1);
            }
        }
    }

    private boolean handleCommand(Command<Boolean> command) {
        waitThroughCandidacy();
        if (mode == ServerMode.FOLLOWER) {
            LOG.info("This is a follower node. Redirecting request to leader: " + command);
            if (leader == null) {
                LOG.info("There is no known leader at this point. Client should retry.");
                return false;
            }
            final Replica leader = replicaRegistry.getReplica(this.leader);
            return leader.send(command);
        } else {
            LOG.info("Appending request to log book: " + command);
            final int index = entries.size();
            entries.add(new ImmutableLogEntry(command, term));
            LOG.info("Waiting for entry to be committed across the state machine ...");
            while (committed <= index) {
                waitQuietly(1);
            }
            return true;
        }
    }

    @Override
    public boolean store(final String key, final Serializable value) {
        return handleCommand(DataStoreCommands.store(key, value));
    }

    @Override
    public boolean delete(final String key) {
        return handleCommand(DataStoreCommands.delete(key));
    }

    @Override
    public Serializable get(final String key) {
        return delegate.get(key);
    }

    @Override
    public Set<String> keys() {
        return delegate.keys();
    }

    @Override
    public long truncate() {
        return delegate.truncate();
    }

    @Override
    public boolean has(final String key) {
        return delegate.has(key);
    }

    @Override
    public Map<String, Serializable> all() {
        return delegate.all();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> R receive(final Command<R> command) {
        if (command instanceof AppendRaftCommand) {
            return (R) append((AppendRaftCommand) command);
        } else if (command instanceof VoteRaftCommand) {
            return (R) vote(((VoteRaftCommand) command));
        } else if (command instanceof ModeRaftCommand) {
            return (R) mode.name();
        } else if (command instanceof TermRaftCommand) {
            return (R) String.valueOf(term);
        } else if (command instanceof LeaderRaftCommand) {
            return (R) leader;
        }
        throw new UnsupportedOperationException("Command not recognized: " + command);
    }

    private synchronized RaftResponse vote(final VoteRaftCommand command) {
        LOG.info("Received request to vote for: " + command.signature());
        if (command.term() < term) {
            LOG.info("Refusing to vote due to shorter tenure");
            return RaftResponse.reject(term);
        }
        final LogReference reference = getHead();
        if (votedFor != null && command.head().compareTo(reference) < 0) {
            LOG.info("Rejecting since candidate is not up-to-date enough");
            return RaftResponse.reject(term);
        }
        votedFor = command.signature();
        term = command.term();
        mode = ServerMode.FOLLOWER;
        updateTimestamp();
        LOG.info("Casting a vote for " + command.signature());
        return RaftResponse.accept(term);
    }

    private LogReference getHead() {
        final LogReference reference;
        if (entries.isEmpty()) {
            reference = new ImmutableLogReference(-1, -1);
        } else {
            reference = new ImmutableLogReference(entries.size() - 1, entries.get(entries.size() - 1).term());
        }
        return reference;
    }

    private synchronized RaftResponse append(final AppendRaftCommand command) {
        if (command.term() < term) {
            LOG.info("Rejecting append request due to shorter tenure");
            return RaftResponse.reject(term);
        }
        updateTimestamp();
        mode = ServerMode.FOLLOWER;
        term = command.term();
        leader = command.signature();
        if (!command.head().isRoot()) {
            if (entries.size() < command.head().index() || entries.get(command.head().index()).term() != command.head().term()) {
                LOG.info("Rejecting due to log inconsistencies");
                return RaftResponse.reject(term);
            }
            LOG.info("Trimming log to leader size: " + command.head());
            while (command.head().index() <= entries.size()) {
                entries.remove(entries.size() - 1);
            }
        } else {
            LOG.info("Clearing current log");
            entries.clear();
        }
        LOG.info("Appending entries: " + command.entries());
        entries.addAll(command.entries());
        if (command.commit() > committed) {
            committed = Math.min(command.commit(), entries.size());
            LOG.info("Updating commit index: " + committed);
        }
        LOG.info("Returning acceptance to leader");
        return RaftResponse.accept(term);
    }

    private void updateTimestamp() {
        timestamp = System.currentTimeMillis();
    }

    @Override
    public void destroy(final ServerConfiguration serverConfiguration) {
        logCommitter.stop();
        heartbeatTransmitter.stop();
        electionWatcher.stop();
        replicaRegistry.destroy(getSignature());
    }

    @Override
    public void init(final ServerConfiguration serverConfiguration,
                     final DataStoreConfiguration dataStoreConfiguration) {
        replicaRegistry.init(getSignature(), getUri(serverConfiguration));
        new Thread(electionWatcher).start();
        new Thread(heartbeatTransmitter).start();
        new Thread(logCommitter).start();
    }

    private static void waitQuietly(long timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ElectionWatcher extends AbstractStoppable {

        private final RaftDataStore target;

        private ElectionWatcher(RaftDataStore target) {
            this.target = target;
        }

        @Override
        protected void iterate() {
            if (target.mode != ServerMode.LEADER) {
                if (target.timestamp + target.timeout < System.currentTimeMillis()) {
                    LOG.info("Starting an election ...");
                    synchronized (target) {
                        target.mode = ServerMode.CANDIDATE;
                        target.timestamp = System.currentTimeMillis();
                        target.votedFor = null;
                        target.leader = null;
                        target.term ++;
                    }
                    int votes = 0;
                    final Set<Replica> replicaSet = target.replicaRegistry.getReplicaSetFor(target.getSignature());
                    for (Replica replica : replicaSet) {
                        final RaftResponse response = replica.send(RaftCommands.vote(target.term, target.getSignature(), target.getHead()));
                        if (response.success()) {
                            votes ++;
                        } else if (response.term() > target.term) {
                            waitQuietly(target.timeout);
                            return;
                        }
                    }
                    if (target.mode != ServerMode.CANDIDATE || votes < replicaSet.size() / 2) {
                        waitQuietly(target.timeout);
                        return;
                    }
                    LOG.info("Received enough votes to become the leader: " + votes);
                    synchronized (target) {
                        target.timestamp = System.currentTimeMillis();
                        target.votedFor = null;
                        target.leader = null;
                        target.term ++;
                        target.mode = ServerMode.LEADER;
                        target.knownIndex = new ConcurrentHashMap<>();
                        target.matchIndex = new ConcurrentHashMap<>();
                        for (Replica replica : replicaSet) {
                            target.knownIndex.put(replica.getSignature(), target.getHead().index() + 1);
                            target.matchIndex.put(replica.getSignature(), 0);
                        }
                    }
                }
            }
            waitQuietly(target.timeout);
        }

    }

    private static class HeartbeatTransmitter extends AbstractStoppable {

        private final RaftDataStore target;

        private HeartbeatTransmitter(RaftDataStore target) {
            this.target = target;
        }

        @Override
        protected void iterate() {
            if (target.mode == ServerMode.LEADER) {
                LOG.info("Sending heartbeat signal");
                final Set<Replica> replicaSet = target.replicaRegistry.getReplicaSetFor(target.getSignature());
                for (Replica replica : replicaSet) {
                    if (!heartbeat(replica)) {
                        waitQuietly(target.timeout / 2);
                        return;
                    }
                }
            }
            waitQuietly(target.timeout / 2);
        }

        private boolean heartbeat(Replica replica) {
            if (target.mode != ServerMode.LEADER) {
                return false;
            }
            final LogReference head;
            if (target.entries.isEmpty()) {
                head = new ImmutableLogReference(-1, -1);
            } else {
                final Integer index = target.knownIndex.get(replica.getSignature());
                head = new ImmutableLogReference(index, target.entries.get(index));
            }
            final RaftResponse response = replica.send(RaftCommands.append(target.term, target.getSignature(), head, target.committed, Collections.emptyList()));
            if (!response.success()) {
                if (response.term() > target.term) {
                    LOG.info(replica.getSignature() + ": Discovered another leader, relegating.");
                    target.mode = ServerMode.FOLLOWER;
                    target.term = response.term();
                    target.knownIndex.clear();
                    target.matchIndex.clear();
                    return false;
                } else {
                    LOG.info(replica.getSignature() + ": Inconsistent with follower, back-walking.");
                    target.knownIndex.put(replica.getSignature(), head.index() - 1);
                    return heartbeat(replica);
                }
            } else {
                LOG.info(replica.getSignature() + ": Everything is okay. Saving state.");
                target.knownIndex.put(replica.getSignature(), head.index() + 1);
                target.matchIndex.put(replica.getSignature(), head.index());
                return true;
            }
        }

    }

    private static class LogCommitter extends AbstractStoppable {

        private final RaftDataStore target;
        private final DataStoreCommandDispatcher dispatcher;

        private LogCommitter(RaftDataStore target) {
            this.target = target;
            dispatcher = new DataStoreCommandDispatcher(target.delegate);
        }

        @Override
        protected void iterate() {
            while (target.committed > target.applied) {
                final LogEntry entry = target.entries.get(target.applied);
                dispatcher.dispatch(entry.command());
                target.applied ++;
            }
            waitQuietly(1);
        }

    }

}
