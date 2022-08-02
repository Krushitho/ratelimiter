package org.learning.ratelimiter.core.request;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface RequestLimitRulesSupplier<T> {
    static Set<RequestLimitRule> buildDefaultRuleSet(Set<RequestLimitRule> rules) {
        return rules.stream()
            .filter(rule -> rule.getKeys() == null)
            .collect(Collectors.toSet());
    }

    static Map<String,Set<RequestLimitRule>> buildRuleMap(Set<RequestLimitRule> rules) {
        Map<String, Set<RequestLimitRule>> ruleMap = new HashMap<>();

        for (RequestLimitRule rule : rules) {
            if (rule.getKeys() == null) {
                continue;
            }
            for (String key : rule.getKeys()) {
                ruleMap.computeIfAbsent(key, k -> new HashSet<>()).add(rule);
            }
        }

        return ruleMap;
    }

    T getRules(String key);
}
