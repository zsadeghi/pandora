package me.theyinspire.pandora.rest.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommand;
import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommandDispatcher;
import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommands;
import me.theyinspire.pandora.core.server.error.ServerException;
import me.theyinspire.pandora.rest.protocol.RequestMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/11/17, 8:12 PM)
 */
public class DataStoreServlet extends HttpServlet {

    private static final Log LOG = LogFactory.getLog("pandora.server.rest");
    private static final long serialVersionUID = -727378734335803763L;
    private final DataStoreCommandDispatcher dispatcher;
    private final ObjectMapper mapper;
    private final RestServer server;

    public DataStoreServlet(DataStore dataStore, ObjectMapper mapper, RestServer server) {
        dispatcher = new DataStoreCommandDispatcher(dataStore);
        this.mapper = mapper;
        this.server = server;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatch(RequestMethod.GET, req, resp);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatch(RequestMethod.HEAD, req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatch(RequestMethod.PUT, req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatch(RequestMethod.DELETE, req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String url = req.getRequestURI().substring(req.getContextPath().length());
        if (url.matches("/shutdown/?")) {
            final ServletOutputStream outputStream = resp.getOutputStream();
            mapper.writeValue(outputStream, "bye");
            server.stop();
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void doDispatch(RequestMethod method, HttpServletRequest request, HttpServletResponse response) {
        final DataStoreCommand<?> command = getCommand(method, request);
        final Object result;
        try {
            LOG.info("Executing command: " + command);
            result = dispatcher.dispatch(command);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        if (result == null) {
            System.err.println("Bad input: " + command);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final ServletOutputStream outputStream;
        try {
            outputStream = response.getOutputStream();
        } catch (IOException e) {
            throw new ServerException("Failed to get the output", e);
        }
        try {
            mapper.writeValue(outputStream, result);
        } catch (IOException e) {
            throw new ServerException("Failed to write the result", e);
        }
    }

    private DataStoreCommand<?> getCommand(RequestMethod method, HttpServletRequest request) {
        final String url = request.getRequestURI().substring(request.getContextPath().length());
        switch (method) {
            case GET:
                if (url.matches("/keys/size/?")) {
                    return DataStoreCommands.size();
                } else if (url.matches("/keys/?")) {
                    return DataStoreCommands.keys();
                } else if (url.startsWith("/data/")) {
                    return DataStoreCommands.get(key(url));
                } else if (url.matches("/data/?")) {
                    return DataStoreCommands.all();
                }
                break;
            case HEAD:
                if (url.startsWith("/data/")) {
                    return DataStoreCommands.has(key(url));
                }
                break;
            case PUT:
                if (url.startsWith("/data/")) {
                    return DataStoreCommands.store(key(url), value(request));
                }
                break;
            case DELETE:
                if (url.startsWith("/data/")) {
                    return DataStoreCommands.delete(key(url));
                } else if (url.matches("/data/?")) {
                    return DataStoreCommands.truncate();
                }
                break;
        }
        return null;
    }

    private String value(HttpServletRequest request) {
        final ServletInputStream inputStream;
        try {
            inputStream = request.getInputStream();
        } catch (IOException e) {
            throw new ServerException("Failed to open the input", e);
        }
        try {
            return mapper.readValue(inputStream, String.class);
        } catch (IOException e) {
            throw new ServerException("Failed to read the input", e);
        }
    }

    private String key(String url) {
        return url.substring("/data/".length());
    }

}
