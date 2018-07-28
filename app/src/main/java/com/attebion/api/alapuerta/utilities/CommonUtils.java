package com.attebion.api.alapuerta.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by amar-pc on 5/24/2016.
 */
public class CommonUtils {

    private final String TAG = getClass().getSimpleName();
    private Context _context;

    public CommonUtils(Context context) {

        this._context = context;

    }
    /*
        static method to check if internet is connected or not. of not connected
        then ask user to connect to internet or terminiate.
     */
    public  boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        Log.d(TAG, "In isConnected");
        if (activeNetwork != null) {
            Log.d(TAG, "activeNetwork is not null ");
            return true;

        } else {
            Log.d(TAG, "activeNetwork is null ");
            return false;
        }

    }



}
