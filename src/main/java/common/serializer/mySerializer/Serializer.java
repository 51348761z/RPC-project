package common.serializer.mySerializer;

public interface Serializer {
    // serialize an object to a byte array
    byte[] serialize(Object object);
    public static final int OBJECT_SERIALIZER = 0;
    public static final int JSON_SERIALIZER = 1;

    // deserialize a byte array to an object of type T
    Object deserialize(byte[] bytes, int messageType);

    // get the type of the serializer
    int getType();

    static Serializer getSerializerByCode(int code) {
        return switch (code) {
            case OBJECT_SERIALIZER -> new ObjectSerializer();
            case JSON_SERIALIZER -> new JsonSerializer();
            default -> null;
        };
    }
}
