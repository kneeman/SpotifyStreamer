package com.knee.spotifystreamer.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by c_cknee on 7/16/2015.
 */
public class Utils {
    public static boolean isNetworkConnected(Context pContext){
        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager)pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
