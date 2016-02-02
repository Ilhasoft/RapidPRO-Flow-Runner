package in.ureport.flowrunner.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.com.ilhasoft.support.manager.WrapLinearLayoutManager;
import in.ureport.flowrunner.R;
import in.ureport.flowrunner.managers.TranslateManager;
import in.ureport.flowrunner.models.CustomLocale;
import in.ureport.flowrunner.models.FlowAction;
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
    private static final String EXTRA_HAVE_NEXT_STEP = "haveNextStep";
    private static final String EXTRA_ACTION_SET_LIST = "actionSetList";
    private static final String EXTRA_FLOW_DEFINITION = "flowDefinition";
    private static final String EXTRA_RESPONSE_BUTTON = "responseButton";
    private static final String EXTRA_LANGUAGE = "language";

    public static final String ACTION_TYPE_REPLY = "reply";

    @LayoutRes
    private int responseButtonRes;

    private FlowRuleset ruleSet;
    private FlowDefinition flowDefinition;
    private ArrayList<FlowActionSet> actionSetList;

    private FlowFragment.FlowListener flowListener;
    private FlowFragment.FlowFunctionsListener flowFunctionsListener;
    private QuestionAdapter.OnQuestionAnsweredListener onQuestionAnsweredListener;

    private boolean haveNextStep;
    private String preferredLanguage;
    private HashSet<String> flowLanguages;
    private QuestionAdapter questionAdapter;

    public static QuestionFragment newInstance(FlowDefinition flowDefinition,
                                               ArrayList<FlowActionSet> actionSetList,
                                               FlowRuleset ruleSet, boolean haveNextStep,
                                               String language, @LayoutRes int responseButtonRes) {
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_HAVE_NEXT_STEP, haveNextStep);
        args.putParcelableArrayList(EXTRA_ACTION_SET_LIST, actionSetList);
        args.putParcelable(EXTRA_RULE_SET, ruleSet);
        args.putParcelable(EXTRA_FLOW_DEFINITION, flowDefinition);
        args.putInt(EXTRA_RESPONSE_BUTTON, responseButtonRes);
        args.putString(EXTRA_LANGUAGE, language);

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

        setupData();
        setupQuestionLanguages();
        setupView(view);
    }

    private void setupData() {
        Bundle arguments = this.getArguments();
        if (arguments == null || !arguments.containsKey(EXTRA_FLOW_DEFINITION))
            throw new RuntimeException("Must instantiate this fragment calling newInstance method.");

        ruleSet = arguments.getParcelable(EXTRA_RULE_SET);
        preferredLanguage = arguments.getString(EXTRA_LANGUAGE);
        haveNextStep = arguments.getBoolean(EXTRA_HAVE_NEXT_STEP);
        responseButtonRes = arguments.getInt(EXTRA_RESPONSE_BUTTON);
        flowDefinition = arguments.getParcelable(EXTRA_FLOW_DEFINITION);
        actionSetList = arguments.getParcelableArrayList(EXTRA_ACTION_SET_LIST);
    }

    private void setupQuestionLanguages() {
        if (preferredLanguage == null)
            preferredLanguage = flowDefinition.getBaseLanguage();

        flowLanguages = new HashSet<>();
        FlowActionSet actionSet = actionSetList.get(0);
        if (actionSet != null) {
            for (FlowAction flowAction : actionSet.getActions()) {
                if (ACTION_TYPE_REPLY.equals(flowAction.getType())) {
                    Map<String, String> messages = flowAction.getMessage();
                    flowLanguages.addAll(messages.keySet());
                    return;
                }
            }
        }
    }

    private void setupView(View view) {
        RecyclerView choiceList = (RecyclerView) view.findViewById(R.id.choicesList);
        choiceList.setLayoutManager(new WrapLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        SpaceItemDecoration spaceItemDecoration = new SpaceItemDecoration();
        spaceItemDecoration.setVerticalSpaceHeight(5);
        choiceList.addItemDecoration(spaceItemDecoration);

        questionAdapter = new QuestionAdapter(flowDefinition, ruleSet, haveNextStep,
                this.buildQuestion(preferredLanguage), preferredLanguage, responseButtonRes);
        questionAdapter.setOnQuestionAnsweredListener(onQuestionAnsweredListener);
        choiceList.setAdapter(questionAdapter);

        TextView settings = (TextView) view.findViewById(R.id.settings);
        settings.setVisibility(flowLanguages.size() > 1 ? View.VISIBLE : View.GONE);
        settings.setOnClickListener(onSettingsClickListener);
    }

    public CharSequence buildQuestion(String preferredLanguage) {
        String questionText = "";
        for (FlowActionSet actionSet : actionSetList) {
            List<FlowAction> actions = actionSet.getActions();
            for (FlowAction flowAction : actions) {
                if (ACTION_TYPE_REPLY.equals(flowAction.getType())) {
                    Map<String, String> messageMap = flowAction.getMessage();
                    if (messageMap.containsKey(preferredLanguage))
                        questionText += messageMap.get(preferredLanguage) + "<br>";
                    else
                        questionText += messageMap.get(flowDefinition.getBaseLanguage()) + "<br>";
                }
            }
        }
        questionText = TranslateManager.translateContactFields(flowDefinition.getContact(), questionText.substring(0, questionText.length() - 4));
        return Html.fromHtml(questionText);
    }

    public void setFlowFunctionsListener(FlowFragment.FlowFunctionsListener flowFunctionsListener) {
        this.flowFunctionsListener = flowFunctionsListener;
    }

    public void setOnQuestionAnsweredListener(QuestionAdapter.OnQuestionAnsweredListener onQuestionAnsweredListener) {
        this.onQuestionAnsweredListener = onQuestionAnsweredListener;
    }

    public void setFlowListener(FlowFragment.FlowListener flowListener) {
        this.flowListener = flowListener;
    }

    private View.OnClickListener onSettingsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final CustomLocale[] customLocales = getCustomLocales();
            final String[] languages = toStringArray(customLocales);

            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setTitle(R.string.message_title_languages)
                    .setItems(languages, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            preferredLanguage = customLocales[which].getIso3Language();
                            flowListener.onFlowLanguageChanged(preferredLanguage);
                            questionAdapter.setQuestionText(buildQuestion(preferredLanguage));
                            questionAdapter.setPreferredLanguage(preferredLanguage);
                        }
                    }).create();
            alertDialog.show();
        }
    };

    @NonNull
    private String[] toStringArray(CustomLocale[] customLocales) {
        final String [] languages = new String[customLocales.length];
        for (int i = 0; i < customLocales.length; i++) {
            languages[i] = customLocales[i].getDisplayLanguage();
        }
        return languages;
    }

    @NonNull
    private CustomLocale[] getCustomLocales() {
        List<Locale> locales = flowFunctionsListener.getLocaleForLanguages(flowLanguages);
        final CustomLocale [] languageItems = new CustomLocale[locales.size()];

        for (int i = 0; i < locales.size(); i++) {
            String displayLanguage = locales.get(i).getDisplayLanguage();
            languageItems[i] = new CustomLocale(locales.get(i).getISO3Language(), upperCaseFirstLetter(displayLanguage));
        }
        return languageItems;
    }

    @NonNull
    private String upperCaseFirstLetter(String displayLanguage) {
        return displayLanguage.substring(0, 1).toUpperCase() + displayLanguage.substring(1);
    }
}
