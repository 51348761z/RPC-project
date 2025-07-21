package common.message;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class RpcResponse implements Serializable {
    // state information
    private int code;
    private String message;
    private Class<?> dataType;
    // concrete data
    private Object data;
    // success information
    public static RpcResponse success(Object data) {
        return RpcResponse.builder().code(200).data(data).build();
    }
    // error information
    public static RpcResponse fail() {
        return RpcResponse.builder().code(500).message("Internal Server Error").build();
    }
}
