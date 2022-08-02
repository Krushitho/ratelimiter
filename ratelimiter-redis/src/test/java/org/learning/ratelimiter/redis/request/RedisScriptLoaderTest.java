package org.learning.ratelimiter.redis.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.learning.ratelimiter.redis.extension.RedisStandaloneConnectionSetupExtension;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static io.lettuce.core.ScriptOutputType.VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RedisScriptLoaderTest {
    @RegisterExtension
    static RedisStandaloneConnectionSetupExtension extension = new RedisStandaloneConnectionSetupExtension();

    private void scriptFlush() {
        extension.getScriptingReactiveCommands().scriptFlush().block();
    }

    @Test
    @DisplayName("should load rate limit lua script into Redis")
    public void shouldLoadScript() {
        RedisScriptLoader scriptLoader = new RedisScriptLoader(extension.getScriptingReactiveCommands(), "hello-world.lua");
        scriptFlush();

        String sha = scriptLoader.storedScript().block(Duration.ofSeconds(5)).getSha();
        assertThat(sha).isNotEmpty();
        assertThat(extension.getScriptingReactiveCommands().scriptExists(sha).blockFirst()).isTrue();
    }

    @Test
    @DisplayName("should cache loaded sha")
    public void shouldCache() {
        RedisScriptLoader scriptLoader = new RedisScriptLoader(extension.getScriptingReactiveCommands(), "hello-world.lua");

        assertThat(scriptLoader.storedScript().block(Duration.ofSeconds(5)).getSha()).isNotEmpty();

        scriptFlush();

        assertThat(scriptLoader.storedScript().block(Duration.ofSeconds(5)).getSha()).isNotEmpty();
    }

    @Test
    @DisplayName("should fail if script not found")
    void shouldFailedIfScriptNotFound() {

        Throwable exception = assertThrows(IllegalArgumentException.class,
            () -> new RedisScriptLoader(extension.getScriptingReactiveCommands(), "not-found-script.lua", true));
        assertThat(exception.getMessage()).contains("not found");
    }

    @Test
    @DisplayName("should fail if script not found")
    public void shouldExecuteScript() {

        RedisScriptLoader scriptLoader = new RedisScriptLoader(extension.getScriptingReactiveCommands(), "hello-world.lua", true);
        String sha = scriptLoader.storedScript().block(Duration.ofSeconds(5)).getSha();

        Object result = extension.getScriptingReactiveCommands().evalsha(sha, VALUE).blockFirst();
        assertThat((String) result).isEqualTo("hello world!");
    }

    @Test
    @DisplayName("should dispose stored script if scripted flushed from redis")
    public void shouldReloadScriptIfFlushed() {

        RedisScriptLoader scriptLoader = new RedisScriptLoader(extension.getScriptingReactiveCommands(), "hello-world.lua", true);
        RedisScriptLoader.StoredScript storedScript = scriptLoader.storedScript().block(Duration.of(2, ChronoUnit.SECONDS));
        assertThat((String) extension.getScriptingReactiveCommands().evalsha(storedScript.getSha(), VALUE).blockFirst()).isEqualTo("hello world!");

        scriptFlush();
        storedScript.dispose();

        storedScript = scriptLoader.storedScript().block(Duration.of(2, ChronoUnit.SECONDS));
        assertThat((String) extension.getScriptingReactiveCommands().evalsha(storedScript.getSha(), VALUE).blockFirst()).isEqualTo("hello world!");
    }
}
