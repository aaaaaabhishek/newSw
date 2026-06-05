package com.MT_MX.demo.rule;

import com.MT_MX.demo.config.RuleConfig;
import org.springframework.stereotype.Component;

@Component
public class OnlyOneOfRequiredFactory implements ConditionalRuleFactory{
    @Override
    public String getType() {
        return "onlyOneOf";
    }

    @Override
    public ConditionalRule create(RuleConfig config) {
        return new OnlyOneOfRule(config.tags);
    }
}
