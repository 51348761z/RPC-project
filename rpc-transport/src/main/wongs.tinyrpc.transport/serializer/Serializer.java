package serializer;

import wongs.tinyrpc.common.protocol.MessageType;
import serializer.Impl.JsonSerializer;
import serializer.Impl.ObjectSerializer;

public interface Serializer {
    byte[] serialize(Object object);
    Object deserialize(byte[] bytes, MessageType messageType);
    int getCode();

    static Serializer createSerializer(SerializerType type) {
        return switch (type) {
            case OBJECT_SERIALIZER -> new ObjectSerializer();
            case JSON_SERIALIZER -> new JsonSerializer();
            default -> null;
        };
    }
}
