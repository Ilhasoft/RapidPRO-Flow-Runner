package in.ureport.flowrunner.views.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    private RulesetResponse response;

    private Context context;

    private FlowDefinition flowDefinition;
    private FlowRuleset ruleSet;

    private OnQuestionAnsweredListener onQuestionAnsweredListener;

    public QuestionAdapter(FlowDefinition flowDefinition, FlowRuleset ruleSet) {
        this.flowDefinition = flowDefinition;
        this.ruleSet = ruleSet;
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        initContext(parent);
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == TYPE_RESPOND) {
            return new RespondViewHolder(inflater.inflate(R.layout.item_respond_flow_question, parent, false));
        } else if(viewType == TYPE_HIDE) {
            return new BaseViewHolder(inflater.inflate(R.layout.item_hide, parent, false));
        } else {
            switch(Type.values()[viewType]) {
                case Choice:
                    ChoiceViewHolder choiceViewHolder = new ChoiceViewHolder(inflater.inflate(R.layout.item_choice, parent, false));
                    choiceViewHolder.setOnChoiceSelectedListener(this);
                    return choiceViewHolder;
                case Date:
                    DateViewHolder dateViewHolder = new DateViewHolder(inflater.inflate(R.layout.item_open, parent, false));
                    dateViewHolder.setOnResponseChangedListener(onResponseChangedListener);
                    return dateViewHolder;
                default:
                case OpenField:
                    OpenFieldViewHolder openFieldViewHolder = new OpenFieldViewHolder(inflater.inflate(R.layout.item_open, parent, false));
                    openFieldViewHolder.setOnResponseChangedListener(onResponseChangedListener);
                    return openFieldViewHolder;
            }
        }
    }

    private void initContext(ViewGroup parent) {
        if(context == null) {
            context = parent.getContext();
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch(getItemViewType(position)) {
            case TYPE_RESPOND:
                ((RespondViewHolder)holder).bindView();
                break;
            default:
                FlowRule rule = ruleSet.getRules().get(position);
                ((BaseViewHolder)holder).bindView(rule, response);
        }
    }

    @Override
    public long getItemId(int position) {
        if(isItemType(position)) {
            return ruleSet.getRules().get(position).getUuid().hashCode();
        }
        return TYPE_RESPOND;
    }

    @Override
    public int getItemViewType(int position) {
        if(isItemType(position)) {
            FlowRule rule = ruleSet.getRules().get(position);
            if(FlowRunnerManager.hasRecursiveDestination(flowDefinition, ruleSet, rule)) {
                return TYPE_HIDE;
            }
            return getTypeValidationByRule(rule).getType().ordinal();
        }
        return TYPE_RESPOND;
    }

    private boolean isItemType(int position) {
        return !(ruleSet == null || position == ruleSet.getRules().size());
    }

    @NonNull
    private TypeValidation getTypeValidationByRule(FlowRule rule) {
        return TypeValidation.getTypeValidationForRule(rule);
    }

    @Override
    public int getItemCount() {
        return ruleSet != null ? ruleSet.getRules().size() + 1 : 1;
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

    private class RespondViewHolder extends RecyclerView.ViewHolder {

        private final Button respond;

        public RespondViewHolder(View itemView) {
            super(itemView);

            respond = (Button) itemView.findViewById(R.id.respond);
            respond.setOnClickListener(onRespondClickListener);
        }

        private void bindView() {
            respond.setVisibility(hasDestination() ? View.VISIBLE : View.GONE);
        }

        private boolean hasDestination() {
            return ruleSet != null && !ruleSet.getRules().isEmpty()
                && ruleSet.getRules().get(getLayoutPosition()-1).getDestination() != null;
        }

        private View.OnClickListener onRespondClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishQuestion();
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
        public void onResponseFinished() {
            finishQuestion();
        }
    };

    private void finishQuestion() {
        if(response == null || response.getResponse() == null) {
            displayAlert(R.string.error_choose_answer);
        } else if(onQuestionAnsweredListener != null) {
            validateResponseAndSend();
        }
    }

    private void validateResponseAndSend() {
        if(FlowRunnerManager.validateResponse(flowDefinition, response)) {
            onQuestionAnsweredListener.onQuestionAnswered(ruleSet, response);
        } else {
            TypeValidation typeValidation = TypeValidation.getTypeValidationForRule(response.getRule());
            displayAlert(typeValidation.getMessage());
        }
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

    public interface OnQuestionAnsweredListener {
        void onQuestionAnswered(FlowRuleset ruleSet, RulesetResponse response);
    }
}
