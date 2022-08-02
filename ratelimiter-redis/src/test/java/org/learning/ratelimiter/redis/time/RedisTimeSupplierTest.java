package org.learning.ratelimiter.redis.time;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RedisTimeSupplierTest {
    private static RedisClient client;
    private static StatefulRedisConnection<String, String> connect;

    @BeforeAll
    public static void beforeAll() {
        client = RedisClient.create("redis://localhost:6379");
        connect = client.connect();
    }

    @AfterAll
    @SuppressWarnings("FutureReturnValueIgnored")
    public static void afterAll() {
        client.shutdownAsync();
    }

    @Test
    public void shouldGetRedisCurrentTime() {
        Long time = new RedisTimeSupplier(connect).get();
        assertThat(time).isNotNull().isNotNegative().isNotZero();
    }

    @Test
    public void shouldGetAsyncSystemCurrentTime() throws Exception {
        Long time = new RedisTimeSupplier(connect).getAsync().toCompletableFuture().get();
        assertThat(time).isNotNull().isNotNegative().isNotZero();
    }

    @Test
    public void shouldGetReactiveSystemCurrentTime() {
        Long time = new RedisTimeSupplier(connect).getReactive().block();
        assertThat(time).isNotNull().isNotNegative().isNotZero();
    }
}
