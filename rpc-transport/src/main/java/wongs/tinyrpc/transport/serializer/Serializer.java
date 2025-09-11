package wongs.tinyrpc.transport.serializer;

import wongs.tinyrpc.common.protocol.MessageType;
import wongs.tinyrpc.transport.serializer.Impl.*;

public interface Serializer {
    byte[] serialize(Object object);
    Object deserialize(byte[] bytes, MessageType messageType);
    int getCode();

    static Serializer createSerializer(SerializerType type) {
        return switch (type) {
            case OBJECT_SERIALIZER -> new ObjectSerializer();
            case JSON_SERIALIZER -> new JsonSerializer();
            case HESSIAN_SERIALIZER -> new HessianSerializer();
            case KRYO_SERIALIZER -> new KryoSerializer();
            case PROTOSTUFF_SERIALIZER -> new ProtostuffSerializer();
            default -> throw new IllegalArgumentException("Unknown serializer type: " + type);
        };
    }
}
