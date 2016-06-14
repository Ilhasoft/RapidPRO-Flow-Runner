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
import in.ureport.flowrunner.service.UdoAPI;
import in.ureport.flowrunner.service.endpoints.RapidProEndPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gualberto on 6/13/16.
 */
public class RapidProServices {

    private static final String TAG = "RapidProServices";

    RapidProEndPoint rapidProEndPoint;
    private GsonDateTypeAdapter gsonDateTypeAdapter;
    public RapidProServices() {
//        this.endpoint = endpoint;
//        RestAdapter restAdapter = buildRestAdapter();
//        if(BuildConfig.DEBUG) restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
//        service = restAdapter.create(RapidProApi.class);
        UdoAPI udoAPI = new UdoAPI();
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

    public void loadRuns(String userUuid, Date after,Callback<List<FlowRun>> callback ) {
        Call<List<FlowRun>> response = rapidProEndPoint.listRuns(RapidProEndPoint.udoToken, userUuid, gsonDateTypeAdapter.serializeDate(after));
        response.enqueue(callback);
    }
//
    public void loadFlowDefinition(String flowUuid,Callback<FlowDefinition> callback ) {
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
//    public void sendReceivedMessage(String apiKey, String channel, String from, String text) {
//        service.sendReceivedMessage(apiKey, channel, from, text);
//    }
//
//    public void saveFlowStepSet(String apiKey, FlowStepSet flowStepSet) {
//        service.saveFlowStepSet(apiKey, flowStepSet);
//    }
//
//    public Contact saveContact(String apiKey, Contact contact) {
//        return service.saveContact(apiKey, contact);
//    }
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
