package br.com.ilhasoft.flowrunner.views.holders;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import br.com.ilhasoft.flowrunner.R;
import br.com.ilhasoft.flowrunner.managers.FlowRunnerManager;
import br.com.ilhasoft.flowrunner.models.FlowDefinition;
import br.com.ilhasoft.flowrunner.models.FlowRule;
import br.com.ilhasoft.flowrunner.models.RulesetResponse;
import br.com.ilhasoft.flowrunner.models.Type;
import br.com.ilhasoft.flowrunner.models.TypeValidation;

/**
 * Created by johncordeiro on 14/10/15.
 */
public class OpenFieldViewHolder extends BaseViewHolder {

    protected EditText openField;

    protected OnResponseChangedListener onResponseChangedListener;

    private String lastString;

    public OpenFieldViewHolder(View itemView, FlowDefinition flowDefinition) {
        super(itemView, flowDefinition);

        openField = (EditText) itemView.findViewById(R.id.openField);
        openField.addTextChangedListener(onTextChangedListener);
        openField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT && onResponseChangedListener != null) {
                    onResponseChangedListener.onResponseFinished(rule);
                    return true;
                }
                return false;
            }
        });
    }

    public void bindView(FlowRule rule, String language, RulesetResponse currentResponse) {
        super.bindView(rule, language, currentResponse);

        Type type = TypeValidation.getTypeValidationForRule(rule).getType();
        openField.setInputType(FlowRunnerManager.getInputTypeByType(type));

        if(currentResponse != null && isCurrentRule(currentResponse)) {
            openField.setText(currentResponse.getResponse());
            lastString = currentResponse.getResponse();
        } else {
            lastString = null;
            openField.setText(null);
        }
    }

    public final void setOnResponseChangedListener(OnResponseChangedListener onResponseChangedListener) {
        this.onResponseChangedListener = onResponseChangedListener;
    }

    private TextWatcher onTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable editable) {
            if(onResponseChangedListener != null && (lastString != null && lastString.length() > 0)) {
                String response = editable.length() > 0 ? editable.toString() : null;
                onResponseChangedListener.onResponseChanged(rule, response);
            }
            lastString = editable.toString();
        }
    };

    public interface OnResponseChangedListener {
        void onResponseChanged(FlowRule rule, String response);
        void onResponseFinished(FlowRule rule);
    }
}
