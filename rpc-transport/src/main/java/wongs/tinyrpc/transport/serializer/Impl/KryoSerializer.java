package wongs.tinyrpc.transport.serializer.Impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import wongs.tinyrpc.common.model.RpcRequest;
import wongs.tinyrpc.common.model.RpcResponse;
import wongs.tinyrpc.common.protocol.MessageType;
import wongs.tinyrpc.transport.serializer.Serializer;
import wongs.tinyrpc.transport.serializer.SerializerType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer implements Serializer {
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        kryo.setRegistrationRequired(false);
        return kryo;
    });
    @Override
    public byte[] serialize(Object object) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); Output output = new Output(bos)) {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, object);
            return output.toBytes();
        } catch (Exception e) {
            throw new RuntimeException("Kryo serialization failed", e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes, MessageType messageType) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); Input input = new Input(bis)) {
            Kryo kryo = kryoThreadLocal.get();
            Class<?> targetClass = messageType == MessageType.RPC_REQUEST ? RpcRequest.class : RpcResponse.class;
            return kryo.readObject(input, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("Kryo deserialization failed", e);
        }
    }

    @Override
    public int getCode() {
        return SerializerType.KRYO_SERIALIZER.getCode();
    }
}
