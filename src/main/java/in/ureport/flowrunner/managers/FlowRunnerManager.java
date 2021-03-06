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

    public static final String RULESET_TYPE_WAIT_MESSAGE = "wait_message";

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

    public static boolean isResponsableRule(FlowDefinition flowDefinition, FlowRuleset ruleSet, FlowRule rule) {
        return !FlowRunnerManager.hasRecursiveDestination(flowDefinition, ruleSet, rule)
                && FlowRunnerManager.isWaitMessageRulesetType(ruleSet) && !isNoResponseRule(rule);
    }

    private static boolean isNoResponseRule(FlowRule rule) {
        return rule.getCategory() != null && rule.getCategory().containsKey("base")
            && rule.getCategory().get("base").toLowerCase().equals("no response");
    }

    public static boolean validateResponse(FlowDefinition flowDefinition, RulesetResponse response) {
        return validateResponseForRule(flowDefinition, response, response.getRule());
    }

    public static boolean validateResponseForRule(FlowDefinition flowDefinition, RulesetResponse response, FlowRule rule) {
        TypeValidation typeValidation = TypeValidation.getTypeValidationForRule(rule);
        return ValidationFactory.getInstance(typeValidation).validate(flowDefinition, response, rule);
    }

    public static boolean isFlowActive(FlowDefinition flowDefinition) {
        return !FlowRunnerManager.isFlowCompleted(flowDefinition)
                && !FlowRunnerManager.isFlowExpired(flowDefinition);
}

    public static boolean isFlowExpired(FlowDefinition flowDefinition) {
        FlowRun flowRun = flowDefinition.getFlowRun();
        return flowRun != null && flowRun.getExitType() != null;
    }

    public static boolean isFlowCompleted(FlowDefinition flowDefinition) {
        FlowRun flowRun = flowDefinition.getFlowRun();
        return flowRun != null && flowRun.getResponded();
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

    public static boolean isWaitMessageRulesetType(FlowRuleset ruleSet) {
        return ruleSet.getRulesetType() != null && ruleSet.getRulesetType().equals(RULESET_TYPE_WAIT_MESSAGE);
    }

    public static FlowActionSet getFlowActionSetByUuid(FlowDefinition flowDefinition, String destination) {
        for (FlowActionSet actionSet : flowDefinition.getActionSets()) {
            if(destination.equals(actionSet.getUuid())) {
                return actionSet;
            }
        }
        return null;
    }
}
