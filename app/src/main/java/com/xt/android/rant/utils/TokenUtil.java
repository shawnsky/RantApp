package com.xt.android.rant.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by admin on 2017/5/7.
 */

public class TokenUtil {
    public static String getToken(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("token", Activity.MODE_PRIVATE);
        return sharedPreferences.getString("token","");
    }
}
