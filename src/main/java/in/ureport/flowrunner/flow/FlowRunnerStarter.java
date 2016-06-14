package in.ureport.flowrunner.flow;

import android.support.v4.app.FragmentManager;
import android.util.Log;

import in.ureport.flowrunner.fragments.FlowFragment;
import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRuleset;
import in.ureport.flowrunner.models.FlowStepSet;

/**
 * Created by gualberto on 6/13/16.
 */
public class FlowRunnerStarter implements FlowsChecker.Listener, FlowFragment.FlowListener {
    private FragmentManager supportFragmentManager;
    private String gcmId;
    private boolean isFlowReady = false;
    private boolean isRunning = false;
    private FlowDefinition flowDefinition;
    private Listener listener;
    private FlowsChecker flowsChecker;

    public FlowRunnerStarter(String gcmId) {
       flowsChecker = new FlowsChecker(gcmId);
    }

    public void loadFlows() {
        if(!isRunning) {
            isRunning = true;
            flowsChecker.startCheck();
        }
    }
    public void loadFlows(FlowRunnerStarter.Listener listener) {
        this.listener = listener;
        if(!isRunning) {
            isRunning = true;
            flowsChecker.startCheck(); // TODO Ainda vai ter a classe que pega o CONTACT
        }
    }
    public void startFlows(FragmentManager supportFragmentManager) throws Exception {
        if(flowDefinition!=null){
            addFlowDefinition(flowDefinition,supportFragmentManager);
        }else{
            throw new Exception("Flow not loaded");
        }
    }

    @Override
    public void finishHasFlows(boolean hasFlows, FlowDefinition flowDefinition, Exception e) {
        if (hasFlows) {
            this.flowDefinition = flowDefinition;
            isFlowReady = true;
            listener.flowIsReady();
        }else{
            listener.flowError(e);
        }
        isRunning = false;
    }

    private void addFlowDefinition(final FlowDefinition flowDefinition,FragmentManager supportFragmentManager ) {
        FlowFragment flowFragment = FlowFragment.newInstance(flowDefinition, "BRA");
        flowFragment.setFlowListener(this);
        supportFragmentManager.beginTransaction().add(flowFragment,"flow_fragment").commit();
    }

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
    }

    @Override
    public void onFinishedClick() {
    }

    public interface Listener{
        void flowIsReady();
        void flowError(Exception e);
    }
}
