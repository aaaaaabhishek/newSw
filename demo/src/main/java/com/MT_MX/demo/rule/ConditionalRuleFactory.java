package com.MT_MX.demo.rule;

import com.MT_MX.demo.config.RuleConfig;

public interface ConditionalRuleFactory {

    String getType();

    ConditionalRule create(RuleConfig config);
}
