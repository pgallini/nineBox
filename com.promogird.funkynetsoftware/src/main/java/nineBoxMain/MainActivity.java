/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nineBoxMain;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v13.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

import databaseOpenHelper.DatabaseOpenHelper;
import nineBoxCandidates.CandidatesListActivity;
import nineBoxQuestions.QuestionsListActivity;
import nineBoxEvaluation.Evaluation;
import nineBoxReport.ReportActivity;

// for the Showcase Library ..
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
//import com.github.amlcurran.showcaseview.sample.animations.AnimationSampleActivity;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import common.Utilities;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.promogird.funkynetsoftware.BuildConfig;
import com.promogird.funkynetsoftware.R; ;import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * created by Paul Gallini, 2016
 *
 * This activity is the main activity for the Promo Grid app.
 */
public class MainActivity extends AppCompatActivity implements OnShowcaseEventListener {
    private Toolbar toolbar;
    private UserOperations userOperations;
    static public int candidateIndex = 0;
    static public boolean displayTutorialMain = true;
    static public boolean displayTutorialAdd = true;
    static public boolean displayTutorialEval = true;
    static public boolean displayTutorialRpt = true;
    private Menu menu;
    private Tracker mTracker;  // used for Google Analytics

    static final private int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 876;
    static final private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 877;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // for the showcase (tutorial) screen:
    ShowcaseView sv;
    ShowcaseView sv2;
    ShowcaseView sv3;
    ShowcaseView sv4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nine_box_main);

        // initialize preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // attach the layout to the toolbar object and then set the toolbar as the ActionBar ...
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // run init stuff
        inititateApp();

        // this may be needed to allow us to send email
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Obtain the shared Tracker instance.
        common.AnalyticsApplication application = (common.AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        sendScreenImageName(); // send tag to Google Analytics


        // start with running the Tutorial - if that option is selected
        if( getShowTutorial_Main() ) {
            runTutorial();
        }

        findViewById(R.id.button_add_people).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CandidatesListActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.button_set_questions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Evaluation.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.button_see_results).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ReportActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.exit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.rate_app_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rateApp();
            }
        });

    }

    private void runTutorial() {
        // set-up Layout Parameters for the tutorial
        final RelativeLayout.LayoutParams lpsBtn = getLayoutParmsBtn();
        ViewTarget target = new ViewTarget(R.id.button_add_people, this) {
            @Override
            public Point getPoint() {
                return Utilities.getPointTarget(findViewById(R.id.button_add_people), 6);
            }
        };

        // Create an OnClickListener to use with Tutorial and to display the next page ...
        View.OnClickListener tutBtnListener = new View.OnClickListener() {
            public void onClick(View v) {
                ViewTarget target2 = new ViewTarget(R.id.button_set_questions, MainActivity.this) {
                    @Override
                    public Point getPoint() {
//                        return getPointTarget(R.id.button_set_questions,6);
                        return Utilities.getPointTarget(findViewById(R.id.button_set_questions), 6);
                    }
                };

                // hide the previous view
                sv.hide();
                // Create an OnClickListener to use with Tutorial and to display the next page ...
                View.OnClickListener tutBtnListener3 = new View.OnClickListener() {
                    public void onClick(View v) {
                        ViewTarget target3 = new ViewTarget(R.id.button_see_results, MainActivity.this) {
                            @Override
                            public Point getPoint() {
//                                return getPointTarget(R.id.button_see_results,6);
                                return Utilities.getPointTarget(findViewById(R.id.button_see_results), 6 );
                            }
                        };

                        // hide the previous view
                        sv2.hide();
                        // Create an OnClickListener to use with Tutorial and to display the next page ...
                        View.OnClickListener tutBtnListener4 = new View.OnClickListener() {
                            public void onClick(View v) {
                                ViewTarget target4 = new ViewTarget(R.id.tool_bar, MainActivity.this) {
                                    @Override
                                    public Point getPoint() {
//                                        return getPointTarget(R.id.tool_bar, 1);
                                        return Utilities.getPointTarget(findViewById(R.id.tool_bar), 1 );
                                    }
                                };
                                // hide the previous view
                                sv3.hide();
                                // build and display the next view in the tutorial
                                sv4 = buildTutorialView(target4, R.string.showcase_message4, null);
                                // change button text for last
                                sv4.setButtonText(getResources().getString(R.string.showcase_btn_last));
                                sv4.setButtonPosition(lpsBtn);

                            }
                        };
                        // build and display the next view in the tutorial
                        sv3 = buildTutorialView(target3, R.string.showcase_message3, tutBtnListener4);
                        sv3.setButtonPosition(lpsBtn);

                        // forcing text to be above the target because otherwise it goes to the left and is squished
                        sv3.forceTextPosition(ShowcaseView.ABOVE_SHOWCASE);
                    }
                };
                // build and display the next view in the tutorial
                sv2 = buildTutorialView(target2, R.string.showcase_message2, tutBtnListener3);
                sv2.setButtonPosition(lpsBtn);
                // forcing text to be below the target because otherwise it goes to the left and is squished
                sv2.forceTextPosition(ShowcaseView.BELOW_SHOWCASE);
            }
        };
        // instantiate a new view for the the tutorial ...
        sv = buildTutorialView(target, R.string.showcase_message1, tutBtnListener);
        sv.setButtonPosition(lpsBtn);
//        sv.setLayoutParams(layoutParams);   // not working
        MainActivity.displayTutorialMain = false;
        SharedPreferences settings = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        Utilities.evalTutorialToggles(editor);
    }
    private ShowcaseView buildTutorialView(ViewTarget target, int tutorialText, View.OnClickListener tutBtnListener) {
        return new ShowcaseView.Builder(MainActivity.this)
                .withHoloShowcase()    // other options:  withHoloShowcase, withNewStyleShowcase, withMaterialShowcase,
                .setTarget(target)
                .setContentTitle(R.string.showcase_main_title)
                .setContentText(tutorialText)
                .setStyle(R.style.CustomShowcaseTheme)
                .setShowcaseEventListener(MainActivity.this)
                .replaceEndButton(R.layout.view_custom_button)
                .setOnClickListener(tutBtnListener)
                .build();
    }

    private RelativeLayout.LayoutParams getLayoutParmsBtn() {
        // set-up Layout parameters for the Tutorial
        //   Some more ideas on targets:
        //        http://stackoverflow.com/questions/33379121/using-showcaseview-to-target-action-bar-menu-item
        //
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

//        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        int margin = ((Number) (getResources().getDisplayMetrics().density * 6)).intValue();
        lps.setMargins(margin, margin, margin, margin);
        return lps;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;

        String title_on = getResources().getString(R.string.title_toggle_tutorial_on);
        String title_off = getResources().getString(R.string.title_toggle_tutorial_off);
        MenuItem tutMenuItem = menu.findItem(R.id.toggle_tutorial);
        if (getShowTutorial_All()) {
            tutMenuItem.setTitle(title_off);
        } else {
            tutMenuItem.setTitle(title_on);
        }
        return true;
    }

    // use on onPrepareMenuOptions to dynamicaly change the menu items (because onCreate only gets called once)
    @Override
    public boolean onPrepareOptionsMenu( Menu menu ){
        this.menu = menu;

        String title_on = getResources().getString(R.string.title_toggle_tutorial_on);
        String title_off = getResources().getString(R.string.title_toggle_tutorial_off);

        MenuItem tutMenuItem = menu.findItem(R.id.toggle_tutorial);
        if (getShowTutorial_All()) {
            tutMenuItem.setTitle(title_off);
        } else {
            tutMenuItem.setTitle(title_on);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_configure_questions:
                // if this is the Free version of the app, prompt for upgrade

                try {
                    System.out.println( "  BuildConfig.FLAVOR = ");
                    System.out.println( BuildConfig.FLAVOR);

                    // If they click OK, take them to the App Store to buy the Pro version of the app
                    if(BuildConfig.FLAVOR == "free") {
                        // If this is the Free version of the app - show the Upgrade Now dialog
                        showFeatureNotAvailableDialog( this );
                    } else {
                        Intent intent = new Intent(this, QuestionsListActivity.class);
                        this.startActivity(intent);
                    }

                } catch (ActivityNotFoundException ignored) {
                }
                return true;
            case R.id.toggle_tutorial:
                try {
                    showTutorialDialog(MainActivity.this);
                } catch (ActivityNotFoundException ignored) {
                }
                return true;
            case R.id.export_db:
                verifyStoragePermissionsForExport(this);
                return true;
            case R.id.import_db:
                verifyStoragePermissionsForImport(this);
                return true;
            // Decided not to add an About screen - but may add it later  - need to uncomment-out the activity from the Manifest
            // OK - well, decided to display the version using Toast for now.
            case R.id.display_version:
                displayVersionName();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO find way to centralize this.  Can't simply add it to Utilites (can't call non-static method from static context)
    private void showFeatureNotAvailableDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.ic_pg_icon);
        builder.setTitle(getString(R.string.feature_not_available_title));
        builder.setMessage(getString(R.string.feature_not_available_message));

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            Intent rateIntent = upgradeIntentForUrl("market://details");
                            startActivity(rateIntent);
                        } catch (ActivityNotFoundException e) {
                            Intent rateIntent = upgradeIntentForUrl("https://play.google.com/store/apps/details");
                            startActivity(rateIntent);
                        }
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Intent upgradeIntentForUrl(String url)
    {
        String targetPackageName = getResources().getString(R.string.package_name_pro);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, targetPackageName)));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21)
        {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        }
        else
        {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    private void displayVersionName() {
        Toast.makeText(this, "App Version:: " + getVersionInfo() , Toast.LENGTH_LONG).show();
    }

    public String getVersionInfo() {
        String strVersion = " ";

        PackageInfo packageInfo;
        try {
            packageInfo = getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(
                            getApplicationContext().getPackageName(),
                            0
                    );

            strVersion += packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            strVersion += "Unknown";
        }

        return strVersion;
    }

    private void showTutorialDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.confirm_tutorial_toggle_title));
        builder.setIcon(R.drawable.ic_pg_icon);
        if (getShowTutorial_All()) {
            builder.setMessage(getString(R.string.confirm_tutorial_toggle_message_off));

        } else {
            builder.setMessage(getString(R.string.confirm_tutorial_toggle_message_on));

        }

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedpreferences;
                        String title_on = getResources().getString(R.string.title_toggle_tutorial_on);
                        String title_off = getResources().getString(R.string.title_toggle_tutorial_off);

                        sharedpreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();

                        if (getShowTutorial_All()) {
                            editor.putBoolean("pref_sync", false);
                            editor.apply();
                            editor.commit();
                            MenuItem toggleMenuItem = menu.findItem(R.id.toggle_tutorial);
                            toggleMenuItem.setTitle(title_off);


                        } else {
                            editor.putBoolean("pref_sync", true);
                            editor.apply();
                            editor.commit();

                            // set all of the indivudal toggles back on as well ...
                            displayTutorialMain = true;
                            displayTutorialAdd = true;
                            displayTutorialEval = true;
                            displayTutorialRpt = true;

                            MenuItem toggleMenuItem = menu.findItem(R.id.toggle_tutorial);
                            toggleMenuItem.setTitle(title_on);
                            }
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        AlertDialog dialog = builder.create();

        // we're using onDismissListener here to determine if the user just turned on the tutorial.
        //  if they did, then we need to display it after the confirmation dialog is dismissed.
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if( getShowTutorial_Main() ) {
                    runTutorial();
                }
            }
        });
        // display dialog
        dialog.show();
    }

    public boolean getShowTutorial_All() {
        // returns value for overall preference on whether to show Tutorial or not
        SharedPreferences settings = getSharedPreferences("preferences", Context.MODE_PRIVATE);;
        Boolean showTutorial = settings.getBoolean("pref_sync", true);
        return showTutorial;
    }

    private boolean getShowTutorial_Main() {
        // returns value for whether to show tutorial for Main screen or not
        Boolean returnBool = false;
        SharedPreferences settings = getSharedPreferences("preferences", Context.MODE_PRIVATE);;
        Boolean showTutorial = settings.getBoolean("pref_sync", true);
        if(showTutorial & displayTutorialMain) { returnBool = true; }
        return returnBool;
    }

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {
    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {
    }

    @Override
    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {
    }

    // used for managing Preferences
    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    //we need a handler for when the secondary activity (add new candidate) finishes it's work
    //and returns control to this activity...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent != null) {
            Bundle extras = intent.getExtras();

            if (resultCode == RESULT_CANCELED) {
                System.out.println(" Evaluation was Cancelled");
            }
        }
        if(getShowTutorial_Main()){
            runTutorial();
        }
    }

    /**
     * Record a screen view hit for the this activity
     */
    private void sendScreenImageName() {
        String name = getResources().getString(R.string.anal_tag_main);

        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    static public int getCurrentCandidate() {
        return candidateIndex;
    }

    static public void setCurrentCandidate(int newIndex) {
        candidateIndex = newIndex;
    }

    static public void incrementCurrentCandidate() {
        candidateIndex++;
    }

    private void inititateApp() {
        // set-up Evaulations operations ...
        userOperations = new UserOperations(this);
        userOperations.open();
        long resp = userOperations.getUserID(1);

        if (resp == -1) {

            // save basic user record
            userOperations.addUser(1, "unkonwn user", " ");
        }

    }
    /*
    * Start with rating the app
    * Determine if the Play Store is installed on the device
    *
    * */
    public void rateApp()
    {
        try
        {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        }
        catch (ActivityNotFoundException e)
        {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21)
        {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        }
        else
        {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }


    public void export_DB(Context context) throws IOException {
        // set-up pointer to current DB
        String packageName = context.getApplicationContext().getPackageName();
        final String inFileName = "/data/data/" + packageName + "/databases/" + DatabaseOpenHelper.DATABASE_NAME;
        // set-up pointer to back-up DB
        File dbFile = new File(inFileName);
        FileInputStream fis = new FileInputStream(dbFile);
        String date = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
        String time = new SimpleDateFormat("HHmmss").format(new Date());

        String trimmedAppName = getResources().getString(R.string.appname).replaceAll("\\s+","");
        File outDirName = getDBStorageDir(trimmedAppName);
        String outFileName = outDirName + "/" + "promogrid" + "_" + date + ".db";

        // check to see if file already exists
        File file = new File(outFileName);
        if(file.exists()) {
            // if the file already exists, add the time to the end to ensure we don't overwrite
            outFileName = outDirName + "/" + "promogrid" + "_" + date + "_" + time + ".db";
            File file2 = new File(outFileName);
            if(file2.exists()) {
                Toast.makeText(context, "Error - File already exists!", Toast.LENGTH_LONG).show();
            }
        }
            // Open the empty db as the output stream
        OutputStream output = new FileOutputStream(outFileName);

        // Transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }
        // Close the streams
        output.flush();
        output.close();
        fis.close();
        Toast.makeText(context, "Data exported to: " + outFileName, Toast.LENGTH_LONG).show();
        // TODO Remove
        System.out.println("Database copied to: " + outDirName);
    }

    public void import_DB(Context context, String inFileName) throws IOException {
        // set-up pointer to current DB
        String packageName = context.getApplicationContext().getPackageName();
        final String outFileName = "/data/data/" + packageName + "/databases/" + DatabaseOpenHelper.DATABASE_NAME;
        // set-up pointer to back-up DB
        File dbFile = new File(inFileName);
        FileInputStream fis = new FileInputStream(dbFile);
        // Open the empty db as the output stream
        OutputStream output = new FileOutputStream(outFileName);
        // Transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }
        // Close the streams
        output.flush();
        output.close();
        fis.close();
        Toast.makeText(context, "Data imported from " + inFileName, Toast.LENGTH_LONG).show();
        // TODO Remove
        System.out.println("Database copied from: " + inFileName);

    }

    public File getDBStorageDir(String dirName) {

        // Get the directory for the user's public pictures directory.
        File directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), dirName);

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                System.out.println("Failed to create output File.");
                Toast.makeText(this, "Failed to create output directory.", Toast.LENGTH_LONG).show();
            }
        }
        return directory;
    }

    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public void verifyStoragePermissionsForExport(Activity activity) {
        // Check if we have write permission in order to do the Database export
        //   actual call to exportDB is from the call-back method onRequestPermissionsResult

        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            try {
                export_DB(activity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public void verifyStoragePermissionsForImport(Activity activity) {
        // Check if we have write permission in order to do the Database export
        //   actual call to importDB is from the call-back method onRequestPermissionsResult

        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            promptForImportFile(MainActivity.this);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        export_DB(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // permission denied!
                    Toast.makeText(this, R.string.cannot_export_permissions_text, Toast.LENGTH_LONG).show();
                }
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    promptForImportFile(MainActivity.this);
                } else {
                    // permission denied!
                    Toast.makeText(this, R.string.cannot_import_permissions_text, Toast.LENGTH_LONG).show();
                }
        }
    }
    public  ArrayAdapter<String> locateImportDB(String dirName) {
        // TODO consider moving this to Utility Class
        File f = null;
        File[] paths;
        int dirStart = 0;
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
        try {

            //
            f = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), dirName);

            FileFilter filter = new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile();
                }
            };
            // returns pathnames for files and directory
            paths = f.listFiles(filter);

            if (paths == null) {
                Toast.makeText(this, "Cannot find any importable data sources.", Toast.LENGTH_LONG).show();
                System.out.println("Cannot find any importable data sources.");

            } else {
                // for each pathname in pathname array
                for (File path : paths) {
                    dirStart = path.toString().indexOf("Documents");
                    arrayAdapter.add(path.toString().substring(dirStart));
                    // prints file and directory paths
                    System.out.println(path);
                }
            }
        } catch(Exception e) {
            // if any error occurs
            e.printStackTrace();
        }
        return arrayAdapter;
    }
    private void promptForImportFile(Context context) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.ic_pg_icon);
        builderSingle.setTitle(R.string.import_prompt_title);

        String trimmedAppName = getResources().getString(R.string.appname).replaceAll("\\s+","");
        final ArrayAdapter<String> arrayAdapter = locateImportDB(trimmedAppName);

        if (arrayAdapter.isEmpty()) {
            // add diaglog to say No files found
            dialogNoImportFiles();
        }  else {
            builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String strName = arrayAdapter.getItem(which);
                    AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
                    builderInner.setMessage(strName);
                    builderInner.setTitle(R.string.overwrite_warning);
                    builderInner.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                String importFile = Environment.getExternalStorageDirectory() + "/" + strName;
                                import_DB(MainActivity.this, importFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    builderInner.show();
                }
            });
            builderSingle.show();
        }
    }

    private void dialogNoImportFiles() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.ic_pg_icon);
        builderSingle.setTitle(R.string.no_import_file_title);
        builderSingle.setMessage(R.string.no_import_file_message);

        builderSingle.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.show();

    }
}
