package nineBoxEvaluation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.ninebox.nineboxapp.R;

import java.util.ArrayList;

import nineBoxCandidates.CandidateOperations;
import nineBoxCandidates.Candidates;
import nineBoxMain.MainActivity;
import nineBoxQuestions.Questions;
import nineBoxQuestions.QuestionsOperations;

/**
 * Created by Paul Gallini on 4/17/16.
 */
public class Evaluation extends AppCompatActivity {
    TextView cName;
    private final int EVALUATION_ACTIVITY_REQUEST_CODE = 0;
    public ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
    public ArrayList<Questions> questionsList = new ArrayList<>();
    //    public int candidateIndex = 0;
    public int currentQuestionNo = 1;
    public int maxQuestionNo = 0;
    private Toolbar toolbar;
    private int currentResponse = 0;

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
        // start the index at zero
//        candidateIndex = 0;

        // attach the layout to the toolbar object and then set the toolbar as the ActionBar ...
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // set-up questions ...
        QuestionsOperations questionsOperations = new QuestionsOperations(this);
        questionsOperations.open();
        questionsList = questionsOperations.getAllQuestions();
        maxQuestionNo = questionsList.size();

        final TextView nextQuestionButtonView = (TextView) findViewById(R.id.next_question_button);

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
                System.out.println("in setOnClickListener ...  candidateIndex = ");
                System.out.println(candidateIndex);

                if (candidateIndex < candidatesList.size()) {

                    saveResponse(candidatesList.get(candidateIndex).getCandidateID(), questionsList.get((currentQuestionNo - 1)).getQuestionID(), currentResponse);
                    if (currentQuestionNo < maxQuestionNo) {
                        currentQuestionNo++;
                        if (currentQuestionNo == maxQuestionNo) {
                            // if we are now on the last question, change the text of the button to Next Candidate or Done
                            if(candidateIndex == (candidatesList.size() - 1) ) {
                                nextQuestionButtonView.setText(R.string.done_button);
                            } else {
                                nextQuestionButtonView.setText(R.string.next_question_button_alt);
                            }
                        }
                        onResume();
                    } else {
                        // increment the index for the next candidate ...
//                    candidateIndex++;
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
                            ;
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

        if (candidateIndex < candidatesList.size()) {
            System.out.println(" candidatesList.get(i).getCandidateName() = ");
            System.out.println(candidatesList.get(candidateIndex).getCandidateName());
            displayName.setText(candidatesList.get(candidateIndex).getCandidateName());
            candidateID = candidatesList.get(candidateIndex).getCandidateID();

            if (currentQuestionNo <= questionsList.size()) {
                // TODO see if there is a cleaner way to manage what we want to display versus the real index ...
                quesitonTextView.setText(questionsList.get((currentQuestionNo - 1)).getQuestionText());
                questionID = questionsList.get((currentQuestionNo - 1)).getQuestionID();
            }
            // See if there is already a response for this combo of candidate and question ..
            if (candidateID != -1 && questionID != -1) {
                EvaluationOperations evaluationOperations = new EvaluationOperations(this);
                evaluationOperations.open();
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
            finish();

        }
    }

    public void CancelSave(View view) {
        this.kill_activity();
    }

    void kill_activity() {
        finish();
    }
}
