/*

 */

//package com.ninebox.nineboxapp;
package nineBoxEvaluation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.promogrid.funkynetsoftware.R;

import java.util.ArrayList;

import common.Utilities;
import nineBoxCandidates.CandidateOperations;
import nineBoxCandidates.Candidates;
import nineBoxMain.MainActivity;
import nineBoxQuestions.Questions;
import nineBoxQuestions.QuestionsOperations;

/**
 * Created by Paul Gallini, 2017
 * <p>
 * This activity lists out the existing candidates for the purpose of Evaluation
 * (copied from CandidateListActivity)
 */
public class EvalCandidatesListActivity extends AppCompatActivity implements OnShowcaseEventListener {
//    private final int CANDIDATESENTRY_ACTIVITY_REQUEST_CODE = 0;
//    private final int CANDIDATESUPDATE_ACTIVITY_REQUEST_CODE = 0;

    public ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
    private CandidateOperations candidateOperations;
    private ListView mainListView;
    private ArrayAdapter<String> mainArrayAdapter;
    Context context = EvalCandidatesListActivity.this;
    private ArrayList<String> displayList;
    private Toolbar toolbar;
    ShowcaseView sv;   // for the showcase (tutorial) screen:
    ShowcaseView sv2;
    private Tracker mTracker;  // used for Google Analytics
    private int percentageComplete = 0; //used to calculate percentage complete icon
    private int questionCnt = 0;
    private EvaluationOperations evaluationOperations;
    public ArrayList<Questions> questionsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eval_cand_list);

        // set-up the operations class for Candidates ...
        candidateOperations = new CandidateOperations(this);
        candidateOperations.open();

        // set-up Evaulations operations ...
        evaluationOperations = new EvaluationOperations(this);
        evaluationOperations.open();

        // grab count of questions to help calc progress icon
        QuestionsOperations questionsOperations = new QuestionsOperations(this);
        questionsOperations.open();
        questionCnt = questionsOperations.getQuestionsCnt();

        // attach the layout to the toolbar object and then set the toolbar as the ActionBar ...
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // create a list of candidates from what's in the database ...
        candidatesList = candidateOperations.getAllCandidates();
        // make an array of just the Names for the purpose of displaying ...
        displayList = buildDisplayList(candidatesList);

        // find the ListView so we can work with it ...
        mainListView = (ListView) findViewById(R.id.eval_cand_list);

        // Obtain the shared Tracker instance.
        common.AnalyticsApplication application = (common.AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        sendScreenImageName(); // send tag to Google Analytics

        // if we don't have any candidates, then we can't go on
        if (candidatesList.size() < 1) {
            showNoCandidatesDialog();
        } else {
            // Make Sure we have Questions
            questionsList = questionsOperations.getAllQuestions();
            if (questionsList.size() < 1) {
                // If there are NO questions, then show a dialog to explain the situation and quit
                showNoQuestionsDialog();
            } else if (getShowTutorial_Eval()) {
                displayTutorialEval();
                // Now that it's been displayed, lets turn it off
                MainActivity.displayTutorialAdd = false;
            }
            ;

            mainArrayAdapter = new ArrayAdapter<String>(this, R.layout.eval_cand_list_item, R.id.candidate, displayList) {
                @Override
                public View getView(final int position, View convertView, ViewGroup parent) {

                    if (convertView == null) {
                        convertView = getLayoutInflater().inflate(R.layout.eval_cand_list_item, parent, false);
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

                    // set the progress icon for current Candidate
                    percentageComplete = calcPercentageComplete(questionCnt, candidatesList.get(position).getCandidateID());

                    ImageView currentProgIcon = (ImageView) convertView.findViewById(R.id.progress_icon);
                    if (percentageComplete == 0) {
                        currentProgIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.icon_progress_zero, null));
                    } else if (percentageComplete < 30) {
                        currentProgIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.icon_progress_25, null));
                    } else if (percentageComplete < 60) {
                        currentProgIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.icon_progress_50, null));
                    } else if (percentageComplete < 100) {
                        currentProgIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.icon_progress_75, null));
                    } else {
                        currentProgIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.icon_progress_100, null));
                    }

                    // Because the list item contains multiple touch targets, you should not override
                    // onListItemClick. Instead, set a click listener for each target individually.
                    convertView.findViewById(R.id.primary_target).setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    MainActivity.setCurrentCandidate(position);
                                    Intent intent = new Intent(view.getContext(), Evaluation.class);
                                    startActivity(intent);
                                }
                            });
                    // I know it's odd to have the progress icon as it's own target - but doing the same thing as the primary target ...
                    //       but it was the easy thing to do - plus I may want it to do something else in the future?
                    convertView.findViewById(R.id.progress_icon).setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
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

        }
        ;

    }

    private void showNoCandidatesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EvalCandidatesListActivity.this);
        builder.setIcon(R.drawable.ic_pg_icon);
        builder.setTitle(getString(R.string.dialog_no_candidates_title));
        builder.setMessage(getString(R.string.dialog_no_candidates_message));

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        kill_activity();
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        kill_activity();
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    // TODO - if you don't drop the one in Eval - consider combining somehow
    private void showNoQuestionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EvalCandidatesListActivity.this);
        builder.setIcon(R.drawable.ic_pg_icon);
        builder.setTitle(getString(R.string.dialog_no_questions_title));
        builder.setMessage(getString(R.string.dialog_no_questions_message));

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        kill_activity();
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        kill_activity();
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    void kill_activity() {
        finish();
    }

    private int calcPercentageComplete(int questionCnt, long candidateID) {
        int returnResult = 0;
        double tmpResult = 0.0;

        int responseCnt = evaluationOperations.getResponseCnt(candidateID);
        tmpResult = (double) responseCnt / (double) questionCnt;
        returnResult = (int) Math.round(tmpResult * 100);

        return (returnResult);
    }

    private boolean getShowTutorial_Eval() {
        // returns value for whether to show tutorial for Add Candidates screen or not
        Boolean returnBool = false;
        SharedPreferences settings = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        ;
        Boolean showTutorial = settings.getBoolean("pref_sync", true);
        if (showTutorial & MainActivity.displayTutorialEval) {
            returnBool = true;
        }
        return returnBool;
    }

    private void displayTutorialEval() {
        // set-up Layout Parameters for the tutorial
        final RelativeLayout.LayoutParams lps = getLayoutParms();

        // locate the target for the hint
        ViewTarget target = new ViewTarget(R.id.primary_target, this) {
            @Override
            public Point getPoint() {
                return Utilities.getPointTarget(findViewById(R.id.primary_target), 6);
            }
        };

        // Create an OnClickListener to use with Tutorial and to display the next page ...
        View.OnClickListener tutBtnListener2 = new View.OnClickListener() {
            public void onClick(View v) {
                ViewTarget target2 = new ViewTarget(R.id.primary_target, EvalCandidatesListActivity.this) {
                    @Override
                    public Point getPoint() {
                        return Utilities.getPointTarget(findViewById(R.id.primary_target), 1);
                    }
                };
                // hide the previous view
                sv.hide();
                // instantiate a new view for the the tutorial ...
                sv2 = buildTutorialView(target2, R.string.showcase_eval_list_message2, null);
                sv2.setButtonText(getResources().getString(R.string.showcase_btn_last));
                sv2.setButtonPosition(lps);
            }
        };
        // instantiate a new view for the the tutorial ...
        sv = buildTutorialView(target, R.string.showcase_eval_list_message1, tutBtnListener2);
        sv.setButtonPosition(lps);
        MainActivity.displayTutorialEval = false;
        SharedPreferences settings = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        Utilities.evalTutorialToggles(editor);
    }

    // TODO see if we can combine this with others
    private ShowcaseView buildTutorialView(ViewTarget target, int tutorialText, View.OnClickListener tutBtnListener) {
        return new ShowcaseView.Builder(EvalCandidatesListActivity.this)
                .withHoloShowcase()    // other options:  withHoloShowcase, withNewStyleShowcase, withMaterialShowcase,
                .setTarget(target)
                .setContentTitle(R.string.showcase_main_title)
                .setContentText(tutorialText)
                .setStyle(R.style.CustomShowcaseTheme)
                .setShowcaseEventListener(EvalCandidatesListActivity.this)
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

    @Override
    public void onResume() {
        super.onResume();
        if(!(mainArrayAdapter == null)) {
            mainArrayAdapter.notifyDataSetChanged();
        }
    }

    private ArrayList<String> buildDisplayList(ArrayList<Candidates> candidatesList) {
        ArrayList<String> returnList = new ArrayList();
        for (int i = 0; i < candidatesList.size(); i++) {
            returnList.add(candidatesList.get(i).getCandidateName());
        }
        return returnList;
    }

    //we need a handler for when the secondary activity (add new candidate) finishes it's work
    //and returns control to this activity...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        long returnCandidateId = 0;
        super.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     * Record a screen view hit for the this activity
     */
    private void sendScreenImageName() {
        String name = getResources().getString(R.string.anal_tag_eval_list);

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
