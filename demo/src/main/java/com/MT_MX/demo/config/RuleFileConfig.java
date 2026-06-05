package com.MT_MX.demo.config;
import java.util.List;

public class RuleFileConfig {

    public List<RuleConfig> getConditionalRules() {
        return conditionalRules;
    }

    public void setConditionalRules(List<RuleConfig> conditionalRules) {
        this.conditionalRules = conditionalRules;
    }

    public List<RuleConfig> conditionalRules;

}