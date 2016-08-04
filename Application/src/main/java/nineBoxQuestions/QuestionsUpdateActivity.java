package nineBoxQuestions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.ninebox.nineboxapp.R;

/**
 * Created by Paul Gallini on 4/9/16.
 */
public class QuestionsUpdateActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private boolean x_axis_selected = true;
    private int questionId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions_entry);
        questionId = Integer.parseInt(getIntent().getStringExtra("questionId"));
        String questionText = getIntent().getStringExtra("questionText");
        int questionWeight = Integer.parseInt(getIntent().getStringExtra("questionWeight"));
        String questionAxis = getIntent().getStringExtra("questionAxis");

        // find the ListView so we can work with it ...
        EditText questionValue = (EditText) findViewById(R.id.EditQuestionText);
        questionValue.setText(questionText);
        EditText weightValue = (EditText) findViewById(R.id.WeightValue);
        weightValue.setText(Integer.toString(questionWeight));


        final RadioButton x_axis_radio_button = (RadioButton) findViewById(R.id.x_axis_rb);
        final RadioButton y_axis_radio_button = (RadioButton) findViewById(R.id.y_axis_rb);
        // attach the layout to the toolbar object and then set the toolbar as the ActionBar ...
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        if( questionAxis.equals("X")) {
            x_axis_selected = true;
            x_axis_radio_button.setChecked(true);
            y_axis_radio_button.setChecked(false);
        } else {
            x_axis_selected = false;
            x_axis_radio_button.setChecked(false);
            y_axis_radio_button.setChecked(true);
        }
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
        // find the ListView so we can work with it ...
        EditText questionValue = (EditText) findViewById(R.id.EditQuestionText);
        String questionText = questionValue.getText().toString();
        EditText weightValue = (EditText) findViewById(R.id.WeightValue);
        String questionWeightText = weightValue.getText().toString();
        int questionWeight = 0;
        RadioButton x_axis_radio_button = (RadioButton) findViewById(R.id.x_axis_rb);
        String questionAxis = "Y";

        if( x_axis_radio_button.isChecked()) {
            questionAxis = "X";
        }

        // TODO add logic to prevent sum of X or Y > 100
        // TODO consoldate this error checking logic into one place
        if( questionText.isEmpty() ){
            questionValue.setError("Question cannot be empty!");
            errorFound = true;
        }
        if( questionWeightText.isEmpty() ){
            questionValue.setError("Weight cannot be empty!");
            errorFound = true;
        } else
        {
            questionWeight = Integer.parseInt(questionWeightText);
        }
        if( questionWeight < 0 || questionWeight > 100 ) {
            weightValue.setError("Weight must be between 0 and 100!");
            errorFound = true;
        }
        if( !errorFound ){
            //create a new intent so we can return Data ...
            Intent intent = new Intent();

            intent.putExtra("returnKey",Long.toString(questionId));
            intent.putExtra("returnQuestionText",questionText);
            intent.putExtra("returnQuestionWeight",questionWeightText);
            intent.putExtra("returnQuestionAxis",questionAxis);
            intent.putExtra("returnMode","UPDATE");
            //get ready to send the result back to the caller (MainActivity)
            //and put our intent into it (RESULT_OK will tell the caller that
            //we have successfully accomplished our task..
            setResult(RESULT_OK, intent);

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