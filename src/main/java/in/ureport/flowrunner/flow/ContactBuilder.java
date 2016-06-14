package in.ureport.flowrunner.flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.ureport.flowrunner.models.Contact;
import in.ureport.flowrunner.service.services.RapidProServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gualberto on 6/14/16.
 */
public class ContactBuilder {
    private Contact contact;

    public ContactBuilder() {
        contact = new Contact();
    }

    public ContactBuilder setGcmId(final String gcmId) {
        contact.setUrns(new ArrayList<String>() {{
            add("gcm:" + gcmId);
        }});
        return this;
    }

    public ContactBuilder setGroups(final List<String> groups) {
        contact.setGroups(groups);
        return this;
    }

    public ContactBuilder setEmail(final String email) {
        contact.setEmail(email);
        return this;
    }

    public ContactBuilder setName(final String name) {
        contact.setName(name);
        return this;
    }

    public void saveContact(){
        RapidProServices rapidProServices = new RapidProServices();
        rapidProServices.saveContact(contact, new Callback<Contact>() {
            @Override
            public void onResponse(Call<Contact> call, Response<Contact> response) {

            }

            @Override
            public void onFailure(Call<Contact> call, Throwable t) {
            }
        });
    }
}
