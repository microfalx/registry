package net.microfalx.registry;

/**
 * An interface into the serialization/deserialization.
 */
public interface Serde {

    /**
     * Deserializes a type from an array of bytes.
     *
     * @param value the value
     * @param type  the expected type
     * @param <T>   the type of the deserialized value
     * @return the deserialized value
     */
    <T> T to(byte[] value, Class<T> type);

    /**
     * Deserializes a type from a string.
     *
     * @param value the value
     * @param type  the expected type
     * @param <T>   the type of the deserialized value
     * @return the deserialized value
     */
    <T> T to(String value, Class<T> type);

    /**
     * Serializes a value to a byte array.
     *
     * @param value the value to serialize
     * @param <T>   the type of the value
     * @return the serialized byte array
     */
    <T> String asString(T value);

    /**
     * Serializes a value to a byte array.
     *
     * @param value the value to serialize
     * @param <T>   the type of the value
     * @return the serialized byte array
     */
    <T> byte[] asBytes(T value);

}