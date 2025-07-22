package common.serializer.mySerializer;

public interface Serializer {
    byte[] serialize(Object object);
    Object deserialize(byte[] bytes, int messageType);
    SerializerType getType();

    static Serializer createSerializer(SerializerType type) {
        return switch (type) {
            case OBJECT_SERIALIZER -> new ObjectSerializer();
            case JSON_SERIALIZER -> new JsonSerializer();
            default -> null;
        };
    }
}
