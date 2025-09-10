package wongs.tinyrpc.core.client.retry;

import wongs.tinyrpc.core.client.transport.RpcClient;
import wongs.tinyrpc.common.model.RpcRequest;
import wongs.tinyrpc.common.model.RpcResponse;

public interface RetryStrategy {
    RpcResponse excute(RpcRequest request, RpcClient rpcClient);
}
