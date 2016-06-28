package br.com.ilhasoft.flowrunner.views.holders;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RadioButton;

import java.util.Map;

import br.com.ilhasoft.flowrunner.R;
import br.com.ilhasoft.flowrunner.models.FlowDefinition;
import br.com.ilhasoft.flowrunner.models.FlowRule;
import br.com.ilhasoft.flowrunner.models.RulesetResponse;

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

    public void bindView(FlowRule rule, String language, RulesetResponse currentResponse) {
        super.bindView(rule, language, currentResponse);

        check.setChecked(isCurrentRule(currentResponse));
        check.setText(getLanguageFromMap(rule.getCategory()));
    }

    private String getLanguageFromMap(Map<String, String> languageMap) {
        if(languageMap.containsKey(language)) {
            return languageMap.get(language);
        } else {
            return languageMap.get(flowDefinition.getBaseLanguage());
        }
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
            response = getLanguageFromMap(rule.getTest().getTest());
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
