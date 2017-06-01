package nineBoxReport;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.promogrid.funkynetsoftware.BuildConfig;
import com.promogrid.funkynetsoftware.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import common.AnalyticsApplication;
import common.Utilities;
import drawables.drawPoint;
import nineBoxCandidates.CandidateOperations;
import nineBoxCandidates.Candidates;
import nineBoxEvaluation.EvaluationOperations;
import nineBoxMain.MainActivity;
import nineBoxQuestions.Questions;
import nineBoxQuestions.QuestionsOperations;

//import android.support.v7.appcompat.BuildConfig;
//import emailUtility.SendHTMLEmail;

/**
 * Created by Paul Gallini on 5/11/16.
 *
 * This activity drives the generation and presentation of the results grid.
 */
public class ReportActivity extends AppCompatActivity implements OnShowcaseEventListener {
    private Toolbar toolbar;
    private CandidateOperations candidateOperations;
    private ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
    private QuestionsOperations questionsOperations;
    private ArrayList<Questions> questionsList;
    private EvaluationOperations evaluationOperations;
    private Candidates currCandidate;
    private double result_X_axis = 0;
    private double result_Y_axis = 0;
    CustomDrawableView mCustomDrawableView;
    ShowcaseView sv;   // for the showcase (tutorial) screen:
    ShowcaseView sv2;
    private Tracker mTracker;  // used for Google Analytics

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
        // get dimenstions from the integers file
        Resources res =  this.getResources();
        float scale = res.getDisplayMetrics().density;

        int reportgrid_height = 0;
        int reportgrid_width = 0;
        // calc the width of the circle ...
        int widget_width = 0;
        double widget_width_scale_factor = 0.0;
        // Calculate ActionBar height to use when in landscape mode ?
        int actionBarHeight = 0;
        int heightAdj = 0;
        int leftBound = 0;
        int rightBound = 0;
        int topBound = 0;
        int bottomBound = 0;
        int rightBoundAddon = 0;
        int leftBoundAddon = 0;
        int reportgrid_width_adjusted = 0;
        TypedValue tv = new TypedValue();

        // display message if we can't rotate
        checkOrientationChanged();
        // set it so user cannot rotate the screen for app versions < M
        // This is needed because I have been unable to get the report to draw properly in landscape
        // for earlier app versions.  Marshmellow introduces addLayer other methods that help a lot
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // for API 22 and below, we need to prevent rotating to portrait
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        }
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        // if we're in landscape mode, it appears we need to subtract the height of the ActionBar (scaled)
        if( getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            heightAdj = (int) ((actionBarHeight * scale) + res.getDimension(R.dimen.margin_small));
            reportgrid_height = (int) Math.min(res.getDisplayMetrics().widthPixels, res.getDisplayMetrics().heightPixels) - heightAdj;
            reportgrid_width = reportgrid_height;
            widget_width = reportgrid_height / 4  ;


        } else {
            reportgrid_height = (int) Math.min(res.getDisplayMetrics().widthPixels, res.getDisplayMetrics().heightPixels);
            reportgrid_width = reportgrid_height;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                // trying to factor-in scale because the widgets are too large on lower density devices
                widget_width = reportgrid_height / (12 / (int) scale);
            }
            else {
                // trying to factor-in scale because the widgets are too large on lower density devices
                // changing widget width doesn't seem to matter for API 21 and 22
                widget_width = reportgrid_height / 20 ;
                // for API 22 and below, we need to turn-off the ability to flip the report to portrait (the rest of the app should still flip)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            }
        }

        // for early API levels - we need to use a temp drawable to manage adding layers
        Drawable[] layers = new Drawable[2];
        // create a canvas upon which we will draw the final grid
        Canvas gridCanvas = new Canvas(Bitmap.createBitmap(reportgrid_height, reportgrid_width, Bitmap.Config.ARGB_8888));

        // Convert Bitmap to Drawable
        Drawable mainBackgroundDrawable = new BitmapDrawable(getResources(), mainBackground_mb);

        // for cases where API level is 23 or greater, we can use addLayer so we
        // create drawable array starting with just our background
        Drawable finalGrid[] = new Drawable[]{mainBackgroundDrawable};
        // create a custom layerDrawable object ... to which we will add our circles
        GridLayerDrawable layerDrawable = new GridLayerDrawable(finalGrid);

        // using getIntrinsicHeight() inside setWidgetPosition returns different values at times ... so
        //    we will try grabbing it once and passing it in ...
        int gridHeight = layerDrawable.getIntrinsicHeight();

        GridLayerDrawable tmpLayerDrawable;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // for API 22 and below, we start with adding the background grid to layer 1
            layers[0] = mainBackgroundDrawable;
        }
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

        // grab the number of candidates ....
        int numberOfCandidates = candidatesList.size();

        for (int i = 0; i < numberOfCandidates; i++) {

            currCandidate = candidatesList.get(i);

            // grab the next available color ...
            String currentColor = currCandidate.getCandidateColor();
            // convert the String color to an int
            int tmpcolor = Color.parseColor(currentColor);
            Drawable d1 = ResourcesCompat.getDrawable(getResources(), R.drawable.empty_drawable, null);

            Drawable[] emptyDrawableLayers = {d1};

            drawPoint currDrawPoint = new drawPoint(getApplicationContext(), emptyDrawableLayers, 6, 6, tmpcolor);
            LayerDrawable newPoint = currDrawPoint.getPoint(currCandidate.getCandidateInitials());
            Drawable tempPoint = newPoint.mutate();

            result_X_axis = get_X_ResultForCandiate(currCandidate);
            result_Y_axis = get_Y_ResultForCandiate(currCandidate);

            if ( widgetsWillOverlap( result_X_axis, result_Y_axis, i, candidatesList )) {
                // If this icon/widget will overlap with one already drawn, then make small adjustments so both are visible
                result_X_axis = makeSmallAdjustment( result_X_axis );
                result_Y_axis = makeSmallAdjustment( result_Y_axis );
            }

            // if API level is 23 or greater, than we can use addLayer
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                // Note - I'm unclear on the last three parameters
                layerDrawable.addLayer(tempPoint, 4, widget_width, widget_width);
                layerDrawable.setLayerSize(currLayer, widget_width, widget_width);
                layerDrawable.setWidgetPosition(currLayer, result_X_axis, result_Y_axis, widget_width, gridHeight);

            } else {
                // for API 22 and below, we have to add the new point to a new layer within the
                //    array and then replace the index 0 layer of the original
                layers[1] = tempPoint;

                // by creating tmpLayerDrawable, we collapse the existing layers into one
                layerDrawable = new GridLayerDrawable(layers);

                //  the index (1) is hard-coded because we smush the layers down at the end of each iteration
                //    params are index, left offset, top offset, right, bottom
                int adjustedResult_X_axis = (int) Math.max( 1, result_X_axis );
                int adjustedResult_Y_axis = (int) Math.max( 1, result_Y_axis );

                // if we're in landscape mode, it appears we need to subtract the height of the ActionBar (scaled)
                if( getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

                    leftBoundAddon = adjustedResult_X_axis * 6;
                    rightBoundAddon = ( 10 - adjustedResult_X_axis) * 20;
                    reportgrid_width_adjusted = reportgrid_width;
                    widget_width_scale_factor = 2;
                    leftBound = (int) (reportgrid_width_adjusted - ( reportgrid_width_adjusted * (0.1 * (10 - adjustedResult_X_axis ))) - (widget_width/widget_width_scale_factor)) + leftBoundAddon;
                    topBound =  (int) (( reportgrid_height * (0.1 * (10 - adjustedResult_Y_axis ))) - (widget_width/widget_width_scale_factor));
                    rightBound = reportgrid_width_adjusted - (leftBound + (int) (widget_width / widget_width_scale_factor) ) + rightBoundAddon;
                    bottomBound = reportgrid_height - ( topBound + (int) (widget_width / widget_width_scale_factor ));

                    layerDrawable.setLayerInset(1, leftBound, topBound, rightBound, bottomBound);

                } else {
                    widget_width_scale_factor = 32;
                    leftBound = (int) (reportgrid_width - ( reportgrid_width * (0.1 * (10 - adjustedResult_X_axis ))) - (widget_width/widget_width_scale_factor));
                    topBound =  (int) (( reportgrid_height * (0.1 * (10 - adjustedResult_Y_axis ))) - (widget_width/widget_width_scale_factor));
                    rightBound = reportgrid_width - leftBound + ((int) (widget_width / widget_width_scale_factor) );
                    bottomBound = reportgrid_height - topBound + (int) (widget_width / widget_width_scale_factor );

                    layerDrawable.setLayerInset(1, leftBound, topBound, rightBound, bottomBound);
                }
                // reset the layers so that the base (0) layer is the grid background plus any icons added thus far ..
                layers[0] = layerDrawable;
            }
            currLayer++;
        }
        // end Loop

        // Now that we are done building the grid, add it to our View ....
        ImageView gridImageView = (ImageView) findViewById(R.id.grid_background);
        layerDrawable.draw(gridCanvas);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // setting layout params for the gridImageView ....
            android.view.ViewGroup.LayoutParams lp = gridImageView.getLayoutParams();
            lp.width = gridCanvas.getWidth() ;    // 5 almost works for API 21
            lp.height = gridCanvas.getHeight() ;
            gridImageView.requestLayout();
        }

        gridImageView.setImageDrawable(layerDrawable);

        // see if we should show the Tutorial ....
        if( getShowTutorial_Rpt()) {
            displayTutorialRpt();
            // Now that it's been displayed, lets turn it off
            MainActivity.displayTutorialRpt = false;
        }
        // convert the layerDrawable to bitmap so we can save it ...
        Bitmap bitMapToSave = drawableToBitmap(layerDrawable);

        File created_folder = getDir("custom", MODE_PRIVATE);
        File dir = new File(created_folder, "custom_child");
        dir.mkdirs();

        boolean doSave = true;
        if (!dir.exists()) {
            doSave = dir.mkdirs();
        }
        if (doSave) {
            saveBitmapToFile(dir, "current_report.png", bitMapToSave, Bitmap.CompressFormat.PNG, 100);
        } else {
            Log.e("app", "Couldn't create target directory.");
        }

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        sendScreenImageName(); // send tag to Google Analytics

        findViewById(R.id.save_report).setOnClickListener(new View.OnClickListener() {
                                                              @Override
                                                              public void onClick(View view) {
                                                                  printDocument(view);
                                                              }
                                                          }
        );

        findViewById(R.id.cancel_report).setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    finish();
                                                                }
                                                            }
        );
    }

    double makeSmallAdjustment( double incomingResult ) {
        double adjustedResult = 0.0;

        if ( incomingResult < 4.9) {
            adjustedResult = incomingResult + 0.4;
        } else {

            adjustedResult = incomingResult - 0.4;
        }
        return adjustedResult;
    }

    boolean widgetsWillOverlap( double result_X_current, double result_Y_current, int currPosition, ArrayList<Candidates>  candidatesList) {
        boolean boolResult = false;

        for (int i = 0; i < currPosition; i++) {

            currCandidate = candidatesList.get(i);
            double existing_X = get_X_ResultForCandiate(currCandidate);
            double existing_Y = get_Y_ResultForCandiate(currCandidate);

            // If both X & Y are within 0.3 of another icon, return true
            if( Math.abs( existing_X - result_X_current ) < 0.3 &&
                    Math.abs( existing_Y - result_Y_current ) < 0.3) {
                boolResult = true;
            }
        }

            return boolResult;
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkOrientationChanged();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // TODO Remove
        System.out.println( "getResources().getConfiguration().orientation = ");
        System.out.println( getResources().getConfiguration().orientation);

        // Display message if user trying to go landscape and API < 23
        checkOrientationChanged();

    }

    // TODO - get this to work when rotation happens during viewing the report
    private void checkOrientationChanged() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "Landscape rotation Not supported for this feature.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean getShowTutorial_Rpt() {
        // returns value for whether to show tutorial for Report screen or not
        Boolean returnBool = false;
        SharedPreferences settings = getSharedPreferences("preferences", Context.MODE_PRIVATE);;
        Boolean showTutorial = settings.getBoolean("pref_sync", true);
        if(showTutorial & MainActivity.displayTutorialRpt) { returnBool = true; }
        return returnBool;
    }

    private void displayTutorialRpt() {
        // set-up Layout Parameters for the tutorial
        final RelativeLayout.LayoutParams lps = getLayoutParms();
        // locate the target for the hint
        ViewTarget target = new ViewTarget(R.id.grid_background, this) {
            @Override
            public Point getPoint() {
                return Utilities.getPointTarget(findViewById(R.id.grid_background),1.2,4);
            }
        };
        // Create an OnClickListener to use with Tutorial and to display the next page ...
        View.OnClickListener tutBtnListener = new View.OnClickListener() {
            public void onClick(View v) {
                ViewTarget target2 = new ViewTarget(R.id.save_report, ReportActivity.this) {
                    @Override
                    public Point getPoint() {
                        return Utilities.getPointTarget(findViewById(R.id.save_report), 2);
                    }
                };
                // hide the previous view
                sv.hide();
                sv2 = buildTutorialView(target2, R.string.showcase_rpt_message2, null);
                sv2.setButtonText(getResources().getString(R.string.showcase_btn_last));
                sv2.setButtonPosition(lps);
            }
        };
        // instantiate a new view for the the tutorial ...
        sv = buildTutorialView(target, R.string.showcase_rpt_message1, tutBtnListener);
        sv.setButtonPosition(lps);
        MainActivity.displayTutorialRpt = false;
        SharedPreferences settings = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        Utilities.evalTutorialToggles(editor);
    }

    // TODO see if we can combine this with others
    private ShowcaseView buildTutorialView(ViewTarget target, int tutorialText, View.OnClickListener tutBtnListener) {
        return new ShowcaseView.Builder(ReportActivity.this)
                .withHoloShowcase()    // other options:  withHoloShowcase, withNewStyleShowcase, withMaterialShowcase,
                .setTarget(target)
                .setContentTitle(R.string.showcase_main_title)
                .setContentText(tutorialText)
                .setStyle(R.style.CustomShowcaseTheme)
                .setShowcaseEventListener(ReportActivity.this)
                .replaceEndButton(R.layout.view_custom_button)
                .setOnClickListener(tutBtnListener)
                .build();
    }

    // TODO find a way to combine this with the same method in MainActivity
    private RelativeLayout.LayoutParams getLayoutParms() {
        // set-up Layout parameters for the Tutorial
        //   Some more ideas on targets:
        //        http://stackoverflow.com/questions/33379121/using-showcaseview-to-target-action-bar-menu-item
        //
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);
        return lps;
    }

    // Moving to it's own class to facilitate varying by Build Flavor
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void printDocument(View view) {
        Tracker mTracker;  // used for Google Analytics

        if(BuildConfig.FLAVOR == "free") {
            // If this is the Free version of the app - show the Upgrade Now dialog
            showFeatureNotAvailableDialog( this );
        } else {
            // Obtain the shared Tracker instance.
            common.AnalyticsApplication application = (common.AnalyticsApplication) getApplication();
            mTracker = application.getDefaultTracker();
            // send tag to Google Analytics
            mTracker.setScreenName("Image~" + getResources().getString(R.string.anal_tag_rpt_save));
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());

            PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
            String jobName = "Promotion_Grid_Results";
            printManager.print(jobName, new MyPrintDocumentAdapter(this),
                    null);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private String buildIconForEmail(Candidates currCandidate) {
        // build icon for this candidate, save it to storage so it can be included in the e-mail
        // amnd return a handle for the image
        String currentColor = currCandidate.getCandidateColor();
        // convert the String color to an int
        int tmpcolor = Color.parseColor(currentColor);
        Drawable d1 = getResources().getDrawable(R.drawable.empty_drawable, null);
        Drawable[] emptyDrawableLayers = {d1};

        drawPoint currDrawPoint = new drawPoint(getApplicationContext(), emptyDrawableLayers, 6, 6, tmpcolor);
        LayerDrawable newPoint = currDrawPoint.getPoint(currCandidate.getCandidateInitials());

        // convert the layerDrawable to bitmap so we can save it ...
        Bitmap bitMapToSave = drawableToBitmap(newPoint);

        File created_folder = getDir("custom", MODE_PRIVATE);
        File dir = new File(created_folder, "custom_child");
        dir.mkdirs();

        String iconBitmapName = "icon_image_" + Long.toString(currCandidate.getCandidateID()) + ".png";
        boolean doSave = true;
        if (!dir.exists()) {
            doSave = dir.mkdirs();
        }
        if (doSave) {
            saveBitmapToFile(dir, iconBitmapName, bitMapToSave, Bitmap.CompressFormat.PNG, 100);
        } else {
            Log.e("app", "Couldn't create target directory for saving icon bitmap.");
        }
        String iconImageID = "image-icon" + Long.toString(currCandidate.getCandidateID());

        return iconBitmapName;
    }

    //    }
    /*
    * Bitmap.CompressFormat can be PNG,JPEG or WEBP.
    *
    * quality goes from 1 to 100. (Percentage).
    *
    * dir you can get from many places like Environment.getExternalStorageDirectory() or mContext.getFilesDir()
    * depending on where you want to save the image.
    */
    public boolean saveBitmapToFile(File dir, String fileName, Bitmap bm,
                                    Bitmap.CompressFormat format, int quality) {

        File imageFile = new File(dir, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            bm.compress(format, quality, fos);
            fos.close();

            return true;
        } catch (IOException e) {
            Log.e("app", e.getMessage());
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }

    public Bitmap readBitmapFromFile(File dir, String fileName) {

        FileInputStream fis = null;

        Bitmap tmpBitMap = null;
        String fileString = dir + "/" + fileName;
        File file = new File(fileString);
        try {
            fis = new FileInputStream(file);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            tmpBitMap = BitmapFactory.decodeFile(fileString, options);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tmpBitMap;

    }

    public Bitmap drawableToBitmap(LayerDrawable pd) {
        Bitmap bm = Bitmap.createBitmap(pd.getIntrinsicWidth(), pd.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        pd.setBounds(0, 0, pd.getIntrinsicWidth(), pd.getIntrinsicHeight());
        pd.draw(new Canvas(bm));
        return bm;
    }

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
                }
            }
        }
        // divide result by 100, round to hundredth's place and return it.
        return ( ( (double)Math.round((result * 0.01) * 100d) / 100d));
    }

    private double get_Y_ResultForCandiate(Candidates currCandidate) {
        int currResponse = 1;
        double result = 0.0;
        int currWeight = 0;
        long candidateID = -1;
        long questionID = -1;

        candidateID = currCandidate.getCandidateID();

        for (int i = 0; i < questionsList.size(); i++) {

            questionID = questionsList.get(i).getQuestionID();


            if (candidateID != -1 && questionID != -1) {
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
                }
            }
        }
        // divide result by 100, round to hundredth's place and return it.
        return ( ( (double)Math.round((result * 0.01) * 100d) / 100d));
    }

    /**
     * Record a screen view hit for the this activity
     */
    private void sendScreenImageName() {
        String name = getResources().getString(R.string.anal_tag_report);

        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

    }

    // http://www.techotopia.com/index.php/An_Android_Custom_Document_Printing_Tutorial
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public class MyPrintDocumentAdapter extends PrintDocumentAdapter {
        Context context;
        private int pageHeight;
        private int pageWidth;
        public PdfDocument myPdfDocument;
        public int totalpages = 0;  // start pages at 0 - always increment before adding

        public int numCandidates = candidatesList.size();
        public int totalPageSize = 750; // number of pixels available on a single page
        public int canidateDetailHeight = 220;  // number of pixels taken-up by each Candidate details

        public MyPrintDocumentAdapter(Context context) {
            this.context = context;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes,
                             PrintAttributes newAttributes,
                             CancellationSignal cancellationSignal,
                             LayoutResultCallback callback,
                             Bundle metadata) {

            myPdfDocument = new PrintedPdfDocument(context, newAttributes);

            // These dimensions are stored in the object in the form of thousandths of an inch. Since the methods that
            // will use these values later work in units of 1/72 of an inch these numbers are converted before they are stored:
            pageHeight =
                    newAttributes.getMediaSize().getHeightMils() / 1000 * 72;
            pageWidth =
                    newAttributes.getMediaSize().getWidthMils() / 1000 * 72;

            // NOTE:  the user’s color selection can be obtained via a call to the getColorMode() method
            // of the PrintAttributes object which will return a value of either COLOR_MODE_COLOR or COLOR_MODE_MONOCHROME.

            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }

            // calculate the number of pages needed ...
            double spaceNeededPerPage = ( numCandidates * canidateDetailHeight );

            totalpages = (int) (Math.round(( spaceNeededPerPage / (double) totalPageSize ) +  0.5));

            totalpages = totalpages + 1; // add one for first page

            if (totalpages > 0) {
                PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                        .Builder("print_output.pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(totalpages);

                PrintDocumentInfo info = builder.build();
                callback.onLayoutFinished(info, true);
            } else {
                callback.onLayoutFailed("Page count is zero.");
            }
        }

        @Override
        public void onWrite(final PageRange[] pageRanges,
                            final ParcelFileDescriptor destination,
                            final CancellationSignal cancellationSignal,
                            final WriteResultCallback callback) {

            // Draw the Main page first ....
            PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
                    pageHeight, 1).create();

            PdfDocument.Page page =
                    myPdfDocument.startPage(newPage);

            if (cancellationSignal.isCanceled()) {
                callback.onWriteCancelled();
                myPdfDocument.close();
                myPdfDocument = null;
                return;
            }
            drawMainPage(page, 1);
            myPdfDocument.finishPage(page);

            // Now, loop through candidates and write subsequent pages ...

            candidateOperations = new CandidateOperations(this.context);
            candidateOperations.open();
            candidatesList = candidateOperations.getAllCandidates();

            if (numCandidates == 0) {
                // start the second page ...
                newPage = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
                page = myPdfDocument.startPage(newPage);
                drawDetailPageNoCanidates( page, 1 );
                myPdfDocument.finishPage(page);

            } else {

                int drawLine = 60;
                // start the next page ...
                newPage = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
                page = myPdfDocument.startPage(newPage);
                // Loop through the candidates to build the details for the PDF File
                for (int i = 0; i < numCandidates; i++) {

                    if (cancellationSignal.isCanceled()) {
                        callback.onWriteCancelled();
                        myPdfDocument.close();
                        myPdfDocument = null;
                        return;
                    }

                    // Add the details for the current Candidate to the current page
                    drawDetailPage(page, i, drawLine);
                    drawLine = drawLine + canidateDetailHeight;

                    if( drawLine > (totalPageSize - canidateDetailHeight)) {
                        // if we can't fit any more canidates on the current page, finish it and start a new one
                        myPdfDocument.finishPage(page);
                        newPage = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, i).create();
                        page = myPdfDocument.startPage(newPage);
                        drawLine = 60;
                    }

                }
                myPdfDocument.finishPage(page);
            }

            try {
                myPdfDocument.writeTo(new FileOutputStream(
                        destination.getFileDescriptor()));
            } catch (IOException e) {
                callback.onWriteFailed(e.toString());
                return;
            } finally {
                myPdfDocument.close();
                myPdfDocument = null;
            }

            callback.onWriteFinished(pageRanges);
        }

        private void drawDetailPage(PdfDocument.Page page,
                                    int candidateNum, int drawLine) {

            // variables to control placement and size of text ....
            int drawTabH1 = 48;
            int drawTabH2 = 82;
            int drawTabH3 = 120;
            int drawTabPara = 64;
            int lineSpacingBig = 42;
            int lineSpacing = 36;
            int headingMain = 32;
            int headingPara = 24;
            int lineLength = 480;
            int lineStrokeThick = 8;
            int lineStrokeThin = 6;
            int iconWidth = 80;
            int iconTab = 440;
            int iconAdjustment = 24;

            Canvas canvas = page.getCanvas();

            String detailString = " ";
            currCandidate = candidatesList.get(candidateNum);
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(headingMain);
            paint.setFakeBoldText(true);

            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(lineStrokeThick);
            canvas.drawLine( drawTabH1, drawLine, (drawTabH1 + lineLength), drawLine, paint);

            drawLine = drawLine + lineSpacingBig;

            detailString = currCandidate.getCandidateName();
            paint.setColor(Color.BLACK);
            canvas.drawText(detailString, drawTabH1, drawLine, paint);

            // grab candidate ICON from file system and draw it on the PDF page
            File created_folder = getDir("custom", MODE_PRIVATE);
            File dir = new File(created_folder, "custom_child");

            // build the candidate icon, save it to storage, and grab the file name
            String iconBitmapName = buildIconForEmail(currCandidate);

            // Read in the icon bitmap
            String fullUrl = dir + "/" + iconBitmapName;
            Bitmap candIcon = BitmapFactory.decodeFile(fullUrl);

            //scale bitmap to desired size
            Bitmap finalIconScalled = Bitmap.createScaledBitmap(candIcon, iconWidth, iconWidth, true); // Make sure w and h are in the correct order

            canvas.drawBitmap(finalIconScalled, iconTab, drawLine - iconAdjustment , paint);

            paint.setTextSize(headingPara);
            detailString = "(initials: ";
            detailString += currCandidate.getCandidateInitials();
            detailString += ")   ";
            drawLine = drawLine + lineSpacing;
            canvas.drawText(detailString, drawTabH1, drawLine, paint);

            detailString = "Scores (out of 10): ";
            drawLine = drawLine + lineSpacing;
            canvas.drawText(detailString, drawTabH2, drawLine, paint);

            detailString = "Performance:  ";
            detailString += get_X_ResultForCandiate(currCandidate);
            drawLine = drawLine + lineSpacing;
            canvas.drawText(detailString, drawTabH3, drawLine, paint);

            detailString = "Promotability:  ";
            detailString += get_Y_ResultForCandiate(currCandidate);
            drawLine = drawLine + lineSpacing;
            canvas.drawText(detailString, drawTabH3, drawLine, paint);
        }


        private void drawDetailPageNoCanidates(PdfDocument.Page page, int drawLine) {

            // variables to control placement and size of text ....
            int drawTabH1 = 48;
            int drawTabH2 = 82;
            int lineSpacingBig = 42;
            int lineSpacing = 36;
            int headingMain = 32;
            int headingPara = 24;
            int lineLength = 480;
            int lineStrokeThick = 8;

            Canvas canvas = page.getCanvas();

            String detailString = "No Candidates were entered. ";

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(headingMain);
            paint.setFakeBoldText(true);

            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(lineStrokeThick);
            canvas.drawLine( drawTabH1, drawLine, (drawTabH1 + lineLength), drawLine, paint);

            drawLine = drawLine + lineSpacingBig;

            paint.setColor(Color.BLACK);
            canvas.drawText(detailString, drawTabH1, drawLine, paint);

            paint.setTextSize(headingPara);

            detailString = "Please select Add People from the main menu. ";
            drawLine = drawLine + lineSpacing;
            canvas.drawText(detailString, drawTabH2, drawLine, paint);
        }


        private void drawMainPage(PdfDocument.Page page,
                                  int pagenumber) {
            Canvas canvas = page.getCanvas();

            pagenumber++; // Make sure page numbers start at 1

            // variables to control placement and size of text ....
            int drawTabH1 = 48;
            int drawTabPara = 64;
            int drawLine = 60;
            int lineSpacing = 36;
            int headingMain = 32;
            int headingPara = 24;

            Paint paint = new Paint();
            int titleBaseLine = 72;
            int leftMargin = 54;
            String subject = "Results from Promotion Grid";

            String Message = "Greetings! ";
            paint.setColor(Color.BLACK);
            paint.setTextSize(headingMain);
            paint.setFakeBoldText(true);
            canvas.drawText(Message, drawTabH1, drawLine, paint);
            Message = "Here are your results from the Promotion Grid  ";
            paint.setTextSize(headingPara);
            paint.setFakeBoldText(false);
            drawLine = drawLine + lineSpacing; // increment the line on which the text will be drawned
            canvas.drawText(Message, drawTabPara, drawLine, paint);

            drawLine = drawLine + lineSpacing; // increment the line on which the text will be drawned
            Message = "app.  We've included the main grid plus details";
            canvas.drawText(Message, drawTabPara, drawLine, paint);

            drawLine = drawLine + lineSpacing; // increment the line on which the text will be drawned
            Message = "on each person. ";
            canvas.drawText(Message, drawTabPara, drawLine, paint);

            // grab grid from file system and draw it on the PDF page
            File created_folder = getDir("custom", MODE_PRIVATE);
            File dir = new File(created_folder, "custom_child");

            Bitmap finalGrid = readBitmapFromFile(dir, "current_report.png");

            Paint paint2 = new Paint();
            paint2.setAntiAlias(true);
            paint2.setFilterBitmap(true);
            paint2.setDither(true);

            //scale bitmap
            int h = 400; // height in pixels
            int w = 400; // width in pixels
            Bitmap finalGridScalled = Bitmap.createScaledBitmap(finalGrid, w, h, true); // Make sure w and h are in the correct order

            canvas.drawBitmap(finalGridScalled, 120, 220, paint2);
        }
    }

    // TODO find way to centralize this.  Can't simply add it to Utilites (can't call non-static method from static context)
    private void showFeatureNotAvailableDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
        builder.setIcon(R.drawable.ic_pg_icon);
        builder.setTitle(getString(R.string.feature_not_available_title));
        builder.setMessage(getString(R.string.feature_not_available_message));

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If they click OK, take them to the App Store to buy the Pro version of the app
                        try
                        {
                            Intent rateIntent = upgradeIntentForUrl("market://details");
                            startActivity(rateIntent);
                        }
                        catch (ActivityNotFoundException e)
                        {
                            Intent rateIntent = upgradeIntentForUrl("https://play.google.com/store/apps/details");
                            startActivity(rateIntent);
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
        dialog.show();
    }

    private Intent upgradeIntentForUrl(String url)
    {
        String targetPackageName = getResources().getString(R.string.package_name_pro);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, targetPackageName)));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21)
        {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        }
        else
        {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }
}



