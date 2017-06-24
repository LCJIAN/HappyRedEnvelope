package com.lcjian.happyredenvelope.data.network;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.GsonBuilder;
import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BuildConfig;
import com.lcjian.lib.DeviceUuidFactory;
import com.lcjian.lib.util.common.StorageUtils;
import com.lcjian.lib.util.security.MD5Utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class RestAPI {

    private static final String API_URL = BuildConfig.API_URL;

    private static final int DISK_CACHE_SIZE = 20 * 1024 * 1024; // 20MB

    private Retrofit retrofit;

    private RedEnvelopeService redEnvelopeService;

    private static String bodyToString(RequestBody request) {
        try {
            Buffer buffer = new Buffer();
            request.writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            return "";
        }
    }

    private Retrofit getRetrofit() {
        if (retrofit == null) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .cache(getCache());

            clientBuilder.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(@NonNull Chain chain) throws IOException {
                    String deviceId = new DeviceUuidFactory(App.getInstance()).getDeviceUuid().toString();
                    String version = BuildConfig.VERSION_NAME;
                    String platform = "Android";
                    String channel = "QQ";
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    List<String> commonParas = Arrays.asList(deviceId, version, platform, channel, timestamp);
                    Collections.sort(commonParas);
                    String sign = MD5Utils.getMD532(TextUtils.join("", commonParas));

                    Request request = chain.request();
                    RequestBody requestBody = request.body();
                    String postBodyString = bodyToString(requestBody);
                    postBodyString += ((postBodyString.length() > 0) ? "&" : "") + bodyToString(
                            new FormBody.Builder()
                                    .add("device", deviceId)
                                    .add("version", version)
                                    .add("platform", platform)
                                    .add("channel", channel)
                                    .add("timestamp", timestamp)
                                    .add("sign", sign)
                                    .build());
                    return chain.proceed(request.newBuilder().method(request.method(),
                            RequestBody.create(requestBody.contentType(), postBodyString)).build());
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

    public RedEnvelopeService redEnvelopeService() {
        if (redEnvelopeService == null) {
            redEnvelopeService = getRetrofit().create(RedEnvelopeService.class);
        }
        return redEnvelopeService;
    }
}
