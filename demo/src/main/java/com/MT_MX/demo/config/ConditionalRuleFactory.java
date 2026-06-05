package com.MT_MX.demo.config;
import com.MT_MX.demo.rule.*;

public class ConditionalRuleFactory {

    public static ConditionalRule create(RuleConfig cfg) {

        return switch (cfg.type) {

            case "equalsThenRequired" ->
                    new EqualsThenRequiredRule(
                            cfg.ifTag,
                            cfg.ifValue,
                            cfg.requireTag
                    );

            /// update
//            case "equalsThenRequired" ->
//                    new EqualsThenRequiredRule(cfg.ifTag, cfg.ifValue, cfg.requireTag);
//
//            case "required" ->
//                    new RequiredRule(cfg.tag);
//
//            case "pattern" ->
//                    new PatternRule(cfg.tag, cfg.pattern);
//
//            case "presentThenRequired" ->
//                    new PresentThenRequiredRule(cfg.ifTag, cfg.requireTag);
//
//            case "onlyOneOf" ->
//                    new OnlyOneOfRule(cfg.tags);
            default ->
                    throw new RuntimeException(
                            "Unknown rule type: " + cfg.type
                    );
        };
    }
}