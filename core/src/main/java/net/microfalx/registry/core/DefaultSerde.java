package net.microfalx.registry.core;

import net.microfalx.lang.AnnotationUtils;
import net.microfalx.lang.ObjectUtils;
import net.microfalx.lang.StringUtils;
import net.microfalx.lang.annotation.Order;
import net.microfalx.lang.annotation.Provider;
import net.microfalx.lang.annotation.Version;
import net.microfalx.registry.RegistryException;
import net.microfalx.registry.Serde;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Map;

import static net.microfalx.lang.ArgumentUtils.requireNonNull;

@Provider
@Order(Order.AFTER - 10)
public class DefaultSerde implements Serde {

    private static final String BINARY_SERIALIZATION = "BINARY:";

    private Serde delegate;

    public DefaultSerde() {
    }

    public DefaultSerde(Serde delegate) {
        requireNonNull(delegate);
        this.delegate = delegate;
    }

    @Override
    public <T> T to(byte[] value, Class<T> type) {
        if (ObjectUtils.isEmpty(value)) return null;
        String valueAsString = new String(value);
        return to(valueAsString, type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T to(String value, Class<T> type) {
        if (StringUtils.isEmpty(value)) return null;
        T result;
        if (value.startsWith(BINARY_SERIALIZATION)) {
            value = value.substring(BINARY_SERIALIZATION.length());
            byte[] decode = Base64.getDecoder().decode(value);
            result = (T) deserializeBinary(decode);
        } else {
            result = getDelegate().to(value, type);
        }
        if (result instanceof Map) {
            result = (T) unwrap((Map<String, Object>) result);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> String asString(T value) {
        if (ObjectUtils.isEmpty(value)) return null;
        Object raw = value;
        if (value instanceof Map) raw = wrap((Map<String, Object>) value);
        if (canSerializeBinary(raw)) {
            byte[] bytes = asBytes(value);
            return BINARY_SERIALIZATION + Base64.getEncoder().encodeToString(bytes);
        } else {
            return getDelegate().asString(value);
        }
    }

    @Override
    public <T> byte[] asBytes(T value) {
        if (canSerializeBinary(value)) {
            return serializeBinary(value);
        } else {
            return getDelegate().asBytes(value);
        }
    }

    private byte[] serializeBinary(Object value) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(value);
            out.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RegistryException("Failed to serialize object", e);
        }
    }

    private Object deserializeBinary(byte[] value) {
        ByteArrayInputStream bis = new ByteArrayInputStream(value);
        try (ObjectInputStream in = new ObjectInputStream(bis)) {
            return in.readObject();
        } catch (Exception e) {
            throw new RegistryException("Failed to deserialize object", e);
        }
    }

    private <T> boolean canSerializeBinary(T type) {
        return AnnotationUtils.getAnnotation(type, Version.class) != null;
    }

    private Map<String, Object> unwrap(Map<String, Object> source) {
        Map<String, Object> target = new java.util.HashMap<>();
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String stringValue) {
                if (stringValue.startsWith(BINARY_SERIALIZATION)) {
                    value = to(stringValue, Object.class);
                }
            }
            target.put(entry.getKey(), value);
        }
        return target;
    }

    private Map<String, Object> wrap(Map<String, Object> source) {
        Map<String, Object> target = new java.util.HashMap<>();
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            Object value = entry.getValue();
            if (canSerializeBinary(value)) {
                value = asString(value);
            }
            target.put(entry.getKey(), value);
        }
        return target;
    }

    private Serde getDelegate() {
        if (delegate == null) {
            delegate = new JsonSerde();
            ((JsonSerde) delegate).initialize();
        }
        return delegate;
    }
}
