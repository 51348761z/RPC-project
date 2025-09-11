package wongs.tinyrpc.transport.serializer;

import wongs.tinyrpc.common.protocol.MessageType;
import wongs.tinyrpc.transport.serializer.Impl.*;

public interface Serializer {
    byte[] serialize(Object object);
    Object deserialize(byte[] bytes, MessageType messageType);
    int getCode();
    SerializerType getType();
}
