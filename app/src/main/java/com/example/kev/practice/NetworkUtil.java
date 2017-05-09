package com.example.kev.practice;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
    public static boolean hasConnectivity(Context context){
        boolean hasConnectivity=true;
        ConnectivityManager conn=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo inf=conn.getActiveNetworkInfo();
        hasConnectivity= inf!=null && (inf.isConnectedOrConnecting());
        return hasConnectivity;
    }
}



