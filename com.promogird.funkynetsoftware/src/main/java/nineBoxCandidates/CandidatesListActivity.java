/*

 */

//package com.ninebox.nineboxapp;
package nineBoxCandidates;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.promogird.funkynetsoftware.R; ;

import java.util.ArrayList;

import common.Utilities;
import nineBoxEvaluation.Evaluation;
import nineBoxMain.MainActivity;
import nineBoxQuestions.QuestionsEntryActivity;

/**
 *
 * Created by Paul Gallini, 2016
 *
 * This activity lists out the existing candidates and allows for additions, deletions, and evaluation .
 *
 */
public class CandidatesListActivity extends AppCompatActivity implements OnShowcaseEventListener {
    private final int CANDIDATESENTRY_ACTIVITY_REQUEST_CODE = 0;
    private final int CANDIDATESUPDATE_ACTIVITY_REQUEST_CODE = 0;

    public ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
    private CandidateOperations candidateOperations;
    private ListView mainListView;
    private ArrayAdapter<String> mainArrayAdapter;
    Context context = CandidatesListActivity.this;
    private ArrayList<String> displayList;
    private Toolbar toolbar;
    ShowcaseView sv;   // for the showcase (tutorial) screen:
    private Tracker mTracker;  // used for Google Analytics

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.candidates_list);

        // set-up the operations class for Candidates ...
        candidateOperations = new CandidateOperations(this);
        candidateOperations.open();

        // attach the layout to the toolbar object and then set the toolbar as the ActionBar ...
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // create a list of candidates from what's in the database ...
        candidatesList = candidateOperations.getAllCandidates();
        // make an array of just the Names for the purpose of displaying ...
        displayList = buildDisplayList( candidatesList );

        // find the ListView so we can work with it ...
        mainListView = (ListView) findViewById(R.id.candidates_list);

        // Obtain the shared Tracker instance.
        common.AnalyticsApplication application = (common.AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        sendScreenImageName(); // send tag to Google Analytics

        if(getShowTutorial_Add()) {
            displayTutorialAdd();
            // Now that it's been displayed, lets turn it off
            MainActivity.displayTutorialAdd = false;
        }
        mainArrayAdapter = new ArrayAdapter<String>(this, R.layout.candidates_list_item, R.id.candidate, displayList) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.candidates_list_item, parent, false);
                }
                View view = super.getView(position, convertView, parent);
                // display icon for current candidate ...
                String cInitials = candidatesList.get(position).getCandidateInitials();
                String cColor = candidatesList.get(position).getCandidateColor();

                ImageView currentIcon = (ImageView) convertView.findViewById(R.id.current_icon);

                //   TODO - for the imageview current_icon, make the width and height dynamic based on screen size
                currentIcon.setImageDrawable(Candidates.get_icon(this.getContext(), cColor, cInitials));

                TextView candidateText = (TextView) view.findViewById(R.id.candidate);
                candidateText.setText(candidatesList.get(position).getCandidateName());

                // Because the list item contains multiple touch targets, you should not override
                // onListItemClick. Instead, set a click listener for each target individually.
                convertView.findViewById(R.id.primary_target).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // send Update activity details on selected Candidate ..
                                Intent intent = new Intent(CandidatesListActivity.this, CandidatesUpdateActivity.class);

                                intent.putExtra("candidateId", Long.toString(candidatesList.get(position).getCandidateID()));
                                intent.putExtra("candidateName", candidatesList.get(position).getCandidateName());
                                intent.putExtra("candidateNote",candidatesList.get(position).getCandidateNotes());
                                intent.putExtra("candidateInitials", candidatesList.get(position).getCandidateInitials());
                                intent.putExtra("candidateColor", candidatesList.get(position).getCandidateColor());
                                startActivityForResult(intent, CANDIDATESUPDATE_ACTIVITY_REQUEST_CODE);
                            }
                        });
                convertView.findViewById(R.id.delete_action).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Delete icon selected - delete the current Candidate
                                showDeleteDialog(position);
                            }
                        });
                convertView.findViewById(R.id.eval_action).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // TODO figure out how to tell Evaluate to stop after its done with this candidate
                                MainActivity.setCurrentCandidate(position);
                                Intent intent = new Intent(view.getContext(), Evaluation.class);
                                startActivity(intent);
                            }
                        });
                return convertView;
            }

        };

        mainListView.setAdapter(mainArrayAdapter);
        mainArrayAdapter.notifyDataSetChanged();

        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), CandidatesEntryActivity.class);
                intent.putExtra("myKey", "sampleText");
                startActivityForResult(intent, CANDIDATESENTRY_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    private boolean getShowTutorial_Add() {
        // returns value for whether to show tutorial for Add Candidates screen or not
        Boolean returnBool = false;
        SharedPreferences settings = getSharedPreferences("preferences", Context.MODE_PRIVATE);;
        Boolean showTutorial = settings.getBoolean("pref_sync", true);
        if(showTutorial & MainActivity.displayTutorialAdd) { returnBool = true; }
        return returnBool;
    }

    private void displayTutorialAdd() {
        // set-up Layout Parameters for the tutorial
        final RelativeLayout.LayoutParams lps = getLayoutParms();
        // locate the target for the hint
        ViewTarget target = new ViewTarget(R.id.ok_button, this) {
            @Override
            public Point getPoint() {
                return Utilities.getPointTarget(findViewById(R.id.ok_button),2);
            }
        };
        // Create an OnClickListener to use with Tutorial and to display the next page ...
        View.OnClickListener tutBtnListener = new View.OnClickListener() {
            public void onClick(View v) {
                ViewTarget target = new ViewTarget(R.id.ok_button, CandidatesListActivity.this) {
                    @Override
                    public Point getPoint() {
                        return Utilities.getPointTarget(findViewById(R.id.ok_button), 1);
                    }
                };
                // hide the previous view
                sv.hide();
            }
        };
        // instantiate a new view for the the tutorial ...
        sv = buildTutorialView(target, R.string.showcase_add_message1, tutBtnListener);
        sv.setButtonText(getResources().getString(R.string.showcase_btn_last));
        sv.setButtonPosition(lps);
        MainActivity.displayTutorialAdd = false;
        SharedPreferences settings = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        Utilities.evalTutorialToggles(editor);
    }

    // TODO see if we can combine this with others
    private ShowcaseView buildTutorialView(ViewTarget target, int tutorialText, View.OnClickListener tutBtnListener) {
        return new ShowcaseView.Builder(CandidatesListActivity.this)
                .withHoloShowcase()    // other options:  withHoloShowcase, withNewStyleShowcase, withMaterialShowcase,
                .setTarget(target)
                .setContentTitle(R.string.showcase_main_title)
                .setContentText(tutorialText)
                .setStyle(R.style.CustomShowcaseTheme)
                .setShowcaseEventListener(CandidatesListActivity.this)
                .replaceEndButton(R.layout.view_custom_button)
                .setOnClickListener(tutBtnListener)
                .build();
    }

    // TODO find a way to combine this with the same method in MainActivity
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

    private void delete_candidate( int position ) {
        candidateOperations.deleteCandidate(candidatesList.get(position));
        candidatesList.remove(candidatesList.get(position));
        displayList.remove(position);
        // notify mainArrayAdapter that things have changed and a refresh is needed ...
        mainArrayAdapter.notifyDataSetChanged();
        Toast.makeText(CandidatesListActivity.this,
                R.string.candidate_delete_message,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume(){
        super.onResume();
        mainArrayAdapter.notifyDataSetChanged();
        // commenting-out for now - getting more hits than makes sense
//        sendScreenImageName(); // send tag to Google Analytics
    }

    private ArrayList<String> buildDisplayList( ArrayList<Candidates> candidatesList ) {
        ArrayList<String> returnList = new ArrayList();
        for(int i = 0; i <  candidatesList.size(); i++ ) {
            returnList.add( candidatesList.get(i).getCandidateName() );
        }
        return returnList;
    }

    //we need a handler for when the secondary activity (add new candidate) finishes it's work
    //and returns control to this activity...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        long returnCandidateId = 0;

        super.onActivityResult(requestCode, resultCode, intent);
        if (intent != null ) {
            Bundle extras = intent.getExtras();
            String returnCandidateIdString = (extras != null ? extras.getString("returnKey") : "nothing returned");
            if(returnCandidateIdString != null ) {
                returnCandidateId = Long.parseLong(returnCandidateIdString);
            };
            String returnCandidateName = (extras != null ? extras.getString("returnName") : " ");
            String returnCandidateNotes = (extras != null ? extras.getString("returnNotes") : " ");
            String returnCandidateColor = (extras != null ? extras.getString("returnColor") : " ");
            String returnCandidateInitials = (extras != null ? extras.getString("returnInitials") : " ");
            String returnMode = (extras != null ? extras.getString("returnMode") : " ");

            if(returnMode.equals("ADD")) {
                // save to database
                Candidates candidate = candidateOperations.addCandidate(returnCandidateName, returnCandidateNotes, returnCandidateColor, returnCandidateInitials);
                candidatesList.add(candidate);
                displayList.add(candidate.getCandidateName());
                Toast.makeText(CandidatesListActivity.this,
                        R.string.candidate_save_message,
                        Toast.LENGTH_LONG).show();

            } else {
                // save updated candidate to the database
                boolean returnVal = candidateOperations.updateCandidate( returnCandidateId, returnCandidateName, returnCandidateNotes, returnCandidateColor, returnCandidateInitials);

                Toast.makeText(CandidatesListActivity.this,
                                        R.string.candidate_save_message,
                                        Toast.LENGTH_LONG).show();

                // locate the question in questionList and update it ...
                for( int i = 0; i < candidatesList.size(); i++ ) {
                    if( candidatesList.get(i).getCandidateID() == returnCandidateId) {
                        candidatesList.get(i).setCandidateName(returnCandidateName);
                        candidatesList.get(i).setCandidateNotes(returnCandidateNotes);
                        candidatesList.get(i).setCandidateColor(returnCandidateColor);
                        candidatesList.get(i).setCandidateInitials(returnCandidateInitials);
                        break;
                    }
                }
            }
            // notify mainArrayAdapter that things have changed and a refresh is needed ...
            mainArrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // TODO - is this needed here AND in MainActivity?
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_configure_questions:
                try {
                    Intent intent = new Intent(this, QuestionsEntryActivity.class);
                    this.startActivity(intent);

                } catch (ActivityNotFoundException ignored) {
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showDeleteDialog(int position ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CandidatesListActivity.this);
        builder.setTitle(getString(R.string.confirm_delete_title));
        builder.setIcon(R.drawable.ic_pg_icon);
        builder.setMessage(getString(R.string.confirm_delete_message));
        boolean returnBool = false;
        final int curr_postion = position;
        Tracker mTracker;  // used for Google Analytics

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete_candidate( curr_postion );
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


        // Obtain the shared Tracker instance.
        common.AnalyticsApplication application = (common.AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        // send tag to Google Analytics
        mTracker.setScreenName("Image~" + getResources().getString(R.string.anal_tag_candidates_delete));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());


        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }
    /**
     * Record a screen view hit for the this activity
     */
    private void sendScreenImageName() {
        String name = getResources().getString(R.string.anal_tag_candidates_list);

        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
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
}
