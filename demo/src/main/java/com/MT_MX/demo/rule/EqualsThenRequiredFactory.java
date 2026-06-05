package com.MT_MX.demo.rule;

import com.MT_MX.demo.config.RuleConfig;
import org.springframework.stereotype.Component;

@Component
public class EqualsThenRequiredFactory implements ConditionalRuleFactory {

    @Override
    public String getType() {
        return "equalsThenRequired";
    }

    @Override
    public ConditionalRule create(RuleConfig config) {
        return new EqualsThenRequiredRule(
                config.ifTag,
                config.ifValue,
                config.requireTag
        );
    }
}