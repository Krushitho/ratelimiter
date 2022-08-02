package org.learning.ratelimiter.core.request;

import org.junit.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RequestLimitRuleTest {
    @Test
    public void shouldHaveDuration1Seconds() {
        RequestLimitRule requestLimitRule = RequestLimitRule.of(Duration.ofSeconds(1), 5);
        assertThat(requestLimitRule.getDurationSeconds()).isEqualTo(1);
    }


    @Test
    public void shouldHaveDuration60Seconds() {
        RequestLimitRule requestLimitRule = RequestLimitRule.of(Duration.ofMinutes(1), 5);

        assertThat(requestLimitRule.getDurationSeconds()).isEqualTo(60);
    }

    @Test
    public void shouldDefaultPrecisionToEqualDuration() {
        RequestLimitRule requestLimitRule = RequestLimitRule.of(Duration.ofMinutes(1), 5);

        assertThat(requestLimitRule.getPrecision()).isEqualTo(60);
    }

    @Test
    public void shouldHaveLimit5() {
        RequestLimitRule requestLimitRule = RequestLimitRule.of(Duration.ofMinutes(1), 5);

        assertThat(requestLimitRule.getLimit()).isEqualTo(5);
    }

    @Test
    public void shouldHavePrecisionOf10() {
        RequestLimitRule requestLimitRule = RequestLimitRule.of(Duration.ofSeconds(1), 5).withPrecision(Duration.ofSeconds(10));

        assertThat(requestLimitRule.getPrecision()).isEqualTo(10);
    }

    @Test
    public void shouldHaveNameOfBoom() {
        RequestLimitRule requestLimitRule = RequestLimitRule.of(Duration.ofSeconds(1), 5).withName("boom");

        assertThat(requestLimitRule.getName()).isEqualTo("boom");
    }

    @Test
    public void shouldHaveLimitGreaterThanZero() {
        assertThatThrownBy(() -> RequestLimitRule.of(Duration.ofSeconds(1), -1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldHaveDurationGreaterThanZero() {
        assertThatThrownBy(() -> RequestLimitRule.of(Duration.ofSeconds(0), 10)).isInstanceOf(IllegalArgumentException.class);
    }
}
