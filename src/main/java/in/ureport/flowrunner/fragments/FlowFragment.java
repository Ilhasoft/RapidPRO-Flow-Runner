package in.ureport.flowrunner.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.com.ilhasoft.support.utils.KeyboardHandler;
import in.ureport.flowrunner.R;
import in.ureport.flowrunner.managers.FlowRunnerManager;
import in.ureport.flowrunner.models.FlowAction;
import in.ureport.flowrunner.models.FlowActionSet;
import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRuleset;
import in.ureport.flowrunner.models.FlowStep;
import in.ureport.flowrunner.models.FlowStepSet;
import in.ureport.flowrunner.models.RulesetResponse;
import in.ureport.flowrunner.views.adapters.QuestionAdapter;

/**
 * Created by johncordeiro on 14/10/15.
 */
public class FlowFragment extends Fragment implements QuestionAdapter.OnQuestionAnsweredListener {

    public static final String EXTRA_FLOW_DEFINITION = "flowDefinition";

    private KeyboardHandler keyboardHandler;
    private Handler handler;

    private FlowDefinition flowDefinition;
    private FlowStepSet flowStepSet;

    private FlowListener flowListener;

    public static FlowFragment newInstance(FlowDefinition flowDefinition) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_FLOW_DEFINITION, flowDefinition);
        FlowFragment fragment = new FlowFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_flow, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        flowDefinition = getArguments().getParcelable(EXTRA_FLOW_DEFINITION);
        setupObjects();
        setupFlowStepSet();
        moveToQuestion(getFlowActionSetByUuid(flowDefinition.getEntry()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof FlowListener) {
            flowListener = (FlowListener) context;
        }
    }

    private void setupFlowStepSet() {
        flowStepSet = new FlowStepSet();
        flowStepSet.setFlow(flowDefinition.getMetadata().getUuid());
        flowStepSet.setRevision(flowDefinition.getMetadata().getRevision());
        flowStepSet.setStarted(new Date());
        flowStepSet.setCompleted(true);
        flowStepSet.setSteps(new ArrayList<FlowStep>());
    }

    private void setupObjects() {
        handler = new Handler();
        keyboardHandler = new KeyboardHandler();
    }

    @Override
    public void onQuestionAnswered(FlowRuleset ruleSet, RulesetResponse response) {
        keyboardHandler.changeKeyboardVisibility(getActivity(), false);

        FlowActionSet currentActionSet = getFlowActionSetByDestination(ruleSet.getUuid());
        addFlowStep(currentActionSet, response);

        FlowActionSet nextActionSet = getFlowActionSetByUuid(response.getRule().getDestination());
        if(FlowRunnerManager.isLastActionSet(nextActionSet) && flowListener != null) {
            addFlowStep(nextActionSet, response);
            flowListener.onFlowFinished(flowStepSet);
        }

        moveToQuestionDelayed(nextActionSet);
    }

    private void addFlowStep(FlowActionSet flowActionSet, RulesetResponse response) {
        FlowStep flowStep = createFlowStep(response, flowActionSet);
        flowStepSet.getSteps().add(flowStep);
    }

    @NonNull
    private FlowStep createFlowStep(RulesetResponse response, FlowActionSet flowActionSet) {
        FlowAction flowAction = createFlowActionForResponse(response, flowActionSet);

        List<FlowAction> flowActionList = new ArrayList<>();
        flowActionList.add(flowAction);

        FlowStep flowStep = new FlowStep();
        flowStep.setArrivedOn(new Date());
        flowStep.setNode(flowActionSet.getUuid());
        flowStep.setActions(flowActionList);
        return flowStep;
    }

    private FlowAction createFlowActionForResponse(RulesetResponse response, FlowActionSet flowActionSet) {
        FlowAction flowAction = new FlowAction();
        flowAction.setType(flowActionSet.getActions().get(0).getType());

        HashMap<String, String> message = new HashMap<>();
        message.put(flowDefinition.getBaseLanguage(), response.getResponse());

        flowAction.setMessage(message);
        return flowAction;
    }

    private void moveToQuestionDelayed(final FlowActionSet flowActionSet) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                moveToQuestion(flowActionSet);
            }
        }, 100);
    }

    private void moveToQuestion(FlowActionSet flowActionSet) {
        QuestionFragment questionFragment = QuestionFragment.newInstance(flowDefinition
                , flowActionSet, getRulesetForAction(flowActionSet));
        questionFragment.setOnQuestionAnsweredListener(this);
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.content, questionFragment)
                .commit();
    }

    @Nullable
    private FlowActionSet getFlowActionSetByUuid(String uuid) {
        for (FlowActionSet actionSet : flowDefinition.getActionSets()) {
            if(actionSet.getUuid().equals(uuid)) {
                return actionSet;
            }
        }
        return null;
    }

    @Nullable
    private FlowActionSet getFlowActionSetByDestination(String destination) {
        for (FlowActionSet actionSet : flowDefinition.getActionSets()) {
            if(actionSet.getDestination() != null && actionSet.getDestination().equals(destination)) {
                return actionSet;
            }
        }
        return null;
    }

    @Nullable
    private FlowRuleset getRulesetForAction(FlowActionSet actionSet) {
        for (FlowRuleset ruleSet : flowDefinition.getRuleSets()) {
            if(actionSet != null && actionSet.getDestination() != null
            && actionSet.getDestination().equals(ruleSet.getUuid())) {
                return ruleSet;
            }
        }
        return null;
    }

    public void setFlowListener(FlowListener flowListener) {
        this.flowListener = flowListener;
    }

    public interface FlowListener {
        void onFlowFinished(FlowStepSet stepSet);
    }
}
