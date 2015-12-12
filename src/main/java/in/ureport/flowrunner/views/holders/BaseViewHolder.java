package in.ureport.flowrunner.views.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRule;
import in.ureport.flowrunner.models.RulesetResponse;

/**
 * Created by johncordeiro on 14/10/15.
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {

    protected FlowRule rule;
    protected String language;
    protected FlowDefinition flowDefinition;

    public BaseViewHolder(View itemView, FlowDefinition flowDefinition) {
        super(itemView);
        this.flowDefinition = flowDefinition;
    }

    public void bindView(FlowRule rule, String language, RulesetResponse response) {
        this.rule = rule;
        this.language = language;
    }

    public boolean isCurrentRule(RulesetResponse response) {
        return response != null && response.getRule() != null
                && response.getRule().equals(rule);
    }

}
