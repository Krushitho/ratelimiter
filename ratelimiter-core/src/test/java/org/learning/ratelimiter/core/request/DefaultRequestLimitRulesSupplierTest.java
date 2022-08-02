package org.learning.ratelimiter.core.request;

import org.junit.Test;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultRequestLimitRulesSupplierTest {
    private final RequestLimitRulesSupplier<Set<RequestLimitRule>> requestLimitRulesSupplier;

    public DefaultRequestLimitRulesSupplierTest() {
        Set<RequestLimitRule> allRules = new HashSet<>();
        allRules.add(RequestLimitRule.of(Duration.ofSeconds(1), 10).withName("localhostPerSeconds")
            .matchingKeys("localhost", "127.0.0.1"));
        allRules.add(RequestLimitRule.of(Duration.ofHours(1), 2000).withName("localhostPerHours")
            .matchingKeys("localhost", "127.0.0.1"));
        allRules.add(RequestLimitRule.of(Duration.ofSeconds(1), 5).withName("perSeconds"));
        allRules.add(RequestLimitRule.of(Duration.ofHours(1), 1000).withName("perHours"));
        requestLimitRulesSupplier = new DefaultRequestLimitRulesSupplier(allRules);
    }

    @Test
    public void shouldContainsDefaultRules() {
        Set<String> ruleNames = requestLimitRulesSupplier.getRules("other")
            .stream()
            .map(RequestLimitRule::getName)
            .collect(Collectors.toSet());

        assertThat(ruleNames)
            .hasSize(2)
            .contains("perSeconds", "perHours");
    }

    @Test
    public void shouldContainLocalhostRulesForLocalhost() {
        Set<String> ruleNames = requestLimitRulesSupplier.getRules("localhost")
            .stream()
            .map(RequestLimitRule::getName)
            .collect(Collectors.toSet());

        assertThat(ruleNames)
            .hasSize(2)
            .contains("localhostPerSeconds", "localhostPerHours");
    }

    @Test
    public void shouldContainLocalhostRulesFor127_0_0_1() {
        Set<String> ruleNames = requestLimitRulesSupplier.getRules("127.0.0.1")
            .stream()
            .map(RequestLimitRule::getName)
            .collect(Collectors.toSet());

        assertThat(ruleNames)
            .hasSize(2)
            .contains("localhostPerSeconds", "localhostPerHours");
    }
}
