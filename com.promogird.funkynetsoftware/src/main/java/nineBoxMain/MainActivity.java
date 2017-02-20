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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import nineBoxCandidates.CandidatesListActivity;
import nineBoxQuestions.QuestionsListActivity;
import nineBoxEvaluation.Evaluation;
import nineBoxReport.ReportActivity;

// for the Showcase Library ..
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
//import com.github.amlcurran.showcaseview.sample.animations.AnimationSampleActivity;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import common.common.Utilities;
import com.promogird.funkynetsoftware.R; ;

/**
 *
 * created by Paul Gallini, 2016
 *
 * This activity is the main activity for the Promo Grid app.
 */
public class MainActivity extends AppCompatActivity implements OnShowcaseEventListener {
    //    public class MainActivity extends Activity {
    private Toolbar toolbar;
    private UserOperations userOperations;
    static public int candidateIndex = 0;
    static public boolean displayTutorialMain = true;
    static public boolean displayTutorialAdd = true;
    static public boolean displayTutorialEval = true;
    static public boolean displayTutorialRpt = true;

    private Menu menu;

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
                try {
                    Intent intent = new Intent(this, QuestionsListActivity.class);
                    this.startActivity(intent);

                } catch (ActivityNotFoundException ignored) {
                }
                return true;
            case R.id.toggle_tutorial:
                try {
                    showTutorialDialog(MainActivity.this);
                } catch (ActivityNotFoundException ignored) {
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyBrowser extends WebViewClient {
        @Override
        // TODO - decide if we really want to allow the clicking on a link within the displayed page
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private void showTutorialDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.confirm_tutorial_toggle_title));
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
        // TODO get this to work - display tutorial if the user just turned it on
        if(getShowTutorial_Main()){
            runTutorial();
        }
    }

    static public int getCurrentCandidate() {
        return candidateIndex;
    }

    static public void setCurrentCandidate(int newIndex) {
        candidateIndex = newIndex;
    }

    static public void incrementCurrentCandidate() {
        // TODO consider adding test to ensure we don't incrment it past candidatesList.size()
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
}
