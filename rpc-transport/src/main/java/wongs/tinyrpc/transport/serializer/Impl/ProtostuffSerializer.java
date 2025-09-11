package wongs.tinyrpc.transport.serializer.Impl;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimePipeSchema;
import io.protostuff.runtime.RuntimeSchema;
import wongs.tinyrpc.common.model.RpcRequest;
import wongs.tinyrpc.common.model.RpcResponse;
import wongs.tinyrpc.common.protocol.MessageType;
import wongs.tinyrpc.transport.serializer.Serializer;
import wongs.tinyrpc.transport.serializer.SerializerType;

public class ProtostuffSerializer implements Serializer {
    private final LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    @Override
    public byte[] serialize(Object object) {
        Class<?> clazz = object.getClass();
        Schema schema = RuntimeSchema.getSchema(clazz);
        try {
            return ProtobufIOUtil.toByteArray(object, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public Object deserialize(byte[] bytes, MessageType messageType) {
        Class<?> targetClass = messageType == MessageType.RPC_REQUEST ? RpcRequest.class : RpcResponse.class;
        Schema schema = RuntimeSchema.getSchema(targetClass);
        Object message = schema.newMessage();
        ProtobufIOUtil.mergeFrom(bytes, message, schema);
        return message;
    }

    @Override
    public int getCode() {
        return SerializerType.PROTOSTUFF_SERIALIZER.getCode();
    }
}
