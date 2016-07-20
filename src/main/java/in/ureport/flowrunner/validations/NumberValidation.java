package in.ureport.flowrunner.validations;

import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRule;
import in.ureport.flowrunner.models.RulesetResponse;

/**
 * Created by johncordeiro on 15/10/15.
 */
public class NumberValidation extends FlowRuleValidation {

    @Override
    public boolean validate(FlowDefinition flowDefinition, RulesetResponse response, FlowRule rule) {
        try {
            Integer value = Integer.valueOf(response.getResponse());
            return value != null;
        } catch(NumberFormatException exception) {
            return false;
        }
    }
}
