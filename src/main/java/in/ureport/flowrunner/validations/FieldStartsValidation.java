package in.ureport.flowrunner.validations;

import java.util.Map;

import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRuleTest;
import in.ureport.flowrunner.models.RulesetResponse;

/**
 * Created by johncordeiro on 15/10/15.
 */
public class FieldStartsValidation implements FlowRuleValidation {

    @Override
    public boolean validate(FlowDefinition flowDefinition, RulesetResponse response) {
        FlowRuleTest flowRuleTest = response.getRule().getTest();
        Map<String, String> object = (Map<String, String>) flowRuleTest.getTest();
        return response.getResponse().startsWith(object.get(flowDefinition.getBaseLanguage()));
    }

}
