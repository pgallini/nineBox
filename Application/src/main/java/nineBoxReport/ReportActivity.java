package nineBoxReport;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.ninebox.nineboxapp.R;

import android.graphics.drawable.LayerDrawable;
import android.graphics.Canvas;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

//import emailUtility.SendHTMLEmail;
import nineBoxCandidates.CandidateOperations;
import nineBoxCandidates.Candidates;
import drawables.drawPoint;
import nineBoxEvaluation.EvaluationOperations;
import nineBoxMain.User;
import nineBoxMain.UserOperations;
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

    @TargetApi(Build.VERSION_CODES.M)
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
        // grab the number of candidates ....
        int numberOfCandidates = candidatesList.size();
        for (int i = 0; i < numberOfCandidates; i++) {

            currCandidate = candidatesList.get(i);

            // grab the next available color ...
            String currentColor = currCandidate.getCandidateColor();
            // convert the String color to an int
            int tmpcolor = Color.parseColor(currentColor);
            // TODO - see if there is a cleaner way to do this ...
            Drawable d1 = null;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                d1 = getResources().getDrawable(R.drawable.empty_drawable, null);
            d1 = ResourcesCompat.getDrawable(getResources(), R.drawable.empty_drawable, null);
//            }
            Drawable[] emptyDrawableLayers = {d1};

            drawPoint currDrawPoint = new drawPoint(getApplicationContext(), emptyDrawableLayers, 6, 6, tmpcolor);
            LayerDrawable newPoint = currDrawPoint.getPoint(currCandidate.getCandidateInitials());

//            Drawable tempPoint = getSingleDrawable(newPoint);
            Drawable tempPoint = newPoint.mutate();

            // TODO figure out the purpose of the last two params - they don't seem to do anything
            layerDrawable.addLayer(tempPoint, 4, widget_width, widget_width);
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

        // convert the layerDrawable to bitmap so we can save it ...
        Bitmap bitMapToSave = drawableToBitmap(layerDrawable);

        File files_folder = getFilesDir();
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void printDocument(View view) {

        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        String jobName = "Promotion_Grid_Results";
        printManager.print(jobName, new MyPrintDocumentAdapter(this),
                null);
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
        // TODO get rid of one of these
        File file = new File(dir + "/" + fileName);
        String fileString = dir + "/" + fileName;

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
        // divide result by 100 and return it.
        return (result * 0.01);
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
        // divide result by 100 and return it.
        return (result * 0.01);
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
            // TODO Remove
            System.out.println("in constructor MyPrintDocumentAdapter ");
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

            // TODO Remove
            System.out.println( "( spaceNeededPerPage / (double) totalPageSize ) +  0.5))= " );
            System.out.println( ( spaceNeededPerPage / (double) totalPageSize ) +  0.5);

            totalpages = (int) (Math.round(( spaceNeededPerPage / (double) totalPageSize ) +  0.5));
            // TODO Remove
            System.out.println("totalpages =  ");
            System.out.println(totalpages);

            totalpages = totalpages + 1; // add one for first page
            // TODO Remove
            System.out.println("totalpages =  ");
            System.out.println(totalpages);
            System.out.println("numCandidates =  ");
            System.out.println(numCandidates);

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

                    // TODO Remove
                    System.out.println("after call to drawrDetailPage - i =  ");
                    System.out.println(i);
                    System.out.println("after call to drawrDetailPage - drawLine =  ");
                    System.out.println(drawLine);
                    System.out.println("after call to drawrDetailPage - canidateDetailHeight =  ");
                    System.out.println(canidateDetailHeight);
                    System.out.println("after call to drawrDetailPage - (totalPageSize - canidateDetailHeight) =  ");
                    System.out.println((totalPageSize - canidateDetailHeight));

                    if( drawLine > (totalPageSize - canidateDetailHeight)) {
                        // TODO Remove
                        System.out.println("about to star a new page  ");
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

            // TODO fix the placement of this
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
}



