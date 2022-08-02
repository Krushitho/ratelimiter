package org.learning.ratelimiter.redis.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learning.ratelimiter.core.request.RequestLimitRule;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestLimitRuleJsonSerialiserTest {
    private final LimitRuleJsonSerialiser serialiser = new LimitRuleJsonSerialiser();

    @Test
    @DisplayName("should encode limit rule in JSON array")
    public void shouldEncode() {

        List<RequestLimitRule> rules = List.of(RequestLimitRule.of(Duration.ofSeconds(10), 10L),
            RequestLimitRule.of(Duration.ofMinutes(1), 20L));

        assertThat(serialiser.encode(rules)).isEqualTo("[[10,10,10],[60,20,60]]");
    }

    @Test
    @DisplayName("should encode limit rule with precision in JSON array")
    public void shouldEncodeWithPrecisions() {

        List<RequestLimitRule> rules = List.of(RequestLimitRule.of(Duration.ofSeconds(10), 10L).withPrecision(Duration.ofSeconds(4)),
            RequestLimitRule.of(Duration.ofMinutes(1), 20L).withPrecision(Duration.ofSeconds(8)));

        assertThat(serialiser.encode(rules)).isEqualTo("[[10,10,4],[60,20,8]]");
    }
}
