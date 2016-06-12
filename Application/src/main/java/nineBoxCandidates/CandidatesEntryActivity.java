package nineBoxCandidates;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.ninebox.nineboxapp.R;
import java.util.ArrayList;
import java.util.List;

import colorPicker.ColorPicker;
import colorPicker.ColorPickerDialog;
import colorPicker.ColorPickerSwatch;
import databaseOpenHelper.DatabaseOpenHelper;
import drawables.drawPoint;
import nineBoxMain.MainActivity;

//
//  Note:  using icons from:  https://materialdesignicons.com/
//     using this color for all icons:  #616161
//
// What do I lose when we move from AppCompatActivity to FragmentActivity ?
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
        // load colols from DB for spinner ...
        // Spinner Drop down elements
        dbHelper = new DatabaseOpenHelper(this);
        colorList = dbHelper.getAllColors();

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lables);
        List<String> labels = getColorLabels(colorList);
//        SpinnerArrayAdapter adapter = new SpinnerArrayAdapter(this, R.layout.spinner_item, labels);
        // TODO - consider changing this to ArrayAdapter adapter ....
//        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.spinner_item, labels);
        // TODO delete SpinnerArrayAdapter if we're really not going to use it ....
//        ArrayAdapter<String> adapter = new SpinnerArrayAdapter(this, R.layout.spinner_item, labels);

//        Spinner spinner = (Spinner) findViewById(R.id.spinner_widget);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(this);

        // grab the initials
//        final TextView Initialstext = (TextView) findViewById( R.id.candidate_initials);
//        candidateInitials = Initialstext.getText().toString();

        display_icon();


//        findViewById(R.id.edit_candidate_initials).setOnTouchListener(
//                 new View.OnTouchListener() {
//
//                     @Override
//                     public boolean onTouch(View view, MotionEvent motionEvent) {
//                         // refresh the view once you have the new initials
//                         View thisView = findViewById(R.id.candidate_initials);
//                         thisView.invalidate();
//                         return true;
//                     }
//                 });

        findViewById(R.id.EditTextName).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Remove
                System.out.println(" inside onFocusChange");

                TextView candidateNameTV = (TextView) findViewById( R.id.EditTextName );
                String candidateName = candidateNameTV.getText().toString();
                candidateInitials = calculateInitials( candidateName );

                display_icon();
                // grab the initials
//                final TextView Initialstext = (TextView) findViewById( R.id.candidate_initials);
//                Initialstext.setText(candidateInitials);

            }
        });

        findViewById(R.id.edit_candidate_icon).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // edit Initials selected - call dialog
                        showEditInitialsDialog(candidateInitials);
//                        Initialstext.setText(candidateInitials);

                        // refresh the view once you have the new initials
//                        View thisView = findViewById(R.id.candidate_initials);
//                        thisView.invalidate();
                    }
                });
//
//        findViewById(R.id.edit_candidate_initials).setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        // edit Initials selected - call dialog
//                        showEditInitialsDialog(candidateInitials);
//                        Initialstext.setText(candidateInitials);
//
//                        // refresh the view once you have the new initials
//                        View thisView = findViewById(R.id.candidate_initials);
//                        thisView.invalidate();
//                    }
//                });
//
//        findViewById(R.id.edit_candidate_colors).setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Remove
//                System.out.println(" about to initialize ColorPicker");
//
////                ColorPicker colorPicker = new ColorPicker();
////                colorPicker.;
//                ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
//
//                // TODO switch this out to use our custom list of colors
//                colorPickerDialog.initialize(R.string.color_select_title, new int[]{Color.CYAN, Color.LTGRAY, Color.BLACK, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.RED, Color.GRAY, Color.YELLOW}, Color.YELLOW, 3, 2);
//                android.app.FragmentManager fragMan = getFragmentManager();
//                colorPickerDialog.show(fragMan, "colorpicker");
//
//                colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
//
//                                                                 @Override
//                                                                 public void onColorSelected(int color) {
//                                                                     // Toast.makeText(MainActivity.this, "selectedColor : " + color, Toast.LENGTH_SHORT).show();
//                                                                     // TODO Remove
//                                                                     System.out.println(" color selected = ");
//                                                                     System.out.println(color);
//                                                                 }
//                                                             }
//                );
//            }
//        });

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

    private void display_icon() {
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

        ImageView currentIcon = (ImageView) findViewById(R.id.current_icon);

        //   TODO - for the imageview current_icon, make the width and height dynamic based on screen size
        currentIcon.setImageDrawable(newPoint);
    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        // TODO Remove
//        System.out.println("inside the damn onResume");
//        // refresh the view once you have the new initials
//        // this doesnt work
//        View thisView = findViewById(R.id.candidate_initials);
//        thisView.invalidate();
//
//    }

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
//        boolean returnBool = false;

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
                            // TODO Remove
                            System.out.println(" just set candidateInitials");

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

//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            public void onDismiss(DialogInterface dialog) {
//
//
//                // TODO Remove
//                System.out.println(" inside setOnDismissListener ");
//                // refresh the view once you have the new initials
//                View thisView = findViewById(R.id.candidate_initials);
//                thisView.invalidate();            }
//        });
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
//        TextView Initialstext = (TextView) findViewById( R.id.candidate_initials);
//        String candidateInitials = Initialstext.getText().toString();

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
