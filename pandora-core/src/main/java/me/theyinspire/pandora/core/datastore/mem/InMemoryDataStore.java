package me.theyinspire.pandora.core.datastore.mem;

import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.core.datastore.CommandReceiver;
import me.theyinspire.pandora.core.datastore.LockingDataStore;
import me.theyinspire.pandora.core.datastore.cmd.TestCommand;
import me.theyinspire.pandora.core.server.ServerConfiguration;
import me.theyinspire.pandora.core.server.UriServerConfigurationWriter;
import me.theyinspire.pandora.core.server.error.ServerException;
import me.theyinspire.pandora.core.server.impl.DefaultUriServerConfigurationWriter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/26/17, 6:11 PM)
 */
public class InMemoryDataStore implements LockingDataStore, CommandReceiver {

    private final Map<String, Entry> storage;
    private final LockKeeper lockKeeper;
    private final UriServerConfigurationWriter configurationWriter;
    private final String signature;

    public InMemoryDataStore(int initialCapacity, LockingMethod locking) {
        storage = new HashMap<>(initialCapacity);
        lockKeeper = LockingMethod.OPTIMISTIC.equals(locking) ? new OptimisticLockKeeper(storage) : new PessimisticLockKeeper(storage);
        configurationWriter = new DefaultUriServerConfigurationWriter();
        signature = UUID.randomUUID().toString();
    }

    @Override
    public String getUri(ServerConfiguration configuration) {
        return configurationWriter.write(configuration);
    }

    @Override
    public String lock(String key) {
        return lockKeeper.lock(key);
    }

    @Override
    public void restore(String key, String lock) {
        lockKeeper.relinquish(key, lock);
    }

    @Override
    public void unlock(String key, String lock) {
        lockKeeper.unlock(key, lock);
    }

    @Override
    public boolean store(String key, Serializable value, String lock) {
        try {
            lockKeeper.mutate(key, lock, value);
        } catch (Exception e) {
            throw new ServerException("Failed to mutate locked data: " + key, e);
        }
        return true;
    }

    @Override
    public boolean delete(String key, String lock) {
        try {
            lockKeeper.delete(key, lock);
        } catch (Exception e) {
            throw new ServerException("Failed to delete locked data: " + key, e);
        }
        return true;
    }

    @Override
    public Serializable get(String key, String lock) {
        return lockKeeper.get(key, lock);
    }

    @Override
    public boolean locked(String key) {
        return lockKeeper.isLocked(key);
    }

    @Override
    public String getSignature() {
        return signature;
    }

    @Override
    public long size() {
        return keys().size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean store(String key, Serializable value) {
        if (isExclusivelyLocked(key)) {
            return false;
        }
        if (!storage.containsKey(key)) {
            storage.put(key, new Entry(key, value));
        } else {
            storage.get(key).mutate(value);
        }
        return true;
    }

    @Override
    public Serializable get(String key) {
        if (storage.containsKey(key)) {
            final Entry entry = storage.get(key);
            if (entry.isDeleted()) {
                return null;
            }
            return entry.getValue();
        }
        return null;
    }

    @Override
    public boolean delete(String key) {
        if (!storage.containsKey(key) || isExclusivelyLocked(key)) {
            return false;
        }
        storage.remove(key);
        return true;
    }

    @Override
    public Set<String> keys() {
        return storage.values().stream()
                .filter(entry -> !entry.isDeleted())
                .map(Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public long truncate() {
        final int size = storage.size();
        storage.clear();
        return size;
    }

    @Override
    public boolean has(String key) {
        return storage.containsKey(key) && !storage.get(key).isDeleted();
    }

    @Override
    public Map<String, Serializable> all() {
        final Map<String, Serializable> map = new HashMap<>();
        storage.values().stream()
                .filter(entry -> !entry.isDeleted())
                .forEach(entry -> map.put(entry.getKey(), entry.getValue()));
        return map;
    }

    private boolean isExclusivelyLocked(String key) {
        return lockKeeper.isLocked(key) && lockKeeper.isExclusive();
    }

    @Override
    public <R> R receive(final Command<R> command) {
        if (command instanceof TestCommand) {
            final int count = 100;
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < count; i++) {
                builder.append(i + 1)
                       .append(" - all work and no play makes Jack a dull boy")
                       .append("\n");
            }
            //noinspection unchecked
            return (R) builder.toString();
        }
        throw new IllegalArgumentException();
    }

    private static class Entry {

        private final String key;
        private Serializable value;
        private long timestamp;
        private boolean deleted;
        private boolean fresh;

        private Entry(String key, Serializable value) {
            this.key = key;
            mutate(value);
            deleted = false;
            fresh = false;
        }

        private void mutate(Serializable value) {
            mutate(value, false);
        }

        private void mutate(Serializable value, boolean keepTimestamp) {
            this.value = value;
            deleted = false;
            if (!keepTimestamp) {
                this.timestamp = System.currentTimeMillis();
            }
        }

        private Entry copy() {
            final Entry entry = new Entry(key, value);
            entry.timestamp = timestamp;
            return entry;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public Serializable getValue() {
            return value;
        }

        public String getKey() {
            return key;
        }

        public void markDeleted() {
            markDeleted(false);
        }

        public void markFresh() {
            fresh = true;
        }

        public void markDeleted(boolean keepTimestamp) {
            mutate(null, keepTimestamp);
            deleted = true;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public boolean isFresh() {
            return fresh;
        }

        public void bake() {
            fresh = false;
        }
    }

    private static class Lock {

        private final Entry entry;
        private final String id;

        private Lock(Entry entry) {
            this.entry = entry;
            id = UUID.randomUUID().toString();
        }

        public String getId() {
            return id;
        }

        public Entry getEntry() {
            return entry;
        }

    }

    private interface LockKeeper {

        boolean isExclusive();

        String lock(String key);

        void relinquish(String key, String lock);

        void unlock(String key, String lock);

        boolean isLocked(String key);

        void mutate(String key, String lock, Serializable value);

        void delete(String key, String lock);

        Serializable get(String key, String lock);

    }

    private static abstract class AbstractLockKeeper implements LockKeeper {

        private final Map<String, Entry> entries;
        private final boolean exclusive;

        public AbstractLockKeeper(Map<String, Entry> entries, boolean exclusive) {
            this.entries = entries;
            this.exclusive = exclusive;
        }

        @Override
        public boolean isExclusive() {
            return exclusive;
        }

        protected Map<String, Entry> getEntries() {
            return entries;
        }

        @Override
        public String lock(String key) {
            if (!entries.containsKey(key)) {
                final Entry entry = new Entry(key, null);
                entry.markFresh();
                entries.put(key, entry);
            }
            if (isExclusive() && isLocked(key)) {
                throw new ServerException("Key is already locked: " + key);
            }
            return doLock(key).getId();
        }

        @Override
        public void relinquish(String key, String lock) {
            checkLock(key, lock);
            doRelinquish(key, lock);
        }

        @Override
        public void unlock(String key, String lock) {
            checkLock(key, lock);
            doUnlock(key, lock);
        }

        @Override
        public void mutate(String key, String lock, Serializable value) {
            checkLock(key, lock);
            doMutate(key, lock, value);
        }

        @Override
        public void delete(String key, String lock) {
            checkLock(key, lock);
            doDelete(key, lock);
        }

        @Override
        public Serializable get(String key, String lock) {
            checkLock(key, lock);
            final Entry entry = doGet(key, lock);
            if (entry.isDeleted()) {
                return null;
            }
            return entry.getValue();
        }

        private void checkLock(String key, String lock) {
            if (!entries.containsKey(key)) {
                throw new ServerException("Entry does not exist: " + key);
            }
            if (!isLocked(key)) {
                throw new ServerException("Key is not locked: " + key);
            }
            if (!isHeldBy(key, lock)) {
                throw new ServerException("Attempting to unlock a lock with the wrong key");
            }
        }


        protected abstract Lock doLock(String key);

        protected abstract void doRelinquish(String key, String lock);

        protected abstract boolean isHeldBy(String key, String lock);

        protected abstract void doUnlock(String key, String lock);

        protected abstract void doMutate(String key, String lock, Serializable value);

        protected abstract void doDelete(String key, String lock);

        protected abstract Entry doGet(String key, String lock);

    }

    private static class PessimisticLockKeeper extends AbstractLockKeeper {

        private final Map<String, Lock> locks;

        private PessimisticLockKeeper(Map<String, Entry> entries) {
            super(entries, true);
            locks = new HashMap<>();
        }

        @Override
        public boolean isLocked(String key) {
            return locks.containsKey(key);
        }

        @Override
        protected Lock doLock(String key) {
            final Lock lock = new Lock(getEntries().get(key).copy());
            locks.put(key, lock);
            return lock;
        }

        @Override
        protected void doRelinquish(String key, String lock) {
            final Lock value = locks.remove(key);
            final Entry entry = value.getEntry();
            if (entry.isFresh() || entry.isDeleted() && getEntries().containsKey(key)) {
                getEntries().remove(key);
            } else {
                getEntries().put(key, entry);
            }
        }

        @Override
        protected boolean isHeldBy(String key, String lock) {
            return locks.get(key).getId().equals(lock);
        }

        @Override
        protected void doUnlock(String key, String lock) {
            locks.remove(key);
            final Entry entry = getEntries().get(key);
            if (entry.isFresh()) {
                entry.bake();
            }
        }

        @Override
        protected void doMutate(String key, String lock, Serializable value) {
            getEntries().get(key).mutate(value);
        }

        @Override
        protected void doDelete(String key, String lock) {
            getEntries().get(key).markDeleted();
        }

        @Override
        protected Entry doGet(String key, String lock) {
            return getEntries().get(key);
        }

    }

    private static class OptimisticLockKeeper extends AbstractLockKeeper {

        private final Map<String, Map<String, Lock>> locks;

        public OptimisticLockKeeper(Map<String, Entry> entries) {
            super(entries, false);
            locks = new HashMap<>();
        }

        @Override
        public boolean isLocked(String key) {
            return locks.containsKey(key);
        }

        @Override
        protected Lock doLock(String key) {
            if (!locks.containsKey(key)) {
                locks.put(key, new HashMap<>());
            }
            final Map<String, Lock> itemLocks = locks.get(key);
            final Entry entry = getEntries().get(key);
            final Entry copy = entry.copy();
            final Lock lock = new Lock(copy);
            itemLocks.put(lock.getId(), lock);
            return lock;
        }

        @Override
        protected void doRelinquish(String key, String lock) {
            final Map<String, Lock> itemLocks = locks.get(key);
            itemLocks.remove(lock);
            if (itemLocks.isEmpty()) {
                locks.remove(key);
            }
        }

        @Override
        protected boolean isHeldBy(String key, String lock) {
            return locks.get(key).containsKey(lock);
        }

        @Override
        protected void doUnlock(String key, String lock) {
            if (!getEntries().containsKey(key)) {
                throw new ServerException("Optimistic locking failure. Item deleted prior to the conclusion of transaction: " + key);
            }
            final Map<String, Lock> itemLocks = locks.get(key);
            final Lock itemLock = itemLocks.get(lock);
            final Entry entry = itemLock.getEntry();
            final Entry original = getEntries().get(key);
            if (original.getTimestamp() != entry.getTimestamp()) {
                throw new ServerException("Optimistic locking failure. Item modified prior to the conclusion of transaction: " + key);
            }
            if (entry.isDeleted()) {
                getEntries().remove(key);
            } else {
                if (entry.isFresh()) {
                    entry.bake();
                }
                getEntries().put(key, entry);
            }
            itemLocks.remove(lock);
            if (itemLocks.isEmpty()) {
                locks.remove(key);
            }
        }

        @Override
        protected void doMutate(String key, String lock, Serializable value) {
            final Entry entry = locks.get(key).get(lock).getEntry();
            entry.mutate(value, true);
        }

        @Override
        protected void doDelete(String key, String lock) {
            final Entry entry = locks.get(key).get(lock).getEntry();
            entry.markDeleted(true);
        }

        @Override
        protected Entry doGet(String key, String lock) {
            return locks.get(key).get(lock).getEntry();
        }

    }

}
