package com.android.eng.drydemo.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Eng on 2018/5/16.
 * Related to network
 */

public class NetworkUtils {
    /**
     * get network info
     */
    private static NetworkInfo getgetActiveNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * get network status
     */
    public static boolean isAvailable(Context context) {
        NetworkInfo info = getgetActiveNetworkInfo(context);
        return info != null && info.isAvailable();
    }
}
