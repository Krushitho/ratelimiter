package org.learning.ratelimiter.core.request;

import lombok.Getter;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Defines a limit rule that can support regular and token bucket rate limits.
 */
@Getter
public class RequestLimitRule {
    private final String name;
    private final int durationSeconds;
    private final long limit;
    private final int precision;
    private final Set<String> keys;

    private RequestLimitRule(int durationSeconds, long limit, int precision) {
        this(durationSeconds, limit, precision, null);
    }

    private RequestLimitRule(int durationSeconds, long limit, int precision, String name) {
        this(durationSeconds, limit, precision, name, null);
    }

    private RequestLimitRule(int durationSeconds, long limit, int precision, String name, Set<String> keys) {
        this.durationSeconds = durationSeconds;
        this.limit = limit;
        this.precision = precision;
        this.name = name;
        this.keys = keys;
    }

    private static void checkDuration(Duration duration) {
        requireNonNull(duration, "duration can not be null");
        if (Duration.ofSeconds(1).compareTo(duration) > 0) {
            throw new IllegalArgumentException("duration must be great than 1 second");
        }
    }

    public static RequestLimitRule of(Duration duration, long limit) {
        checkDuration(duration);
        if (limit < 0) {
            throw new IllegalArgumentException("limit must be greater than zero.");
        }
        int durationSeconds = (int) duration.getSeconds();
        return new RequestLimitRule(durationSeconds, limit, durationSeconds);
    }

    public RequestLimitRule withPrecision(Duration precision) {
        checkDuration(precision);
        return new RequestLimitRule(this.durationSeconds, this.limit, (int) precision.getSeconds(), this.name, this.keys);
    }

    public RequestLimitRule withName(String name) {
        return new RequestLimitRule(this.durationSeconds, this.limit, this.precision, name, this.keys);
    }

    public RequestLimitRule matchingKeys(String... keys) {
        Set<String> keySet = keys.length > 0 ? new HashSet<>(Arrays.asList(keys)) : null;
        return matchingKeys(keySet);
    }

    public RequestLimitRule matchingKeys(Set<String> keys) {
        return new RequestLimitRule(this.durationSeconds, this.limit, this.precision, this.name, keys);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RequestLimitRule)) {
            return false;
        }
        RequestLimitRule that = (RequestLimitRule) o;
        return durationSeconds == that.durationSeconds
            && limit == that.limit
            && precision == that.precision
            && Objects.equals(name, that.name)
            && Objects.equals(keys, that.keys);
    }

    @Override
    public int hashCode() {
        return Objects.hash(durationSeconds, limit, precision, name, keys);
    }
}
