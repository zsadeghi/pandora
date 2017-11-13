package me.theyinspire.pandora.tcp.protocol;

import me.theyinspire.pandora.core.error.ProtocolException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 1:10 PM)
 */
public class TcpProtocolReaderTest {

    private TcpProtocolReader reader;

    @BeforeMethod
    public void setUp() throws Exception {
        reader = new TcpProtocolReader();
    }

    @Test
    public void testReadingProperMessage() throws Exception {
        final String message = "This is a test";
        final String input = TcpProtocol.PREFIX + message + TcpProtocol.SUFFIX;
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        final String read = reader.read(inputStream);
        assertThat(read, is(message));
    }

    @Test(expectedExceptions = ProtocolException.class, expectedExceptionsMessageRegExp = ".*Expected.*but found.*")
    public void testReadingWithoutPrefix() throws Exception {
        final String message = "This is a test";
        final String input = message + TcpProtocol.SUFFIX;
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        reader.read(inputStream);
    }

    @Test(expectedExceptions = ProtocolException.class, expectedExceptionsMessageRegExp = ".*the end.*")
    public void testReadingEmptyMessage() throws Exception {
        final String input = "";
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        reader.read(inputStream);
    }

    @Test(expectedExceptions = ProtocolException.class, expectedExceptionsMessageRegExp = ".*Premature.*")
    public void testReadingMessageWithoutSuffix() throws Exception {
        final String message = "This is a test";
        final String input = TcpProtocol.PREFIX + message;
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        reader.read(inputStream);
    }

    @Test(expectedExceptions = ProtocolException.class, expectedExceptionsMessageRegExp = ".*Premature.*")
    public void testReadingMessageTooShort() throws Exception {
        final String message = "Th";
        final String input = TcpProtocol.PREFIX + message;
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        reader.read(inputStream);
    }

    @Test
    public void testReadingMessageContainingSuffix() throws Exception {
        final String payload = "This is a test";
        final String message = payload + TcpProtocol.SUFFIX + "lalala";
        final String input = TcpProtocol.PREFIX + message;
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        final String read = reader.read(inputStream);
        assertThat(read, is(payload));
    }

}