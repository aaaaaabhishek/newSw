package com.MT_MX.demo.rule;

import com.MT_MX.demo.config.RuleConfig;
import org.springframework.stereotype.Component;

@Component
public class PatternFactory implements ConditionalRuleFactory{
    @Override
    public String getType() {
        return "pattern";
    }

    @Override
    public ConditionalRule create(RuleConfig config) {
        return new PatternRule(
                config.tag, config.regex
        );
    }
}
