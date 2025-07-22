package common.serializer.mySerializer;

import lombok.Getter;

@Getter
public enum SerializerType {
    OBJECT_SERIALIZER(0), JSON_SERIALIZER(1);

    private final int value;

    SerializerType(int value) {
        this.value = value;
    }
    public static SerializerType fromValue(int value) {
        for (SerializerType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown serializer type: " + value);
    }
}