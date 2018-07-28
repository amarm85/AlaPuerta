package com.attebion.api.alapuerta.volley;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.attebion.api.alapuerta.R;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by amar-pc on 5/28/2016.
 */

/*
    Common class for handle the network error or error reponse from server
 */
public class VolleyErrorListener implements Response.ErrorListener {

    private Context mcontext;
    private ComInterface mListener;

    private final String TAG = getClass().getSimpleName();

    /*
    public VolleyErrorListener(){

    }
    */

    public VolleyErrorListener(Context mcontext, Fragment fragment) {

        this.mcontext = mcontext;
        try {
            mListener = (VolleyErrorListener.ComInterface) fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(fragment.toString()
                    + " must implement VolleyErrorListener.ComInterface Interface");
        }
    }

    public VolleyErrorListener(Context mcontext, Activity activity) {

        this.mcontext = mcontext;
        try {
            mListener = (VolleyErrorListener.ComInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement VolleyErrorListener.ComInterface Interface");
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

        String responseBody = null;
        JSONObject serverResponseJSONObject;
        NetworkResponse networkResponse = error.networkResponse;

        if (networkResponse != null && networkResponse.data != null) {
            Log.d(TAG,"network status "+ networkResponse.statusCode);
            switch (networkResponse.statusCode) {

                case 400:
                case 401:
                    try {
                        responseBody = new String(networkResponse.data, "UTF-8");
                        serverResponseJSONObject = new JSONObject(responseBody);

                        Log.d(TAG,"Response JSON"+ serverResponseJSONObject.toString());
                        /*
                        call com interface method to ask calling fragment or activity
                        to do UI work based on parsed message
                         */

                        mListener.volleyDisplayError(serverResponseJSONObject);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;

            }

        } else {

            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                Toast.makeText(mcontext, R.string.volley_connetion_timeout_msg, Toast.LENGTH_LONG).show();
                mListener.volleyConnectionError();
            } else if (error instanceof AuthFailureError) {
                Log.d(TAG, "Authentication failure error occurred !");
                mListener.volleyConnectionError();
            } else if (error instanceof ServerError) {
                Log.d(TAG, "ServerError error occurred !");
                mListener.volleyConnectionError();
            } else if (error instanceof NetworkError) {
                Log.d(TAG, "NetworkError error occurred !");
                mListener.volleyConnectionError();

            } else if (error instanceof ParseError) {
                Toast.makeText(mcontext, "Server response cannot be parsed ", Toast.LENGTH_LONG).show();
                mListener.volleyConnectionError();
            }
        }


    }
    /*
        The interface to communicate with calling fragment or activity
        Calling Fragment or Activity has to implement this interface .
     */
    public interface ComInterface {
        void volleyDisplayError(JSONObject json);
        void volleyConnectionError();
    }
}
