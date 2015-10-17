package in.ureport.flowrunner.views.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import in.ureport.flowrunner.managers.FlowRunnerManager;
import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRule;
import in.ureport.flowrunner.models.FlowRuleset;
import in.ureport.flowrunner.models.RulesetResponse;

/**
 * Created by johncordeiro on 14/10/15.
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {

    protected FlowRule rule;

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public void bindView(FlowRule rule, RulesetResponse response) {
        this.rule = rule;
    }

    public boolean isCurrentRule(RulesetResponse response) {
        return response != null && response.getRule() != null
                && response.getRule().equals(rule);
    }

}
