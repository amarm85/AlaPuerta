package com.attebion.api.alapuerta.fragments;

import android.app.Activity;
import android.content.Context;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;

import com.android.volley.toolbox.JsonObjectRequest;
import com.attebion.api.alapuerta.R;
import com.attebion.api.alapuerta.models.LoginJsonResponse;

import com.attebion.api.alapuerta.utilities.PhotoUtils;
import com.attebion.api.alapuerta.volley.VolleyErrorListener;
import com.attebion.api.alapuerta.volley.VolleySingleton;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.Listener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener,VolleyErrorListener.ComInterface {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private final String TAG = getClass().getSimpleName();

    private Listener mListener;
    private LoginJsonResponse loginJsonResponse;

    // declare the UI variables
    EditText inputEmail;
    EditText inputPassword;
    ActionProcessButton btnLogin;
    TextView signupLink;
    TextView forgotPasswordLink;
    ImageView ivlogo;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    /*
        Initialize all UI components and linked the objects with UI components
        also place the listener on UI components
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Initialize all UI variables
        ivlogo = (ImageView) getActivity().findViewById(R.id.login_logo);

        //Set the app logo programmatically from drawable resource. This is necessary
        //to avoid main thread to do resizing on image based on view container.

        try {
            PhotoUtils.getInstance(getContext(),getString(R.string.app_name),null,null)
                    .decodeAndSetView(ivlogo,getActivity().getResources(),R.drawable.mexicobandera,100);
        } catch (IOException e) {
            e.printStackTrace();
        }

        inputEmail = (EditText) getActivity().findViewById(R.id.input_email);
        inputPassword = (EditText) getActivity().findViewById(R.id.input_password);
        btnLogin = (ActionProcessButton) getActivity().findViewById(R.id.btn_login);
        signupLink = (TextView) getActivity().findViewById(R.id.link_signup);
        forgotPasswordLink = (TextView) getActivity().findViewById(R.id.login_forget_password);

        // set endless mode to action progress Button
        btnLogin.setMode(ActionProcessButton.Mode.ENDLESS);

        // set on click listener to login button
        btnLogin.setOnClickListener(this);

        // set on click listener for sign on link

        signupLink.setOnClickListener(this);

        // set on click listener for forgot password link
        forgotPasswordLink.setOnClickListener(this);

    } // end of onActivityCreated

    /*
    This method is to disable all input fields when app is doing validation or
    network calls to server. This will avoid user to change something in UI while
    process is running in back ground.
     */
    private void disableInputs() {
        inputEmail.setEnabled(false);
        inputPassword.setEnabled(false);
        btnLogin.setEnabled(false);
        signupLink.setEnabled(false);
        forgotPasswordLink.setEnabled(false);
    }

    /*
    This method will enable all input fields when the background process finished for
    validation or network.
     */
    private void enableInput() {
        inputEmail.setEnabled(true);
        inputPassword.setEnabled(true);
        btnLogin.setEnabled(true);
        signupLink.setEnabled(true);
        forgotPasswordLink.setEnabled(true);
    }

    private void login() {
        String message;
        Toast.makeText(getContext(), "In login starting", Toast.LENGTH_LONG).show();

        //Before starting the login hide the soft key is it is visible on screen
        View cView = getActivity().getCurrentFocus();
        if (cView != null) {
            hideTheSoftKey(cView);
        }

        // Validate the input fields if everything is okay then do network operation
        // to verify user in server . Otherwise call onLoginFailed method
        if (isValidInput()) {
            //buildNetworkRequest();


            addToVolleyRequestQueue(buildNetworkRequest());
            enableInput();
            btnLogin.setProgress(100);

        } else {
            message = getString(R.string.error_message_for_invalid_input);
            onLoginFailed(message);
        }



    } // end of login method

    private void addToVolleyRequestQueue(JsonObjectRequest request) {
        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private JsonObjectRequest buildNetworkRequest() {
        Log.d(TAG,"In buildNetworkRequest");
        // Create instance of response and error listeners for volley network call.
        VResponseListener responseListener = new VResponseListener();
        VolleyErrorListener errorListener = new VolleyErrorListener(getContext(),LoginFragment.this);

        // get email id and password from UI components
        String emailId = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String appType = getString(R.string.app_type);
        // Create a json request object from input fields
        JSONObject jsonInput = new JSONObject();
        try {
            jsonInput.put("email",emailId);
            jsonInput.put("password",password);
            jsonInput.put("appType",appType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // temp log
        Log.d(TAG,"built Input JSON object "+ jsonInput.toString());


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST
        ,getString(R.string.login_api_url),jsonInput,responseListener,errorListener)
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }
        };

        return jsonObjectRequest;

        //return null;
    }

    private void onLoginFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

        enableInput();
        btnLogin.setProgress(-1);
    }

    /*
        validate the input fields for user entries . IF user missed to fill
        any details and filled invalid details then set error messsage on
        the invalid input title return false.
     */
    private boolean isValidInput() {

        boolean valid = true;
        String emailId = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        // validate email id si not empty or does not flow email pattern
        if (emailId.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailId).matches()) {
            inputEmail.setError(getString(R.string.login_email_error));
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        // Validate password in not empty or less then 4 chars
        if (password.isEmpty() || password.trim().length() < 5) {
            inputPassword.setError(getString(R.string.login_password_error));
            valid = false;
        } else {
            inputPassword.setError(null);
        }


        return valid;
    }


    private void hideTheSoftKey(View cView) {

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(cView.getWindowToken(), 0);

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (LoginFragment.Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LoginFragment.Listener Interface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /*
    On click listener method implementation for all click event on this fragment UI
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                // set the progress bar on action progress button
                btnLogin.setProgress(1);
                // disable the input fields
                disableInputs();
                // Do the login process
                login();
                break;
            case R.id.link_signup:
                // send user to sign in process
                mListener.goToSignPage();

                break;
            case R.id.login_forget_password:
                ;
                // disable the input fields
                //disableInputs();
                // Send user to forget password process.
                Toast.makeText(getContext(), "In forget", Toast.LENGTH_LONG).show();
                break;

            default:
                break;
        }
    }

    /*
        This method is implementation for VolleyErrorListener's comInterface
        This method will be called when there was an error in volley network
        response and it will get the json object from server that will be
        converted into an object of LoginJsonResponse class by GSON google API.
     */
    @Override
    public void volleyDisplayError(JSONObject json) {
        Gson gson = new Gson();
        loginJsonResponse = gson.fromJson(json.toString(),LoginJsonResponse.class);
        Log.d(TAG,"Server status code" + loginJsonResponse.getStatus());
        Log.d(TAG,"Server Message "+ loginJsonResponse.getMessage());
       // need to work on this for passing right value from server
        onLoginFailed(getString(R.string.login_fail_mgs) + loginJsonResponse.getMessage());
    }

    @Override
    public void volleyConnectionError() {
        onLoginFailed(getString(R.string.login_fail_mgs));
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface Listener {

        void goToSignPage();
        void loginSaveDataInDevice(String email,String apiKey,String serviceApiUrl);
        void finishedLogin();

    }

    private class VResponseListener implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject response) {

            // Convert the  JSON response into an object of LoginJsonResponse
            // class by GSON google API.
            Gson gson = new Gson();
            loginJsonResponse = gson.fromJson(response.toString(),LoginJsonResponse.class);

            /*
                Check is success flag in status object. if true then call
                 interface method to ask calling Activity to save the response into
                 Shared preference for future reference.
             */

            //mListener.loginSaveDataInDevice();

            // if the success flag in status object in false than
            // get the message and display to user using onLoginFailed method.
            // onLoginFailed(message);
        }

    }


}
