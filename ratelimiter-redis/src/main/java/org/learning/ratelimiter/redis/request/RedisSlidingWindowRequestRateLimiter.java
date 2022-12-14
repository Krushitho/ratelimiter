package org.learning.ratelimiter.redis.request;

import io.lettuce.core.RedisNoScriptException;
import io.lettuce.core.api.reactive.RedisKeyReactiveCommands;
import io.lettuce.core.api.reactive.RedisScriptingReactiveCommands;
import lombok.extern.java.Log;
import org.learning.ratelimiter.core.request.ReactiveRequestRateLimiter;
import org.learning.ratelimiter.core.request.RequestLimitRule;
import org.learning.ratelimiter.core.request.RequestLimitRulesSupplier;
import org.learning.ratelimiter.core.request.RequestRateLimiter;
import org.learning.ratelimiter.core.time.SystemTimeSupplier;
import org.learning.ratelimiter.core.time.TimeSupplier;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Set;

import static io.lettuce.core.ScriptOutputType.VALUE;
import static java.util.Objects.requireNonNull;

@Log
public class RedisSlidingWindowRequestRateLimiter implements RequestRateLimiter, ReactiveRequestRateLimiter {
    private static final Duration BLOCK_TIMEOUT = Duration.of(5, ChronoUnit.SECONDS);

    private final RedisScriptingReactiveCommands<String, String> redisScriptingReactiveCommands;
    private final RedisKeyReactiveCommands<String, String> redisKeyCommands;
    private final RedisScriptLoader scriptLoader;
    private final RequestLimitRulesSupplier<String> requestLimitRulesSupplier;
    private final TimeSupplier timeSupplier;

    public RedisSlidingWindowRequestRateLimiter(RedisScriptingReactiveCommands<String, String> redisScriptingReactiveCommands, RedisKeyReactiveCommands<String, String> redisKeyCommands, RequestLimitRule rule) {
        this(redisScriptingReactiveCommands, redisKeyCommands, Collections.singleton(rule));
    }

    public RedisSlidingWindowRequestRateLimiter(RedisScriptingReactiveCommands<String, String> redisScriptingReactiveCommands, RedisKeyReactiveCommands<String, String> redisKeyCommands, Set<RequestLimitRule> rules) {
        this(redisScriptingReactiveCommands, redisKeyCommands, rules, new SystemTimeSupplier());
    }

    public RedisSlidingWindowRequestRateLimiter(RedisScriptingReactiveCommands<String, String> redisScriptingReactiveCommands, RedisKeyReactiveCommands<String, String> redisKeyCommands, Set<RequestLimitRule> rules, TimeSupplier timeSupplier) {
        requireNonNull(rules, "rules can not be null");
        requireNonNull(timeSupplier, "time supplier can not be null");
        requireNonNull(redisScriptingReactiveCommands, "redisScriptingReactiveCommands can not be null");
        requireNonNull(redisKeyCommands, "redisKeyCommands can not be null");
        this.redisScriptingReactiveCommands = redisScriptingReactiveCommands;
        this.redisKeyCommands = redisKeyCommands;
        scriptLoader = new RedisScriptLoader(redisScriptingReactiveCommands, "sliding-window-ratelimit.lua");
        requestLimitRulesSupplier = new SerializedRequestLimitRulesSupplier(rules);
        this.timeSupplier = timeSupplier;
    }

    private static boolean startWithNoScriptError(Throwable throwable) {
        return throwable instanceof RedisNoScriptException;
    }



    @Override
    public Mono<Boolean> overLimitWhenIncrementedReactive(String key) {
        return overLimitWhenIncrementedReactive(key, 1);
    }

    @Override
    public Mono<Boolean> overLimitWhenIncrementedReactive(String key, int weight) {
        return overLimitWhenIncrementedReactive(key, 1);
    }

    @Override
    public Mono<Boolean> geLimitWhenIncrementedReactive(String key) {
        return geLimitWhenIncrementedReactive(key, 1);
    }

    @Override
    public Mono<Boolean> geLimitWhenIncrementedReactive(String key, int weight) {
        return eqOrGeLimitReactive(key, weight, false);
    }

    @Override
    public Mono<Boolean> resetLimitReactive(String key) {
        return redisKeyCommands.del(key).map(count -> count > 0);
    }

    @Override
    public boolean overLimitWhenIncremented(String key) {
        return overLimitWhenIncremented(key, 1);
    }

    @Override
    public boolean overLimitWhenIncremented(String key, int weight) {
        return throwOnTimeout(eqOrGeLimitReactive(key, weight, true));
    }

    private Mono<Boolean> eqOrGeLimitReactive(String key, int weight, boolean strictlyGreater) {
        requireNonNull(key);
        String rulesJson = requestLimitRulesSupplier.getRules(key);

        return Mono.zip(timeSupplier.getReactive(), scriptLoader.storedScript())
            .flatMapMany(tuple -> {
                Long time = tuple.getT1();
                RedisScriptLoader.StoredScript script = tuple.getT2();
                return redisScriptingReactiveCommands
                    .evalsha(script.getSha(), VALUE, new String[]{key}, rulesJson, time.toString(), Integer.toString(weight), toStringOneZero(strictlyGreater))
                    .doOnError(RedisSlidingWindowRequestRateLimiter::startWithNoScriptError, e -> script.dispose());
            })
            .retryWhen(Retry.max(1).filter(RedisSlidingWindowRequestRateLimiter::startWithNoScriptError))
            .single()
            .map("1"::equals)
            .doOnSuccess(over -> {
                if (over) {
                    log.info(String.format("Requests matched by key '{}' incremented by weight {} are greater than {} the limit", key, weight, strictlyGreater ? "" : "or equal to "));
                }
            });
    }

    private String toStringOneZero(boolean strictlyGreater) {
        return strictlyGreater ? "1" : "0";
    }

    @Override
    public boolean geLimitWhenIncremented(String key) {
        return geLimitWhenIncremented(key, 1);
    }

    @Override
    public boolean geLimitWhenIncremented(String key, int weight) {
        return throwOnTimeout(eqOrGeLimitReactive(key, weight, false));
    }

    @Override
    public boolean resetLimit(String key) {
        return throwOnTimeout(resetLimitReactive(key));
    }

    private boolean throwOnTimeout(Mono<Boolean> mono) {
        Boolean result = mono.block(BLOCK_TIMEOUT);
        if (result == null) {
            throw new RuntimeException("waited " + BLOCK_TIMEOUT + "before timing out");
        }
        return result;
    }
}
