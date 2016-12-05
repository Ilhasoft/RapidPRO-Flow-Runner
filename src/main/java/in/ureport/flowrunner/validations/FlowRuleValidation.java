package in.ureport.flowrunner.validations;

import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRule;
import in.ureport.flowrunner.models.RulesetResponse;

/**
 * Created by johncordeiro on 15/10/15.
 */
public abstract class FlowRuleValidation {

    public abstract boolean validate(FlowDefinition flowDefinition, RulesetResponse response, FlowRule rule);

    public boolean validate(FlowDefinition flowDefinition, RulesetResponse response) {
        return validate(flowDefinition, response, response.getRule());
    }

}
