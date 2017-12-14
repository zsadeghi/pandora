package me.theyinspire.pandora.rest.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import me.theyinspire.pandora.core.client.error.ClientException;
import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.core.datastore.CommandReceiver;
import me.theyinspire.pandora.core.datastore.LockingDataStore;
import me.theyinspire.pandora.core.server.ServerConfiguration;
import me.theyinspire.pandora.rest.protocol.RequestMethod;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/11/17, 8:19 PM)
 */
public class DataStoreClient implements LockingDataStore, CommandReceiver {

    private final String prefix;

    public DataStoreClient(String prefix, ObjectMapper mapper) {
        this.prefix = prefix.replaceFirst("/$", "");
        Unirest.setObjectMapper(new JacksonObjectMapper(mapper));
    }

    @Override
    public long size() {
        return execute(Long.class, RequestMethod.GET, "/keys/size");
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean store(String key, Serializable value) {
        return execute(Boolean.class, RequestMethod.PUT, "/data/" + key, value);
    }

    @Override
    public Serializable get(String key) {
        return cleanUpString(execute(String.class, RequestMethod.GET, "/data/" + key));
    }

    private String cleanUpString(String response) {
        if (response.matches("\".*?\"")) {
            response = response.substring(1, response.length() - 1)
                               .replaceAll("\\\\n", "\n")
                               .replaceAll("\\\\r", "\r")
                               .replaceAll("\\\\t", "\t");
        }
        return response;
    }

    @Override
    public boolean delete(String key) {
        return execute(Boolean.class, RequestMethod.DELETE, "/data/" + key);
    }

    @Override
    public Set<String> keys() {
        //noinspection unchecked
        return execute(Set.class, RequestMethod.GET, "/keys");
    }

    @Override
    public long truncate() {
        return execute(Long.class, RequestMethod.DELETE, "/data");
    }

    @Override
    public boolean has(String key) {
        return execute(Boolean.class, RequestMethod.HEAD, "/data/" + key);
    }

    @Override
    public Map<String, Serializable> all() {
        //noinspection unchecked
        return execute(Map.class, RequestMethod.GET, "/data");
    }

    @Override
    public String getUri(ServerConfiguration configuration) {
        return execute(String.class, RequestMethod.GET, "/uri");
    }

    @Override
    public String lock(String key) {
        return execute(String.class, RequestMethod.POST, "/locks/" + key);
    }

    @Override
    public void restore(String key, String lock) {
        execute(String.class, RequestMethod.POST, "/locks/" + key + "/" + lock + "/restore");
    }

    @Override
    public void unlock(String key, String lock) {
        execute(String.class, RequestMethod.POST, "/locks/" + key + "/" + lock + "/unlock");
    }

    @Override
    public boolean store(String key, Serializable value, String lock) {
        return execute(Boolean.class, RequestMethod.PUT, "/locks/" + key + "/" + lock, value);
    }

    @Override
    public boolean delete(String key, String lock) {
        return execute(Boolean.class, RequestMethod.DELETE, "/locks/" + key + "/" + lock);
    }

    @Override
    public Serializable get(String key, String lock) {
        return execute(String.class, RequestMethod.GET, "/locks/" + key + "/" + lock);
    }

    @Override
    public boolean locked(String key) {
        return execute(Boolean.class, RequestMethod.HEAD, "/locks/" + key);
    }

    @Override
    public String getSignature() {
        return execute(String.class, RequestMethod.GET, "/signature");
    }

    public String exit() {
        return cleanUpString(execute(String.class, RequestMethod.POST, "/shutdown"));
    }

    @Override
    public <R> R receive(final Command<R> command) {
        return null;//execute(Object.class, RequestMethod.POST, "/commands", command);
    }

    private <T> T execute(Class<T> type, RequestMethod method, String path) {
        return execute(type, method, path, null);
    }

    private <T> T execute(Class<T> type, RequestMethod method, String path, Object body) {
        final HttpResponse<T> result;
        try {
            final HttpRequest request = getRequest(method, path);
            if (request instanceof HttpRequestWithBody) {
                HttpRequestWithBody withBody = (HttpRequestWithBody) request;
                withBody.body(body);
            }
            result = request
                    .asObject(type);
        } catch (UnirestException e) {
            throw new ClientException("Failed to create request", e);
        }
        if (result.getStatus() / 100 != 2) {
            throw new ClientException("Bad request sent to server, status code: " + result.getStatus());
        }
        try {
            Unirest.shutdown();
        } catch (IOException e) {
            throw new ClientException("Failed to shutdown the REST client", e);
        }
        return result.getBody();
    }

    private HttpRequest getRequest(RequestMethod method, String path) {
        switch (method) {
            case GET:
                return Unirest.get(prefix.concat(path));
            case HEAD:
                return Unirest.head(prefix.concat(path));
            case PUT:
                return Unirest.put(prefix.concat(path));
            case POST:
                return Unirest.post(prefix.concat(path));
            case DELETE:
                return Unirest.delete(prefix.concat(path));
            default:
                throw new UnsupportedOperationException();
        }
    }
}
