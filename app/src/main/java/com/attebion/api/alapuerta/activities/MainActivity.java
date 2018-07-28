package com.attebion.api.alapuerta.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.attebion.api.alapuerta.R;
import com.attebion.api.alapuerta.fragments.ConnectionErrorFragment;
import com.attebion.api.alapuerta.fragments.LoginFragment;
import com.attebion.api.alapuerta.fragments.SigninFragment;
import com.attebion.api.alapuerta.fragments.UpdateProfilePicFragment;
import com.attebion.api.alapuerta.utilities.CommonUtils;
import com.attebion.api.alapuerta.utilities.SecurePreferences;

public class MainActivity extends AppCompatActivity implements ConnectionErrorFragment.Listener,
        LoginFragment.Listener, SigninFragment.Listener,UpdateProfilePicFragment.Listener {

    private ConnectionErrorFragment connectionErrorFragment = null;
    private LoginFragment loginFragment = null;
    private SigninFragment signinFragment = null;
    private UpdateProfilePicFragment updateProfilePicFragment = null;

    FragmentManager fm = null;

    private final String F1TAG = "ConnectionErrorFragment";
    private final String F2TAG = "LoginFragment";
    private final String F3TAG = "SignInFragment";
    private final String F4TAG = "updateProfilePicFragment";

    private final String TAG = getClass().getSimpleName();

    public static final String SharedPreferenceFile = "user_profile.xml";

    private String apiKey = null;
    private String emailId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "oncreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        /* Check if save instance state has value that means we come back from
        rotation or from pause
         */
        if (savedInstanceState != null) {
            processSaveInstanceState(savedInstanceState);
        } else {
            /*check if the device is connected to internet or not. If not then send to
             page for internet connection error.
            */
            if (!isDeviceConnectedToInternet()) {
                Log.d(TAG, "In send to internet error part");
                sendUserToInternetErrorPage();
            } else {
            /*
                Redirect user based on availability of api key
            */
                redirectUser();
            }
        }


    } // end of On create


    private void processSaveInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "In process save instancestate");
        fm = getSupportFragmentManager();
        connectionErrorFragment = (ConnectionErrorFragment) fm.findFragmentByTag(F1TAG);
        loginFragment = (LoginFragment) fm.findFragmentByTag(F2TAG);
        signinFragment = (SigninFragment) fm.findFragmentByTag(F3TAG);
        updateProfilePicFragment = (UpdateProfilePicFragment) fm.findFragmentByTag(F4TAG);
        /*
            Check with fragment was active before user rotated the screen
            based on active fragment redirect the user to that fragment
            again.
         */

        if (connectionErrorFragment != null) {
            sendUserToInternetErrorPage();
            Log.d(TAG, "After Rotation object alive connectionErrorFragment");
        }
        if (loginFragment != null) {
            redirectToLoginPage();
            Log.d(TAG, "After Rotation object alive loginFragment");
        }
        if (signinFragment != null) {
            redirectToSignInPage();
            Log.d(TAG, "After Rotation object alive signinFragment");
        }
        if (updateProfilePicFragment != null) {
            redirectToUpdateProfilePicPage();
            Log.d(TAG, "After Rotation object alive updateProfilePicFragment");
        }
    }



    /*
    This method will check  if device connected to internet or not and return true
    or false based on that.
     */
    private boolean isDeviceConnectedToInternet() {
        Log.d(TAG, "In isDeviceConnectedToInternet");
        CommonUtils commonUtils = new CommonUtils(MainActivity.this);
        Log.d(TAG, "In isDeviceConnectedToInternet " + commonUtils.isConnected());
        return commonUtils.isConnected();

    }

    /*
    This method will create Fragment for network error page and display
    that fragment to user.
     */
    private void sendUserToInternetErrorPage() {
        Log.d(TAG, "In sendUserToInternetErrorPage");
        addOrRemoveConnectionErrorFragment(true);
    }


    /*
    This method will check if the API key is already stored and exists in device
    shared preference file or not. if yes then return true otherwise return false
    also this method wil set the values of APIkey and EmailID class variables.
     */
    private boolean isApiKeyExists() {
        SharedPreferences spref = new SecurePreferences(getApplicationContext(), getString(R.string.share_preference_key), MainActivity.SharedPreferenceFile);
        apiKey = spref.getString("apikey", null);
        emailId = spref.getString("emailid", null);
        if (apiKey != null && emailId != null) {
            Log.d(TAG, "API key is :" + apiKey);
            Log.d(TAG, "Email is :" + emailId);
            return true;
        } else {
            return false;
        }

    }

    /* Check if API key exists in Shared Preference. if yes then make call to server
        and get the list of services in user area. If API key does not exist in share preference
        then direct the user to login page
         */
    private void redirectUser() {

        if (isApiKeyExists()) {
            redirectToServicePage();
        } else {
            redirectToLoginPage();
        }
    }

    private void redirectToServicePage() {

    }

    /*
    This method will redirect the user to logon page.
     */
    private void redirectToLoginPage() {
        addOrRemoveLoginFragment(true);
    }

    /*
    This method will redirect the user to sign in page.
     */
    private void redirectToSignInPage() {
        addOrRemoveSignInFragment(true);
    }

    /*
        This method will redirect the user to update profile picture page
     */
    private void redirectToUpdateProfilePicPage() {

        addorRemoveUpdateProfilePicFragment(true);

    }



    /*
    This method adds or deletes connection error fragment page based on boolean value
    passed.
     */
    private void addOrRemoveConnectionErrorFragment(boolean add) {

        fm = getSupportFragmentManager();
        connectionErrorFragment = (ConnectionErrorFragment) fm.findFragmentByTag(F1TAG);

        if (add) {

            if (connectionErrorFragment == null) {
                connectionErrorFragment = new ConnectionErrorFragment();
            }
            fm.beginTransaction()
                    .replace(R.id.fm_main_activity, connectionErrorFragment, F1TAG)
                            //    .addToBackStack(TAG)
                    .commit();

        } else {

            if (connectionErrorFragment != null) {
                fm.beginTransaction().remove(connectionErrorFragment).commit();
            }
        }


    }

    /*
    This method adds or deletes login fragment page based on boolean value
    passed.
     */
    private void addOrRemoveLoginFragment(boolean add) {

        fm = getSupportFragmentManager();
        loginFragment = (LoginFragment) fm.findFragmentByTag(F2TAG);

        if (add) {

            if (loginFragment == null) {
                loginFragment = new LoginFragment();
            }
            fm.beginTransaction()
                    .replace(R.id.fm_main_activity, loginFragment, F2TAG)
                            //    .addToBackStack(TAG)
                    .commit();
        } else {

            if (loginFragment != null) {
                fm.beginTransaction().remove(loginFragment).commit();
            }
        }


    }

    /*
    This method adds or deletes login fragment page based on boolean value
    passed.
    */
    private void addOrRemoveSignInFragment(boolean add) {

        fm = getSupportFragmentManager();
        signinFragment = (SigninFragment) fm.findFragmentByTag(F3TAG);

        if (add) {

            if (signinFragment == null) {
                signinFragment = new SigninFragment();
            }
            fm.beginTransaction()
                    .replace(R.id.fm_main_activity, signinFragment, F3TAG)
                            //    .addToBackStack(TAG)
                    .commit();
        } else {

            if (signinFragment != null) {
                fm.beginTransaction().remove(signinFragment).commit();
            }
        }


    }

    /*
    This method adds or deletes profile picture update  fragment page based on boolean value
    passed.
    */
    private void addorRemoveUpdateProfilePicFragment(boolean add) {

        fm = getSupportFragmentManager();
        updateProfilePicFragment = (UpdateProfilePicFragment) fm.findFragmentByTag(F4TAG);

        if (add) {

            if (updateProfilePicFragment == null) {
                updateProfilePicFragment = new UpdateProfilePicFragment();
            }
            fm.beginTransaction()
                    .replace(R.id.fm_main_activity, updateProfilePicFragment, F4TAG)
                            //    .addToBackStack(TAG)
                    .commit();
        } else {

            if (updateProfilePicFragment != null) {
                fm.beginTransaction().remove(updateProfilePicFragment).commit();
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "In on activity Result");
            if(updateProfilePicFragment != null){
                Log.d(TAG, "In on activity Result update profile fragment is not null");
                updateProfilePicFragment.onActivityResult(requestCode,resultCode, data);
            }

        Log.d(TAG, "In on activity Result requestCode "+ requestCode);
        Log.d(TAG, "In on activity Result resultCode "+ resultCode);
    }

    /*
 implement the fragment ConnectionErrorFragment.Listener methods for
 communication with fragment
 */

    /*
    If network is available now then remove the network error page and redirect
    user to logon page or service page based on API key exists in
    shared preference file
     */
    public void yesNetworkConnected() {
        addOrRemoveConnectionErrorFragment(false);
        redirectUser();
    }

    /*
    If still network is not available and user presses retry button then again
    redirect user to network error page.
     */
    public void noNetworkConnected() {
        sendUserToInternetErrorPage();
    }


    /*
        Implement the interface methods for  LoginFragment.Listener
     */

    // send user to sign in page if user clicks go to sign in page from
    // login page.
    @Override
    public void goToSignPage() {
        redirectToUpdateProfilePicPage();
        //redirectToSignInPage();
    }

    @Override
    public void loginSaveDataInDevice(String email, String apiKey, String serviceApiUrl) {

    }

    @Override
    public void finishedLogin() {

    }


    /*
       Implement the interface methods for  SignInFragment.Listener
    */

    // send user to login  page if user clicks go to login in page from
    // sign in page
    @Override
    public void goToLogInPage() {
        redirectToLoginPage();
    }

    @Override
    public void loginSaveDataInDevice(String email, String firstName, String lastName, String apiKey, String serviceApiUrl) {

    }

    @Override
    public void finishedSignIn() {
        addorRemoveUpdateProfilePicFragment(true);
    }

    @Override
    public void cancelPorfilePicUpdate() {

    }
}
