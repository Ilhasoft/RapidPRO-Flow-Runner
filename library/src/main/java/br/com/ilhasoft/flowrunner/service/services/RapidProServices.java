package br.com.ilhasoft.flowrunner.service.services;

import java.util.Date;
import java.util.List;

import br.com.ilhasoft.flowrunner.helpers.GsonDateTypeAdapter;
import br.com.ilhasoft.flowrunner.models.Contact;
import br.com.ilhasoft.flowrunner.models.FlowDefinition;
import br.com.ilhasoft.flowrunner.models.FlowRun;
import br.com.ilhasoft.flowrunner.models.Message;
import br.com.ilhasoft.flowrunner.models.ApiResponse;
import br.com.ilhasoft.flowrunner.service.UdoAPI;
import br.com.ilhasoft.flowrunner.service.endpoints.RapidProEndPoint;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by gualberto on 6/13/16.
 */
public class RapidProServices {

    private final String token;
    private final RapidProEndPoint rapidProEndPoint;
    private final GsonDateTypeAdapter gsonDateTypeAdapter;

    public RapidProServices(String token) {
        this.token = token;
        rapidProEndPoint = UdoAPI.buildApi(RapidProEndPoint.class);
        gsonDateTypeAdapter = new GsonDateTypeAdapter();
    }

    public Call<ApiResponse<FlowRun>> loadRuns(String userUuid, Date after) {
        return rapidProEndPoint.listRuns(token, userUuid, gsonDateTypeAdapter.serializeDate(after));
    }

    public Call<FlowDefinition> loadFlowDefinition(String flowUuid) {
        return rapidProEndPoint.loadFlowDefinition(token, flowUuid);
    }

    public Call<Contact> loadContact(String urn) {
        return rapidProEndPoint.loadContact(token, urn);
    }

    public Call<ResponseBody> sendReceivedMessage(String channel, String from, String msg) {
        return rapidProEndPoint.sendReceivedMessage(token, channel, from, msg);
    }

    public Call<Contact> saveContact(Contact contact) {
        return rapidProEndPoint.saveContact(token, contact);
    }

    public Call<ApiResponse<Message>> loadMessages(String contactUuid) {
        return rapidProEndPoint.listMessages(token, contactUuid);
    }

    public Call<ApiResponse<Message>> loadMessageById(Integer messageId) {
        return rapidProEndPoint.listMessageById(token, messageId);
    }

}
