package me.theyinspire.pandora.rest.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.core.cmd.impl.ImmutableCommandWithArguments;
import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommandDispatcher;
import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommands;
import me.theyinspire.pandora.core.datastore.cmd.LockingDataStoreCommands;
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
import java.util.List;

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
            doDispatch(RequestMethod.POST, req, resp);
        }
    }

    private void doDispatch(RequestMethod method, HttpServletRequest request, HttpServletResponse response) {
        final Command<?> command = getCommand(method, request);
        if (command == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final Object result;
        try {
            LOG.info("Executing command: " + command);
            result = dispatcher.dispatch(command);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOG.error(e);
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

    private Command<?> getCommand(RequestMethod method, HttpServletRequest request) {
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
                } else if (url.matches("/uri/?")) {
                    return LockingDataStoreCommands.getUri(server.getConfiguration());
                } else if (url.matches("/signature/?")) {
                    return LockingDataStoreCommands.signature();
                } else if (url.matches("/locks/[^/]+/[^/]+/?")) {
                    return LockingDataStoreCommands.get(lockedKey(url), lock(url));
                }
                break;
            case HEAD:
                if (url.startsWith("/data/")) {
                    return DataStoreCommands.has(key(url));
                } else if (url.matches("/locks/[^/]+/?")) {
                    return LockingDataStoreCommands.isLocked(lockedKey(url));
                }
                break;
            case PUT:
                if (url.startsWith("/data/")) {
                    return DataStoreCommands.store(key(url), value(request));
                } else if (url.matches("/locks/[^/]+/[^/]+/?")) {
                    return LockingDataStoreCommands.store(lockedKey(url), lock(url), value(request));
                }
                break;
            case DELETE:
                if (url.matches("/data/[^/]+/?")) {
                    return DataStoreCommands.delete(key(url));
                } else if (url.matches("/data/?")) {
                    return DataStoreCommands.truncate();
                } else if (url.matches("/locks/[^/]+/[^/]+/?")) {
                    return LockingDataStoreCommands.delete(lockedKey(url), lock(url));
                }
                break;
            case POST:
                if (url.matches("/locks/[^/]+/?")) {
                    final String key = lockedKey(url);
                    return LockingDataStoreCommands.lock(key);
                } else if (url.matches("/locks/[^/]+/[^/]+/unlock/?")) {
                    return LockingDataStoreCommands.unlock(lockedKey(url), lock(url));
                } else if (url.matches("/locks/[^/]+/[^/]+/restore/?")) {
                    return LockingDataStoreCommands.restore(lockedKey(url), lock(url));
                } else if (url.matches("/commands/[^/]+/?")) {

                    return new ImmutableCommandWithArguments(command(url), args(request));
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

    private List<String> args(HttpServletRequest request) {
        final ServletInputStream inputStream;
        try {
            inputStream = request.getInputStream();
        } catch (IOException e) {
            throw new ServerException("Failed to open the input", e);
        }
        try {
            return mapper.readValue(inputStream, new TypeReference<List<String>>(){});
        } catch (IOException e) {
            throw new ServerException("Failed to read the input", e);
        }
    }

    private String key(String url) {
        return url.substring("/data/".length());
    }

    private String lockedKey(String url) {
        url = url.replaceFirst("/$", "");
        final String substring = url.substring("/locks/".length());
        final String[] split = substring.split("/");
        return split[0];
    }

    private String lock(String url) {
        url = url.replaceFirst("/$", "");
        final String substring = url.substring("/locks/".length());
        final String[] split = substring.split("/");
        return split[1];
    }

    private String command(String url) {
        url = url.replaceFirst("/$", "");
        final String substring = url.substring("/commands/".length());
        return substring;
    }

}
