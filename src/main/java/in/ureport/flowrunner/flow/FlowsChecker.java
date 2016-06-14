package in.ureport.flowrunner.flow;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.ureport.flowrunner.R;
import in.ureport.flowrunner.managers.FlowRunnerManager;
import in.ureport.flowrunner.models.Contact;
import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRun;
import in.ureport.flowrunner.service.UdoAPI;
import in.ureport.flowrunner.service.endpoints.RapidProEndPoint;
import in.ureport.flowrunner.service.services.RapidProServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gualberto on 6/13/16.
 */
public class FlowsChecker {
    private static final int EARLY_MONTHS = 1;
    private Listener listener;
    private static String TAG = "FlowsChecker";
    private FlowRun lastFlowRun;
    private Contact contact;
    private String gcmId;
    final RapidProServices rapidProServices = new RapidProServices();

    public FlowsChecker(String gcmId) {
        this.gcmId = gcmId;

    }

    public void startCheck() {
        getContact(gcmId);
    }

    public void getContact(String gcmId) {
        rapidProServices.loadContact(gcmId, callbackLoadContact);
    }

    public void hasFlows() {
        rapidProServices.loadRuns(contact.getUuid(), getMinimumDate(), callbackFlowRun);
    }

    Callback<Contact> callbackLoadContact = new Callback<Contact>() {
        @Override
        public void onResponse(Call<Contact> call, Response<Contact> response) {
            contact = response.body();
            hasFlows();
        }

        @Override
        public void onFailure(Call<Contact> call, Throwable t) {
            listener.finishHasFlows(false, null, new Exception(t.getMessage()));
        }
    };

    Callback<List<FlowRun>> callbackFlowRun = new Callback<List<FlowRun>>() {
        @Override
        public void onResponse(Call<List<FlowRun>> call, Response<List<FlowRun>> response) {
            if (!response.body().isEmpty()) {
                lastFlowRun = response.body().get(0);
                rapidProServices.loadFlowDefinition(lastFlowRun.getFlowUuid(), callbackFlowDefinition);

            } else {
                listener.finishHasFlows(false, null, new Exception("Response of FlowRuns isEmpty"));
            }
        }

        @Override
        public void onFailure(Call<List<FlowRun>> call, Throwable t) {
            listener.finishHasFlows(false, null, new Exception(t.getMessage()));
        }
    };

    Callback<FlowDefinition> callbackFlowDefinition = new Callback<FlowDefinition>() {
        @Override
        public void onResponse(Call<FlowDefinition> call, Response<FlowDefinition> response) {
            FlowDefinition flowDefinition = response.body();
            flowDefinition.setContact(new Contact()); // TODO Ainda vai ter a classe que pega o CONTACT
            flowDefinition.setFlowRun(lastFlowRun);
            if(FlowRunnerManager.isFlowExpired(flowDefinition)){
                listener.finishHasFlows(true, flowDefinition, null);
            }else{
                listener.finishHasFlows(false,null,new Exception("Flow is Expired"));
            }

        }

        @Override
        public void onFailure(Call<FlowDefinition> call, Throwable t) {
            listener.finishHasFlows(false, null, new Exception(t.getMessage()));
        }
    };

    @NonNull
    private Date getMinimumDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -EARLY_MONTHS);
        return calendar.getTime();
    }

    public interface Listener {
        void finishHasFlows(boolean hasFlows, FlowDefinition flowDefinition, Exception e);
    }
}
