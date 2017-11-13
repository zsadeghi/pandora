package me.theyinspire.pandora.tcp.protocol;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 1:33 PM)
 */
public class TcpProtocolTest {

    private TcpProtocol protocol;

    @BeforeMethod
    public void setUp() throws Exception {
        protocol = TcpProtocol.getInstance();
    }

    @Test
    public void testReadAndWrite() throws Exception {
        final TcpProtocolReader reader = protocol.getReader();
        final TcpProtocolWriter writer = protocol.getWriter();
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final String written = "this is a test";
        writer.write(outputStream, written);
        final String read = reader.read(new ByteArrayInputStream(outputStream.toByteArray()));
        assertThat(read, is(written));
    }

}