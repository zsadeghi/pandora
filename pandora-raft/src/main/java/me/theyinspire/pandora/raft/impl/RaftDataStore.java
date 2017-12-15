package me.theyinspire.pandora.raft.impl;

import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.core.datastore.*;
import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommandDispatcher;
import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommands;
import me.theyinspire.pandora.core.datastore.cmd.LockingDataStoreCommands;
import me.theyinspire.pandora.core.server.ServerConfiguration;
import me.theyinspire.pandora.raft.Clock;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/12/17, 6:20 PM)
 */
public class RaftDataStore implements DataStore, CommandReceiver, InitializingDataStore, DestroyableDataStore, Synchronized {

    private static final Log LOG = LogFactory.getLog("pandora.server.raft");
    private static final int HALF_LIFE = 150;
    private final DataStore delegate;
    private final ReplicaRegistry replicaRegistry;
    private final HeartbeatTransmitter heartbeatTransmitter;
    private final ElectionWatcher electionWatcher;
    private final LogCommitter logCommitter;
    private List<LogEntry> entries;
    private int term;
    private String votedFor;
    private ServerMode mode;
    private int applied;
    private int committed;
    private String leader;
    private Map<String, Integer> knownIndex;
    private Map<String, Integer> matchIndex;
    private final Clock clock;

    public RaftDataStore(final DataStore delegate,
                         final ReplicaRegistry replicaRegistry) {
        this.delegate = delegate;
        this.replicaRegistry = replicaRegistry;
        LOG.info("Starting RAFT node: " + delegate.getSignature());
        entries = new CopyOnWriteArrayList<>();
        votedFor = null;
        applied = 0;
        committed = 0;
        leader = null;
        heartbeatTransmitter = new HeartbeatTransmitter(this);
        electionWatcher = new ElectionWatcher(this);
        logCommitter = new LogCommitter(this);
        long timeMillis = System.currentTimeMillis();
        replicaRegistry.getReplicaSetFor(delegate.getSignature());
        timeMillis = (System.currentTimeMillis() - timeMillis) * 4;
        final long halfLife = Math.max(HALF_LIFE, timeMillis);
        clock = new SimpleClock(() -> (long) (halfLife + Math.random() * halfLife));
        resetTerm(ServerMode.FOLLOWER, 0);
    }

    private void resetTerm(ServerMode mode, int term) {
        if (this.term != term) {
            LOG.info("Starting term <" + term + "> in mode <" + mode + ">");
        } else {
            LOG.info("Still in term <" + term + "> with mode <" + mode + ">");
        }
        this.term = term;
        this.mode = mode;
        clock.reset();
    }

    @Override
    public String getUri(final ServerConfiguration configuration) {
        return delegate.getUri(configuration);
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
                waitQuietly();
            }
        }
    }

    private boolean handleCommand(Command<Boolean> command) {
        return handleCommand(command, () -> true);
    }

    private <E> E handleCommand(Command<E> command, Supplier<E> value) {
        waitThroughCandidacy();
        if (mode == ServerMode.FOLLOWER) {
            LOG.info("This is a follower node. Redirecting request to leader: " + command);
            if (leader == null) {
                LOG.info("There is no known leader at this point. Client should retry.");
                throw new IllegalStateException();
            }
            final Replica leader = replicaRegistry.getReplica(this.leader);
            return leader.send(command);
        } else {
            LOG.info("Appending request to log book: " + command);
            final int index = entries.size();
            entries.add(new ImmutableLogEntry(command, term));
            new Thread(new LogReplicator(this)).start();
            LOG.info("Waiting for entry to be committed across the state machine ...");
            while (committed <= index) {
                waitQuietly();
            }
            LOG.info("Entry committed: " + command);
            return value.get();
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
        final long size = size();
        return handleCommand(DataStoreCommands.truncate(), () -> size);
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
            if (leader == null) {
                return (R) "(this is the leader node)";
            } else {
                final Replica replica = replicaRegistry.getReplica(leader);
                final StringBuilder builder = new StringBuilder(leader);
                builder.append(": ");
                final String uri = replica.send(LockingDataStoreCommands.getUri(null));
                builder.append(uri);
                return (R) builder.toString();
            }
        } else if (command instanceof LogRaftCommand) {
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < entries.size(); i++) {
                LogEntry entry = entries.get(i);
                if (applied > i) {
                    builder.append('A');
                } else {
                    builder.append(' ');
                }
                if (committed > i) {
                    builder.append('C');
                } else {
                    builder.append(' ');
                }
                builder.append(' ');
                builder.append(String.format("%03d", i));
                builder.append(' ');
                builder.append(entry);
                builder.append('\n');
            }
            return (R) builder.toString();
        }
        if (delegate instanceof CommandReceiver) {
            CommandReceiver receiver = (CommandReceiver) delegate;
            return receiver.receive(command);
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
        if (votedFor != null && command.head().compareTo(reference) <= 0) {
            LOG.info("Rejecting since candidate is not up-to-date enough");
            return RaftResponse.reject(term);
        }
        votedFor = command.signature();
        resetTerm(ServerMode.FOLLOWER, command.term());
        updateTimestamp();
        LOG.info("Casting a vote for " + command.signature());
        return RaftResponse.accept(term);
    }

    private LogReference getHead() {
        final LogReference reference;
        if (entries.isEmpty()) {
            reference = new ImmutableLogReference(0, -1);
        } else {
            reference = new ImmutableLogReference(entries.size(), entries.get(entries.size() - 1).term());
        }
        return reference;
    }

    private synchronized RaftResponse append(final AppendRaftCommand command) {
        if (command.term() < term) {
            LOG.info("Rejecting sendAppend request due to shorter tenure (" + command.term() + " vs. " + term + ")");
            return RaftResponse.reject(term);
        }
        resetTerm(ServerMode.FOLLOWER, command.term());
        leader = command.signature();
        // If head.index == 0, it means the leader doesn't have any records, which we cannot disagree with
        final int leaderHeadIndex = command.head().index();
        if (leaderHeadIndex != 0) {
            if (entries.size() < leaderHeadIndex || entries.get(leaderHeadIndex - 1).term() != command.head().term()) {
                LOG.info("Rejecting due to log inconsistencies (expected " + leaderHeadIndex + " but was " + entries.size() + ")");
                return RaftResponse.reject(term);
            }
        }
        LOG.info("Trimming log to leader size: " + command.head());
        while (command.head().index() < entries.size()) {
            entries.remove(entries.size() - 1);
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
        clock.reset();
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
    
    private void sendAppend() {
        final List<LogEntry> entries = new ArrayList<>(this.entries);
        LOG.info("Initiating log replication up to size " + entries.size());
        final Set<Replica> replicaSet = replicaRegistry.getReplicaSetFor(getSignature());
        if (replicaSet.isEmpty()) {
            LOG.info("Replica set is empty; committing locally.");
            this.committed = entries.size();
            return;
        }
        final AtomicBoolean committed = new AtomicBoolean(false);
        final AtomicBoolean preempted = new AtomicBoolean(false);
        final AtomicInteger count = new AtomicInteger();
        replicaSet.parallelStream()
                .map(replica -> !preempted.get() && sendAppend(replica, entries))
                .peek(success -> {
                    if (!preempted.get() && !success) {
                        LOG.info("This server was preempted as a leader. Abandoning replication.");
                        preempted.set(true);
                    }
                })
                .forEach(success -> {
                    if (!preempted.get()) {
                        if (count.incrementAndGet() > replicaSet.size() / 2 && !committed.get()) {
                            committed.set(true);
                            this.committed = entries.size();
                        }
                    }
                });
    }

    private boolean sendAppend(Replica replica, List<LogEntry> entries) {
        if (mode != ServerMode.LEADER) {
            return false;
        }
        if (!knownIndex.containsKey(replica.getSignature())) {
            // Assume that the follower has committed everything the leader has. This will be auto-corrected if wrong.
            knownIndex.put(replica.getSignature(), entries.size());
        }
        if (!matchIndex.containsKey(replica.getSignature())) {
            // Assume that we don't know the last match index for this replica.
            matchIndex.put(replica.getSignature(), 0);
        }
        final Integer index = knownIndex.get(replica.getSignature());
        final LogReference head = new ImmutableLogReference(index, index > 0 && !entries.isEmpty() ? entries.get(index - 1).term() : -1);
        final List<LogEntry> list = index == 0 ? entries : entries.subList(index - 1, entries.size());
        final AppendRaftCommand append = RaftCommands.append(term, getSignature(), head, committed, list);
        final RaftResponse response = replica.send(append);
        if (!response.success()) {
            if (response.term() > term) {
                LOG.info(replica.getSignature() + ": Discovered another leader, relegating.");
                resetTerm(ServerMode.FOLLOWER, response.term());
                knownIndex.clear();
                matchIndex.clear();
                return false;
            } else {
                LOG.info(replica.getSignature() + ": Inconsistent with follower, back-walking.");
                knownIndex.put(replica.getSignature(), head.index() - 1);
                return sendAppend(replica, entries);
            }
        } else {
            LOG.info(replica.getSignature() + ": Everything is okay. Saving state.");
            knownIndex.put(replica.getSignature(), head.index());
            matchIndex.put(replica.getSignature(), head.index());
            return true;
        }
    }

    private static void waitQuietly() {
        try {
            Thread.sleep(1);
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
            target.clock.waitQuietly();
            if (target.mode != ServerMode.LEADER && target.clock.timedOut()) {
                LOG.info("Starting an election ...");
                synchronized (target) {
                    target.resetTerm(ServerMode.CANDIDATE, target.term + 1);
                    target.updateTimestamp();
                    target.votedFor = null;
                    target.leader = null;
                }
                int votes = 0;
                final Set<Replica> replicaSet = target.replicaRegistry.getReplicaSetFor(target.getSignature());
                for (Replica replica : replicaSet) {
                    final RaftResponse response = replica.send(RaftCommands.vote(target.term, target.getSignature(), target.getHead()));
                    if (response.success()) {
                        votes++;
                    } else if (response.term() > target.term) {
                        target.clock.waitQuietly();
                        return;
                    }
                }
                if (target.mode != ServerMode.CANDIDATE || votes < replicaSet.size() / 2) {
                    target.clock.waitQuietly();
                    return;
                }
                LOG.info("Received enough votes to become the leader: " + votes);
                synchronized (target) {
                    target.updateTimestamp();
                    target.votedFor = null;
                    target.leader = null;
                    target.resetTerm(ServerMode.LEADER, target.term + 1);
                    target.knownIndex = new ConcurrentHashMap<>();
                    target.matchIndex = new ConcurrentHashMap<>();
                    for (Replica replica : replicaSet) {
                        target.knownIndex.put(replica.getSignature(), target.entries.size());
                        target.matchIndex.put(replica.getSignature(), 0);
                    }
                }
            }
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
                target.sendAppend();
                final Set<Replica> replicaSet = target.replicaRegistry.getReplicaSetFor(target.getSignature());
                int newCommitted = target.entries.size();
                while (newCommitted > target.committed) {
                    if (target.entries.get(newCommitted - 1).term() == target.term) {
                        int count = 0;
                        for (Replica replica : replicaSet) {
                            final String signature = replica.getSignature();
                            if (target.matchIndex.containsKey(signature) && target.matchIndex.get(signature) >= newCommitted) {
                                count ++;
                            }
                        }
                        if (count >= replicaSet.size() / 2) {
                            target.committed = newCommitted;
                            break;
                        }
                    }
                    newCommitted --;
                }
            }
            target.clock.waitQuietly(0.1);
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
            waitQuietly();
        }

    }

    private static class LogReplicator implements Runnable {

        private final RaftDataStore target;

        private LogReplicator(RaftDataStore target) {
            this.target = target;
        }

        @Override
        public void run() {
            target.sendAppend();
        }

    }

}
