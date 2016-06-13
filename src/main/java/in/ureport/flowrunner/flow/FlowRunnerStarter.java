package in.ureport.flowrunner.flow;

import in.ureport.flowrunner.models.FlowDefinition;

/**
 * Created by gualberto on 6/13/16.
 */
public class FlowRunnerStarter implements VerifyFlows.Listener {

    public FlowRunnerStarter() {
        verifyFlows();
    }

    private void verifyFlows() {
        VerifyFlows verifyFlows = new VerifyFlows(""); // TODO Ainda vai ter a classe que pega o CONTACT
    }

    @Override
    public void finishHasFlows(boolean hasFlows, FlowDefinition flowDefinition) {
        if (hasFlows) {
            addFlowDefinition(flowDefinition);
        }
    }

    private void addFlowDefinition(final FlowDefinition flowDefinition) {
//        FlowFragment flowFragment = FlowFragment.newInstance(flowDefinition, UserManager.getUserLanguage());
//        flowFragment.setFlowListener(PollsResultsFragment.this);
//        getFragmentManager().beginTransaction()
//                .replace(R.id.topBar, flowFragment)
//                .commit();
    }
}
