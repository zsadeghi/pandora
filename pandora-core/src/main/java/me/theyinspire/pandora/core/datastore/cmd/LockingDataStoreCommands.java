package me.theyinspire.pandora.core.datastore.cmd;

import me.theyinspire.pandora.core.server.ServerConfiguration;

import java.io.Serializable;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 4:25 PM)
 */
public class LockingDataStoreCommands {

    private LockingDataStoreCommands() {
        throw new UnsupportedOperationException();
    }

    public static LockCommand lock(String key) {
        return new LockCommandImpl(key);
    }

    public static UnlockCommand unlock(String key, String lock) {
        return new UnlockCommandImpl(key, lock);
    }

    public static RestoreCommand restore(String key, String lock) {
        return new RestoreCommandImpl(key, lock);
    }

    public static IsLockedCommand isLocked(String key) {
        return new IsLockedCommandImpl(key);
    }

    public static SignatureCommand signature() {
        return new SignatureCommandImpl();
    }

    public static LockedStoreCommand store(String key, String lock, Serializable value) {
        return new LockedStoreCommandImpl(key, value, lock);
    }

    public static LockedDeleteCommand delete(String key, String lock) {
        return new LockedDeleteCommandImpl(key, lock);
    }

    public static LockedGetCommand get(String key, String lock) {
        return new LockedGetCommandImpl(key, lock);
    }

    public static GetUriCommand getUri(ServerConfiguration serverConfiguration) {
        return new GetUriCommandImpl(serverConfiguration);
    }

    private static class LockCommandImpl implements LockCommand {

        private final String key;

        private LockCommandImpl(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String toString() {
            return "{lock(" + key + ")}";
        }

    }

    private static class UnlockCommandImpl implements UnlockCommand {

        private final String key;
        private final String lock;

        private UnlockCommandImpl(String key, String lock) {
            this.key = key;
            this.lock = lock;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String toString() {
            return "{unlock(" + key + "," + lock + ")}";
        }

        @Override
        public String getLock() {
            return lock;
        }

    }

    private static class RestoreCommandImpl implements RestoreCommand {

        private final String key;
        private final String lock;

        private RestoreCommandImpl(String key, String lock) {
            this.key = key;
            this.lock = lock;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getLock() {
            return lock;
        }

        @Override
        public String toString() {
            return "{restore(" + key + "," + lock + ")}";
        }

    }

    private static class IsLockedCommandImpl implements IsLockedCommand {

        private final String key;

        private IsLockedCommandImpl(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String toString() {
            return "{isLocked(" + key + ")}";
        }

    }

    private static class GetUriCommandImpl implements GetUriCommand {

        private final ServerConfiguration serverConfiguration;

        private GetUriCommandImpl(ServerConfiguration serverConfiguration) {
            this.serverConfiguration = serverConfiguration;
        }

        @Override
        public String toString() {
            return "{getUri()}";
        }

        @Override
        public ServerConfiguration getServerConfiguration() {
            return serverConfiguration;
        }
    }

    private static class SignatureCommandImpl implements SignatureCommand {

        @Override
        public String toString() {
            return "{getSignature()}";
        }

    }

    private static class LockedDeleteCommandImpl implements LockedDeleteCommand {

        private final String key;
        private final String lock;

        private LockedDeleteCommandImpl(String key, String lock) {
            this.key = key;
            this.lock = lock;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getLock() {
            return lock;
        }

        @Override
        public String toString() {
            return "{deleteLocked(" + key + "," + lock + ")}";
        }

    }

    private static class LockedGetCommandImpl implements LockedGetCommand {

        private final String key;
        private final String lock;

        private LockedGetCommandImpl(String key, String lock) {
            this.key = key;
            this.lock = lock;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getLock() {
            return lock;
        }

        @Override
        public String toString() {
            return "{getLocked(" + key + "," + lock + ")}";
        }

    }

    private static class LockedStoreCommandImpl implements LockedStoreCommand {

        private final String key;
        private final Serializable value;
        private final String lock;

        private LockedStoreCommandImpl(String key, Serializable value, String lock) {
            this.key = key;
            this.value = value;
            this.lock = lock;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Serializable getValue() {
            return value;
        }

        @Override
        public String getLock() {
            return lock;
        }

        @Override
        public String toString() {
            return "{putLocked(" + key + "," + value + "," + lock + ")}";
        }

    }

}
