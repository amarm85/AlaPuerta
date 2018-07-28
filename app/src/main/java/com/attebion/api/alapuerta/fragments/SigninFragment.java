package com.attebion.api.alapuerta.fragments;

import android.app.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.attebion.api.alapuerta.R;
import com.attebion.api.alapuerta.models.LoginJsonResponse;
import com.attebion.api.alapuerta.models.SignInJsonResponse;
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
 * {@link SigninFragment.Listener} interface
 * to handle interaction events.
 */
public class SigninFragment extends Fragment implements View.OnClickListener,VolleyErrorListener.ComInterface{

    private Listener mListener;
    private SignInJsonResponse signInJsonResponse;

    // declare the UI variables
    EditText inputEmail;
    EditText inputPassword;
    EditText inputPasswordReenter;
    EditText inputFirstName;
    EditText inputLastName;
    CheckBox cbAgeConfirmation;
    CheckBox cbTermAndConditions;

    ActionProcessButton btnSignin;
    TextView loginLink;
    ImageView ivlogo;
    ImageView ivTermandCondition;

    private final String TAG = getClass().getSimpleName();

    public SigninFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signin, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        inputEmail = (EditText) getActivity().findViewById(R.id.input_email);
        inputPassword = (EditText) getActivity().findViewById(R.id.input_password);
        inputPasswordReenter = (EditText) getActivity().findViewById(R.id.input_password_reenter);
        inputFirstName = (EditText) getActivity().findViewById(R.id.input_first_name);
        inputLastName = (EditText) getActivity().findViewById(R.id.input_Last_name);
        cbAgeConfirmation = (CheckBox) getActivity().findViewById(R.id.cb_age);
        cbTermAndConditions = (CheckBox) getActivity().findViewById(R.id.cb_term_condition);
        btnSignin = (ActionProcessButton) getActivity().findViewById(R.id.btn_signin);
        loginLink = (TextView) getActivity().findViewById(R.id.link_login);
        ivlogo = (ImageView) getActivity().findViewById(R.id.signin_logo);
        ivTermandCondition = (ImageView) getActivity().findViewById(R.id.iv_term_condition);

        // set endless mode to action progress Button

        btnSignin.setMode(ActionProcessButton.Mode.ENDLESS);

        // Set onclick listener for signin button
        btnSignin.setOnClickListener(this);

        // Set onclick listener for Login link
        loginLink.setOnClickListener(this);

        // Set onclick listener for Term and condistion log
        ivTermandCondition.setOnClickListener(this);

        //Set onclick listener for age confirmation check box to set the error message to null
        cbAgeConfirmation.setOnClickListener(this);

        //Set onclick listener for Term and condition  check box to set the error message to null
        cbTermAndConditions.setOnClickListener(this);

        //Set the app logo programmatically from drawable resource. This is necessary
        //to avoid main thread to do resizing on image based on view container.

        try {
            PhotoUtils.getInstance(getContext(), getString(R.string.app_name), null, null)
                    .decodeAndSetView(ivlogo,getActivity().getResources(),R.drawable.mexicobandera,100);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Set the Term and condition logo  programmatically from drawable resource. This is necessary
        //to avoid main thread to do resizing on image based on view container.

        try {
            PhotoUtils.getInstance(getContext(), getString(R.string.app_name), null, null)
                    .decodeAndSetView(ivTermandCondition,getActivity().getResources(),R.drawable.termcondition,20);
        } catch (IOException e) {
            e.printStackTrace();
        }


    } //end of on Activity created


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

            case R.id.btn_signin:
                // set the progress bar on action progress button
                btnSignin.setProgress(1);
                // disable the input fields
                disableInputs();
                // Do the login process
                signIn();
                break;
            case R.id.link_login:
                // send user to sign in process
                mListener.goToLogInPage();
                break;
            case R.id.iv_term_condition:
                break;
            case R.id.cb_age:
                cbAgeConfirmation.setError(null);
                break;
            case R.id.cb_term_condition:
                cbTermAndConditions.setError(null);
                break;
            default:
                break;


        }
    }


    /*
        This method is to disable all input fields when app is doing validation or
        network calls to server. This will avoid user to change something in UI while
        process is running in back ground.
         */
    private void disableInputs() {

        inputEmail.setEnabled(false);
        inputPassword.setEnabled(false);
        inputPasswordReenter.setEnabled(false);
        inputFirstName.setEnabled(false);
        inputLastName.setEnabled(false);
        cbAgeConfirmation.setEnabled(false);
        cbTermAndConditions.setEnabled(false);
        btnSignin.setEnabled(false);
        loginLink.setEnabled(false);
        ivTermandCondition.setEnabled(false);

    }

    private void signIn() {

        String message;
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
            //btnSignin.setProgress(100);

        } else {
            message = getString(R.string.error_message_for_invalid_input);
            onSignInFailed(message);
        }


    } // end of signIn


    /*
    This method will enable all input fields when the background process finished for
    validation or network.
     */

    private void enableInput() {

        inputEmail.setEnabled(true);
        inputPassword.setEnabled(true);
        inputPasswordReenter.setEnabled(true);
        inputFirstName.setEnabled(true);
        inputLastName.setEnabled(true);
        cbAgeConfirmation.setEnabled(true);
        cbTermAndConditions.setEnabled(true);
        btnSignin.setEnabled(true);
        loginLink.setEnabled(true);
        ivTermandCondition.setEnabled(true);
    }

    /*
        validate the input fields for user entries . IF user missed to fill
        any details and filled invalid details then set error messsage on
        the invalid input title return false.
     */

    private boolean isValidInput() {
        String namePattern = "^[a-zA-Z ]*$";
        boolean valid = true;
        String emailId = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String reenterPassword = inputPasswordReenter.getText().toString();
        String firstName = inputFirstName.getText().toString();
        String lastName = inputLastName.getText().toString();

        // validate email id si not empty or does not flow email pattern
        if (emailId.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailId).matches()) {
            inputEmail.setError(getString(R.string.login_email_error));
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        // Validate password in not empty or less then 5 chars
        if (password.isEmpty() || password.trim().length() < 5) {
            inputPassword.setError(getString(R.string.login_password_error));
            valid = false;
        } else {
            inputPassword.setError(null);
        }

        // Validate password and reenter passwords are same
        if(!password.equals(reenterPassword)){
            inputPasswordReenter.setError(getString(R.string.sigin_reenter_password_error_msg));
            valid = false;
        }else{
            inputPasswordReenter.setError(null);
        }


        // Validate the first name for space and numeric chars
        if(firstName.trim().isEmpty() ||!firstName.matches(namePattern)){
            inputFirstName.setError(getString(R.string.signin_name_check_error_msg));
            valid = false;
        }else{
            inputFirstName.setError(null);
        }

        // Validate the Last name for space and numeric chars
        if(lastName.trim().isEmpty() ||!lastName.matches(namePattern)){
            inputLastName.setError(getString(R.string.signin_name_check_error_msg));
            valid = false;
        }else{
            inputLastName.setError(null);
        }

        // validate if the age confirmation check box is checked or not
        if(!cbAgeConfirmation.isChecked()){
            valid = false;
            cbAgeConfirmation.setError(getString(R.string.siginin_age_conf_error_msg));

        }else{
            cbAgeConfirmation.setError(null);
        }

        // validate if the Team and condition check box is checked or not
        if(!cbTermAndConditions.isChecked()){
            valid = false;
            cbTermAndConditions.setError(getString(R.string.sigin_term_cond_error_msg));
        }else{
            cbTermAndConditions.setError(null);
        }



        return valid;
    }



    private void hideTheSoftKey(View cView) {

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(cView.getWindowToken(), 0);

    }


    private void onSignInFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

        enableInput();
        btnSignin.setProgress(-1);
    }

    private JsonObjectRequest buildNetworkRequest() {
        Log.d(TAG, "In buildNetworkRequest");

        // Create instance of response and error listeners for volley network call.
        VResponseListener responseListener = new VResponseListener();
        VolleyErrorListener errorListener = new VolleyErrorListener(getContext(),SigninFragment.this);

        // get the values from UI components
        String emailId = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String firstName = inputFirstName.getText().toString();
        String lastName = inputLastName.getText().toString();
        String appType = getString(R.string.app_type);

        // Create a json request object from input fields
        JSONObject jsonInput = new JSONObject();
        try {
            jsonInput.put("email",emailId);
            jsonInput.put("password",password);
            jsonInput.put("firstName",firstName);
            jsonInput.put("lastName",lastName);
            jsonInput.put("appType",appType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // temp log
        Log.d(TAG,"built Input JSON object "+ jsonInput.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST
                ,getString(R.string.signin_api_url),jsonInput,responseListener,errorListener)
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }
        };

        return jsonObjectRequest;

        //return null;

    }

    private void addToVolleyRequestQueue(JsonObjectRequest request) {
        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
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
        signInJsonResponse = gson.fromJson(json.toString(),SignInJsonResponse.class);
        Log.d(TAG,"Server status code" + signInJsonResponse.getStatus());
        Log.d(TAG,"Server Message "+ signInJsonResponse.getMessage());
        // need to work on this for passing right value from server
        onSignInFailed(getString(R.string.login_fail_mgs) + signInJsonResponse.getMessage());
       // only for testing
        mListener.finishedSignIn();
    }

    @Override
    public void volleyConnectionError() {

        onSignInFailed(getString(R.string.login_fail_mgs));

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

        void goToLogInPage();
        void loginSaveDataInDevice(String email,String firstName,
                                   String lastName,String apiKey,String serviceApiUrl);
        void finishedSignIn();
    }


    private class VResponseListener implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject response) {

            // Convert the  JSON response into an object of LoginJsonResponse
            // class by GSON google API.
            Gson gson = new Gson();
            signInJsonResponse = gson.fromJson(response.toString(),SignInJsonResponse.class);

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
