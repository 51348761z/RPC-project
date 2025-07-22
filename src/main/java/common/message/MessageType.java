package common.message;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public enum MessageType {
    RPC_REQUEST(0), RPC_RESPONSE(1);
    private final int type;

    MessageType(int type) {
        this.type = type;
    }

    public static MessageType fromValue(int value) {
        for (MessageType messageType : values()) {
            if (messageType.getType() == value) {
                return messageType;
            }
        }
        return null;
    }
}
