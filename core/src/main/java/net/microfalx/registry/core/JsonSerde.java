package net.microfalx.registry.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.microfalx.lang.Initializable;
import net.microfalx.lang.annotation.Order;
import net.microfalx.lang.annotation.Provider;
import net.microfalx.registry.Serde;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;

import static net.microfalx.lang.ArgumentUtils.requireNonNull;
import static net.microfalx.lang.ExceptionUtils.rethrowExceptionAndReturn;

@Provider
@Order(Order.AFTER)
public class JsonSerde implements Serde, Initializable {

    private ObjectMapper objectMapper;
    private final Collection<Module> modules = new ArrayList<>();

    @Override
    public <T> T to(byte[] value, Class<T> type) {
        requireNonNull(type);
        if (value == null) return null;
        try {
            return objectMapper.readValue(new ByteArrayInputStream(value), type);
        } catch (IOException e) {
            return rethrowExceptionAndReturn(e);
        }
    }

    @Override
    public <T> T to(String value, Class<T> type) {
        requireNonNull(type);
        if (value == null) return null;
        try {
            return objectMapper.readValue(new StringReader(value), type);
        } catch (IOException e) {
            return rethrowExceptionAndReturn(e);
        }
    }

    @Override
    public <T> String asString(T value) {
        if (value == null) return null;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return rethrowExceptionAndReturn(e);
        }
    }

    @Override
    public <T> byte[] asBytes(T value) {
        if (value == null) return null;
        try {
            return objectMapper.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            return rethrowExceptionAndReturn(e);
        }

    }

    public void addModule(Module module) {
        requireNonNull(module);
        modules.add(module);
    }

    @Override
    public void initialize(Object... context) {
        objectMapper = new  ObjectMapper();
        objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        for (Module module : modules) {
            objectMapper.registerModule(module);
        }

        JavaTimeModule timeModule = new JavaTimeModule();
        objectMapper.registerModule(timeModule);

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
    }
}
