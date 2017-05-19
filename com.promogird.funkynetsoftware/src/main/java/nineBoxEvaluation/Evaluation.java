package nineBoxEvaluation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.promogird.funkynetsoftware.R; ;

import java.util.ArrayList;

import common.Utilities;
import nineBoxCandidates.CandidateOperations;
import nineBoxCandidates.Candidates;
import nineBoxMain.MainActivity;
import nineBoxQuestions.Questions;
import nineBoxQuestions.QuestionsOperations;

/**
 * Created by Paul Gallini on 4/17/16.
 *
 * The slider drawables are generated from here:  http://android-holo-colors.com/
 */
public class Evaluation extends AppCompatActivity implements OnShowcaseEventListener {
    TextView cName;
    private final int EVALUATION_ACTIVITY_REQUEST_CODE = 0;
    public ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
    public ArrayList<Questions> questionsList = new ArrayList<>();
    public int currentQuestionNo = 1;
    public int maxQuestionNo = 0;
    private Toolbar toolbar;
    private int currentResponse = 0;
    // for the showcase (hint) screen:
    ShowcaseView sv;
    ShowcaseView sv2;
    private Tracker mTracker;  // used for Google Analytics

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int returnCode = 0;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.evaluation_entry);

        CandidateOperations candidateOperations;
        // set-up the operations class for Candidates ...
        candidateOperations = new CandidateOperations(this);
        candidateOperations.open();
        // create a list of candidates from what's in the database ...
        candidatesList = candidateOperations.getAllCandidates();

        // attach the layout to the toolbar object and then set the toolbar as the ActionBar ...
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // set-up questions ...
        QuestionsOperations questionsOperations = new QuestionsOperations(this);
        questionsOperations.open();
        questionsList = questionsOperations.getAllQuestions();
        maxQuestionNo = questionsList.size();

        if(maxQuestionNo < 1 ) {
            // If there are NO questions, then show a dialog to explain the situation and quit
            showNoQuestionsDialog();
        }

        final TextView nextQuestionButtonView = (TextView) findViewById(R.id.next_question_button);
        // Obtain the shared Tracker instance.
        common.AnalyticsApplication application = (common.AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        sendScreenImageName(); // send tag to Google Analytics

        SeekBar seek = (SeekBar) findViewById(R.id.responseSeekBar);
        seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentResponse = progress;
            }

            @Override
            public void onStartTrackingTouch(final SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
            }
        });

        findViewById(R.id.next_question_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save the response
                int candidateIndex = MainActivity.getCurrentCandidate();

                if (candidateIndex < candidatesList.size()) {

                    if ((currentQuestionNo - 1) <= questionsList.size()) {
                        saveResponse(candidatesList.get(candidateIndex).getCandidateID(), questionsList.get((currentQuestionNo - 1)).getQuestionID(), currentResponse);
                        if (currentQuestionNo < maxQuestionNo) {
                            currentQuestionNo++;
                            if (currentQuestionNo == maxQuestionNo) {
                                // if we are now on the last question, change the text of the button to Next Candidate or Done
                                if (candidateIndex == (candidatesList.size() - 1)) {
                                    nextQuestionButtonView.setText(R.string.done_button);
                                } else {
                                    nextQuestionButtonView.setText(R.string.next_question_button_alt);
                                }
                            }
                            onResume();
                        } else {
                            // increment the index for the next candidate ...
                            MainActivity.incrementCurrentCandidate();
                            // reset the Questions
                            currentQuestionNo = 1;
                            if (candidateIndex < candidatesList.size()) {
                                // reset label of the Next btn
                                nextQuestionButtonView.setText(R.string.next_question_button);
                                onResume();
                            } else {
                                // unless we are on the last candidate
                                MainActivity.setCurrentCandidate(0);
                                finish();
                            }
                        }
                    }
                } else {
                    // if we've looped past the last candidate, reset the index to 0 and finish
                    MainActivity.setCurrentCandidate(0);
                    finish();
                }
            }
        });
        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    //create a new intent so we can return Candidate Data ...
                                                                    Intent intent = new Intent();
                                                                    setResult(RESULT_CANCELED, intent);
                                                                    finish();
                                                                }
                                                            }
        );
    }

    private void saveResponse(long candidate_id, long question_id, int response) {
        boolean wasSuccessful = false;
        EvaluationOperations evaluationOperations = new EvaluationOperations(this);
        evaluationOperations.open();
        long foundRespID = evaluationOperations.getResponseID(candidate_id, question_id);
        if (foundRespID == -1) {
            wasSuccessful = evaluationOperations.addResponse(candidate_id, question_id, response);
        } else {
            wasSuccessful = evaluationOperations.updateResponse(foundRespID, candidate_id, question_id, response);
        }
    }

    public void onResume() {
        super.onResume();
        TextView displayName = (TextView) findViewById(R.id.evalCandidateName);
        TextView currQuestionNoView = (TextView) findViewById(R.id.curr_question_no);
        TextView maxQuestionNoView = (TextView) findViewById(R.id.max_question_no);
        TextView quesitonTextView = (TextView) findViewById(R.id.question_text);

        int currResponse = 1;
        long candidateID = -1;
        long questionID = -1;
        int candidateIndex = MainActivity.getCurrentCandidate();

        currQuestionNoView.setText(Integer.toString(currentQuestionNo));
        maxQuestionNoView.setText(Integer.toString(maxQuestionNo));

        // commenting-out for now - otherwise we get a count for every question.  Would rather have once per candidate
//        sendScreenImageName(); // send tag to Google Analytics

        if (candidateIndex < candidatesList.size()) {
            displayName.setText(candidatesList.get(candidateIndex).getCandidateName());
            candidateID = candidatesList.get(candidateIndex).getCandidateID();

            if (currentQuestionNo <= questionsList.size()) {
                quesitonTextView.setText(questionsList.get((currentQuestionNo - 1)).getQuestionText());
                questionID = questionsList.get((currentQuestionNo - 1)).getQuestionID();
            }

            // See if there is already a response for this combo of candidate and question ..
            if (candidateID != -1 && questionID != -1) {
                EvaluationOperations evaluationOperations = new EvaluationOperations(this);
                evaluationOperations.open();

                if(getShowTutorial_Eval()) {
                    displayHint();
                    MainActivity.displayTutorialEval = false;
                }

                long foundRespID = evaluationOperations.getResponseID(candidateID, questionID);
                if (foundRespID != -1) {
                    // if there is a response in the DB, then set the seekbar to that value ...
                    currResponse = evaluationOperations.getResponseValue(candidateID, questionID);
                    SeekBar seek = (SeekBar) findViewById(R.id.responseSeekBar);
                    seek.setProgress(currResponse);
                }
            }
        } else {
            MainActivity.setCurrentCandidate(0);

            // if there aren't any candidates, then display a dialog to that effect and get out
            if(candidatesList.size() < 1 ) {
                showNoCandidatesDialog();
            } else {
                finish();
            }
        }
    }

    private boolean getShowTutorial_Eval() {
        // returns value for whether to show tutorial for Main screen or not
        Boolean returnBool = false;
        SharedPreferences settings = getSharedPreferences("preferences", Context.MODE_PRIVATE);;
        Boolean showTutorial = settings.getBoolean("pref_sync", true);
        if(showTutorial & MainActivity.displayTutorialEval) { returnBool = true; }
        return returnBool;
    }

    public void CancelSave(View view) {
        this.kill_activity();
    }

    void kill_activity() {
        finish();
    }

    // TODO consider removing this since we added it to EvalCandidatesListActivity
    private void showNoQuestionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Evaluation.this);
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

    private void showNoCandidatesDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_no_candidates_title));
        builder.setIcon(R.drawable.ic_pg_icon);
        builder.setMessage(getString(R.string.dialog_no_candidates_message));

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        kill_activity();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        // display dialog
        dialog.show();
    }

    private void displayHint() {
        // set-up Layout Parameters for the tutorial
        final RelativeLayout.LayoutParams lps = getLayoutParms();

        // locate the target for the hint
        ViewTarget target = new ViewTarget(R.id.responseSeekBar, this) {
            @Override
            public Point getPoint() {
                return Utilities.getPointTarget(findViewById(R.id.responseSeekBar), 6);
            }
        };

        // Create an OnClickListener to use with Tutorial and to display the next page ...
        View.OnClickListener tutBtnListener2 = new View.OnClickListener() {
            public void onClick(View v) {
                ViewTarget target2 = new ViewTarget(R.id.next_question_button, Evaluation.this) {
                    @Override
                    public Point getPoint() {
                        return Utilities.getPointTarget(findViewById(R.id.next_question_button), 2);
                    }
                };
                // hide the previous view
                sv.hide();
                // instantiate a new view for the the tutorial ...
                sv2 = buildTutorialView(target2, R.string.showcase_eval_message2, null);
                sv2.setButtonText(getResources().getString(R.string.showcase_btn_last));
                sv2.setButtonPosition(lps);
            }
        };
        // instantiate a new view for the the tutorial ...
        sv = buildTutorialView(target, R.string.showcase_eval_message1, tutBtnListener2);
        sv.setButtonPosition(lps);
        MainActivity.displayTutorialEval = false;
        SharedPreferences settings = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        Utilities.evalTutorialToggles(editor);
    }

    // TODO see if we can combine this with others
    private ShowcaseView buildTutorialView(ViewTarget target, int tutorialText, View.OnClickListener tutBtnListener) {
        return new ShowcaseView.Builder(Evaluation.this)
                .withHoloShowcase()    // other options:  withHoloShowcase, withNewStyleShowcase, withMaterialShowcase,
                .setTarget(target)
                .setContentTitle(R.string.showcase_main_title)
                .setContentText(tutorialText)
                .setStyle(R.style.CustomShowcaseTheme)
                .setShowcaseEventListener(Evaluation.this)
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
    /**
     * Record a screen view hit for the this activity
     */
    private void sendScreenImageName() {
        String name = getResources().getString(R.string.anal_tag_eval);

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
