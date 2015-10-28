package in.ureport.flowrunner.views.holders;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RadioButton;

import in.ureport.flowrunner.R;
import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRule;
import in.ureport.flowrunner.models.RulesetResponse;

/**
 * Created by johncordeiro on 14/10/15.
 */
public class ChoiceViewHolder extends BaseViewHolder {

    private static final String TAG = "ChoiceViewHolder";

    private RadioButton check;

    private ChoiceViewHolder.OnChoiceSelectedListener onChoiceSelectedListener;

    public ChoiceViewHolder(View itemView, FlowDefinition flowDefinition) {
        super(itemView, flowDefinition);

        check = (RadioButton) itemView.findViewById(R.id.check);
        check.setOnClickListener(onCheckClickListener);
    }

    public void bindView(FlowRule rule, RulesetResponse currentResponse) {
        super.bindView(rule, currentResponse);

        check.setChecked(isCurrentRule(currentResponse));
        check.setText(rule.getCategory().get(flowDefinition.getBaseLanguage()));
    }

    private View.OnClickListener onCheckClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(onChoiceSelectedListener != null) {
                onChoiceSelectedListener.onChoiceSelected(rule, getResponseFromRule());
            }
        }
    };

    @Nullable
    private String getResponseFromRule() {
        String response = rule.getTest().getBase();
        if(response == null && rule.getTest().getTest() != null
        && rule.getTest().getTest().values().size() > 0) {
            response = rule.getTest().getTest().get(flowDefinition.getBaseLanguage());
        }
        return response;
    }

    public void setOnChoiceSelectedListener(ChoiceViewHolder.OnChoiceSelectedListener onChoiceSelectedListener) {
        this.onChoiceSelectedListener = onChoiceSelectedListener;
    }

    public interface OnChoiceSelectedListener {
        void onChoiceSelected(FlowRule rule, String response);
    }
}
