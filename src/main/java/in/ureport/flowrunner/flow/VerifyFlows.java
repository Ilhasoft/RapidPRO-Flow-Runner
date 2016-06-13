package in.ureport.flowrunner.flow;

import android.support.annotation.NonNull;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.ureport.flowrunner.managers.FlowRunnerManager;
import in.ureport.flowrunner.models.Contact;
import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRun;
import in.ureport.flowrunner.service.services.RapidProServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gualberto on 6/13/16.
 */
public class VerifyFlows {
    private String apiToken;
    private static final int EARLY_MONTHS = 1;
    private Listener listener;
    private static String TAG ="VerifyFlows";

    public VerifyFlows(String apiToken) {
        this.apiToken = apiToken;
        hasFlows();
    }

    public void hasFlows() {
        final RapidProServices rapidProServices = new RapidProServices();
        rapidProServices.loadRuns(apiToken, "", getMinimumDate(), new Callback<List<FlowRun>>() {
            @Override
            public void onResponse(Call<List<FlowRun>> call, Response<List<FlowRun>> response) {
                if(!response.body().isEmpty()) {
                    FlowRun lastFlowRun = response.body().get(0);
                    FlowDefinition flowDefinition = rapidProServices.loadFlowDefinition(apiToken, lastFlowRun.getFlowUuid());
                    flowDefinition.setContact(new Contact()); // TODO Ainda vai ter a classe que pega o CONTACT
                    flowDefinition.setFlowRun(lastFlowRun);
                    listener.finishHasFlows(FlowRunnerManager.isFlowExpired(flowDefinition), flowDefinition);
                }else{
                    listener.finishHasFlows(false, null);
                }
            }

            @Override
            public void onFailure(Call<List<FlowRun>> call, Throwable t) {

            }
        });
    }

    @NonNull
    private Date getMinimumDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -EARLY_MONTHS);
        return calendar.getTime();
    }

    public interface Listener {
        void finishHasFlows(boolean hasFlows, FlowDefinition flowDefinition);
    }
}
