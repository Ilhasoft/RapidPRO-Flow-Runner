package in.ureport.flowrunner.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import br.com.ilhasoft.support.utils.KeyboardHandler;
import in.ureport.flowrunner.R;
import in.ureport.flowrunner.loaders.LocaleLoader;
import in.ureport.flowrunner.managers.FlowRunnerManager;
import in.ureport.flowrunner.models.FlowAction;
import in.ureport.flowrunner.models.FlowActionSet;
import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRule;
import in.ureport.flowrunner.models.FlowRuleset;
import in.ureport.flowrunner.models.FlowStep;
import in.ureport.flowrunner.models.FlowStepSet;
import in.ureport.flowrunner.models.RulesetResponse;
import in.ureport.flowrunner.views.adapters.QuestionAdapter;

/**
 * Created by johncordeiro on 14/10/15.
 */
public class FlowFragment extends Fragment implements QuestionAdapter.OnQuestionAnsweredListener
        , LoaderManager.LoaderCallbacks<Locale []> {

    private static final String EXTRA_RESPONSE_BUTTON = "responseButton";
    private static final String EXTRA_FLOW_DEFINITION = "flowDefinition";
    private static final String EXTRA_LANGUAGE = "language";
    private static final String EXTRA_FLOW_STEP_SET = "flowStepSet";

    private KeyboardHandler keyboardHandler;
    private Handler handler;

    @LayoutRes
    private int responseButtonRes;
    private FlowDefinition flowDefinition;
    private FlowStepSet flowStepSet;

    private FlowListener flowListener;
    private Locale[] availableLocales;

    private String preferredLanguage;
    private Boolean showLastMessage = true;

    public static FlowFragment newInstance(FlowDefinition flowDefinition, String language) {
        return FlowFragment.newInstance(flowDefinition, language, R.layout.item_respond_flow_question);
    }

    public static FlowFragment newInstance(FlowDefinition flowDefinition, String language,
                                           @LayoutRes int responseButtonRes) {
        Bundle args = new Bundle();
        args.putString(EXTRA_LANGUAGE, language);
        args.putInt(EXTRA_RESPONSE_BUTTON, responseButtonRes);
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

        setupData();
        setupObjects();
        loadAllLanguages();
        setupFlowStepSet(savedInstanceState);
        setupInitialData(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_FLOW_STEP_SET, flowStepSet);
    }

    private void setupInitialData(Bundle savedInstanceState) {
        if (FlowRunnerManager.isFlowCompleted(flowDefinition)) {
            moveToQuestion(null);
        } else if(savedInstanceState != null && savedInstanceState.containsKey(EXTRA_FLOW_STEP_SET)) {
            Fragment fragment = getChildFragmentManager().findFragmentById(R.id.content);
            if(fragment instanceof QuestionFragment) {
                setupQuestionListeners((QuestionFragment)fragment);
            }
        } else {
            moveToQuestion(getFlowActionSetByUuid(flowDefinition.getEntry()));
        }
    }

    private void setupData() {
        Bundle arguments = this.getArguments();
        preferredLanguage = arguments.getString(EXTRA_LANGUAGE);
        responseButtonRes = arguments.getInt(EXTRA_RESPONSE_BUTTON);
        flowDefinition = arguments.getParcelable(EXTRA_FLOW_DEFINITION);
    }

    private void loadAllLanguages() {
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FlowListener) {
            flowListener = (FlowListener) context;
        }
    }

    private void setupFlowStepSet(Bundle savedInstanceBundle) {
        if(savedInstanceBundle != null && savedInstanceBundle.containsKey(EXTRA_FLOW_STEP_SET)) {
            flowStepSet = savedInstanceBundle.getParcelable(EXTRA_FLOW_STEP_SET);
        } else {
            flowStepSet = new FlowStepSet();
            flowStepSet.setFlow(flowDefinition.getMetadata().getUuid());
            flowStepSet.setRevision(flowDefinition.getMetadata().getRevision());
            flowStepSet.setStarted(new Date());
            flowStepSet.setCompleted(true);
            flowStepSet.setSteps(new ArrayList<FlowStep>());
        }
    }

    private void setupObjects() {
        handler = new Handler();
        keyboardHandler = new KeyboardHandler();
    }

    @Override
    public void onQuestionAnswered(FlowRuleset ruleSet, RulesetResponse response) {
        if (flowListener != null)
            flowListener.onFlowResponse(ruleSet);

        keyboardHandler.changeKeyboardVisibility(getActivity(), false);

        FlowActionSet currentActionSet = getFlowActionSetByDestination(ruleSet.getUuid());
        addFlowStep(currentActionSet, response);

        FlowActionSet nextActionSet = getFlowActionSetByUuid(response.getRule().getDestination());
        moveToQuestionDelayed(nextActionSet);
//        if (flowListener != null && FlowRunnerManager.isLastActionSet(nextActionSet))
//            flowListener.onFlowFinished(flowStepSet);
    }

    @Override
    public void onQuestionFinished() {
        if (flowListener != null) {
            flowListener.onFlowFinished(flowStepSet);
        }

        if (showLastMessage) {
            moveToQuestionDelayed(null);
        }
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
        Fragment nextStepFragment;
        ArrayList<FlowActionSet> flowActionSetList = this.getFlowActionSetListForType(flowActionSet, QuestionFragment.ACTION_TYPE_REPLY);
        if (flowActionSet != null && !flowActionSetList.isEmpty()) {
            FlowActionSet flowActionSetLast = flowActionSetList.get(flowActionSetList.size() - 1);

            FlowRuleset flowRuleset = this.getRulesetForAction(flowActionSetLast);
            QuestionFragment questionFragment = QuestionFragment.newInstance(flowDefinition,
                    flowActionSetList, flowRuleset, this.hasNextStep(flowRuleset), preferredLanguage, responseButtonRes);
            setupQuestionListeners(questionFragment);

            nextStepFragment = questionFragment;
        } else {
            nextStepFragment = new InfoFragment();
        }

        if (this.isAdded()) {
            this.getChildFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.content, nextStepFragment).commit();
        }
    }

    private void setupQuestionListeners(QuestionFragment questionFragment) {
        questionFragment.setOnQuestionAnsweredListener(this);
        questionFragment.setFlowFunctionsListener(flowFunctionsListener);
        questionFragment.setFlowListener(onFlowListener);
    }

    public void setShowLastMessage(Boolean showLastMessage) {
        this.showLastMessage = showLastMessage;
    }

    private boolean hasNextStep(FlowRuleset flowRuleset) {
        if (flowRuleset != null) {
            List<FlowRule> rules = flowRuleset.getRules();
            for (FlowRule flowRule : rules) {
                if (FlowRunnerManager.isResponsableRule(flowDefinition, flowRuleset, flowRule)
                && this.haveAnyReplyAction(flowRule.getDestination()))
                    return true;
            }
        }
        return false;
    }

    private boolean haveAnyReplyAction(String uuid) {
        return this.haveAnyReplyAction(this.getFlowActionSetByUuid(uuid));
    }

    private boolean haveAnyReplyAction(FlowActionSet flowActionSet) {
        return !this.getFlowActionSetListForType(flowActionSet, QuestionFragment.ACTION_TYPE_REPLY).isEmpty();
    }

    private ArrayList<FlowActionSet> getFlowActionSetList(FlowActionSet flowActionSetFirst) {
        return this.getFlowActionSetListForType(flowActionSetFirst, null);
    }

    private ArrayList<FlowActionSet> getFlowActionSetListForType(FlowActionSet flowActionSetFirst, String type) {
        ArrayList<FlowActionSet> flowActionSetList = new ArrayList<>();
        FlowActionSet flowActionSet = flowActionSetFirst;

        if (TextUtils.isEmpty(type)) {
            while (flowActionSet != null) {
                flowActionSetList.add(flowActionSet);
                flowActionSet = this.getFlowActionSetByUuid(flowActionSet.getDestination());
            }
        } else {
            while (flowActionSet != null) {
                List<FlowAction> actions = flowActionSet.getActions();
                for (FlowAction flowAction : actions) {
                    if (type.equals(flowAction.getType())) {
                        flowActionSetList.add(flowActionSet);
                        break;
                    }
                }
                flowActionSet = this.getFlowActionSetByUuid(flowActionSet.getDestination());
            }
        }

        return flowActionSetList;
    }

    @Nullable
    private FlowActionSet getFlowActionSetByUuid(String uuid) {
        if (!TextUtils.isEmpty(uuid)) {
            List<FlowActionSet> actionSetList = flowDefinition.getActionSets();
            for (FlowActionSet actionSet : actionSetList) {
                if (actionSet.getUuid().equals(uuid))
                    return actionSet;
            }
        }
        return null;
    }

    @Nullable
    private FlowActionSet getFlowActionSetByDestination(String destination) {
        if (!TextUtils.isEmpty(destination)) {
            String destinationToTest;
            List<FlowActionSet> flowActionSetList = flowDefinition.getActionSets();
            for (FlowActionSet actionSet : flowActionSetList) {
                destinationToTest = actionSet.getDestination();
                if (destinationToTest != null && destinationToTest.equals(destination))
                    return actionSet;
            }
        }
        return null;
    }

    @Nullable
    private FlowRuleset getRulesetForAction(FlowActionSet actionSet) {
        for (FlowRuleset ruleset : flowDefinition.getRuleSets()) {
            String destination = actionSet.getDestination();
            if (destination != null && destination.equals(ruleset.getUuid()))
                return ruleset;
        }
        return null;
    }

    public void setFlowListener(FlowListener flowListener) {
        this.flowListener = flowListener;
    }

    public FlowDefinition getFlowDefinition() {
        return flowDefinition;
    }

    @Override
    public Loader<Locale[]> onCreateLoader(int id, Bundle args) {
        return new LocaleLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<Locale[]> loader, Locale[] data) {
        this.availableLocales = data;
    }

    @Override
    public void onLoaderReset(Loader<Locale[]> loader) {}

    private FlowFunctionsListener flowFunctionsListener = new FlowFunctionsListener() {
        @Override
        public List<Locale> getLocaleForLanguages(Set<String> languages) {
            Map<String, Locale> localeForLanguages = new HashMap<>();
            if(availableLocales != null && languages != null) {
                for (Locale locale : availableLocales) {
                    if(locale.getISO3Language() != null && languages.contains(locale.getISO3Language())) {
                        localeForLanguages.put(locale.getISO3Language(), locale);
                    }
                    if(localeForLanguages.size() == languages.size()) break;
                }
            }
            return new ArrayList<>(localeForLanguages.values());
        }
    };

    private FlowListener onFlowListener = new FlowListener() {
        @Override
        public void onFlowLanguageChanged(String iso3Language) {
            FlowFragment.this.flowListener.onFlowLanguageChanged(iso3Language);
            preferredLanguage = iso3Language;
        }
        @Override
        public void onFlowResponse(FlowRuleset ruleset) {
            FlowFragment.this.flowListener.onFlowResponse(ruleset);
        }
        @Override
        public void onFlowFinished(FlowStepSet stepSet) {
            FlowFragment.this.flowListener.onFlowFinished(stepSet);
        }
    };


    public interface FlowListener {
        void onFlowLanguageChanged(String iso3Language);
        void onFlowResponse(FlowRuleset ruleset);
        void onFlowFinished(FlowStepSet stepSet);
    }

    interface FlowFunctionsListener {
        List<Locale> getLocaleForLanguages(Set<String> languages);
    }
}
