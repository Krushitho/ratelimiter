package org.learning.ratelimiter.core.concurrent;

public interface ConcurrentRequestLimiter {
    Baton acquire(String key);

    Baton acquire(String key, int weight);
}
