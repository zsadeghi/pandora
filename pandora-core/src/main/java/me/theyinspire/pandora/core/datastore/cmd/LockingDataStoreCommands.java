package me.theyinspire.pandora.core.datastore.cmd;

import me.theyinspire.pandora.core.server.ServerConfiguration;

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

    public static UnlockCommand unlock(String key) {
        return new UnlockCommandImpl(key);
    }

    public static RestoreCommand restore(String key) {
        return new RestoreCommandImpl(key);
    }

    public static IsLockedCommand isLocked(String key) {
        return new IsLockedCommandImpl(key);
    }

    public static SignatureCommand signature() {
        return new SignatureCommandImpl();
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

        private UnlockCommandImpl(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String toString() {
            return "{unlock(" + key + ")}";
        }

    }

    private static class RestoreCommandImpl implements RestoreCommand {

        private final String key;

        private RestoreCommandImpl(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String toString() {
            return "{restore(" + key + ")}";
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

}
