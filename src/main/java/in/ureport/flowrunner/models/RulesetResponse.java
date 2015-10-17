package in.ureport.flowrunner.models;

/**
 * Created by johncordeiro on 14/10/15.
 */
public class RulesetResponse {

    private FlowRule rule;

    private String response;

    public RulesetResponse(FlowRule rule, String response) {
        this.rule = rule;
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public FlowRule getRule() {
        return rule;
    }

    public void setRule(FlowRule rule) {
        this.rule = rule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RulesetResponse that = (RulesetResponse) o;
        return rule.equals(that.rule);

    }

    @Override
    public int hashCode() {
        return rule.hashCode();
    }
}
