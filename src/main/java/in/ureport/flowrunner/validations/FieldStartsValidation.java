package in.ureport.flowrunner.validations;

import java.util.Map;

import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRule;
import in.ureport.flowrunner.models.FlowRuleTest;
import in.ureport.flowrunner.models.RulesetResponse;

/**
 * Created by johncordeiro on 15/10/15.
 */
public class FieldStartsValidation extends FlowRuleValidation {

    @Override
    public boolean validate(FlowDefinition flowDefinition, RulesetResponse response, FlowRule rule) {
        FlowRuleTest flowRuleTest = rule.getTest();
        Map<String, String> object = flowRuleTest.getTest();
        return response.getResponse().startsWith(object.get(flowDefinition.getBaseLanguage()));
    }

}
