package br.com.ilhasoft.flowrunner.flow;

import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentManager;

import br.com.ilhasoft.flowrunner.fragments.DialogFlowFragment;
import br.com.ilhasoft.flowrunner.fragments.FlowFragment;
import br.com.ilhasoft.flowrunner.fragments.InfoFragment;
import br.com.ilhasoft.flowrunner.models.FlowDefinition;
import br.com.ilhasoft.flowrunner.models.FlowRuleset;
import br.com.ilhasoft.flowrunner.models.FlowStepSet;
import br.com.ilhasoft.flowrunner.service.services.RapidProServices;
import br.com.ilhasoft.flowrunner.tasks.SendFlowResponseTask;

/**
 * Created by gualberto on 6/13/16.
 */
public class FlowRunnerStarter implements FlowsChecker.Listener, FlowFragment.FlowListener {
    private FragmentManager supportFragmentManager;
    private String gcmId;
    private String channel;
    private @LayoutRes int responseButtonRes = -1;

    private boolean isFlowReady = false;
    private boolean isRunning = false;

    private FlowDefinition flowDefinition;
    private FlowListener flowListener;
    private FlowsChecker flowsChecker;

    private SendFlowResponseTask sendFlowResponseTask;
    private DialogFlowFragment dialogFlowFragment;
    private DialogListener dialogListener;

    public FlowRunnerStarter(String gcmId, String channel, String udoToken) {
        this.gcmId = gcmId;
        flowsChecker = new FlowsChecker(gcmId, this);
        this.channel = channel;
        sendFlowResponseTask = new SendFlowResponseTask(channel);
        RapidProServices.udoToken = udoToken;
    }

    public void loadFlows() {
        if (!isRunning) {
            isRunning = true;
            flowsChecker.startCheck();
        }
    }

    public void loadFlows(FlowListener flowListener) {
        this.flowListener = flowListener;
        if (!isRunning) {
            isRunning = true;
            flowsChecker.startCheck();
        }
    }

    public void startFlow(FragmentManager supportFragmentManager, DialogListener dialogListener) {
        if (flowDefinition != null) {
            this.dialogListener = dialogListener;
            showFlowDefinition(flowDefinition, supportFragmentManager);
        }
    }
    public void startFlow(FragmentManager supportFragmentManager) {
        if (flowDefinition != null) {
            showFlowDefinition(flowDefinition, supportFragmentManager);
        }
    }
    public FlowFragment getFlowFragment() throws Exception {
        if (flowDefinition != null) {
            FlowFragment flowFragment = FlowFragment.newInstance(flowDefinition, flowDefinition.getBaseLanguage());
            flowFragment.setFlowListener(this);
            return flowFragment;
        } else {
            throw new Exception("Flow not ready");
        }
    }

    @Override
    public void finishHasFlows(boolean hasFlows, FlowDefinition flowDefinition, Exception e) {
        if (hasFlows) {
            this.flowDefinition = flowDefinition;
            isFlowReady = true;
            if (flowListener != null)
                flowListener.onflowIsReady(FlowFragment.newInstance(flowDefinition, flowDefinition.getBaseLanguage()));
        } else {
            if (flowListener != null)
                flowListener.onflowError(e);
        }
        isRunning = false;
    }

    private void showFlowDefinition(final FlowDefinition flowDefinition, FragmentManager supportFragmentManager) {
        FlowFragment flowFragment;
        if(responseButtonRes == -1) {
            flowFragment = FlowFragment.newInstance(flowDefinition, flowDefinition.getBaseLanguage(), onDialogClickExit);
        }else{
            flowFragment = FlowFragment.newInstance(flowDefinition, flowDefinition.getBaseLanguage(),responseButtonRes ,onDialogClickExit);
        }
        flowFragment.setFlowListener(this);
        dialogFlowFragment = DialogFlowFragment.newInstance(flowFragment);
        dialogFlowFragment.show(supportFragmentManager, "flow_fragment");
    }

    public void setResponseButtonRes(int responseButtonRes) {this.responseButtonRes = responseButtonRes;}

    public boolean isFlowReady() {
        return isFlowReady;
    }

    @Override
    public void onFlowLanguageChanged(String iso3Language) {
    }

    @Override
    public void onFlowResponse(FlowRuleset ruleset) {
    }

    @Override
    public void onFlowFinished(FlowStepSet stepSet) {
        sendFlowResponseTask.sendFlowStepSet(stepSet, gcmId);
        flowDefinition = null;
        isFlowReady = false;
        if (dialogListener != null) {
            dialogListener.onFlowFinished();
        }
    }

    @Override
    public void onFinishedClick() {
    }

    public interface FlowListener {
        void onflowIsReady(FlowFragment flowFragment);

        void onflowError(Exception e);
    }

    public interface DialogListener {
        void onFlowFinished();
    }

    InfoFragment.onDialogClickExit onDialogClickExit = new InfoFragment.onDialogClickExit() {
        @Override
        public void onClickExit() {
            dialogFlowFragment.dismiss();
        }
    };
}