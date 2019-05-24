package com.example.rotemy213.itsadate;

//imports
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import butterknife.ButterKnife;

public class MainActivity extends Activity {
    /**
     * This activity is mainly used to log in to the app.
     * Authentication is based on the google account of the user.
     * if the user starts using the app, with not any history related to it, the app will suggest
     * him to choose which google account's calendar would he prefer using.
     * Thereafter, all the app would be ready to access most of his google calendar details.
     * if the user has used the app before, he would enter automatically to the system.
     */

    //Attributes
    //A Google Calendar API service object used to access the API.
    static com.google.api.services.calendar.Calendar mService;
    private GoogleAccountCredential credential;
    //the client side socket variable.
    private Socket client;
    private PrintWriter pw;
    ProgressDialog mProgress;
    private final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    private final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    // SCOPES represent the permissions level that my app requires.
    // As for now, my app requires to be able to get users details and to edit it.
    private static final String[] SCOPES = {CalendarScopes.CALENDAR, CalendarScopes.CALENDAR};

    /**
     * Creates the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //The paralleling code in xml is called activity_main
        setContentView(R.layout.activity_main);

        //Creating a dialog which is presented to the user while he's waiting for the app.
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google Calendar ...");;

        ButterKnife.bind(this);

        // Initialize credentials and service object.
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();


    }


    /**
     * Called whenever this activity is pushed to the foreground, such as after
     * a call to onCreate().
     */
    @Override
    protected void onResume() {
        super.onResume();
        refreshResults();
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            // get user's account name;
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(accountName);
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(MainActivity.this, "Account unspecified", Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {
                    chooseAccount();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Attempt to get a set of data from the Google Calendar API to display. If the
     * email address isn't known yet, then call chooseAccount() method so the
     * user can pick an account.
     */
    private void refreshResults() {
        System.out.println("refreshResults");

        if (credential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            if (isDeviceOnline()) {
                mProgress.show();
                new ApiAsyncTask(this).execute();
            } else {
                Toast.makeText(MainActivity.this, "No network connection available.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Send data to server according to response from the ApiAsyncTask.
     *
     * @param dataStrings a List of the upcoming events to send the server with.
     */
    public void updateResultsText(final List<String> dataStrings, final  List<String> events) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dataStrings == null) {
                    Toast.makeText(MainActivity.this, "Error retrieving data!",
                            Toast.LENGTH_LONG).show();
                } else if (dataStrings.size() == 0) {
                    Toast.makeText(MainActivity.this, "No data found.",
                            Toast.LENGTH_LONG).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                client = new Socket("192.168.43.32", 8002);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return;
                            }

                            try {
                                sendData(client, events);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }}).start();


                    // else clean text input and broadcast a toast says: "wrong details"
                    Intent in = new Intent(MainActivity.this, Profile.class);
                    if(dataStrings.size() >= 5)
                    {
                        in.putExtra("event1", dataStrings.get(0));
                        in.putExtra("event2", dataStrings.get(1));
                        in.putExtra("event3", dataStrings.get(2));
                        in.putExtra("event4", dataStrings.get(3));
                        in.putExtra("event5", dataStrings.get(4));
                    }
                    else
                    {
                        for(int i=0; i<dataStrings.size(); i++)
                        {
                            in.putExtra("event" + Integer.toString(i+1), dataStrings.get(i));
                        }
                    }
                    in.putExtra("usermail", (credential.getSelectedAccountName()).toString());
                    startActivity(in);
                }
            }
        });
    }


    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account.
     */
    private void chooseAccount() {
        startActivityForResult(
                credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        System.out.println("isDeviceOnline");
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date. Will
     * launch an error dialog for the user to update Google Play Services if
     * possible.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        System.out.println("osGooglePlayServicesAvailable");

        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        MainActivity.this,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    /**
     * @param client a socket that represents the client side.
     * @param dates List of strings of all of the taken dates in the user calendar
     * @throws IOException
     */

    public void sendData(Socket client, List<String> dates) throws IOException {
        pw = new PrintWriter(client.getOutputStream());
        String userEmail = credential.getSelectedAccountName();
        System.out.println("Events," + userEmail + "," + dates.toString());
        pw.write("Events," + userEmail + "," + dates.toString());
        pw.flush();
        pw.close();
        client.close();
    }
}
