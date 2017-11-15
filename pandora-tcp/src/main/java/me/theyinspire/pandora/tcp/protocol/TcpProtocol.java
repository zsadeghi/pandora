package me.theyinspire.pandora.tcp.protocol;

import me.theyinspire.pandora.core.config.ProtocolOptionRegistry;
import me.theyinspire.pandora.core.protocol.impl.AbstractProtocol;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 1:23 PM)
 */
public class TcpProtocol extends AbstractProtocol<TcpProtocolReader, TcpProtocolWriter> {

    public final static String PREFIX = "^<<";
    public final static String SUFFIX = "^>>&";

    private static final TcpProtocol INSTANCE = new TcpProtocol();

    public static TcpProtocol getInstance() {
        return INSTANCE;
    }

    private TcpProtocol() {
        super(new TcpProtocolReader(), new TcpProtocolWriter());
    }

    @Override
    public String getName() {
        return "tcp";
    }

    @Override
    public String transform(String message) {
        return PREFIX.concat(message).concat(SUFFIX);
    }

    @Override
    public void defineOptions(ProtocolOptionRegistry registry) {
        registry.register("host", "The host name", "0.0.0.0");
        registry.register("port", "The communication port", "8081");
    }

}
