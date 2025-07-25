package common.serializer.mySerializer;

import lombok.Getter;

@Getter
public enum SerializerType {
    OBJECT_SERIALIZER(0), JSON_SERIALIZER(1);

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