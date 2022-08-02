package org.learning.ratelimiter.redis.time;

import io.lettuce.core.api.StatefulRedisConnection;
import org.learning.ratelimiter.core.time.TimeSupplier;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletionStage;

import static java.util.Objects.requireNonNull;

/**
 * A Redis based time supplier.
 * <p>It may be desirable to use a Redis based time supplier if the software is running on a group of servers with
 * different clocks or unreliable clocks. A disadvantage of the Redis based time supplier is that it introduces
 * an additional network round trip.
 */
public class RedisTimeSupplier implements TimeSupplier {

    private final StatefulRedisConnection<String, String> connection;

    public RedisTimeSupplier(StatefulRedisConnection<String, String> connection) {
        this.connection = requireNonNull(connection);
    }

    @Override
    public CompletionStage<Long> getAsync() {
        return connection.async().time().thenApply(strings -> Long.parseLong(strings.get(0)));
    }

    @Override
    public Mono<Long> getReactive() {
        return connection.reactive().time().next().map(Long::parseLong);
    }

    @Override
    public long get() {
        return Long.parseLong(connection.sync().time().get(0));
    }
}
