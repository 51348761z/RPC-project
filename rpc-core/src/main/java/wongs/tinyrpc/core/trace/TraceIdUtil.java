package wongs.tinyrpc.core.trace;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.slf4j.MDC;

public class TraceIdUtil {
    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";
    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(1, 1);

    public static String getNextId() {
        return String.valueOf(SNOWFLAKE.nextId());
    }

    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID, traceId);
    }

    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }

    public static void setSpanId(String spanId) {
        MDC.put(SPAN_ID, spanId);
    }

    public static String getSpanId() {
        return MDC.get(SPAN_ID);
    }

    public static void clear() {
        MDC.remove(TRACE_ID);
        MDC.remove(SPAN_ID);
    }
}
