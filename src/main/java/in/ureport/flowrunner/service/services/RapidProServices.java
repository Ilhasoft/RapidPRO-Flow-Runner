package in.ureport.flowrunner.service.services;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import in.ureport.flowrunner.helpers.GsonDateTypeAdapter;
import in.ureport.flowrunner.models.Contact;
import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRun;
import in.ureport.flowrunner.models.ResponseFlowRun;
import in.ureport.flowrunner.service.UdoAPI;
import in.ureport.flowrunner.service.endpoints.RapidProEndPoint;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gualberto on 6/13/16.
 */
public class RapidProServices {

    private static final String TAG = "RapidProServices";

    RapidProEndPoint rapidProEndPoint;
    UdoAPI udoAPI = new UdoAPI();
    private GsonDateTypeAdapter gsonDateTypeAdapter = new GsonDateTypeAdapter();

    public RapidProServices() {
        udoAPI = new UdoAPI();
        rapidProEndPoint = udoAPI.buildApi(RapidProEndPoint.class);
    }
//    public Response<Boundary> loadBoundaries(String apiKey, Integer page) {
//        return service.listBoundaries(apiKey, page, true);
//    }
//
//    public List<Field> loadFields(String apiKey) {
//        Response<Field> response = service.listFields(apiKey);
//        return response.getResults();
//    }

    public void loadRuns(String userUuid, Date after, Callback<ResponseFlowRun<FlowRun>> callback) {
        Call<ResponseFlowRun<FlowRun>> response = rapidProEndPoint.listRuns(RapidProEndPoint.udoToken, userUuid, gsonDateTypeAdapter.serializeDate(after));
        response.enqueue(callback);
    }

    //
    public void loadFlowDefinition(String flowUuid, Callback<FlowDefinition> callback) {
        Call<FlowDefinition> response = rapidProEndPoint.loadFlowDefinition(RapidProEndPoint.udoToken, flowUuid);
        response.enqueue(callback);
    }

    public void loadContact(String urn, Callback<Contact> callback) {
        Call<Contact> response = rapidProEndPoint.loadContact(RapidProEndPoint.udoToken, urn);
        response.enqueue(callback);
    }

    //
//    public List<Group> loadGroups(String apiKey) {
//        Response<Group> response = service.listGroups(apiKey);
//        return response.getResults();
//    }
//
    public void sendReceivedMessage(String channel, String from, String msg, Callback<ResponseBody> callback) {
        Call<ResponseBody> response = rapidProEndPoint.sendReceivedMessage(RapidProEndPoint.udoToken, channel, from, msg);
        response.enqueue(callback);
    }

    //
//    public void saveFlowStepSet(String apiKey, FlowStepSet flowStepSet) {
//        service.saveFlowStepSet(apiKey, flowStepSet);
//    }
//
    public void saveContact(Contact contact, Callback<Contact> callback) {
        rapidProEndPoint.saveContact(RapidProEndPoint.udoToken, contact).enqueue(callback);
    }
//
//    private RestAdapter buildRestAdapter() {
//        gsonDateTypeAdapter = new GsonDateTypeAdapter();
//
//        Gson gson = new GsonBuilder()
//                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
//                .registerTypeAdapter(Date.class, gsonDateTypeAdapter)
//                .registerTypeAdapter(HashMap.class, new HashMapTypeAdapter())
//                .create();
//
//        return new RestAdapter.Builder()
//                .setEndpoint(endpoint)
//                .setConverter(new GsonConverter(gson))
//                .build();
//    }
//
//    private String getRapidproUserId(String userId) {
//        return userId.replace(":", "").replace("-", "");
//    }

    public void addUpdateContact(Contact contact) {
        UdoAPI udoApi = new UdoAPI();
        udoApi.buildApi(RapidProEndPoint.class).saveContact(RapidProEndPoint.udoToken, contact).enqueue(new Callback<Contact>() {
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
