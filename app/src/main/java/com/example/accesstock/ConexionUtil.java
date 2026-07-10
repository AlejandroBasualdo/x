package com.example.accesstock;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConexionUtil {

    public static boolean hayInternet(Context context) {
        if (context == null) return false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}