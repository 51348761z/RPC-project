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
}
