package br.com.ilhasoft.flowrunner.flow;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

import br.com.ilhasoft.flowrunner.managers.FlowRunnerManager;
import br.com.ilhasoft.flowrunner.models.Contact;
import br.com.ilhasoft.flowrunner.models.FlowDefinition;
import br.com.ilhasoft.flowrunner.models.FlowRun;
import br.com.ilhasoft.flowrunner.models.ResponseFlowRun;
import br.com.ilhasoft.flowrunner.service.services.RapidProServices;
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

    public FlowsChecker(String gcmId, FlowsChecker.Listener listener) {
        this.gcmId = gcmId;
        this.listener = listener;

    }

    public void startCheck() {
        getContact(gcmId);
    }

    public void getContact(String gcmId) {
        rapidProServices.loadContact("gcm:" + gcmId, callbackLoadContact);
    }

    public void hasFlows() {
        rapidProServices.loadRuns(contact.getUuid(), getMinimumDate(), callbackFlowRun);
    }

    public Contact getContactLoaded() {
        return this.contact;
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

    Callback<ResponseFlowRun<FlowRun>> callbackFlowRun = new Callback<ResponseFlowRun<FlowRun>>() {
        @Override
        public void onResponse(Call<ResponseFlowRun<FlowRun>> call, Response<ResponseFlowRun<FlowRun>> response) {
            if(response.body()!=null) {
                if (!response.body().getResults().isEmpty()) {
                    lastFlowRun = response.body().getResults().get(0);
                    rapidProServices.loadFlowDefinition(lastFlowRun.getFlowUuid(), callbackFlowDefinition);

                } else {
                    listener.finishHasFlows(false, null, new Exception("Response of FlowRuns isEmpty"));
                }
            }else{
                listener.finishHasFlows(false, null, new Exception("Response of FlowRuns isEmpty"));
            }
        }

        @Override
        public void onFailure(Call<ResponseFlowRun<FlowRun>> call, Throwable t) {
            listener.finishHasFlows(false, null, new Exception(t.getMessage()));
        }
    };

    Callback<FlowDefinition> callbackFlowDefinition = new Callback<FlowDefinition>() {
        @Override
        public void onResponse(Call<FlowDefinition> call, Response<FlowDefinition> response) {
            FlowDefinition flowDefinition = response.body();
            if (flowDefinition != null) {
                flowDefinition.setContact(contact);
                flowDefinition.setFlowRun(lastFlowRun);
                if (FlowRunnerManager.isFlowActive(flowDefinition)) {
                    listener.finishHasFlows(true, flowDefinition, null);
                } else {
                    listener.finishHasFlows(false, null, new Exception("Flow is not Active"));
                }
            } else {
                listener.finishHasFlows(false, null, new Exception("Response of FlowDefinitions is empty"));
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
