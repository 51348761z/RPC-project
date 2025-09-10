package Client.rpcClient;

import RpcCommon.RpcMessage.RpcRequest;
import RpcCommon.RpcMessage.RpcResponse;

public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
}
