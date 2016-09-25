package com.dbulgakov.task2.model.api;

import com.dbulgakov.task2.model.api.ApiInterface;
import com.dbulgakov.task2.other.App;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiModule {

    private static final int CONNECT_TIMEOUT = 45;
    private static final int WRITE_TIMEOUT = 45;
    private static final int READ_TIMEOUT = 45;
    private static final long CACHE_SIZE = 10 * 1024 * 1024;


    private ApiModule() {

    }

    public static ApiInterface getApiInterface() {

        File cacheDir = new File(App.getContext().getCacheDir(), "response");
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(new Cache(cacheDir, CACHE_SIZE))
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .build();


        Retrofit.Builder builder = new Retrofit.Builder().
                baseUrl(com.dbulgakov.task2.other.Const.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());

        return builder.build().create(ApiInterface.class);
    }
}
