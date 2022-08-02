package org.learning.ratelimiter.core.request;

import java.io.Closeable;
import java.util.Set;

public interface RequestRateLimiterFactory extends Closeable {
    RequestRateLimiter getInstance(Set<RequestLimitRule> rules);

    ReactiveRequestRateLimiter getInstanceReactive(Set<RequestLimitRule> rules);
}
