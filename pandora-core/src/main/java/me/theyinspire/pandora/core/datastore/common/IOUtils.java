package me.theyinspire.pandora.core.datastore.common;

import java.io.*;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/27/17, 12:52 AM)
 */
public final class IOUtils {

    private IOUtils() {
        throw new UnsupportedOperationException();
    }

    public static String readString(InputStream inputStream) throws IOException {
        final StringWriter stringWriter = new StringWriter();
        char[] buffer = new char[1024 * 4];
        int n = 0;
        final InputStreamReader reader = new InputStreamReader(inputStream);
        while (true) {
            n = reader.read(buffer);
            stringWriter.write(buffer, 0, n);
            if (n < buffer.length) {
                break;
            }
        }
        return stringWriter.toString();
    }

    public static void writeString(OutputStream outputStream, String string) throws IOException {
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        writer.write(string);
    }

}
