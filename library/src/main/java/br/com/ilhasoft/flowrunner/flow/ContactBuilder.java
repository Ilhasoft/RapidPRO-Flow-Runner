package br.com.ilhasoft.flowrunner.flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.ilhasoft.flowrunner.models.Contact;
import br.com.ilhasoft.flowrunner.service.services.RapidProServices;
import retrofit2.Callback;

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

    public ContactBuilder setFields(HashMap<String, Object> fields) {
        contact.setFields(fields);
        return this;
    }

    public void saveContact(Callback<Contact> contactCallback) {
        RapidProServices rapidProServices = new RapidProServices(FlowRunnerStarter.token);
        rapidProServices.saveContact(contact).enqueue(contactCallback);
    }
}
