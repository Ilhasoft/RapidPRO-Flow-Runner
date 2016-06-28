package br.com.ilhasoft.flowrunner.managers;

import android.util.Log;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.Date;
import java.util.Set;

import br.com.ilhasoft.flowrunner.models.Contact;

/**
 * Created by johncordeiro on 10/11/15.
 */
public class TranslateManager {

    private static final String TAG = "TranslateManager";

    private static final String CONTACT_VARIABLE = "(@contact)";
    private static final String CONTACT_FIELD = "(@contact\\.%1$s)";

    private static final String DEFAULT_VALUE = "";

    public static String translateContactFields(Contact contact, String message) {
        try {
            message = translatePhone(contact, message);
            message = translateRootFields(contact, message);
            message = translateCustomFields(contact, message);

            return message.replaceAll(CONTACT_VARIABLE, contact.getName());
        } catch(Exception exception) {
            Log.e(TAG, "translateContactFields ", exception);
            return message;
        }
    }

    private static String translatePhone(Contact contact, String message) throws Exception {
        message = message.replaceAll(String.format(CONTACT_FIELD, "tel"), getValueForObject(contact.getPhone()));
        message = message.replaceAll(String.format(CONTACT_FIELD, "tel_e164"), getValueForObject(contact.getPhone()));
        return message;
    }

    private static String translateCustomFields(Contact contact, String message) throws Exception {
        if (contact.getFields() != null) {
            Set<String> fieldKeys = contact.getFields().keySet();
            for (String fieldKey : fieldKeys) {
                Object value = contact.getFields().get(fieldKey);
                message = message.replaceAll(String.format(CONTACT_FIELD, fieldKey)
                        , getValueForObject(value));
            }
        }
        return message;
    }

    private static String translateRootFields(Contact contact, String message) throws Exception {
        for (Field field : contact.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            message = message.replaceAll(String.format(CONTACT_FIELD, field.getName()), getStringForField(contact, field));
        }
        return message;
    }

    private static String getStringForField(Contact contact, Field field) {
        try {
            Object fieldValue = field.get(contact);
            return getValueForObject(fieldValue);
        } catch (Exception exception) {
            Log.e(TAG, "translateContactFields ", exception);
            return "";
        }
    }

    private static String getValueForObject(Object value) {
        if(value == null)
            return DEFAULT_VALUE;
        else if(value instanceof Date)
            return DateFormat.getDateInstance().format((Date) value);
        else
            return String.valueOf(value);
    }

}
