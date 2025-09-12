package wongs.tinyrpc.core.client.transport;

import wongs.tinyrpc.common.model.RpcRequest;
import wongs.tinyrpc.common.model.RpcResponse;

public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);

    void close();
}
