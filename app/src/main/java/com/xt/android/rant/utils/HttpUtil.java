package com.xt.android.rant.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by admin on 2017/5/2.
 */

public class HttpUtil {
    public static void sendGetRequest(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
