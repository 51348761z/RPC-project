package wongs.tinyrpc.fault.retry;

import lombok.extern.slf4j.Slf4j;
import wongs.tinyrpc.core.client.retry.RetryStrategy;
import wongs.tinyrpc.core.client.transport.RpcClient;
import com.github.rholder.retry.*;
import wongs.tinyrpc.common.model.RpcRequest;
import wongs.tinyrpc.common.model.RpcResponse;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GuavaRetryStrategy implements RetryStrategy {
    private RpcClient rpcClient;

    @Override
    public RpcResponse execute(RpcRequest request, RpcClient rpcClient) {
        this.rpcClient = rpcClient;
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfException()
                .retryIfResult(response -> response != null && Objects.equals(response.getCode(), 500))
                .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("{}", "RetryListener: calling retry for attempt " + attempt.getAttemptNumber());

                    }
                })
                .build();
        try {
            return retryer.call(() -> rpcClient.sendRequest(request));
        } catch (RetryException e) {
            log.info("{}", "All retry attempts failed after 3 tries");
            if (e.getLastFailedAttempt().hasException()) {
                e.getLastFailedAttempt().getExceptionCause().printStackTrace();
            }
        }
        catch (Exception e) {
            log.info("{}", "Unexpected exception during retry: " + e.getMessage());
            log.error("An error occurred", e);
        }
        return RpcResponse.fail();
    }
}
