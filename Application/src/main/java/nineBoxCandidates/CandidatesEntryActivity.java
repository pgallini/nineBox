package nineBoxCandidates;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import com.ninebox.nineboxapp.R;

import nineBoxQuestions.Questions;
import nineBoxQuestions.QuestionsOperations;


public class CandidatesEntryActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidates_entry);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void saveCandidate(View view) {
        // find the ListView so we can work with it ...
        EditText Nametext = (EditText) findViewById( R.id.EditTextName);
        String canidateName = Nametext.getText().toString();
        EditText Notestext = (EditText) findViewById( R.id.NotesText);
        String candidateNotes = Notestext.getText().toString();

        // save to database
        //create a new intent so we can return Canidate Data ...
        Intent intent = new Intent();
        //add "returnKey" as a key and assign it the value in the textbox...
        intent.putExtra("returnKey",canidateName);
        intent.putExtra("returnNotes",candidateNotes);
        //get ready to send the result back to the caller (MainActivity)
        //and put our intent into it (RESULT_OK will tell the caller that
        //we have successfully accomplished our task..
        setResult(RESULT_OK,intent);
        finish();
    }

    public void CancelSave(View view) {
        this.kill_activity();
    }

    void kill_activity()
    {
        finish();
    }
}
