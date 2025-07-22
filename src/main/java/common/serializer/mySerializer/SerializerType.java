package common.serializer.mySerializer;

import lombok.Getter;

@Getter
public enum SerializerType {
    OBJECT_SERIALIZER(0), JSON_SERIALIZER(1);

    private final int value;

    SerializerType(int value) {
        this.value = value;
    }
}