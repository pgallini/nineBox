package nineBoxReport;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.PictureDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import emailUtility.SendHTMLEmail;
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
        for (int i = 0; i < candidatesList.size(); i++) {

            currCandidate = candidatesList.get(i);

            // grab the next available color ...
            String currentColor = currCandidate.getCandidateColor();
            // convert the String color to an int
            int tmpcolor = Color.parseColor(currentColor);
            // TODO - see if there is a cleaner way to do this ...
            Drawable d1 = getResources().getDrawable(R.drawable.empty_drawable, null);
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


        findViewById(R.id.send_report).setOnClickListener(new View.OnClickListener() {
                                                              @Override
                                                              public void onClick(View view) {
                                                                  try {
                                                                      promptEmailSendReport();
                                                                  } catch (MessagingException e) {
                                                                      e.printStackTrace();
                                                                  }
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

    private void promptEmailSendReport() throws MessagingException {

        UserOperations userOperations = new UserOperations(this);
        userOperations.open();
        User currentUser = userOperations.getUser(1);

        String currentEmailAddress = currentUser.getUserEmail();

        // TODO remove
        System.out.println("currentEmailAddress before dialog = ");
        System.out.println(currentEmailAddress);

        // prompt for email address
        showEditEmailDialog(currentEmailAddress);

    }

    private void sendReport(String mailTo) throws MessagingException {
        String host = "smtp.gmail.com";
        String port = "587";
        String mailFrom = "funkynetsoftware@gmail.com";
        String password = "********";

//        // TODO remove
        System.out.println("mailTo = ");
        System.out.println(mailTo);

        // TODO move these constants to a resouce file dude
        // outgoing message information
//        String mailTo = currentEmailAddress;
        String subject = "Results from Promotion Grid";
        // outgoing message information
        MimeMultipart multipart = new MimeMultipart("mixed");

        String introMessage = "<H1>Greetings!</H1><br><H3>   Here are your results from the Promotion Grid app.  ";
        introMessage += "  We've included the main grid plus details on each person.  </H3> <br><br>";
        String reportHtmlText = "<H1>Details on each candidate</H1>";

        MimeBodyPart messageBodyPart_details;
        messageBodyPart_details = buildReportDetails();

        // first part (the html)
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        String htmlText = introMessage + "<img src=\"cid:image\">" + reportHtmlText;

        try {
            messageBodyPart.setContent(htmlText, "text/html");
            // add it
            multipart.addBodyPart(messageBodyPart);

            // second part (the main report image)
            messageBodyPart = new MimeBodyPart();
            DataSource fds = new FileDataSource("/data/user/0/com.ninebox.nineboxapp/app_custom/custom_child/current_report.png");

            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<image>");

            // add image to the multipart
            multipart.addBodyPart(messageBodyPart);

            // now, add the candidate details
            messageBodyPart_details.setHeader("Content-ID", "<details>");

            messageBodyPart_details.setDisposition(MimeBodyPart.INLINE);
            multipart.addBodyPart(messageBodyPart_details);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        SendHTMLEmail mailer = new SendHTMLEmail();

        try {
            // here we actually try to send the e-mail
            mailer.sendHtmlEmail(host, port, mailFrom, password, mailTo,
                    subject, multipart);
//            Toast.makeText(ReportActivity.this,
//                    R.string.email_sent_success, Toast.LENGTH_LONG).show();
            System.out.println("Email sent.");
        } catch (Exception ex) {
//            Toast.makeText(ReportActivity.this,
//                    R.string.email_sent_failure, Toast.LENGTH_LONG).show();
            System.out.println("Failed to sent email.");
            ex.printStackTrace();
        }
    }

    private void showEditEmailDialog(String currentEmail) {

        // Get the layout inflater
        LayoutInflater inflater = ReportActivity.this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View dialoglayout = inflater.inflate(R.layout.user_edit_email, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
        builder.setView(dialoglayout);

        builder.setTitle(getString(R.string.edit_user_email_hint));
        builder.setMessage(getString(R.string.confirm_edit_email_message));

        TextView emailText = (TextView) dialoglayout.findViewById(R.id.email_address);
        emailText.setText(currentEmail);

        final UserOperations userOperations = new UserOperations(this);
        userOperations.open();

        final User currentUser = userOperations.getUser(1);
//        final String newEmail;
        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // grab the initials

                        TextView emailText = (TextView) dialoglayout.findViewById(R.id.email_address);
                        if (emailText != null) {
                            // now set the User email address
                            final String newEmail = emailText.getText().toString();
                            currentUser.setUserEmail(newEmail);
                            // save new email to database
                            userOperations.updateUserEmail(1, newEmail);
                            // now send the report
                            // trying to run this as a background thread
                            Toast.makeText(ReportActivity.this,R.string.email_sent_background, Toast.LENGTH_LONG).show();
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        sendReport(newEmail);
                                    } catch (MessagingException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();

                            Toast.makeText(ReportActivity.this,
                                    R.string.email_save_message,
                                    Toast.LENGTH_LONG).show();
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

    private MimeBodyPart buildReportDetails() throws MessagingException {
        String detailString = " ";
        String detailTextString = " ";
        String currentColor = " ";
//        MimeBodyPart messageBodyPart_details = new MimeBodyPart();
        MimeMultipart messageMultipart = new MimeMultipart("alternative");
        MimeBodyPart returnBodyPart = new MimeBodyPart();
        MimeMultipart tempMessageMultipart = new MimeMultipart("alternative");
        MimeBodyPart tempReturnBodyPart = new MimeBodyPart();
        MimeBodyPart candidateBodyPart = new MimeBodyPart();

        candidateOperations = new CandidateOperations(this);
        candidateOperations.open();
        candidatesList = candidateOperations.getAllCandidates();
        int numCandidates = candidatesList.size();
        if (numCandidates == 0) {
            detailString = "No Candidates were entered.";
            // TODO - add this to SetContent
        } else {

            // Loop through the candidates to build the details for the e-mail
            for (int i = 0; i < numCandidates; i++) {
                currCandidate = candidatesList.get(i);
                detailTextString = createDetailTextString(currCandidate);
                // build the candidate icon, save it to storage, and grab the file name
                String iconBitmapName = buildIconForEmail(currCandidate);
                // build the name for this candidate's icon image
                String iconImageID = "image-icon" + Long.toString(currCandidate.getCandidateID());
                // add onto the detailString with the placeholder for the icon image
                detailString = detailString + "<img src=\"cid:" + iconImageID + "\">" + detailTextString + "<br>";
                // TODO remove
//                System.out.println(" detailString = ");
//                System.out.println(detailString);

                try {

                    DataSource fds = new FileDataSource(
                            "/data/user/0/com.ninebox.nineboxapp/app_custom/custom_child/" + iconBitmapName);

                    // create a MIMEBodyPart and add each icon image as we loop
                    MimeBodyPart candidateIconBodyPart = new MimeBodyPart();
                    candidateIconBodyPart.setDisposition(MimeBodyPart.INLINE);
                    candidateIconBodyPart.setDataHandler(new DataHandler(fds));
                    candidateIconBodyPart.setHeader("Content-ID", "<" + iconImageID + ">");

                    // add BodyPart containing the icon image to the multipart
                    messageMultipart.addBodyPart(candidateIconBodyPart);

                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
        // Now that we are out of the loop and have assembled the string holding the HTML for the details
        //   build a MIME Body part to hold it
        candidateBodyPart.setContent(detailString, "text/html");
        // now add that body part to the MultiPart
        messageMultipart.addBodyPart(candidateBodyPart);

        // convert multi to body and add it to another multi
        // TODO - see if we can just return the candidateBodyPart
        try {
            tempReturnBodyPart.setContent(messageMultipart);
            try {
                tempMessageMultipart.addBodyPart(tempReturnBodyPart);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        // convert the multipart to a regular body part so we can return it ( and it can be added to the main multipart)
        try {
            // trying this to see if the BodyPart can hold on to ALL of the lines
            returnBodyPart.setDisposition(MimeBodyPart.INLINE);
            returnBodyPart.setContent(tempMessageMultipart);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return returnBodyPart;
    }

    private String createDetailTextString(Candidates currCandidate) {
        String detailString = "<strong>";
        detailString += currCandidate.getCandidateName();
        detailString += "</strong>";
        detailString += "   (initials: ";
        detailString += currCandidate.getCandidateInitials();
        detailString += ")   ";
        detailString += "<br>";
        detailString += "-            Performance Score (out of 10): ";
        detailString += currCandidate.getxCoordinate();
        detailString += "<br>";
        detailString += "-            Promotability Score (out of 10): ";
        detailString += currCandidate.getyCoordinate();
        detailString += "<br>";
        detailString += "<br>";
        return detailString;
    }

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

}


