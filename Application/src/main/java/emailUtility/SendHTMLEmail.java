package emailUtility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GoogleApiAvailability;
//import com.google.api.client.extensions.android.http.AndroidHttp;
//import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
//import com.google.api.client.http.HttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.client.util.ExponentialBackOff;
//import com.google.api.services.gmail.GmailScopes;

//import android.os.AsyncTask;
//
////import com.google.api.services.gmail.model.*;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import javax.mail.MessagingException;
//import javax.mail.internet.AddressException;
//
///**
// * Created by Paul Gallini on 7/4/16.
// * <p/>
// * see:  http://www.tutorialspoint.com/javamail_api/javamail_api_send_inlineimage_in_email.htm
// */
//public class SendHTMLEmail {
//    // set-up google email api stuff
//    static final int REQUEST_ACCOUNT_PICKER = 1000;
//    static final int REQUEST_AUTHORIZATION = 1001;
//    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
//    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
//
//    private static final String PREF_ACCOUNT_NAME = "accountName";
//    private static final String[] SCOPES = {GmailScopes.GMAIL_LABELS};
//
//    GoogleAccountCredential mCredential;
//
//    public void sendHtmlEmail(Context context) throws AddressException,
//            MessagingException {
//
//        // Initialize credentials and service object.
//        mCredential = GoogleAccountCredential.usingOAuth2(
//                context, Arrays.asList(SCOPES))
//                .setBackOff(new ExponentialBackOff());
//
//        // read this developers.google.com/identity/protocols/OAuth2
////        mCredential.setSelectedAccount("funkynetsoftware@gmail.com");
//
//        if (!isGooglePlayServicesAvailable(context)) {
//            acquireGooglePlayServices(context);
//        } else if (mCredential.getSelectedAccountName() == null) {
////            chooseAccount();
//            // TODO figure-out if we need to raise an error in this situation
//            // we don't want to prompt the user for an account
//            System.out.println("mCredential.getSelectedAccountName() IS NULL");
//
//        } else if (!isDeviceOnline(context)) {
//            // TODO move string to Strings
//            System.out.println("No network connection available.");
////            Toast.makeText(context,
////                    "No network connection available.", Toast.LENGTH_LONG).show();
//        } else {
//            new MakeRequestTask(mCredential).execute();
//        }
//    }
//
//    /**
//     * Checks whether the device currently has a network connection.
//     *
//     * @return true if the device has a network connection, false otherwise.
//     */
//    private boolean isDeviceOnline(Context context) {
//        ConnectivityManager connMgr =
//                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//        return (networkInfo != null && networkInfo.isConnected());
//    }
//
//    /**
//     * Check that Google Play services APK is installed and up to date.
//     *
//     * @return true if Google Play Services is available and up to
//     * date on this device; false otherwise.
//     */
//    private boolean isGooglePlayServicesAvailable(Context context) {
//        GoogleApiAvailability apiAvailability =
//                GoogleApiAvailability.getInstance();
//        final int connectionStatusCode =
//                apiAvailability.isGooglePlayServicesAvailable(context);
//        return connectionStatusCode == ConnectionResult.SUCCESS;
//    }
//
//    /**
//     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
//     * Play Services installation via a user dialog, if possible.
//     */
//    private void acquireGooglePlayServices(Context context) {
//        GoogleApiAvailability apiAvailability =
//                GoogleApiAvailability.getInstance();
//        final int connectionStatusCode =
//                apiAvailability.isGooglePlayServicesAvailable(context);
//        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
//
//            // TODO move string to Strings
////            Toast.makeText(context,
////                    "GooglePlay Service not available - cannot send e-mail.", Toast.LENGTH_LONG).show();
//            System.out.println("GooglePlay Service not available - cannot send e-mail.");
////            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
//        }
//    }
//
//
//    /**
//     * Display an error dialog showing that Google Play Services is missing
//     * or out of date.
//     *
//     * @param connectionStatusCode code describing the presence (or lack of)
//     *                             Google Play Services on this device.
//     */
////    void showGooglePlayServicesAvailabilityErrorDialog(
////            final int connectionStatusCode) {
////        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
////        Dialog dialog = apiAvailability.getErrorDialog(
////                MainActivity.this,
////                connectionStatusCode,
////                REQUEST_GOOGLE_PLAY_SERVICES);
////        dialog.show();
////    }
//
//    /**
//     * An asynchronous task that handles the Gmail API call.
//     * Placing the API calls in their own task ensures the UI stays responsive.
//     */
//    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
//        private com.google.api.services.gmail.Gmail mService = null;
//        private Exception mLastError = null;
//
//        public MakeRequestTask(GoogleAccountCredential credential) {
//            HttpTransport transport = AndroidHttp.newCompatibleTransport();
//            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
//            mService = new com.google.api.services.gmail.Gmail.Builder(
//                    transport, jsonFactory, credential)
//                    .setApplicationName("Gmail API Android Quickstart")
//                    .build();
//        }
//
//        /**
//         * Background task to call Gmail API.
//         *
//         * @param params no parameters needed for this task.
//         */
//        @Override
//        protected List<String> doInBackground(Void... params) {
//            try {
//                return getDataFromApi();
//            } catch (Exception e) {
//                mLastError = e;
//                cancel(true);
//                return null;
//            }
//        }
//
//        /**
//         * Fetch a list of Gmail labels attached to the specified account.
//         *
//         * @return List of Strings labels.
//         * @throws IOException
//         */
//        private List<String> getDataFromApi() throws IOException {
//            // Get the labels in the user's account.
//            String user = "me";
//            List<String> labels = new ArrayList<String>();
//            ListLabelsResponse listResponse =
//                    mService.users().labels().list(user).execute();
//            for (Label label : listResponse.getLabels()) {
//                labels.add(label.getName());
//            }
//            return labels;
//        }
//    }
//}