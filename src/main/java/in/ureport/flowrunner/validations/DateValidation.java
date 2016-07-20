package in.ureport.flowrunner.validations;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import in.ureport.flowrunner.managers.FlowRunnerManager;
import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRule;
import in.ureport.flowrunner.models.RulesetResponse;

/**
 * Created by johncordeiro on 15/10/15.
 */
public class DateValidation extends FlowRuleValidation {

    public static final String DEFAULT_TEST = "@date.today|time_delta:";

    @Override
    public boolean validate(FlowDefinition flowDefinition, RulesetResponse response, FlowRule rule) {
        try {
            Date date = FlowRunnerManager.getDefaultDateFormat().parse(response.getResponse());
            return date != null;
        } catch (ParseException e) {
            return false;
        }
    }

    protected Integer getTimeDeltaValue(FlowRule rule) throws NumberFormatException {
        String timeDelta = rule.getTest().getTest().values().iterator().next();
        return Integer.valueOf(timeDelta.replace(DEFAULT_TEST, "").replace("'", ""));
    }

    @NonNull
    protected Date getDeltaTime(Integer timeDelta) {
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.DAY_OF_MONTH, timeDelta);
        return calendar.getTime();
    }

}
