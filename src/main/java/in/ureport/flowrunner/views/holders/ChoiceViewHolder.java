package in.ureport.flowrunner.views.holders;

import android.view.View;
import android.widget.RadioButton;

import in.ureport.flowrunner.R;
import in.ureport.flowrunner.models.FlowRule;
import in.ureport.flowrunner.models.RulesetResponse;

/**
 * Created by johncordeiro on 14/10/15.
 */
public class ChoiceViewHolder extends BaseViewHolder {

    private static final String TAG = "ChoiceViewHolder";

    private RadioButton check;

    private ChoiceViewHolder.OnChoiceSelectedListener onChoiceSelectedListener;

    public ChoiceViewHolder(View itemView) {
        super(itemView);

        check = (RadioButton) itemView.findViewById(R.id.check);
        check.setOnClickListener(onCheckClickListener);
    }

    public void bindView(FlowRule rule, RulesetResponse currentResponse) {
        super.bindView(rule, currentResponse);

        check.setChecked(isCurrentRule(currentResponse));
        check.setText(rule.getCategory().get("eng"));
    }

    private View.OnClickListener onCheckClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(onChoiceSelectedListener != null) {
                onChoiceSelectedListener.onChoiceSelected(rule, rule.getTest().getBase());
            }
        }
    };

    public void setOnChoiceSelectedListener(ChoiceViewHolder.OnChoiceSelectedListener onChoiceSelectedListener) {
        this.onChoiceSelectedListener = onChoiceSelectedListener;
    }

    public interface OnChoiceSelectedListener {
        void onChoiceSelected(FlowRule rule, String response);
    }
}
