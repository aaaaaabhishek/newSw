package com.MT_MX.demo.rule;

import com.MT_MX.demo.validator.ConditionalValidator;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public final class MT103ConditionalRules {

    private MT103ConditionalRules() {}

    public static ConditionalValidator validator() {

        return new ConditionalValidator(List.of(

                // IF 71A = OUR → 71F required
                new EqualsThenRequiredRule("71A","OUR","71F")

        ));
    }
}