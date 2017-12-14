package me.theyinspire.pandora.core.datastore.cmd;

import java.io.Serializable;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/26/17, 6:35 PM)
 */
public final class DataStoreCommands {

    private DataStoreCommands() {
        throw new UnsupportedOperationException();
    }

    public static SizeCommand size() {
        return new SizeCommandImpl();
    }

    public static IsEmptyCommand isEmpty() {
        return new IsEmptyCommandImpl();
    }

    public static StoreCommand store(String key, Serializable value) {
        return new StoreCommandImpl(key, value);
    }

    public static GetCommand get(String key) {
        return new GetCommandImpl(key);
    }

    public static DeleteCommand delete(String key) {
        return new DeleteCommandImpl(key);
    }

    public static KeysCommand keys() {
        return new KeysCommandImpl();
    }

    public static TruncateCommand truncate() {
        return new TruncateCommandImpl();
    }

    public static HasCommand has(String key) {
        return new HasCommandImpl(key);
    }

    public static AllCommand all() {
        return new AllCommandImpl();
    }

    public static TestCommand test() {
        return new TestCommandImpl();
    }

    private static class SizeCommandImpl implements SizeCommand {

        @Override
        public String toString() {
            return "{size()}";
        }
    }

    private static class IsEmptyCommandImpl implements IsEmptyCommand {

        @Override
        public String toString() {
            return "{isEmpty()}";
        }
    }

    private static class StoreCommandImpl implements StoreCommand {

        private final String key;
        private final Serializable value;

        private StoreCommandImpl(String key, Serializable value) {
            this.key = key;
            this.value = value;
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
        public String toString() {
            return "{put(" + key + "," + value + ")}";
        }
    }

    private static class GetCommandImpl implements GetCommand {

        private final String key;

        private GetCommandImpl(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String toString() {
            return "{get(" + key + ")}";
        }
    }

    private static class DeleteCommandImpl implements DeleteCommand {

        private final String key;

        private DeleteCommandImpl(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String toString() {
            return "{delete(" + key + ")}";
        }
    }

    private static class KeysCommandImpl implements KeysCommand {

        @Override
        public String toString() {
            return "{keys()}";
        }
    }

    private static class TruncateCommandImpl implements TruncateCommand {

        @Override
        public String toString() {
            return "{truncate()}";
        }
    }

    private static class HasCommandImpl implements HasCommand {

        private final String key;

        private HasCommandImpl(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String toString() {
            return "{has(" + key + ")}";
        }
    }

    private static class AllCommandImpl implements AllCommand {

        @Override
        public String toString() {
            return "{all()}";
        }
    }

    private static class TestCommandImpl implements TestCommand {

        @Override
        public String toString() {
            return "{test}";
        }

    }
}
