package nineBoxCandidates;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.ninebox.nineboxapp.R;
import java.util.ArrayList;
import java.util.List;

import databaseOpenHelper.DatabaseOpenHelper;
import drawables.drawPoint;


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
        // attach the layout to the toolbar object and then set the toolbar as the ActionBar ...
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        // load colols from DB for spinner ...
        // Spinner Drop down elements
        dbHelper = new DatabaseOpenHelper(this);
        colorList = dbHelper.getAllColors();

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lables);
        List<String> labels = getColorLabels(colorList);
//        SpinnerArrayAdapter adapter = new SpinnerArrayAdapter(this, R.layout.spinner_item, labels);
        // TODO - consider changing this to ArrayAdapter adapter ....
        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.spinner_item, labels);
        // TODO delete SpinnerArrayAdapter if we're really not going to use it ....
//        ArrayAdapter<String> adapter = new SpinnerArrayAdapter(this, R.layout.spinner_item, labels);

        Spinner spinner = (Spinner) findViewById(R.id.spinner_widget);
        // set the layout of the drop-down view
//        adapter.setDropDownViewResource(R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        adapter.setDropDownViewTheme();
//        TextView spinnerItem = (TextView) findViewById(R.id.spinner_text);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // grab the initials
        EditText Initialstext = (EditText) findViewById( R.id.EditCandidateInitials);
        candidateInitials = Initialstext.getText().toString();

        // grab the next available color ...
        currentColor = getNetAvailableColor( colorList );
        // convert the String color to an int
        int tmpcolor = Color.parseColor(currentColor);
        // set-up current icon based on the current color for this candidate ...
        // TODO look for cleaner way to do this
        Drawable d1 =  getResources().getDrawable(R.drawable.empty_drawable, null);
        Drawable[] emptyDrawableLayers = {d1};
        // TODO make this an attribute of candidate - assign it as such and, in reports, grab it using a get
        drawPoint currDrawPoint = new drawPoint(getApplicationContext(), emptyDrawableLayers, 6, 6, tmpcolor);
        LayerDrawable newPoint = currDrawPoint.getPoint( candidateInitials );

        // TODO Remove ...
        System.out.println( " just called getPoint");

        View currentIcon = (View) findViewById(R.id.current_icon);

        currentIcon.setBackgroundDrawable(newPoint);

        findViewById(R.id.save_candidate).setOnClickListener(new View.OnClickListener() {
                                                                 @Override
                                                                 public void onClick(View view) {
                                                                     saveCandidate(view);
//                                                                     Toast.makeText(getParent(), "@string/candidate_saved", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String tmpColorNum = colorList.get( pos ).getColor_number();
        currentColor = tmpColorNum;
        int tmpcolor = Color.parseColor(currentColor);
        // refresh current icon based on the current color for this candidate ...

        // TODO look for cleaner way to do this
        Drawable d1 =  getResources().getDrawable(R.drawable.empty_drawable, null);
        Drawable[] emptyDrawableLayers = {d1};

        // grab the initials
        EditText Initialstext = (EditText) findViewById( R.id.EditCandidateInitials);
        candidateInitials = Initialstext.getText().toString();

        drawPoint currDrawPoint = new drawPoint(getApplicationContext(), emptyDrawableLayers, 6, 6, tmpcolor);
        LayerDrawable newPoint = currDrawPoint.getPoint( candidateInitials );
//        ShapeDrawable newPoint = drawPoint(this, 2, 2, tmpcolor);
        View currentIcon = (View) findViewById(R.id.current_icon);

        currentIcon.setBackgroundDrawable(newPoint);
        Toast.makeText(this, "Selection: " + tmpColorNum, Toast.LENGTH_SHORT).show();
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
        EditText Initialstext = (EditText) findViewById( R.id.EditCandidateInitials);
        String candidateInitials = Initialstext.getText().toString();

        // save to database
        //create a new intent so we can return Canidate Data ...
        Intent intent = new Intent();
        //add "returnKey" as a key and assign it the value in the textbox...
        intent.putExtra("returnKey",canidateName);
        intent.putExtra("returnNotes",candidateNotes);
        intent.putExtra("returnColor",currentColor);
        intent.putExtra("returnInitials",candidateInitials);
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

    // TODO if this is useful here - we need to collapse this one with the same method in ReportActivity
//    public static ShapeDrawable drawPoint(Context context, int width, int height, int color)  {
//
//        ShapeDrawable oval = new ShapeDrawable(new OvalShape());
//        oval.setIntrinsicHeight(height);
//        oval.setIntrinsicWidth(width);
//        oval.getPaint().setColor(color);
//        oval.setPadding(10, 10, 10, 10);
//        return oval;
//    }
}
