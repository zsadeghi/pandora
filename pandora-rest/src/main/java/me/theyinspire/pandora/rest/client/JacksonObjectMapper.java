package me.theyinspire.pandora.rest.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.ObjectMapper;

import java.io.IOException;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/11/17, 9:11 PM)
 */
public class JacksonObjectMapper implements ObjectMapper {

    private final com.fasterxml.jackson.databind.ObjectMapper mapper;

    public JacksonObjectMapper(com.fasterxml.jackson.databind.ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public <T> T readValue(String value, Class<T> valueType) {
        try {
            return mapper.readValue(value, valueType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String writeValue(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
