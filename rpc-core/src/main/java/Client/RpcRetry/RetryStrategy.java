package Client.RpcRetry;

import Client.rpcClient.RpcClient;
import RpcCommon.RpcMessage.RpcRequest;
import RpcCommon.RpcMessage.RpcResponse;

public interface RetryStrategy {
    RpcResponse excute(RpcRequest request, RpcClient rpcClient);
}
