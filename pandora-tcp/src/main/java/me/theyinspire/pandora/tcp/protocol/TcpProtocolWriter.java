package me.theyinspire.pandora.tcp.protocol;

import me.theyinspire.pandora.core.error.CommunicationException;
import me.theyinspire.pandora.core.error.IOCommunicationException;
import me.theyinspire.pandora.core.error.ProtocolException;
import me.theyinspire.pandora.core.protocol.ProtocolWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 1:22 PM)
 */
public class TcpProtocolWriter implements ProtocolWriter {

    @Override
    public void write(OutputStreamWriter writer, String output) throws CommunicationException {
        if (output.contains(TcpProtocol.SUFFIX)) {
            throw new ProtocolException("Message contains illegal sequence " + TcpProtocol.SUFFIX);
        }
        try {
            writer.write(TcpProtocol.PREFIX);
            writer.write(output);
            writer.write(TcpProtocol.SUFFIX);
            writer.flush();
        } catch (IOException e) {
            throw new IOCommunicationException("Failed to write to the output buffer", e);
        }
    }

}
