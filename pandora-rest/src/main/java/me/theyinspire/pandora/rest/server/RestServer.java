package me.theyinspire.pandora.rest.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.server.error.ServerException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Logger;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/11/17, 7:43 PM)
 */
public class RestServer implements me.theyinspire.pandora.core.server.Server {

    private static final Log LOG = LogFactory.getLog("pandora.server.rest");
    private final String hostname;
    private final int port;
    private final String contextPath;
    private final DataStore dataStore;
    private Server server;
    private Thread serverThread;

    public RestServer(String hostname, int port, String contextPath, DataStore dataStore) {
        this.hostname = hostname;
        this.port = port;
        this.contextPath = contextPath == null ? "" : contextPath
                .replaceAll("/+", "/")
                .replaceFirst("^/", "")
                .replaceFirst("/$", "");
        this.dataStore = dataStore;
    }

    @Override
    public void start() throws ServerException {
        org.eclipse.jetty.util.log.Log.setLog(new NoLogging());
        serverThread = Thread.currentThread();
        server = new Server();
        final ServerConnector connector = new ServerConnector(server);
        connector.setHost(hostname);
        connector.setPort(port);
        server.setConnectors(new Connector[]{connector});
        LOG.debug("Setting up the servlet context");
        final ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/" + contextPath);
        context.addServlet(new ServletHolder(new DataStoreServlet(dataStore, new ObjectMapper(), this)), "/*");
        final HandlerCollection handlers = new HandlerCollection();
        handlers.addHandler(context);
        server.setHandler(handlers);
        try {
            LOG.info("Starting up the server @ " + hostname + ":" + port);
            server.start();
        } catch (Exception e) {
            try {
                LOG.info("Stopping the server due to an error");
                stop();
            } catch (Exception f) {
                //noinspection ThrowFromFinallyBlock
                throw new IllegalStateException(f);
            }
            throw new ServerException("Failed to start the server", e);
        }
        try {
            LOG.info("Waiting for shutdown sequence ...");
            server.join();
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void stop() throws ServerException {
        try {
            LOG.info("Initiating shutdown sequence ...");
            serverThread.interrupt();
            server.stop();
        } catch (Exception e) {
            throw new ServerException("Failed to stop the server", e);
        }
    }

    private class NoLogging implements Logger {
        @Override
        public String getName() {
            return "no";
        }

        @Override
        public void warn(String msg, Object... args) {
        }

        @Override
        public void warn(Throwable thrown) {
        }

        @Override
        public void warn(String msg, Throwable thrown) {
        }

        @Override
        public void info(String msg, Object... args) {
        }

        @Override
        public void info(Throwable thrown) {
        }

        @Override
        public void info(String msg, Throwable thrown) {
        }

        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        @Override
        public void setDebugEnabled(boolean enabled) {
        }

        @Override
        public void debug(String msg, Object... args) {
        }

        @Override
        public void debug(String msg, long value) {

        }

        @Override
        public void debug(Throwable thrown) {
        }

        @Override
        public void debug(String msg, Throwable thrown) {
        }

        @Override
        public Logger getLogger(String name) {
            return this;
        }

        @Override
        public void ignore(Throwable ignored) {
        }
    }

}
