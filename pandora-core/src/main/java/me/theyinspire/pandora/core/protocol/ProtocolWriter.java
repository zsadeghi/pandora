package me.theyinspire.pandora.core.protocol;

import me.theyinspire.pandora.core.error.CommunicationException;
import me.theyinspire.pandora.core.error.IOCommunicationException;
import me.theyinspire.pandora.core.error.ProtocolException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 12:55 PM)
 */
public interface ProtocolWriter {

    default void write(OutputStream stream, String output) throws CommunicationException {
        try {
            write(new OutputStreamWriter(stream, "UTF-8"), output);
        } catch (UnsupportedEncodingException e) {
            throw new ProtocolException("Protocol does not allow for UTF-8 encoding", e);
        }
    }

    default void writeAndClose(OutputStream stream, String output) throws CommunicationException {
        final OutputStreamWriter writer;
        try {
            writer = new OutputStreamWriter(stream, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new ProtocolException("Protocol does not allow for UTF-8 encoding", e);
        }
        write(writer, output);
        try {
            writer.close();
        } catch (IOException e) {
            throw new IOCommunicationException("Failed to close the communication channel", e);
        }
    }

    void write(OutputStreamWriter stream, String output) throws CommunicationException;

}
