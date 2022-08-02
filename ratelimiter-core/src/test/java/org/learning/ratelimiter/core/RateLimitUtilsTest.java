package org.learning.ratelimiter.core;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RateLimitUtilsTest {
    @Test
    public void shouldReturnFirst() {
        Object first = new Object();
        Object second = new Object();

       assertThat(RateLimitUtils.coalesec(first, second)).isEqualTo(first);
    }

    @Test
    public void shouldReturnSecond() {
        Object first = null;
        Object second = new Object();

        assertThat(RateLimitUtils.coalesec(first, second)).isEqualTo(second);
    }
}
