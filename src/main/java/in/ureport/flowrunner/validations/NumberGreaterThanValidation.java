package in.ureport.flowrunner.validations;

import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRule;
import in.ureport.flowrunner.models.RulesetResponse;

/**
 * Created by johncordeiro on 15/10/15.
 */
public class NumberGreaterThanValidation extends FlowRuleValidation {

    @Override
    public boolean validate(FlowDefinition flowDefinition, RulesetResponse response, FlowRule rule) {
        try {
            String testValue = rule.getTest().getTest().values().iterator().next();

            Integer value = Integer.valueOf(response.getResponse());
            return value > Integer.valueOf(testValue);
        } catch(Exception exception) {
            return false;
        }
    }
}
