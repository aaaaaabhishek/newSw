package com.MT_MX.demo.rule;

import com.MT_MX.demo.config.RuleFileConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RuleLoader {

    private final RuleRegistry registry;
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public List<ConditionalRule> load(String file) {

        try (InputStream is =
                     RuleLoader.class.getClassLoader().getResourceAsStream(file)) {

            if (is == null) {
                throw new IllegalStateException("Rule file not found: " + file);
            }

            RuleFileConfig cfg = mapper.readValue(is, RuleFileConfig.class);

            return cfg.getConditionalRules()
                    .stream()
                    .map(registry::create)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Failed to load rule file: " + file, e);
        }
    }
}