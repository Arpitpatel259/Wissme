package com.convertex.wissme.network;

import com.convertex.wissme.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {

    public static Retrofit retrofit = null;

    public static Retrofit getClient() {

        final OkHttpClient okHttpClient = new OkHttpClient.Builder ( )
                .connectTimeout ( 60, TimeUnit.SECONDS )
                .writeTimeout ( 60, TimeUnit.SECONDS )
                .readTimeout ( 60, TimeUnit.SECONDS )
                .build ( );
        if (retrofit == null) {
            Gson gson = new GsonBuilder ()
                    .setLenient()
                    .create();
            retrofit = new Retrofit.Builder ( )
                    .baseUrl ( Constants.BASE_URL )
                    .addConverterFactory ( GsonConverterFactory.create (gson) )
                    .client ( okHttpClient )
                    .build ( );
        }
        return retrofit;
    }
}
