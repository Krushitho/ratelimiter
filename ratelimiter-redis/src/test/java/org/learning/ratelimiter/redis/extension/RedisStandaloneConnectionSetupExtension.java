package org.learning.ratelimiter.redis.extension;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisKeyReactiveCommands;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.api.reactive.RedisScriptingReactiveCommands;
import lombok.Getter;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class RedisStandaloneConnectionSetupExtension implements BeforeAllCallback, AfterAllCallback, AfterEachCallback {
    private static RedisClient client;
    private static StatefulRedisConnection<String, String> connect;
    private static RedisReactiveCommands<String, String> reactiveCommands;

    @Override
    public void afterAll(ExtensionContext context) {
        client.shutdownAsync();
    }

    @Override
    public void afterEach(ExtensionContext context)  {
        connect.sync().flushdb();
    }

    @Override
    public void beforeAll(ExtensionContext context)  {
        client = RedisClient.create("redis://localhost:6379");
        connect = client.connect();
        reactiveCommands = connect.reactive();
    }

    public RedisClient getClient() {
        return client;
    }

    public RedisScriptingReactiveCommands<String, String> getScriptingReactiveCommands() {
        return reactiveCommands;
    }

    public RedisKeyReactiveCommands<String, String> getKeyReactiveCommands() {
        return reactiveCommands;
    }

}
