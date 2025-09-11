package wongs.tinyrpc.transport.serializer;

import lombok.Getter;

@Getter
public enum SerializerType {
    OBJECT_SERIALIZER(0), JSON_SERIALIZER(1), HESSIAN_SERIALIZER(2), KRYO_SERIALIZER(3), PROTOSTUFF_SERIALIZER(4);

    private final int code;

    SerializerType(int code) {
        this.code = code;
    }
    public static SerializerType fromValue(int value) {
        for (SerializerType type : values()) {
            if (type.getCode() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown serializer type: " + value);
    }
}