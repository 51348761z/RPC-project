package wongs.tinyrpc.transport.serializer.Impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import wongs.tinyrpc.common.protocol.MessageType;
import wongs.tinyrpc.transport.serializer.Serializer;
import wongs.tinyrpc.transport.serializer.SerializerType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerializer implements Serializer {

    @Override
    public byte[] serialize(Object object) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Hessian2Output output = new Hessian2Output(bos);
            output.writeObject(object);
            output.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Hessian serialization error", e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes, MessageType messageType) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
            Hessian2Input input = new Hessian2Input(bis);
            return input.readObject();
        } catch (IOException e) {
            throw new RuntimeException("Hessian deserialization error", e);
        }
    }

    @Override
    public int getCode() {
        return SerializerType.HESSIAN_SERIALIZER.getCode();
    }

    @Override
    public SerializerType getType() {
        return SerializerType.HESSIAN_SERIALIZER;
    }
}
