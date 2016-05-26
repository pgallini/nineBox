package nineBoxReport;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.ninebox.nineboxapp.R;

import android.graphics.drawable.LayerDrawable;
import android.graphics.Canvas;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.util.ArrayList;

import nineBoxCandidates.CandidateOperations;
import nineBoxCandidates.Candidates;
import nineBoxEvaluation.EvaluationOperations;
import nineBoxMain.MainActivity;
import nineBoxQuestions.Questions;
import nineBoxQuestions.QuestionsOperations;


/**
 * Created by Paul Gallini on 5/11/16.
 * <p/>
 * This activity drives the generation and presentation of the results grid.
 */
public class ReportActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private CandidateOperations candidateOperations;
    private ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
    private QuestionsOperations questionsOperations;
    private ArrayList<Questions> questionsList;
    private EvaluationOperations evaluationOperations;
    private Candidates currCandidate;
    CustomDrawableView mCustomDrawableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

        // attach the layout to the toolbar object and then set the toolbar as the ActionBar ...
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        // create bitmap of the main Grid background
        Bitmap mainBackground = BitmapFactory.decodeResource(getResources(), R.drawable.grid_background);
        // convert the bitmap to make it mutable (otherwise, unmutable error will occur)
        Bitmap mainBackground_mb = mainBackground.copy(Bitmap.Config.ARGB_8888, true);
        // create a canvas upon which we will draw the final grid
        Canvas gridCanvas = new Canvas(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888));
        // Convert Bitmap to Drawable
        Drawable mainBackgroundDrawable = new BitmapDrawable(getResources(), mainBackground_mb);
        // create drawable array starting with just our background
        Drawable finalGrid[] = new Drawable[]{mainBackgroundDrawable};
        // create a custom layerDrawable object ... to which we will add our circles
        GridLayerDrawable layerDrawable = new GridLayerDrawable(finalGrid);

        // grab a list of all of the questions ..

        questionsOperations = new QuestionsOperations(this);
        questionsOperations.open();
        questionsList = questionsOperations.getAllQuestions();

        // set-up Evaulations operations ...
        evaluationOperations = new EvaluationOperations(this);
        evaluationOperations.open();

        // Loop through Candidates and add their results to the Grid ...
        // create a list of candidates from what's in the database ...
        // set-up the operations class for Candidates ...

        int currLayer = 1;
        candidateOperations = new CandidateOperations(this);
        candidateOperations.open();
        candidatesList = candidateOperations.getAllCandidates();
        for(int i = 0;  i < candidatesList.size(); i++) {

            currCandidate = candidatesList.get( i );

            // grab the next available color ...
            String currentColor = currCandidate.getCandidateColor();
            // convert the String color to an int
            int tmpcolor = Color.parseColor(currentColor);
            ShapeDrawable newPoint = drawPoint(this, 2, 2, tmpcolor);

            layerDrawable.addLayer(newPoint, 4 , 300, 300);
            // temp X & Y -axis reading from a candidate ...
            double result_X_axis = get_X_ResultForCandiate(currCandidate);
            double result_Y_axis = get_Y_ResultForCandiate(currCandidate);
            // set the size of the circle (not sure why this can't be done inside add
            layerDrawable.setLayerSize(currLayer, 100, 100);
            // set the position of the circle

            // TODO remove
            System.out.println("result_X_axis = ");
            System.out.println(result_X_axis);
            System.out.println("result_Y_axis = ");
            System.out.println(result_Y_axis);

            layerDrawable.setWidgetPosition(currLayer, result_X_axis, result_Y_axis);

            currLayer++;
        }
        // end Loop

        // Now that we are done building the grid, add it to our View ....
        ImageView gridImageView = (ImageView) findViewById(R.id.grid_background);
        layerDrawable.draw(gridCanvas);
        gridImageView.setImageDrawable(layerDrawable);
    }

//        findViewById(R.id.save_candidate).setOnClickListener(new View.OnClickListener() {
//                                                                 @Override
//                                                                 public void onClick(View view) {
//                                                                     saveCandidate(view);
//                                                                 }
//                                                             }

//        );
//
//        findViewById(R.id.cancel_save_candidate).setOnClickListener(new View.OnClickListener() {
//                                                                        @Override
//                                                                        public void onClick(View view) {
//                                                                            finish();
//                                                                        }
//                                                                    }
//
//        );


    private double get_X_ResultForCandiate(Candidates currCandidate) {
        int currResponse = 1;
        double result = 0.0;
        int currWeight = 0;
        long candidateID = -1;
        long questionID = -1;

        candidateID = currCandidate.getCandidateID();

        for (int i = 0; i < questionsList.size(); i++) {

            System.out.println( "looping through question list i = " );
            System.out.println( i );

            questionID = questionsList.get(i).getQuestionID();


            if (candidateID != -1 && questionID != -1) {

                System.out.println( "questionsList.get(i).getQuestionAxis() = ");
                System.out.println( questionsList.get(i).getQuestionAxis());

                // If the Axis of the current question is not X, then ignore it ...
                if (questionsList.get(i).getQuestionAxis().equals("X")) {

                    // grab the weight
                    currWeight = questionsList.get(i).getQuestionWeight();
                    // grab the response ...
                    currResponse = evaluationOperations.getResponseValue(candidateID, questionID);
                    if (currResponse > -1) {
                        // add the response multiplied by the weight and add it to the result ...
                        result = result + (currResponse * currWeight);
                    }

                    // TODO remove
                    System.out.println( " currWeight & currResponse == ");
                    System.out.println( currWeight );
                    System.out.println( currResponse );
                }
            }
        }
        // divide result by 100 and return it.
        return ( result * 0.01 );
    }

    private double get_Y_ResultForCandiate( Candidates currCandidate ) {
        int currResponse = 1;
        double result = 0.0;
        int currWeight = 0;
        long candidateID = -1;
        long questionID = -1;

        candidateID = currCandidate.getCandidateID();

        for (int i = 0; i < questionsList.size(); i++) {

            System.out.println( "looping through question list i = " );
            System.out.println( i );

            questionID = questionsList.get(i).getQuestionID();


            if (candidateID != -1 && questionID != -1) {

                System.out.println( "questionsList.get(i).getQuestionAxis() = ");
                System.out.println( questionsList.get(i).getQuestionAxis());

                // If the Axis of the current question is not Y, then ignore it ...
                if (questionsList.get(i).getQuestionAxis().equals("Y")) {

                    // grab the weight
                    currWeight = questionsList.get(i).getQuestionWeight();
                    // grab the response ...
                    currResponse = evaluationOperations.getResponseValue(candidateID, questionID);
                    if (currResponse > -1) {
                        // add the response multiplied by the weight and add it to the result ...
                        result = result + (currResponse * currWeight);
                    }

                    // TODO remove
                    System.out.println( " currWeight & currResponse == ");
                    System.out.println( currWeight );
                    System.out.println( currResponse );
                }
            }
        }
        // divide result by 100 and return it.
        return ( result * 0.01 );
    }

    public static ShapeDrawable drawPoint(Context context, int width, int height, int color) {

        ShapeDrawable oval = new ShapeDrawable(new OvalShape());
        oval.setIntrinsicHeight(height);
        oval.setIntrinsicWidth(width);
        oval.getPaint().setColor(color);
        oval.setPadding(10, 10, 10, 10);
        return oval;
    }
}



