package br.com.ilhasoft.flowrunner.validations;

import br.com.ilhasoft.flowrunner.models.FlowDefinition;
import br.com.ilhasoft.flowrunner.models.RulesetResponse;

/**
 * Created by johncordeiro on 15/10/15.
 */
public class FieldExistsValidation implements FlowRuleValidation {

    @Override
    public boolean validate(FlowDefinition flowDefinition, RulesetResponse response) throws NullPointerException {
        return !response.getResponse().isEmpty();
    }
}
