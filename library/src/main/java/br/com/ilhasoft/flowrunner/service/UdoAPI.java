package br.com.ilhasoft.flowrunner.service;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.HashMap;

import br.com.ilhasoft.flowrunner.helpers.GsonDateTypeAdapter;
import br.com.ilhasoft.flowrunner.helpers.HashMapTypeAdapter;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gualberto on 6/13/16.
 */
public class UdoAPI {

    private static final String BASE = "https://udo.ilhasoft.mobi/";

    public static <T> T buildApi (Class<T> endPoint) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new GsonDateTypeAdapter())
                .registerTypeAdapter(HashMap.class, new HashMapTypeAdapter())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE)
                .client(httpClient.build())
                .addConverterFactory (GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(endPoint);
    }

}
