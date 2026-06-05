package com.MT_MX.demo.rule;

import com.MT_MX.demo.config.RuleConfig;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RuleRegistry {

    private final Map<String, ConditionalRuleFactory> registry;

    public RuleRegistry(List<ConditionalRuleFactory> factories) {

        this.registry = factories.stream()
                .collect(Collectors.toMap(
                        ConditionalRuleFactory::getType,
                        f -> f
                ));
    }

    public ConditionalRule create(RuleConfig config) {

        ConditionalRuleFactory factory = registry.get(config.type);

        if (factory == null) {
            throw new RuntimeException("Unknown rule type: " + config.type);
        }

        return factory.create(config);
    }
}