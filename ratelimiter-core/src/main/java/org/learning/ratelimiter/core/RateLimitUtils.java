package org.learning.ratelimiter.core;

public class RateLimitUtils {
    public static <T> T coalesec(T first, T second) {
        return first == null ? second : first;
    }
}
