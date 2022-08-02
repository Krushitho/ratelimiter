package org.learning.ratelimiter.core.time;

import reactor.core.publisher.Mono;

import java.util.concurrent.CompletionStage;

public interface TimeSupplier {
    CompletionStage<Long> getAsync();
    Mono<Long> getReactive();
    long get();
}
