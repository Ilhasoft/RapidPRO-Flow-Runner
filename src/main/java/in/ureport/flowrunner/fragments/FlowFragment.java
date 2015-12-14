package in.ureport.flowrunner.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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

    private static final String EXTRA_FLOW_DEFINITION = "flowDefinition";
    private static final String EXTRA_LANGUAGE = "language";

    private static final String TAG = "FlowFragment";

    private KeyboardHandler keyboardHandler;
    private Handler handler;

    private FlowDefinition flowDefinition;
    private FlowStepSet flowStepSet;

    private FlowListener flowListener;
    private Locale[] availableLocales;

    private String preferredLanguage;
    private Boolean showLastMessage = true;

    public static FlowFragment newInstance(FlowDefinition flowDefinition, String language) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_FLOW_DEFINITION, flowDefinition);
        args.putString(EXTRA_LANGUAGE, language);

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
        setupFlowStepSet();
        loadAllLanguages();
        setupInitialData();
    }

    private void setupInitialData() {
        if(FlowRunnerManager.isFlowCompleted(flowDefinition)) {
            moveToQuestion(null);
        } else {
            moveToQuestion(getFlowActionSetByUuid(flowDefinition.getEntry()));
        }
    }

    private void setupData() {
        flowDefinition = getArguments().getParcelable(EXTRA_FLOW_DEFINITION);
        preferredLanguage = getArguments().getString(EXTRA_LANGUAGE);
    }

    private void loadAllLanguages() {
        getLoaderManager().initLoader(0, null, this).forceLoad();
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
        if(flowListener != null)
            flowListener.onFlowResponse(ruleSet);

        keyboardHandler.changeKeyboardVisibility(getActivity(), false);

        FlowActionSet currentActionSet = getFlowActionSetByDestination(ruleSet.getUuid());
        addFlowStep(currentActionSet, response);

        FlowActionSet nextActionSet = getFlowActionSetByUuid(response.getRule().getDestination());
        if(FlowRunnerManager.isLastActionSet(nextActionSet) && flowListener != null) {
            flowListener.onFlowFinished(flowStepSet);
        }

        moveToQuestionDelayed(nextActionSet);
    }

    @Override
    public void onQuestionFinished() {
        if(flowListener != null)
            flowListener.onFinishedClick();

        if(showLastMessage) {
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
        if(flowActionSet != null) {
            nextStepFragment = QuestionFragment.newInstance(flowDefinition
                    , flowActionSet, getRulesetForAction(flowActionSet), preferredLanguage);
            ((QuestionFragment)nextStepFragment).setOnQuestionAnsweredListener(this);
            ((QuestionFragment)nextStepFragment).setFlowFunctionsListener(flowFunctionsListener);
            ((QuestionFragment)nextStepFragment).setFlowListener(onFlowListener);
        } else {
            nextStepFragment = new InfoFragment();
        }

        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.content, nextStepFragment)
                .commit();
    }

    public void setShowLastMessage(Boolean showLastMessage) {
        this.showLastMessage = showLastMessage;
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

        @Override
        public void onFinishedClick() {
            FlowFragment.this.flowListener.onFinishedClick();
        }
    };

    public interface FlowListener {
        void onFlowLanguageChanged(String iso3Language);
        void onFlowResponse(FlowRuleset ruleset);
        void onFlowFinished(FlowStepSet stepSet);
        void onFinishedClick();
    }

    interface FlowFunctionsListener {
        List<Locale> getLocaleForLanguages(Set<String> languages);
    }
}
