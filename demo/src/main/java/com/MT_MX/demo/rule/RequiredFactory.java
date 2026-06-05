package com.MT_MX.demo.rule;

import com.MT_MX.demo.config.RuleConfig;
import org.springframework.stereotype.Component;

@Component
public class RequiredFactory implements ConditionalRuleFactory{
    @Override
    public String getType() {
        return "required";
    }

    @Override
    public ConditionalRule create(RuleConfig config) {
        return new RequiredRule(config.tag);
    }
}
