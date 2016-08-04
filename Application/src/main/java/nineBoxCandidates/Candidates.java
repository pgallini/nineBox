package nineBoxCandidates;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import com.ninebox.nineboxapp.R;
import java.util.Scanner;
import drawables.drawPoint;
/**
 * Created by Paul Gallini on 2/22/16.
 */
public class Candidates {
    private String candidateName = " ";
    private String candidateNotes = " ";
    private int xCoordinate = 0;
    private int yCoordinate = 0;
    private long candidateID = 0;
    private String candidateColor = " ";
    private String candidateInitials = " ";
    private Responses responseSet = new Responses();

    private int pomptForResponse(String qText, Scanner scanner) {
        int tmpResponse = 1;
        System.out.println(qText.toString());
        System.out.print("Enter response [1 ... 10]: ");

        boolean needValidResp = true;
        while( needValidResp ) {
            String respStr = scanner.nextLine();
            needValidResp = false;
            try {
                tmpResponse = Integer.parseInt(respStr);
                if(tmpResponse > 10 || tmpResponse < 1) {
                    needValidResp = true;
                    System.out.println("Invalid Response!!!  Please enter an integer between 1 and 10:  ");
                }
            } catch (NumberFormatException e) {
                needValidResp = true;
                System.out.println("Invalid Response!!!  Please enter an integer between 1 and 10:  ");
            }
        }
        return tmpResponse;
    }


    public Candidates() {
        super();
        int tempResponse = 1;
    }

    public Candidates(String candidateName) {
        super();
        this.candidateName = candidateName;
        int tempResponse = 1;
    }

    static public LayerDrawable get_icon(Context context, String currentColor,String candidateInitials ) {
        // this method generates and returns an icon for a candidate
        // convert the String color to an int
        int tmpcolor = Color.parseColor(currentColor);
        // set-up current icon based on the current color for this candidate ...
        Drawable d1 =  context.getResources().getDrawable(R.drawable.empty_drawable, null);
        Drawable[] emptyDrawableLayers = {d1};
        drawPoint currDrawPoint = new drawPoint(context, emptyDrawableLayers, 6, 6, tmpcolor);
        LayerDrawable newPoint = currDrawPoint.getPoint( candidateInitials );

        return newPoint;
    }

    public int getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public void setCandidateName( String name ) { candidateName = name; }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateNotes( String notes ) { candidateNotes = notes; };

    public String getCandidateNotes() { return candidateNotes; }

    public void setCandidateID( long id ) { candidateID = id; }

    public long getCandidateID() { return candidateID; }

    public String getCandidateColor() { return candidateColor; }

    public void setCandidateColor(String candidateColor) { this.candidateColor = candidateColor; }

    public String getCandidateInitials() { return candidateInitials; }

    public void setCandidateInitials(String candidateInitials) { this.candidateInitials = candidateInitials; }

}
