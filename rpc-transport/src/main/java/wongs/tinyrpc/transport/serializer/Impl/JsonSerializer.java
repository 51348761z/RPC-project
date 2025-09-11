package wongs.tinyrpc.transport.serializer.Impl;

import wongs.tinyrpc.transport.serializer.Serializer;
import wongs.tinyrpc.transport.serializer.SerializerType;
import com.alibaba.fastjson.JSONObject;
import wongs.tinyrpc.common.protocol.MessageType;
import wongs.tinyrpc.common.model.RpcResponse;
import wongs.tinyrpc.common.model.RpcRequest;

public class JsonSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {
        byte[] bytes = JSONObject.toJSONBytes(object);
        return bytes;
    }

    @Override
    public Object deserialize(byte[] bytes, MessageType messageType) {
        Object object = null;
        switch (messageType) {
            case RPC_REQUEST:
                RpcRequest request = JSONObject.parseObject(bytes, RpcRequest.class);
                Object[] objects = new Object[request.getParameterTypes().length];
                for (int i = 0; i < objects.length; i++) {
                    Class<?> paramsType = request.getParameterTypes()[i];
                    if (!paramsType.isAssignableFrom(request.getParameters()[i].getClass())) {
                        objects[i] = JSONObject.toJavaObject((JSONObject) request.getParameters()[i], request.getParameterTypes()[i]);
                    } else {
                        objects[i] = request.getParameters()[i];
                    }
                }
                request.setParameters(objects);
                object = request;
                break;
            case RPC_RESPONSE:
                RpcResponse response = JSONObject.parseObject(bytes, RpcResponse.class);
                Class<?> dataType = response.getDataType();
                if (dataType == null) {
                    System.out.println("Data type is null, defaulting to Object");
                    dataType = Object.class; // Default to Object if no data type is specified
                }
                if (response.getData() != null && !dataType.isAssignableFrom(response.getData().getClass())) {
                    response.setData(JSONObject.toJavaObject((JSONObject) response.getData(), dataType));
                }
                object = response;
                break;
            default:
                System.out.println("Unknown message type: " + messageType);
                throw new RuntimeException();
        }
        return object;
    }

    @Override
    public int getCode() {
        return SerializerType.JSON_SERIALIZER.getCode();
    }

    @Override
    public  SerializerType getType() {
        return SerializerType.JSON_SERIALIZER;
    }
}
