package org.learning.ratelimiter.redis.request;

import org.learning.ratelimiter.core.request.RequestLimitRule;
import org.learning.ratelimiter.core.request.RequestLimitRulesSupplier;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SerializedRequestLimitRulesSupplier implements RequestLimitRulesSupplier<String> {
    private final LimitRuleJsonSerialiser serialiser = new LimitRuleJsonSerialiser();
    private final Map<String,String> serializedRuleMap;
    private final String serializedDefaultRuleSet;

    SerializedRequestLimitRulesSupplier(Set<RequestLimitRule> rules) {
        this.serializedRuleMap = RequestLimitRulesSupplier.buildRuleMap(rules)
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, kv -> serialiser.encode(kv.getValue()) ));
        this.serializedDefaultRuleSet = serialiser.encode(RequestLimitRulesSupplier.buildDefaultRuleSet(rules));
    }

    @Override
    public String getRules(String key) {
        return serializedRuleMap.getOrDefault(key, serializedDefaultRuleSet);
    }
}
