package nineBoxQuestions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import com.ninebox.nineboxapp.R;

/**
 * Created by ase408 on 4/9/16.
 */
public class QuestionsEntryActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private boolean x_axis_selected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions_entry);

        final RadioButton x_axis_radio_button = (RadioButton) findViewById(R.id.x_axis_rb);
        final RadioButton y_axis_radio_button = (RadioButton) findViewById(R.id.y_axis_rb);
        // attach the layout to the toolbar object and then set the toolbar as the ActionBar ...
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        x_axis_radio_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                x_axis_selected = true;
                y_axis_radio_button.setChecked(false);
            }
        });

        y_axis_radio_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                x_axis_selected = false;
                x_axis_radio_button.setChecked(false);
            }


        });
    }

    public void saveQuestion(View view) {
        boolean errorFound = false;
        QuestionsOperations questionsOperations = new QuestionsOperations(this);
        questionsOperations.open();

        // find the ListView so we can work with it ...
        EditText questionValue = (EditText) findViewById(R.id.EditQuestionText);
        String questionText = questionValue.getText().toString();
        EditText weightValue = (EditText) findViewById(R.id.WeightValue);
        String questionWeightText = weightValue.getText().toString();
        int questionWeight = Integer.parseInt(questionWeightText);

        if( questionText.isEmpty() ){
            questionValue.setError("Question cannot be empty!");
            errorFound = true;
        }
        if( questionWeight < 0 || questionWeight > 100 ) {
            weightValue.setError("Weight must be between 0 and 100!");
            errorFound = true;
        }
        if( !errorFound ){
            // save the question
            Questions question = questionsOperations.addQuestion(questionText, questionWeight, x_axis_selected);
            finish();
        }
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.x_axis_rb:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.y_axis_rb:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }

    public void CancelSave(View view) {
        this.kill_activity();
    }

    void kill_activity() {
        finish();
    }
}
