package me.theyinspire.pandora.tcp.protocol;

import me.theyinspire.pandora.core.error.ProtocolException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 1:24 PM)
 */
public class TcpProtocolWriterTest {

    private TcpProtocolWriter writer;
    private TcpProtocol protocol;

    @BeforeMethod
    public void setUp() throws Exception {
        writer = new TcpProtocolWriter();
        protocol = TcpProtocol.getInstance();
    }

    @Test
    public void testWritingNonEmptyMessage() throws Exception {
        final String message = "this is a test";
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        writer.write(stream, message);
        assertThat(stream.toString(), is(protocol.transform(message)));
    }

    @Test
    public void testWritingEmptyMessage() throws Exception {
        final String message = "";
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        writer.write(stream, message);
        assertThat(stream.toString(), is(protocol.transform(message)));
    }

    @Test(expectedExceptions = ProtocolException.class, expectedExceptionsMessageRegExp = ".*illegal sequence.*")
    public void testWritingMessageContainingSuffix() throws Exception {
        final String message = protocol.transform("message");
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        writer.write(stream, message);
        assertThat(stream.toString(), is(protocol.transform(message)));
    }

}