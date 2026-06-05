package com.MT_MX.demo.config;
import com.MT_MX.demo.rule.ConditionalRule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class RuleLoader {

    public static List<ConditionalRule> load(String file) {

        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

            InputStream is =
                    RuleLoader.class
                            .getClassLoader()
                            .getResourceAsStream(file);

                RuleFileConfig cfg =
                    mapper.readValue(is, RuleFileConfig.class);

            return cfg.conditionalRules
                    .stream()
                    .map(ConditionalRuleFactory::create)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Rule load failed", e);
        }
    }
}