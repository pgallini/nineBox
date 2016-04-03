package nineBoxCandidates;

import java.util.ArrayList;
import java.util.Scanner;

import nineBoxQuestions.Questions;
/**
 * Created by Paul Gallini on 2/22/16.
 */
public class Candidates {
    private String candidateName = " ";
    private String candidateNotes = " ";
    private int xCoordinate = 0;
    private int yCoordinate = 0;
    private long candidateID = 0;
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

    public Candidates(String candidateName, Questions questionSet_X_Axis, Questions questionSet_Y_Axis, Scanner scanner) {
        super();
        this.candidateName = candidateName;
        int tempResponse = 1;

        ArrayList<String> tmpQuestionText_X_Axis = questionSet_X_Axis.getQuestionText();

        for( String qText : tmpQuestionText_X_Axis) {
            tempResponse = pomptForResponse(qText, scanner);
            responseSet.addQuestionResponse(tempResponse);
        }

        ArrayList<String> tmpQuestionText_Y_Axis = questionSet_Y_Axis.getQuestionText();

        for( String qText : tmpQuestionText_Y_Axis) {
            tempResponse = pomptForResponse(qText, scanner);
            responseSet.addQuestionResponse(tempResponse);
        }
    }

    public int calcCandidate_Coordinate(Questions questionSet) {
        ArrayList<Integer> tmpQuestionWeight;
        tmpQuestionWeight = questionSet.getQuestionWeight();
        int coordinate = 0;
        int currResponse = 0;
        int i = 0;

        for( int qText : tmpQuestionWeight) {
            currResponse = responseSet.getQuestionResponse(i);
            i++;

            coordinate = coordinate + ( qText * currResponse);
            System.out.println("weight = " + qText + "  response = " + currResponse + "  xCoordinate = " + coordinate);
        }
        return coordinate;
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



}
