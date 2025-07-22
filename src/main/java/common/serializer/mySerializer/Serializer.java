package common.serializer.mySerializer;

public interface Serializer {
    // serialize an object to a byte array
    byte[] serialize(Object object);

    // deserialize a byte array to an object of type T
    Object deserialize(byte[] bytes, int messageType);

    // get the type of the serializer
    SerializerType getType();

    static Serializer getSerializerByCode(SerializerType type) {
        return switch (type) {
            case OBJECT_SERIALIZER -> new ObjectSerializer();
            case JSON_SERIALIZER -> new JsonSerializer();
            default -> null;
        };
    }
}
