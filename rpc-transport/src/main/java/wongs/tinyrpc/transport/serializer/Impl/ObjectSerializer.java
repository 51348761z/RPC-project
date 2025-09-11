package wongs.tinyrpc.transport.serializer.Impl;

import lombok.extern.slf4j.Slf4j;
import wongs.tinyrpc.common.protocol.MessageType;
import wongs.tinyrpc.transport.serializer.Serializer;
import wongs.tinyrpc.transport.serializer.SerializerType;

import java.io.*;

@Slf4j
public class ObjectSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException e) {
            log.error("An error occurred", e);
        }
        return bytes;
    }

    @Override
    public Object deserialize(byte[] bytes, MessageType messageType) {
        Object object = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            object = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException | ClassNotFoundException e) {
            log.error("An error occurred", e);
        }
        return object;
    }

    @Override
    public int getCode() {
        return SerializerType.OBJECT_SERIALIZER.getCode();
    }

    @Override
    public SerializerType getType() {
        return SerializerType.OBJECT_SERIALIZER;
    }
}