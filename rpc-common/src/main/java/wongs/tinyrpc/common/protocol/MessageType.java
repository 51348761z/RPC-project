package wongs.tinyrpc.common.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageType {
    RPC_REQUEST(0), RPC_RESPONSE(1);
    private final int code;

    public static MessageType fromValue(int value) {
        for (MessageType messageType : values()) {
            if (messageType.getCode() == value) {
                return messageType;
            }
        }
        return null;
    }
}
