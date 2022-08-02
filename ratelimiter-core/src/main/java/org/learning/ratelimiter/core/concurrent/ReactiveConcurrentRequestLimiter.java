package org.learning.ratelimiter.core.concurrent;

import reactor.core.publisher.Mono;

public interface ReactiveConcurrentRequestLimiter {
    Mono<Boolean> checkoutBaton(String key);

    Mono<Boolean> checkoutBaton(String key, int weight);
}
