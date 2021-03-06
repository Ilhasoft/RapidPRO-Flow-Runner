package in.ureport.flowrunner.views.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import in.ureport.flowrunner.R;
import in.ureport.flowrunner.managers.FlowRunnerManager;
import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRule;
import in.ureport.flowrunner.models.FlowRuleset;
import in.ureport.flowrunner.models.RulesetResponse;
import in.ureport.flowrunner.models.Type;
import in.ureport.flowrunner.models.TypeValidation;
import in.ureport.flowrunner.views.holders.BaseViewHolder;
import in.ureport.flowrunner.views.holders.ChoiceViewHolder;
import in.ureport.flowrunner.views.holders.DateViewHolder;
import in.ureport.flowrunner.views.holders.OpenFieldViewHolder;

/**
 * Created by johncordeiro on 7/17/15.
 */
public class QuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ChoiceViewHolder.OnChoiceSelectedListener {

    private static final int TYPE_RESPOND = -1;
    private static final int TYPE_HIDE = -2;
    private static final int TYPE_QUESTION = -3;

    private RulesetResponse response;

    private Context context;

    @LayoutRes
    private int responseButtonRes;
    private String preferredLanguage;
    private final FlowRuleset ruleSet;
    private final boolean haveNextStep;
    private Spanned questionText;
    private final FlowDefinition flowDefinition;

    private OnQuestionAnsweredListener onQuestionAnsweredListener;
    private RulesetResponse rulesetResponse;

    public QuestionAdapter(FlowDefinition flowDefinition, FlowRuleset ruleSet, boolean haveNextStep,
                           Spanned questionText, String preferredLanguage, int responseButtonRes) {
        this.ruleSet = ruleSet;
        this.haveNextStep = haveNextStep;
        this.flowDefinition = flowDefinition;
        this.questionText = questionText;
        this.preferredLanguage = preferredLanguage;
        this.responseButtonRes = responseButtonRes;
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        initContext(parent);
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_QUESTION) {
            AppCompatTextView textView = new AppCompatTextView(parent.getContext());
            textView.setAutoLinkMask(Linkify.WEB_URLS);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            return new QuestionViewHolder(textView);
        } else if (viewType == TYPE_RESPOND) {
            return new RespondViewHolder(inflater.inflate(responseButtonRes, parent, false));
        } else if(viewType == TYPE_HIDE) {
            return new BaseViewHolder(inflater.inflate(R.layout.item_hide, parent, false), flowDefinition);
        } else {
            switch(Type.values()[viewType]) {
                case Choice:
                    ChoiceViewHolder choiceViewHolder = new ChoiceViewHolder(inflater.inflate(R.layout.item_choice, parent, false), flowDefinition);
                    choiceViewHolder.setOnChoiceSelectedListener(this);
                    return choiceViewHolder;
                case Date:
                    DateViewHolder dateViewHolder = new DateViewHolder(inflater.inflate(R.layout.item_open, parent, false), flowDefinition);
                    dateViewHolder.setOnResponseChangedListener(onResponseChangedListener);
                    return dateViewHolder;
                default:
                case OpenField:
                    OpenFieldViewHolder openFieldViewHolder = new OpenFieldViewHolder(inflater.inflate(R.layout.item_open, parent, false), flowDefinition);
                    openFieldViewHolder.setOnResponseChangedListener(onResponseChangedListener);
                    return openFieldViewHolder;
            }
        }
    }

    private void initContext(ViewGroup parent) {
        if (context == null)
            context = parent.getContext();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (this.getItemViewType(position)) {
            case TYPE_QUESTION:
                ((QuestionViewHolder) holder).bind(questionText);
                break;
            case TYPE_RESPOND:
                ((RespondViewHolder) holder).bindView();
                break;
            default:
                FlowRule rule = ruleSet.getRules().get(position - 1);
                ((BaseViewHolder) holder).bindView(rule, preferredLanguage, response);
        }
    }

    @Override
    public long getItemId(int position) {
        if (isItemType(position))
            return ruleSet.getRules().get(position - 1).getUuid().hashCode();
        else if (position == 0)
            return TYPE_QUESTION;
        else
            return TYPE_RESPOND;
    }

    @Override
    public int getItemViewType(int position) {
        if (isItemType(position)) {
            FlowRule rule = ruleSet.getRules().get(position - 1);
            if (!FlowRunnerManager.isResponsableRule(flowDefinition, ruleSet, rule)) {
                return TYPE_HIDE;
            } else if (isNumericMultipleOptions()) {
                return getViewTypeForMultipleOpenFields(position);
            } else {
                return getTypeValidationByRule(rule).getType().ordinal();
            }
        } else if (position == 0)
            return TYPE_QUESTION;
        else
            return TYPE_RESPOND;
    }



    private int getViewTypeForMultipleOpenFields(int position) {
        if (isFirstPosition(position)) {
            return Type.OpenField.ordinal();
        } else {
            return TYPE_HIDE;
        }
    }

    private boolean isFirstPosition(int position) {
        return position == 1;
    }

    private boolean isNumericMultipleOptions() {
        int openFields = 0;
        for (FlowRule flowRule : ruleSet.getRules()) {
            if (getTypeValidationByRule(flowRule).getType() == Type.Number) {
                openFields = openFields + 1;
            }
        }
        return openFields > 1;
    }

    private boolean isItemType(int position) {
        return !(ruleSet == null || position == ruleSet.getRules().size() + 1 || position == 0);
    }

    @NonNull
    private TypeValidation getTypeValidationByRule(FlowRule rule) {
        return TypeValidation.getTypeValidationForRule(rule);
    }

    @Override
    public int getItemCount() {
        return ruleSet != null ? ruleSet.getRules().size() + 2 : 2;
    }

    public void setQuestionText(Spanned questionText) {
        this.questionText = questionText;
        this.notifyItemChanged(0);
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
        this.notifyDataSetChanged();
    }

    public void setOnQuestionAnsweredListener(OnQuestionAnsweredListener onQuestionAnsweredListener) {
        this.onQuestionAnsweredListener = onQuestionAnsweredListener;
    }

    @Override
    public void onChoiceSelected(FlowRule rule, String response) {
        this.response = new RulesetResponse(rule, response);

        try {
            notifyDataSetChanged();
        } catch(Exception ignored){}
    }

    private void notifyItemChangedForRule(FlowRule rule) {
        try {
            notifyItemChanged(ruleSet.getRules().indexOf(rule));
        } catch(Exception ignored) {}
    }

    private class QuestionViewHolder extends RecyclerView.ViewHolder {
        public QuestionViewHolder(TextView itemView) {
            super(itemView);
            itemView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        }

        private void bind(Spanned questionText) {
            ((TextView)itemView).setText(questionText);
        }
    }

    private class RespondViewHolder extends RecyclerView.ViewHolder {

        private final Button respond;

        public RespondViewHolder(View itemView) {
            super(itemView);
            respond = (Button) itemView.findViewById(R.id.respond);
            respond.setOnClickListener(onRespondClickListener);
        }

        private void bindView() {
            respond.setText(haveNextStep ? R.string.label_next_question : R.string.label_finish_poll);
        }

        private View.OnClickListener onRespondClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishQuestion(haveNextStep);
            }
        };
    }

    private OpenFieldViewHolder.OnResponseChangedListener onResponseChangedListener =
            new OpenFieldViewHolder.OnResponseChangedListener() {
        @Override
        public void onResponseChanged(FlowRule rule, String response) {
            updateResponse(rule, response);
        }
        @Override
        public void onResponseFinished(FlowRule rule) {
            finishQuestion(hasDestination(rule));
        }
    };

    public RulesetResponse getResponse() {
        return response;
    }

    public void setResponse(RulesetResponse response) {
        this.response = response;
        notifyDataSetChanged();
    }

    private void finishQuestion(boolean hasDestination) {
        if ((response == null || response.getResponse() == null)) {
            if (hasDestination) {
                displayAlert(R.string.error_choose_answer);
            } else {
                onQuestionAnsweredListener.onQuestionFinished();
            }
        } else if (validateResponseAndSend() && !hasDestination)
            onQuestionAnsweredListener.onQuestionFinished();
    }

    private boolean validateResponseAndSend() {
        boolean isValidResponse;

        if (isNumericMultipleOptions()) {
            isValidResponse = isNumbersValid();
        } else {
            isValidResponse = FlowRunnerManager.validateResponse(flowDefinition, response);
        }

        if (isValidResponse)
            onQuestionAnsweredListener.onQuestionAnswered(ruleSet, response);
        else {
            TypeValidation typeValidation = TypeValidation.getTypeValidationForRule(response.getRule());
            displayAlert(typeValidation.getMessage());
        }
        return isValidResponse;
    }

    private boolean isNumbersValid() {
        for (FlowRule flowRule : ruleSet.getRules()) {
            if (FlowRunnerManager.validateResponseForRule(flowDefinition, response, flowRule)) {
                response.setRule(flowRule);
                return true;
            }
        }
        return false;
    }

    private void displayAlert(@StringRes int message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    private void updateResponse(FlowRule rule, String response) {
        if(QuestionAdapter.this.response != null) {
            if(!this.response.getRule().equals(rule))
                notifyItemChangedForRule(this.response.getRule());

            QuestionAdapter.this.response.setRule(rule);
            QuestionAdapter.this.response.setResponse(response);
        } else {
            QuestionAdapter.this.response = new RulesetResponse(rule, response);
        }
    }

    private boolean hasDestination(FlowRule rule) {
        return rule.getDestination() != null;
    }

    public interface OnQuestionAnsweredListener {
        void onQuestionAnswered(FlowRuleset ruleSet, RulesetResponse response);
        void onQuestionFinished();
    }
}
