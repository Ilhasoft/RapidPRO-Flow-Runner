package in.ureport.flowrunner.managers;

import android.text.InputType;

import java.text.DateFormat;
import java.util.Date;

import in.ureport.flowrunner.models.FlowActionSet;
import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRule;
import in.ureport.flowrunner.models.FlowRuleset;
import in.ureport.flowrunner.models.FlowRun;
import in.ureport.flowrunner.models.RulesetResponse;
import in.ureport.flowrunner.models.Type;
import in.ureport.flowrunner.models.TypeValidation;
import in.ureport.flowrunner.validations.ValidationFactory;

/**
 * Created by johncordeiro on 14/10/15.
 */
public class FlowRunnerManager {

    public static int getInputTypeByType(Type type) {
        switch (type) {
            case Date:
                return InputType.TYPE_CLASS_DATETIME;
            case Number:
                return InputType.TYPE_CLASS_NUMBER;
            case Phone:
                return InputType.TYPE_CLASS_PHONE;
            default:
            case OpenField:
                return InputType.TYPE_CLASS_TEXT;
        }
    }

    public static DateFormat getDefaultDateFormat() {
        return DateFormat.getDateInstance();
    }

    public static boolean validateResponse(FlowDefinition flowDefinition, RulesetResponse response) {
        TypeValidation typeValidation = TypeValidation.getTypeValidationForRule(response.getRule());
        return ValidationFactory.getInstance(typeValidation).validate(flowDefinition, response);
    }

    public static boolean isFlowActive(FlowRun flowRun) {
        return flowRun.getExpiredOn() != null
            || (!flowRun.getCompleted() && (flowRun.getExpiresOn() != null && flowRun.getExpiresOn().after(new Date())));
    }

    public static boolean isLastActionSet(FlowActionSet flowActionSet) {
        return flowActionSet == null || flowActionSet.getDestination() == null
                || flowActionSet.getDestination().isEmpty();
    }

    public static boolean hasRecursiveDestination(FlowDefinition flowDefinition, FlowRuleset ruleSet, FlowRule rule) {
        if(rule.getDestination() != null) {
            FlowActionSet actionSet = getFlowActionSetByUuid(flowDefinition, rule.getDestination());
            return actionSet != null && actionSet.getDestination() != null
                && actionSet.getDestination().equals(ruleSet.getUuid());
        }
        return false;
    }

    private static FlowActionSet getFlowActionSetByUuid(FlowDefinition flowDefinition, String destination) {
        for (FlowActionSet actionSet : flowDefinition.getActionSets()) {
            if(destination.equals(actionSet.getUuid())) {
                return actionSet;
            }
        }
        return null;
    }
}