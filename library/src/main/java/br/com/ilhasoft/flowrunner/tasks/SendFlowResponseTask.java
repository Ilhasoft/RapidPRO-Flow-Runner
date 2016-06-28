package br.com.ilhasoft.flowrunner.tasks;

import android.os.Handler;

import br.com.ilhasoft.flowrunner.models.FlowAction;
import br.com.ilhasoft.flowrunner.models.FlowStep;
import br.com.ilhasoft.flowrunner.models.FlowStepSet;
import br.com.ilhasoft.flowrunner.service.services.RapidProServices;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gualberto on 6/15/16.
 */
public class SendFlowResponseTask {
    private RapidProServices rapidProServices;
    private String channel;
    private final Handler handler = new Handler();

    public SendFlowResponseTask(String channel) {
        rapidProServices = new RapidProServices();
        this.channel = channel;
    }

    public void sendFlowStepSet(FlowStepSet flowStepSet, final String gcmId) {
        for (final FlowStep flowStep : flowStepSet.getSteps()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rapidProServices.sendReceivedMessage(channel, gcmId, getMessageFromStep(flowStep), new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            call.isCanceled();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            call.isCanceled();
                        }
                    });
                }
            }, 1000);

        }
    }

    private String getMessageFromStep(FlowStep flowStep) {
        FlowAction flowAction = flowStep.getActions().get(0);
        return flowAction.getMessage().values().iterator().next();
    }
}

