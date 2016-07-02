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

import com.ninebox.nineboxapp.R;

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
        display_icon();

        findViewById(R.id.EditTextName).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                TextView candidateNameTV = (TextView) findViewById( R.id.EditTextName );
                String candidateName = candidateNameTV.getText().toString();
//                candidateInitials = calculateInitials( candidateName );

                display_icon();
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

    private void display_icon() {
        // TODO find way to combine this method with the one in  CandidatesListActivity
        // grab the next available color ...
//        currentColor = getNetAvailableColor( colorList );
        // convert the String color to an int
        int tmpcolor = Color.parseColor(currentColor);
        // set-up current icon based on the current color for this candidate ...
        // TODO look for cleaner way to do this
        Drawable d1 =  getResources().getDrawable(R.drawable.empty_drawable, null);
        Drawable[] emptyDrawableLayers = {d1};
        // TODO make this an attribute of candidate - assign it as such and, in reports, grab it using a get
        drawPoint currDrawPoint = new drawPoint(getApplicationContext(), emptyDrawableLayers, 6, 6, tmpcolor);
        LayerDrawable newPoint = currDrawPoint.getPoint( candidateInitials );

        ImageView currentIcon = (ImageView) findViewById(R.id.current_icon);

        //   TODO - for the imageview current_icon, make the width and height dynamic based on screen size
        currentIcon.setImageDrawable(newPoint);
    }

    public List<String> getColorLabels(ArrayList<appColor> colorList ) {
        List<String> labels = new ArrayList<String>();

        for (appColor currColor : colorList) {
            labels.add(currColor.getColor_text());
        }
        return labels;
    }

//    private String calculateInitials( String candidateName ) {
//        String returnInitials = " ";
//
//        String tempName = candidateName.trim();
//        if( tempName.length() > 0 )  {
//            returnInitials = tempName.substring(0, 1) ;
//            int firstSpace = tempName.indexOf(" ");
//
//            if( firstSpace != -1  ){
//                returnInitials = returnInitials.concat(tempName.substring((firstSpace+1), (firstSpace + 2)));
//            }
//            else if( tempName.length() > 1 ) {
//                returnInitials = returnInitials.concat(tempName.substring(1, 2));
//            }
//        }
//        return returnInitials;
//    }

//    // TODO consider moving this
//    private String getNetAvailableColor(ArrayList<appColor> colorList  ) {
//        String returnval = " ";
//        for (appColor currColor : colorList) {
//            if( currColor.getColor_inuse() == 0 ) {
//                returnval = currColor.getColor_number();
//                break;
//            }
//        }
//        return returnval;
//    }

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

                            display_icon();
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
        // TODO Remove
        System.out.println(" About to call display_icon ... candidateInitials");
        System.out.println(candidateInitials);

        display_icon();
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

    public void CancelSave(View view) {
        this.kill_activity();
    }

    void kill_activity()
    {
        finish();
    }

}
