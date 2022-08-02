package org.learning.ratelimiter.core.request;

/**
 * A synchronous request rate limiter interface.
 */
public interface RequestRateLimiter {
    /**
     * Determine if the given key, after incrementing by one, has exceeded the configured rate limit.
     * @param key key.
     * @return {@code true} if the key is over the limit, otherwise {@code false}
     */
    boolean overLimitWhenIncremented(String key);
    /**
     * Determine if the given key, after incrementing by the given weight, has exceeded the configured rate limit.
     * @param key key.
     * @param weight A variable weight.
     * @return {@code true} if the key has exceeded the limit, otherwise {@code false} .
     */
    boolean overLimitWhenIncremented(String key, int weight);

    boolean geLimitWhenIncremented(String key);
    boolean geLimitWhenIncremented(String key, int weight);
    boolean resetLimit(String key);
}
