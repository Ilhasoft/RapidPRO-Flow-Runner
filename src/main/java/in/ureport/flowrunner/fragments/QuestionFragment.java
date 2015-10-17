package in.ureport.flowrunner.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.ilhasoft.support.manager.WrapLinearLayoutManager;
import in.ureport.flowrunner.R;
import in.ureport.flowrunner.models.FlowActionSet;
import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRuleset;
import in.ureport.flowrunner.views.adapters.QuestionAdapter;
import in.ureport.flowrunner.views.manager.SpaceItemDecoration;

/**
 * Created by johncordeiro on 14/10/15.
 */
public class QuestionFragment extends Fragment {

    private static final String EXTRA_RULE_SET = "ruleSet";
    private static final String EXTRA_ACTION_SET = "actionSet";
    private static final String EXTRA_FLOW_DEFINITION = "flowDefinition";

    private FlowDefinition flowDefinition;
    private FlowRuleset ruleSet;
    private FlowActionSet actionSet;

    private QuestionAdapter.OnQuestionAnsweredListener onQuestionAnsweredListener;

    public static QuestionFragment newInstance(FlowDefinition flowDefinition, FlowActionSet actionSet, FlowRuleset ruleSet) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ACTION_SET, actionSet);
        args.putParcelable(EXTRA_RULE_SET, ruleSet);
        args.putParcelable(EXTRA_FLOW_DEFINITION, flowDefinition);

        QuestionFragment fragment = new QuestionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choice_question, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getObjects();
        setupView(view);
    }

    private void getObjects() {
        flowDefinition = getArguments().getParcelable(EXTRA_FLOW_DEFINITION);
        actionSet = getArguments().getParcelable(EXTRA_ACTION_SET);
        ruleSet = getArguments().getParcelable(EXTRA_RULE_SET);
    }

    private void setupView(View view) {
        TextView question = (TextView) view.findViewById(R.id.question);
        question.setText(actionSet.getActions().get(0).getMessage().get("eng"));

        RecyclerView choiceList = (RecyclerView) view.findViewById(R.id.choicesList);
        choiceList.setLayoutManager(new WrapLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        SpaceItemDecoration spaceItemDecoration = new SpaceItemDecoration();
        spaceItemDecoration.setVerticalSpaceHeight(5);
        choiceList.addItemDecoration(spaceItemDecoration);

        QuestionAdapter questionAdapter = new QuestionAdapter(flowDefinition, ruleSet);
        questionAdapter.setOnQuestionAnsweredListener(onQuestionAnsweredListener);
        choiceList.setAdapter(questionAdapter);
    }

    public void setOnQuestionAnsweredListener(QuestionAdapter.OnQuestionAnsweredListener onQuestionAnsweredListener) {
        this.onQuestionAnsweredListener = onQuestionAnsweredListener;
    }
}
