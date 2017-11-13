package me.theyinspire.pandora.core.protocol;

import me.theyinspire.pandora.core.error.CommunicationException;
import me.theyinspire.pandora.core.error.IOCommunicationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 12:54 PM)
 */
public interface ProtocolReader {

    default String read(InputStream stream) throws CommunicationException {
        return read(new InputStreamReader(stream));
    }

    default String readAndClose(InputStream stream) throws CommunicationException {
        final InputStreamReader reader = new InputStreamReader(stream, Charset.forName("UTF-8"));
        final String content = read(reader);
        try {
            reader.close();
        } catch (IOException e) {
            throw new IOCommunicationException("Failed to close the communication channel", e);
        }
        return content;
    }

    String read(InputStreamReader reader) throws CommunicationException;

}
