package in.ureport.flowrunner.validations;

import java.text.ParseException;
import java.util.Date;

import in.ureport.flowrunner.managers.FlowRunnerManager;
import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRule;
import in.ureport.flowrunner.models.RulesetResponse;

/**
 * Created by johncordeiro on 15/10/15.
 */
public class DateBeforeValidation extends DateValidation {

    @Override
    public boolean validate(FlowDefinition flowDefinition, RulesetResponse response, FlowRule rule) {
        try {
            Integer timeDelta = getTimeDeltaValue(rule);
            Date deltaTime = getDeltaTime(timeDelta);

            Date date = FlowRunnerManager.getDefaultDateFormat().parse(response.getResponse());
            return date.before(deltaTime);
        } catch(ParseException | NumberFormatException exception) {
            return false;
        }
    }

}
