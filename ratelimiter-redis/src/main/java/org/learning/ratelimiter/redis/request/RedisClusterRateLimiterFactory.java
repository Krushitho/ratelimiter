package org.learning.ratelimiter.redis.request;

import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import org.learning.ratelimiter.core.request.AbstractRequestRateLimiterFactory;
import org.learning.ratelimiter.core.request.ReactiveRequestRateLimiter;
import org.learning.ratelimiter.core.request.RequestLimitRule;
import org.learning.ratelimiter.core.request.RequestRateLimiter;

import java.util.Set;

import static java.util.Objects.requireNonNull;

public class RedisClusterRateLimiterFactory extends AbstractRequestRateLimiterFactory<RedisSlidingWindowRequestRateLimiter> {

    private final RedisClusterClient client;
    private StatefulRedisClusterConnection<String, String> connection;

    public RedisClusterRateLimiterFactory(RedisClusterClient client) {
        this.client = requireNonNull(client);
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
    protected RedisSlidingWindowRequestRateLimiter create(Set<RequestLimitRule> rules) {
        getConnection().reactive();
        return new RedisSlidingWindowRequestRateLimiter(getConnection().reactive(), getConnection().reactive(), rules);
    }

    @Override
    public void close() {
        client.shutdown();
    }

    private StatefulRedisClusterConnection<String, String> getConnection() {
        // going to ignore race conditions at the cost of having multiple connections
        if (connection == null) {
            connection = client.connect();
        }
        return connection;
    }
}
