package nineBoxReport;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.ninebox.nineboxapp.R;

import android.graphics.drawable.LayerDrawable;
import android.graphics.Canvas;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import nineBoxCandidates.CandidateOperations;
import nineBoxCandidates.Candidates;
import drawables.drawPoint;
import nineBoxEvaluation.EvaluationOperations;
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
        // grab the width of the circle ...
        int widget_width = (int) getResources().getDimension(R.dimen.widget_width);
        for(int i = 0;  i < candidatesList.size(); i++) {

            currCandidate = candidatesList.get( i );

            // grab the next available color ...
            String currentColor = currCandidate.getCandidateColor();
            // convert the String color to an int
            int tmpcolor = Color.parseColor(currentColor);
            // TODO - see if there is a cleaner way to do this ...
            Drawable d1 =  getResources().getDrawable(R.drawable.empty_drawable, null);
            Drawable[] emptyDrawableLayers = {d1};

            drawPoint currDrawPoint = new drawPoint(getApplicationContext(), emptyDrawableLayers, 6, 6, tmpcolor);
            LayerDrawable newPoint = currDrawPoint.getPoint( currCandidate.getCandidateInitials() );

//            Drawable tempPoint = getSingleDrawable(newPoint);

            Drawable tempPoint = newPoint.mutate();

            // TODO figure out the purpose of the last two params - they don't seem to do anything
            layerDrawable.addLayer(tempPoint, 4 , widget_width, widget_width);
            // temp X & Y -axis reading from a candidate ...
            double result_X_axis = get_X_ResultForCandiate(currCandidate);
            double result_Y_axis = get_Y_ResultForCandiate(currCandidate);
            // set the size of the circle (not sure why this can't be done inside add
            layerDrawable.setLayerSize(currLayer, widget_width, widget_width);
            // set the position of the circle

            // TODO remove
            System.out.println("result_X_axis = ");
            System.out.println(result_X_axis);
            System.out.println("result_Y_axis = ");
            System.out.println(result_Y_axis);

            layerDrawable.setWidgetPosition(currLayer, result_X_axis, result_Y_axis, widget_width);

            currLayer++;
        }
        // end Loop

        // Now that we are done building the grid, add it to our View ....
        ImageView gridImageView = (ImageView) findViewById(R.id.grid_background);
        layerDrawable.draw(gridCanvas);
        gridImageView.setImageDrawable(layerDrawable);


//        findViewById(R.id.save_candidate).setOnClickListener(new View.OnClickListener() {
//                                                                 @Override
//                                                                 public void onClick(View view) {
//                                                                     saveCandidate(view);
//                                                                 }
//                                                             }

//        );
//
        findViewById(R.id.cancel_report).setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            finish();
                                                                        }
                                                                    }

        );

    }

//    // trying this method to convert from LayerDrawable to Shapedrawable
//    public Drawable getSingleDrawable(LayerDrawable layerDrawable){
//
//        int resourceBitmapHeight = 136, resourceBitmapWidth = 153;
//
//        float widthInInches = 0.9f;
//
//        int widthInPixels = (int)(widthInInches * getResources().getDisplayMetrics().densityDpi);
//        int heightInPixels = (int)(widthInPixels * resourceBitmapHeight / resourceBitmapWidth);
//
//        int insetLeft = 10, insetTop = 10, insetRight = 10, insetBottom = 10;
//
//        layerDrawable.setLayerInset(1, insetLeft, insetTop, insetRight, insetBottom);
//
//        Bitmap bitmap = Bitmap.createBitmap(widthInPixels, heightInPixels, Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(bitmap);
//        layerDrawable.setBounds(0, 0, widthInPixels, heightInPixels);
//        layerDrawable.draw(canvas);
//
//        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
//
////        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
//        bitmapDrawable.setBounds(0, 0, widthInPixels, heightInPixels);
//
//        Drawable sDrawable = bitmapDrawable.mutate();
//
//        ShapeDrawable tempSD = (ShapeDrawable) sDrawable.draw(canvas);
//        return sDrawable;
//    }
//
//




    private double get_X_ResultForCandiate(Candidates currCandidate) {
        int currResponse = 1;
        double result = 0.0;
        int currWeight = 0;
        long candidateID = -1;
        long questionID = -1;

        candidateID = currCandidate.getCandidateID();

        for (int i = 0; i < questionsList.size(); i++) {

            questionID = questionsList.get(i).getQuestionID();
            if (candidateID != -1 && questionID != -1) {
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
//                    System.out.println( " currWeight & currResponse == ");
//                    System.out.println( currWeight );
//                    System.out.println( currResponse );
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

//            System.out.println( "looping through question list i = " );
//            System.out.println( i );

            questionID = questionsList.get(i).getQuestionID();


            if (candidateID != -1 && questionID != -1) {

//                System.out.println( "questionsList.get(i).getQuestionAxis() = ");
//                System.out.println( questionsList.get(i).getQuestionAxis());

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
//                    System.out.println( " currWeight & currResponse == ");
//                    System.out.println( currWeight );
//                    System.out.println( currResponse );
                }
            }
        }
        // divide result by 100 and return it.
        return ( result * 0.01 );
    }


}



