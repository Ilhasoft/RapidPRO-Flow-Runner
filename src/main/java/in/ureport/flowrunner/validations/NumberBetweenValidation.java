package in.ureport.flowrunner.validations;

import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.RulesetResponse;

/**
 * Created by johncordeiro on 15/10/15.
 */
public class NumberBetweenValidation implements FlowRuleValidation {

    @Override
    public boolean validate(FlowDefinition flowDefinition, RulesetResponse response) {
        try {
            Integer value = Integer.valueOf(response.getResponse());
            return value >= Integer.valueOf(response.getRule().getTest().getMin())
                    && value <= Integer.valueOf(response.getRule().getTest().getMax());
        } catch(NumberFormatException exception) {
            return false;
        }
    }
}
