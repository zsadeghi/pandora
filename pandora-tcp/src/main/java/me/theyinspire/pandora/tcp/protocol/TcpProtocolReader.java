package me.theyinspire.pandora.tcp.protocol;

import me.theyinspire.pandora.core.error.CommunicationException;
import me.theyinspire.pandora.core.error.IOCommunicationException;
import me.theyinspire.pandora.core.error.ProtocolException;
import me.theyinspire.pandora.core.protocol.ProtocolReader;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 12:59 PM)
 */
public class TcpProtocolReader implements ProtocolReader {

    @Override
    public String read(InputStreamReader reader) throws CommunicationException {
        readPrefix(reader);
        int read;
        final StringBuilder builder = new StringBuilder();
        while (true) {
            try {
                read = reader.read();
            } catch (IOException e) {
                throw new IOCommunicationException("Failed to read from the input", e);
            }
            if (read == -1) {
                break;
            }
            builder.append((char) read);
            if (hasSuffix(builder)) {
                break;
            }
        }
        if (!hasSuffix(builder)) {
            throw new ProtocolException("Premature end of file");
        }
        deleteSuffix(builder);
        return builder.toString();
    }

    private void deleteSuffix(StringBuilder builder) {
        builder.delete(builder.length() - TcpProtocol.SUFFIX.length(), builder.length());
    }

    private void readPrefix(InputStreamReader reader) {
        for (int i = 0; i < TcpProtocol.PREFIX.length(); i++) {
            final char expected = TcpProtocol.PREFIX.charAt(i);
            final int read;
            try {
                read = reader.read();
            } catch (IOException e) {
                throw new IOCommunicationException("Failed to read from the input", e);
            }
            if (read == -1) {
                throw new ProtocolException("Expected " + expected + " but reached the end of the message");
            }
            if ((char) read != expected) {
                throw new ProtocolException("Expected " + expected + " but found " + ((char) read));
            }
        }
    }

    private boolean hasSuffix(StringBuilder builder) {
        if (builder.length() < TcpProtocol.SUFFIX.length()) {
            return false;
        }
        for (int i = 0; i < TcpProtocol.SUFFIX.length(); i++) {
            final char expected = TcpProtocol.SUFFIX.charAt(i);
            final char read = builder.charAt(builder.length() - TcpProtocol.SUFFIX.length() + i);
            if (read != expected) {
                return false;
            }
        }
        return true;
    }

}
