package com.lcjian.happyredenvelope.data.network;

import android.util.Base64;

import com.google.gson.GsonBuilder;
import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BuildConfig;
import com.lcjian.happyredenvelope.Constants;
import com.lcjian.lib.util.common.StorageUtils;
import com.lcjian.lib.util.security.MD5Utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class RestAPI {

    private static final String API_URL = BuildConfig.API_URL;

    private static final int DISK_CACHE_SIZE = 20 * 1024 * 1024; // 20MB

    private Retrofit retrofit;

    private RedEnvelopeService redEnvelopeService;

    private Retrofit getRetrofit() {
        if (retrofit == null) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .cache(getCache());

            clientBuilder.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    long time = System.currentTimeMillis();
                    String username = chain.request().url().toString() + time;
                    String password = MD5Utils.getMD532(username + Constants.RED_ENVELOPE_API_KEY);
                    return chain.proceed(chain.request().newBuilder()
                            .addHeader("Authorization", "Basic " + Base64.encodeToString((username + ";" + password).getBytes(), Base64.NO_WRAP))
                            .addHeader("Date", String.valueOf(time))
                            .build());
                }
            });
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                clientBuilder.interceptors().add(logging);
            }
            retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setDateFormat("MMM d, yyyy").create()))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(clientBuilder.build())
                    .build();
        }
        return retrofit;
    }

    private Cache getCache() {
        Cache cache = null;
        // Install an HTTP cache in the application cache directory.
        try {
            File cacheDir = new File(StorageUtils.getCacheDirectory(App.getInstance()), "http");
            cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        } catch (Exception e) {
            Timber.e(e, "Unable to install disk cache.");
        }
        return cache;
    }

    public RedEnvelopeService spunSugarService() {
        if (redEnvelopeService == null) {
            redEnvelopeService = getRetrofit().create(RedEnvelopeService.class);
        }
        return redEnvelopeService;
    }
}
