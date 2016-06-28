package br.com.ilhasoft.flowrunner.service.services;

import android.util.Log;

import java.util.Date;

import br.com.ilhasoft.flowrunner.helpers.GsonDateTypeAdapter;
import br.com.ilhasoft.flowrunner.models.Contact;
import br.com.ilhasoft.flowrunner.models.FlowDefinition;
import br.com.ilhasoft.flowrunner.models.FlowRun;
import br.com.ilhasoft.flowrunner.models.ResponseFlowRun;
import br.com.ilhasoft.flowrunner.service.UdoAPI;
import br.com.ilhasoft.flowrunner.service.endpoints.RapidProEndPoint;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gualberto on 6/13/16.
 */
public class RapidProServices {

    private static final String TAG = "RapidProServices";
    public static String udoToken = "token a5378a9ea57be42596fa5023d80628e5e3d9f6b5";
    RapidProEndPoint rapidProEndPoint;
    UdoAPI udoAPI = new UdoAPI();
    private GsonDateTypeAdapter gsonDateTypeAdapter = new GsonDateTypeAdapter();

    public RapidProServices() {
        udoAPI = new UdoAPI();
        rapidProEndPoint = udoAPI.buildApi(RapidProEndPoint.class);
    }

    public void loadRuns(String userUuid, Date after, Callback<ResponseFlowRun<FlowRun>> callback) {
        Call<ResponseFlowRun<FlowRun>> response = rapidProEndPoint.listRuns(udoToken, userUuid, gsonDateTypeAdapter.serializeDate(after));
        response.enqueue(callback);
    }

    public void loadFlowDefinition(String flowUuid, Callback<FlowDefinition> callback) {
        Call<FlowDefinition> response = rapidProEndPoint.loadFlowDefinition(udoToken, flowUuid);
        response.enqueue(callback);
    }

    public void loadContact(String urn, Callback<Contact> callback) {
        Call<Contact> response = rapidProEndPoint.loadContact(udoToken, urn);
        response.enqueue(callback);
    }

    public void sendReceivedMessage(String channel, String from, String msg, Callback<ResponseBody> callback) {
        Call<ResponseBody> response = rapidProEndPoint.sendReceivedMessage(udoToken, channel, from, msg);
        response.enqueue(callback);
    }

    public void saveContact(Contact contact, Callback<Contact> callback) {
        rapidProEndPoint.saveContact(udoToken, contact).enqueue(callback);
    }

    public void addUpdateContact(Contact contact) {
        UdoAPI udoApi = new UdoAPI();
        udoApi.buildApi(RapidProEndPoint.class).saveContact(udoToken, contact).enqueue(new Callback<Contact>() {
            @Override
            public void onResponse(Call<Contact> call, Response<Contact> response) {
                Contact contactResponse = response.body();
                Log.d(TAG, "onResponse() called with: " + "call = [" + call + "], response = [" + response + "]");
            }

            @Override
            public void onFailure(Call<Contact> call, Throwable t) {
                Log.d(TAG, "onFailure() called with: " + "call = [" + call + "], t = [" + t + "]");
            }
        });
    }

}
