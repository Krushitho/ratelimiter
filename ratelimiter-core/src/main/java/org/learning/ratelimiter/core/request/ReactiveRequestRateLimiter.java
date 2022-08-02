package org.learning.ratelimiter.core.request;

import reactor.core.publisher.Mono;

public interface ReactiveRequestRateLimiter {
    Mono<Boolean> overLimitWhenIncrementedReactive(String key);

    Mono<Boolean> overLimitWhenIncrementedReactive(String key, int weight);

    Mono<Boolean> geLimitWhenIncrementedReactive(String key);

    Mono<Boolean> geLimitWhenIncrementedReactive(String key, int weight);

    Mono<Boolean> resetLimitReactive(String key);
}
