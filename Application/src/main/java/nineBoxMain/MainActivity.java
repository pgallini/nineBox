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

//package com.ninebox.nineboxapp;
package nineBoxMain;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.RelativeLayout;

import nineBoxCandidates.CandidatesListActivity;
import nineBoxQuestions.QuestionsListActivity;
import nineBoxEvaluation.Evaluation;
import nineBoxReport.ReportActivity;
import preferences.SettingsActivity;

// for the Showcase Library ..
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
//import com.github.amlcurran.showcaseview.sample.animations.AnimationSampleActivity;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import com.ninebox.nineboxapp.R;

/**
 * This activity is the main activity for the Promo Grid app.
 */
public class MainActivity extends AppCompatActivity implements OnShowcaseEventListener {
    //    public class MainActivity extends Activity {
    private Toolbar toolbar;
    private UserOperations userOperations;
    static public int candidateIndex = 0;
    private Menu menu;

    // for the showcase (tutorial) screen:
    ShowcaseView sv;
    ShowcaseView sv2;
    ShowcaseView sv3;
    ShowcaseView sv4;

    // TODO consider adding a FunkyNet splash screen
    // https://www.bignerdranch.com/blog/splash-screens-the-right-way/
    // 
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
        if( getTutorialShown() ) {
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
    }

    private void runTutorial() {
        // todo determine best way to track this preference
        boolean tutorialShown = getTutorialShown();

        // set-up Layout Parameters for the tutorial
        final RelativeLayout.LayoutParams lps = getLayoutParms();
        // locate the target for the tutorial
        ViewTarget target = new ViewTarget(R.id.button_add_people, this) {
            @Override
            public Point getPoint() {
                return getPointTarget(R.id.button_add_people,6);
            }
        };

        // Create an OnClickListener to use with Tutorial and to display the next page ...
        View.OnClickListener tutBtnListener = new View.OnClickListener() {
            public void onClick(View v) {
                ViewTarget target2 = new ViewTarget(R.id.button_set_questions, MainActivity.this) {
                    @Override
                    public Point getPoint() {
                        return getPointTarget(R.id.button_set_questions,6);
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
                                return getPointTarget(R.id.button_see_results,6);
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
                                        return getPointTarget(R.id.tool_bar, 1);
                                    }
                                };
                                // hide the previous view
                                sv3.hide();
                                // build and display the next view in the tutorial
                                sv4 = buildTutorialView(target4, R.string.showcase_message4, null);
                                // change button text for last
                                sv4.setButtonText(getResources().getString(R.string.showcase_btn_last));
                                sv4.setButtonPosition(lps);
                            }
                        };
                        // build and display the next view in the tutorial
                        sv3 = buildTutorialView(target3, R.string.showcase_message3, tutBtnListener4);
                        sv3.setButtonPosition(lps);
                    }
                };
                // build and display the next view in the tutorial
                sv2 = buildTutorialView(target2, R.string.showcase_message2, tutBtnListener3);
                sv2.setButtonPosition(lps);
            }
        };
        // instantiate a new view for the the tutorial ...
        sv = buildTutorialView(target, R.string.showcase_message1, tutBtnListener);
        sv.setButtonPosition(lps);

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

    private Point getPointTarget(int buttonId, int x_divisor) {
        // given a resource id, return a point to use for the tutorial
        // note that this is set-up to alighn to the right
        // change the / 6 to / 2 to center it
        View targetView = findViewById(buttonId);

        int[] location = new int[2];
        targetView.getLocationInWindow(location);
        int x = location[0] + targetView.getWidth() / x_divisor;
        int y = location[1] + targetView.getHeight() / 2;
        return new Point(x, y);
    }

    private RelativeLayout.LayoutParams getLayoutParms() {
        // set-up Layout parameters for the Tutorial
        //   Some more ideas on targets:
        //        http://stackoverflow.com/questions/33379121/using-showcaseview-to-target-action-bar-menu-item
        //
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
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
        System.out.println(getTutorialShown());
        MenuItem tutMenuItem = menu.findItem(R.id.toggle_tutorial);
        if (getTutorialShown()) {
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

    private void showTutorialDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.confirm_tutorial_toggle_title));
        if (getTutorialShown()) {
            // TODO Remove
            System.out.println("switching dialog text to say OFF  ");
            builder.setMessage(getString(R.string.confirm_tutorial_toggle_message_off));

        } else {
            // TODO Remove
            System.out.println("switching dialog text to say ON  ");
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

                        if (getTutorialShown()) {
                            editor.putBoolean("pref_sync", false);
                            editor.apply();
                            editor.commit();
                            // TODO Remove
                            System.out.println("Turning OFF  ");
                            MenuItem toggleMenuItem = menu.findItem(R.id.toggle_tutorial);
                            toggleMenuItem.setTitle(title_off);
                            // TODO Remove
                            System.out.println("switching title to say ON  ");

                        } else {
                            editor.putBoolean("pref_sync", true);
                            editor.apply();
                            editor.commit();
                            // TODO Remove
                            System.out.println("Turning ON ");

                            MenuItem toggleMenuItem = menu.findItem(R.id.toggle_tutorial);
                            toggleMenuItem.setTitle(title_on);
                            // TODO Remove
                            System.out.println("switching title to say ON  ");

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
        // display dialog
        dialog.show();
    }

    private boolean getTutorialShown() {
        SharedPreferences settings = getSharedPreferences("preferences", Context.MODE_PRIVATE);;
//        return settings.getBoolean("pref_sync", true);
        Boolean showTutorial = settings.getBoolean("pref_sync", true);
        // TODO Remove
        System.out.println("########showTutorial =  ");
        System.out.println(showTutorial);
        return showTutorial;
    }

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            listView.setAlpha(1f);
//        }
//        buttonBlocked.setText(R.string.button_show);
        //buttonBlocked.setEnabled(false);
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
        if(getTutorialShown()){
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

}
