package nineBoxEvaluation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ninebox.nineboxapp.R;

import java.util.ArrayList;

import nineBoxCandidates.CandidateOperations;
import nineBoxCandidates.Candidates;
import nineBoxQuestions.Questions;
import nineBoxQuestions.QuestionsOperations;

/**
 * Created by Paul Gallini on 4/17/16.
 */
public class Evaluation extends Activity {
    TextView cName;
    private final int EVALUATION_ACTIVITY_REQUEST_CODE = 0;
    public ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
    public ArrayList<Questions> questionsList = new ArrayList<>();
    public int currentIndex = 0;
    public int currentQuestionNo = 1;
    public int maxQuestionNo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int returnCode = 0;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.evaluation_entry);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        CandidateOperations candidateOperations;
        // set-up the operations class for Candidates ...
        candidateOperations = new CandidateOperations(this);
        candidateOperations.open();
        // create a list of candidates from what's in the database ...
        candidatesList = candidateOperations.getAllCandidates();
        // start the index at zero
        currentIndex = 0;

        // set-up questions ...
        QuestionsOperations questionsOperations = new QuestionsOperations(this);
        questionsOperations.open();
        questionsList = questionsOperations.getAllQuestions();
//        currentQuestionNo = 1;
        maxQuestionNo = questionsList.size();

        final TextView nextQuestionButtonView = (TextView) findViewById(R.id.next_question_button);

        findViewById(R.id.next_question_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestionNo < maxQuestionNo) {
                    currentQuestionNo++;
                    if (currentQuestionNo == maxQuestionNo) {
                        // if we are now on the last question, change the text of the button to Next Candidate
                        nextQuestionButtonView.setText(R.string.next_question_button_alt);
                    }
                    onResume();
                } else {
                    // increment the index for the next candidate ...
                    currentIndex++;
                    // reset the Questions
                    currentQuestionNo = 1;
                    if (currentIndex < candidatesList.size()) {
                        // reset label of the Next btn
                        nextQuestionButtonView.setText(R.string.next_question_button);
                        onResume();
                    } else {
                        // unless we are on the last candidate
                        finish();
                        ;
                    }
                }
            }
        });
        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO decide if we are really returning anything here (and the done btn)
                //create a new intent so we can return Canidate Data ...
                Intent intent = new Intent();
                //add "returnKey" as a key and assign it the value in the textbox...
//                intent.putExtra("returnKey",canidateName);
//                intent.putExtra("returnNotes",candidateNotes);
                //get ready to send the result back to the caller (MainActivity)
                //and put our intent into it (RESULT_OK will tell the caller that
                //we have successfully accomplished our task..
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        }

        );
        findViewById(R.id.done_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // increment the index for the next candidate ...
                currentIndex++;
                if ( currentIndex < candidatesList.size()) {
                    onResume();
                } else {
                    finish();;
                }
            }
        });
    }
    public void onResume() {
        super.onResume();
        TextView displayName =  (TextView) findViewById(R.id.evalCandidateName);
        TextView currQuestionNoView = (TextView) findViewById(R.id.curr_question_no);
        TextView maxQuestionNoView = (TextView) findViewById(R.id.max_question_no);
        TextView quesitonTextView = (TextView) findViewById(R.id.question_text);

        currQuestionNoView.setText(Integer.toString(currentQuestionNo));
        maxQuestionNoView.setText(Integer.toString(maxQuestionNo));

        if ( currentIndex < candidatesList.size()) {
            System.out.println(" candidatesList.get(i).getCandidateName() = ");
            System.out.println(candidatesList.get(currentIndex).getCandidateName());
            displayName.setText(candidatesList.get(currentIndex).getCandidateName());
        }
        if ( currentQuestionNo <= questionsList.size() ) {
            // TODO see if there is a cleaner way to manage what we want to display versus the real index ...
            quesitonTextView.setText(questionsList.get((currentQuestionNo - 1)).getQuestionText());
        }
    }

    public void CancelSave(View view) {
        this.kill_activity();
    }

    void kill_activity()
    {
        finish();
    }
}
