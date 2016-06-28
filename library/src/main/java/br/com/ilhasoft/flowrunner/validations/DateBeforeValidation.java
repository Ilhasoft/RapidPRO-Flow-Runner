package br.com.ilhasoft.flowrunner.validations;

import java.text.ParseException;
import java.util.Date;

import br.com.ilhasoft.flowrunner.managers.FlowRunnerManager;
import br.com.ilhasoft.flowrunner.models.FlowDefinition;
import br.com.ilhasoft.flowrunner.models.RulesetResponse;

/**
 * Created by johncordeiro on 15/10/15.
 */
public class DateBeforeValidation extends DateValidation implements FlowRuleValidation {

    @Override
    public boolean validate(FlowDefinition flowDefinition, RulesetResponse response) {
        try {
            Integer timeDelta = getTimeDeltaValue(response);
            Date deltaTime = getDeltaTime(timeDelta);

            Date date = FlowRunnerManager.getDefaultDateFormat().parse(response.getResponse());
            return date.before(deltaTime);
        } catch(ParseException | NumberFormatException exception) {
            return false;
        }
    }

}
