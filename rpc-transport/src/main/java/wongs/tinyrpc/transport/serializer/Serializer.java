package wongs.tinyrpc.transport.serializer;

import wongs.tinyrpc.common.protocol.MessageType;
import wongs.tinyrpc.transport.serializer.Impl.JsonSerializer;
import wongs.tinyrpc.transport.serializer.Impl.ObjectSerializer;

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
