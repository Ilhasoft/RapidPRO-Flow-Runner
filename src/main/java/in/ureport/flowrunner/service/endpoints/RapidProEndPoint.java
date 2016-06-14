package in.ureport.flowrunner.service.endpoints;


import java.util.List;
import java.util.Map;

import in.ureport.flowrunner.models.Boundary;
import in.ureport.flowrunner.models.Contact;
import in.ureport.flowrunner.models.FlowDefinition;
import in.ureport.flowrunner.models.FlowRun;
import in.ureport.flowrunner.models.FlowStepSet;
import in.ureport.flowrunner.models.Group;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by gualberto on 6/13/16.
 */
public interface RapidProEndPoint {
    static String udoToken = "token 47f75ea3e659563f806ff8d5d2679e25d52f5f85";
    @FormUrlEncoded
    @POST("external/received/{channel}/")
    Call<ResponseBody> sendReceivedMessage(@Header("Authorization") String udoToken
            , @Path("channel") String channel
            , @Field("from") String from
            , @Field("text") String text);

    @GET("groups.json")
    Call<Group> listGroups(@Header("Authorization") String udoToken);

    @GET("fields.json")
    Call<in.ureport.flowrunner.models.Field> listFields(@Header("Authorization") String udoToken);

    @GET("boundaries.json")
    Call<Boundary> listBoundaries(@Header("Authorization") String udoToken
            , @Query("page") Integer page, @Query("aliases") Boolean aliases);

    @GET("runs.json")
    Call<List<FlowRun>> listRuns(@Header("Authorization") String udoToken
            , @Query("contact") String uuid, @Query("after") String after);

    @GET("flow_definition.json")
    Call<FlowDefinition> loadFlowDefinition(@Header("Authorization") String udoToken, @Query("uuid") String flowUuid); //TODO refactor using CALL

    @POST("steps")
    @Headers({ "Accept: application/json", "Content-Type: application/json" })
    Map<String, Object> saveFlowStepSet(@Header("Authorization") String udoToken, @Body FlowStepSet flowStepSet);

    @GET("contacts.json")
    Call<Contact> loadContact(@Header("Authorization") String udoToken, @Query("urns") String urn);

    @POST("contacts.json")
    Call<Contact> saveContact(@Header("Authorization") String udoToken, @Body Contact contact);
}
