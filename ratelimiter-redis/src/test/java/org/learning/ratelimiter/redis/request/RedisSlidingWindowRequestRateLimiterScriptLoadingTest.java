package org.learning.ratelimiter.redis.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.learning.ratelimiter.core.request.RequestLimitRule;
import org.learning.ratelimiter.redis.extension.RedisStandaloneConnectionSetupExtension;

import java.time.Duration;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class RedisSlidingWindowRequestRateLimiterScriptLoadingTest {
    @RegisterExtension
    static RedisStandaloneConnectionSetupExtension extension = new RedisStandaloneConnectionSetupExtension();

    @Test
    public void shouldRetryWhenScriptIfFlushed() {
        Set<RequestLimitRule> rules = Set.of(RequestLimitRule.of(Duration.ofSeconds(10), 5));
        RedisSlidingWindowRequestRateLimiter requestRateLimiter = new RedisSlidingWindowRequestRateLimiter(extension.getScriptingReactiveCommands(), extension.getKeyReactiveCommands(), rules);

        assertThat(requestRateLimiter.overLimitWhenIncremented("ip:127.0.1.1")).isFalse();

        extension.getScriptingReactiveCommands().scriptFlush().block();

        requestRateLimiter.overLimitWhenIncremented("ip:127.0.1.1");
    }
}
