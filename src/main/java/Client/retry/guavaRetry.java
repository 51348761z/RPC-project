package Client.retry;

import Client.rpcClient.RpcClient;
import com.github.rholder.retry.*;
import common.message.RpcRequest;
import common.message.RpcResponse;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class guavaRetry {
    private RpcClient rpcClient;

    public RpcResponse sendServiceWithRetry(RpcRequest request, RpcClient rpcClient) {
        this.rpcClient = rpcClient;
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfException()
                .retryIfResult(response -> response != null && Objects.equals(response.getCode(), 500))
                .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        System.out.println("RetryListener: calling retry for attempt " + attempt.getAttemptNumber());

                    }
                })
                .build();
        try {
            return retryer.call(() -> rpcClient.sendRequest(request));
        } catch (RetryException e) {
            System.out.println("All retry attempts failed after 3 tries");
            if (e.getLastFailedAttempt().hasException()) {
                e.getLastFailedAttempt().getExceptionCause().printStackTrace();
            }
        }
        catch (Exception e) {
            System.out.println("Unexpected exception during retry: " + e.getMessage());
            e.printStackTrace();
        }
        return RpcResponse.fail();
    }
}
