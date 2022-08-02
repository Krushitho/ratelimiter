package org.learning.ratelimiter.redis.request;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.learning.ratelimiter.core.request.AbstractRequestRateLimiterFactory;
import org.learning.ratelimiter.core.request.ReactiveRequestRateLimiter;
import org.learning.ratelimiter.core.request.RequestLimitRule;
import org.learning.ratelimiter.core.request.RequestRateLimiter;

import java.io.IOException;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class RedisRateLimiterFactory extends AbstractRequestRateLimiterFactory<RedisSlidingWindowRequestRateLimiter> {
    private final RedisClient client;
    private StatefulRedisConnection<String, String> connection;

    public RedisRateLimiterFactory(RedisClient client) {
        this.client = requireNonNull(client);
    }

    @Override
    protected RedisSlidingWindowRequestRateLimiter create(Set<RequestLimitRule> rules) {
        return new RedisSlidingWindowRequestRateLimiter(getConnection().reactive(), getConnection().reactive(), rules);
    }

    @Override
    public RequestRateLimiter getInstance(Set<RequestLimitRule> rules) {
        return lookupInstance(rules);
    }

    @Override
    public ReactiveRequestRateLimiter getInstanceReactive(Set<RequestLimitRule> rules) {
        return lookupInstance(rules);
    }

    @Override
    public void close() {
        client.shutdown();
    }

    private StatefulRedisConnection<String, String> getConnection() {
        // going to ignore race conditions at the cost of having multiple connections
        if (connection == null) {
            connection = client.connect();
        }
        return connection;
    }
}
