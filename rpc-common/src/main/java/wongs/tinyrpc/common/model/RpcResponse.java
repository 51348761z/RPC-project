package wongs.tinyrpc.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements Serializable {
    // state information
    private int code;
    private String message;
    private Class<?> dataType;
    // concrete data
    private Object data;
    // success information
    public static RpcResponse success(Object data) {
        return RpcResponse.builder().code(200).data(data).dataType(data.getClass()).build();
    }
    // error information
    public static RpcResponse fail() {
        return RpcResponse.builder().code(500).message("Internal Server Error").build();
    }
}
