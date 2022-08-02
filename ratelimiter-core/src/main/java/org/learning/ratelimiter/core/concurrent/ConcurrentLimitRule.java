package org.learning.ratelimiter.core.concurrent;

import lombok.Getter;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

@Getter
public class ConcurrentLimitRule {
    private final int concurrentLimit;
    private final long timeoutMillis;
    private final String name;

    private ConcurrentLimitRule(int concurrentLimit, long timeoutMillis) {
        this(concurrentLimit, timeoutMillis, null);
    }

    private ConcurrentLimitRule(int concurrentLimit, long timeoutMillis, String name) {
        this.concurrentLimit = concurrentLimit;
        this.timeoutMillis = timeoutMillis;
        this.name = name;
    }

    public static ConcurrentLimitRule of(int concurrentLimit, TimeUnit timeOutUnit, long timeOut) {
        requireNonNull(timeOutUnit, "time out unit can not be null");
        return new ConcurrentLimitRule(concurrentLimit, timeOutUnit.toMillis(timeOut));
    }

    public ConcurrentLimitRule withName(String name) {
        return new ConcurrentLimitRule(this.concurrentLimit, this.timeoutMillis, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConcurrentLimitRule)) {
            return false;
        }
        ConcurrentLimitRule that = (ConcurrentLimitRule) o;
        return concurrentLimit == that.concurrentLimit &&
            timeoutMillis == that.timeoutMillis &&
            Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(concurrentLimit, timeoutMillis, name);
    }

}
