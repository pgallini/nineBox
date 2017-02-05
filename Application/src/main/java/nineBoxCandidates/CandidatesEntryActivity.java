package nineBoxCandidates;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.ninebox.nineboxapp.R;
import java.util.ArrayList;
import java.util.List;
import databaseOpenHelper.DatabaseOpenHelper;

//
//  Note:  using icons from:  https://materialdesignicons.com/
//     using this color for all icons:  #616161
//
public class CandidatesEntryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Toolbar toolbar;
    private DatabaseOpenHelper dbHelper;
    private String currentColor;
    private String candidateInitials = " ";
    public ArrayList<appColor> colorList;
    // Spinner element
    Spinner spinner;

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
        // load colors from DB for spinner ...
        // Spinner Drop down elements
        dbHelper = new DatabaseOpenHelper(this);
        colorList = dbHelper.getAllColors();

        List<String> labels = getColorLabels(colorList);

        ImageView currentIcon = (ImageView) findViewById(R.id.current_icon);
        currentColor = getNetAvailableColor( colorList );
        currentIcon.setImageDrawable(Candidates.get_icon(getApplicationContext(), currentColor, candidateInitials));

        findViewById(R.id.EditTextName).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setCandidateIcon();
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

    private void setCandidateIcon() {
        TextView candidateNameTV = (TextView) findViewById( R.id.EditTextName );
        String candidateName = candidateNameTV.getText().toString();
        candidateInitials = calculateInitials( candidateName );
        ImageView currentIcon = (ImageView) findViewById(R.id.current_icon);
        currentColor = getNetAvailableColor(colorList);
        currentIcon.setImageDrawable(Candidates.get_icon(getApplicationContext(), currentColor, candidateInitials));
    }

    public List<String> getColorLabels(ArrayList<appColor> colorList ) {
        List<String> labels = new ArrayList<String>();

        for (appColor currColor : colorList) {
            labels.add(currColor.getColor_text());
        }
        return labels;
    }

    private String calculateInitials( String candidateName ) {
        String returnInitials = " ";

        String tempName = candidateName.trim();
        if( tempName.length() > 0 )  {
            returnInitials = tempName.substring(0, 1) ;
            int firstSpace = tempName.indexOf(" ");

            if( firstSpace != -1  ){
                returnInitials = returnInitials.concat(tempName.substring((firstSpace+1), (firstSpace + 2)));
            }
            else if( tempName.length() > 1 ) {
                returnInitials = returnInitials.concat(tempName.substring(1, 2));
            }
        }
        return returnInitials;
    }

    // TODO consider moving this
    private String getNetAvailableColor(ArrayList<appColor> colorList  ) {
        String returnval = " ";
        for (appColor currColor : colorList) {
            if( currColor.getColor_inuse() == 0 ) {
                returnval = currColor.getColor_number();
                break;
            }
        }
        return returnval;
    }

    private void showEditInitialsDialog(String currentInitials ) {

        // Get the layout inflater
        LayoutInflater inflater = CandidatesEntryActivity.this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View dialoglayout = inflater.inflate(R.layout.candidates_edit_initials, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CandidatesEntryActivity.this);
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
                            currentColor = getNetAvailableColor( colorList );
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
        int tmpcolor = Color.parseColor(currentColor);
        // refresh current icon based on the current color for this candidate ...
        ImageView currentIcon = (ImageView) findViewById(R.id.current_icon);
        currentColor = getNetAvailableColor( colorList );
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

        // if the initials have yet to be set, set them now
        if(candidateInitials.trim().length() == 0 ) {
            setCandidateIcon();
        }

        String returnKeyValue = "0";
        // save to database
        //create a new intent so we can return Candidate Data ...
        Intent intent = new Intent();
        //add "returnKey" as a key and assign it the value in the textbox...
        intent.putExtra("returnKey",returnKeyValue);

        intent.putExtra("returnName",canidateName);
        intent.putExtra("returnNotes",candidateNotes);
        intent.putExtra("returnColor",currentColor);
        intent.putExtra("returnInitials",candidateInitials);
        intent.putExtra("returnMode","ADD");
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
