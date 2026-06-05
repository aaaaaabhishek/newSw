package com.MT_MX.demo.rule;

import com.MT_MX.demo.config.RuleConfig;
import org.springframework.stereotype.Component;

@Component
public class PresentThenRequiredFactory implements ConditionalRuleFactory{
    @Override
    public String getType() {
        return "presentThenRequired";
    }

    @Override
    public ConditionalRule create(RuleConfig config) {
        return new PresentThenRequiredRule(config.tag,config.requireTag);
    }
}
