package nineBoxCandidates;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.promogird.funkynetsoftware.R; ;

import java.util.ArrayList;
import java.util.List;

import databaseOpenHelper.DatabaseOpenHelper;
import drawables.drawPoint;


public class CandidatesUpdateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Toolbar toolbar;
    private DatabaseOpenHelper dbHelper;
    private String currentColor;
    private String candidateInitials = " ";
    private long candidateID = 0;
    public ArrayList<appColor> colorList;
    // Spinner element
    Spinner spinner;
    private Tracker mTracker;  // used for Google Analytics

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.candidates_entry);
        // make sure the soft keyboard doesn't push everything up ...
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // attach the layout to the toolbar object and then set the toolbar as the ActionBar ...
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        // load colols from DB for spinner ...
        // Spinner Drop down elements
        dbHelper = new DatabaseOpenHelper(this);
        colorList = dbHelper.getAllColors();

//        List<String> labels = getColorLabels(colorList);

        // get the data on the candidate being updated ...
        candidateID = Integer.parseInt(getIntent().getStringExtra("candidateId"));
        String candidateName = getIntent().getStringExtra("candidateName");
        String candidateNote = getIntent().getStringExtra("candidateNote");
        String candidateInitialsIncoming = getIntent().getStringExtra("candidateInitials");
        String candidateColor = getIntent().getStringExtra("candidateColor");

        TextView candidateNameTV = (TextView) findViewById( R.id.EditTextName );
        candidateNameTV.setText(candidateName);

        TextView candidateNotesTV = (TextView) findViewById( R.id.NotesText );
        candidateNotesTV.setText(candidateNote);

        currentColor = candidateColor;
        candidateInitials = candidateInitialsIncoming;
        ImageView currentIcon = (ImageView) findViewById(R.id.current_icon);
        currentIcon.setImageDrawable(Candidates.get_icon(getApplicationContext(), currentColor, candidateInitials));

        // Obtain the shared Tracker instance.
        common.AnalyticsApplication application = (common.AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        sendScreenImageName(); // send tag to Google Analytics


        findViewById(R.id.EditTextName).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                TextView candidateNameTV = (TextView) findViewById( R.id.EditTextName );
                String candidateName = candidateNameTV.getText().toString();
                ImageView currentIcon = (ImageView) findViewById(R.id.current_icon);
                currentIcon.setImageDrawable(Candidates.get_icon(getApplicationContext(), currentColor, candidateInitials));
            }
        });

        findViewById(R.id.edit_candidate_icon).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // edit Initials selected - call dialog
                        showEditInitialsDialog(candidateInitials);
                    }
                });

        findViewById(R.id.save_candidate).setOnClickListener(new View.OnClickListener() {
                                                                 @Override
                                                                 public void onClick(View view) {
                                                                     saveCandidate(view);
                                                                 }
                                                             }
        );

        findViewById(R.id.cancel_save_candidate).setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            finish();
                                                                        }
                                                                    }
        );
    }

    public List<String> getColorLabels(ArrayList<appColor> colorList ) {
        List<String> labels = new ArrayList<String>();

        for (appColor currColor : colorList) {
            labels.add(currColor.getColor_text());
        }
        return labels;
    }

    private void showEditInitialsDialog(String currentInitials ) {

        // Get the layout inflater
        LayoutInflater inflater = CandidatesUpdateActivity.this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View dialoglayout = inflater.inflate(R.layout.candidates_edit_initials, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CandidatesUpdateActivity.this);
        builder.setView(dialoglayout);

        builder.setTitle(getString(R.string.edit_candidate_initials_hint));
        builder.setMessage(getString(R.string.confirm_edit_initials_message));

        TextView Initialstext = (TextView) dialoglayout.findViewById(R.id.new_initials);
        Initialstext.setText(currentInitials);

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // grab the initials
                        TextView Initialstext = (TextView) dialoglayout.findViewById( R.id.new_initials);
                        if(Initialstext != null) {
                            candidateInitials = Initialstext.getText().toString();

                            ImageView currentIcon = (ImageView) findViewById(R.id.current_icon);
                            currentIcon.setImageDrawable(Candidates.get_icon(getApplicationContext(), currentColor, candidateInitials));
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String tmpColorNum = colorList.get( pos ).getColor_number();
        currentColor = tmpColorNum;
        // refresh current icon based on the current color for this candidate ...
        ImageView currentIcon = (ImageView) findViewById(R.id.current_icon);
        currentIcon.setImageDrawable(Candidates.get_icon(getApplicationContext(), currentColor, candidateInitials));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "Selections cleared.", Toast.LENGTH_SHORT).show();
    }

    public void saveCandidate(View view) {
        // find the ListView so we can work with it ...
        EditText Nametext = (EditText) findViewById( R.id.EditTextName);
        String canidateName = Nametext.getText().toString();
        EditText Notestext = (EditText) findViewById( R.id.NotesText);
        String candidateNotes = Notestext.getText().toString();

        // save to database
        //create a new intent so we can return Candidate Data ...
        Intent intent = new Intent();
        //add "returnKey" as a key and assign it the value in the textbox...

        intent.putExtra("returnKey",Long.toString(candidateID));
        intent.putExtra("returnName",canidateName);
        intent.putExtra("returnNotes",candidateNotes);
        intent.putExtra("returnColor",currentColor);
        intent.putExtra("returnInitials",candidateInitials);
        intent.putExtra("returnMode","UPDATE");
        //get ready to send the result back to the caller (MainActivity)
        //and put our intent into it (RESULT_OK will tell the caller that
        //we have successfully accomplished our task..
        setResult(RESULT_OK,intent);
        finish();
    }
    /**
     * Record a screen view hit for the this activity
     */
    private void sendScreenImageName() {
        String name = getResources().getString(R.string.anal_tag_candidates_edit);

        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void CancelSave(View view) {
        this.kill_activity();
    }

    void kill_activity()
    {
        finish();
    }

}
