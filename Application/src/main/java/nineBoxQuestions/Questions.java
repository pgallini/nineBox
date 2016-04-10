package nineBoxQuestions;

/**
 * Created by ase408 on 2/22/16.
 */

import java.util.ArrayList;

public class Questions {
    // The base class Question allows for a collection of questions including:
    //    - Question Text - the question itself
    //    - Question Weight - how much weight does the question carry?
    //
    private long questionID = 0;
    private String questionText;
    private Integer questionWeight;
    private ArrayList<String> questionTextList = new ArrayList<String>();
    private ArrayList<Integer> questionWeightList = new ArrayList<Integer>();

    // TODO figure out if we really need these ....
//    public void addQuestionText(String newText) {
//        questionTextList.add(newText);
//    }

//    public void addQuestionText(int ord, String newText) {
//        if( ord <= questionTextList.size()) {
//            questionTextList.add(ord, newText);
//        }
//        else
//        {
//            System.out.println("Cannot insert Question Text at given position, it does not exist.  Position = " + ord);
//        }
//    }

//    public void addQuestionWeightList(int newResponse) {
//        questionWeightList.add(newResponse);
//    }
//
//    public void addQuestionWeightList(int ord, int newText) {
//        if( ord <= questionWeightList.size()) {
//            questionWeightList.add(ord, newText);
//        }
//        else
//        {
//            System.out.println("Cannot insert Question Weight at given position, it does not exist.  Position = " + ord);
//        }
//    }

    public ArrayList<String> getQuestionTextList() {
        return questionTextList;
    }

    public ArrayList<Integer> getQuestionWeightList() {
        return questionWeightList;
    }

    public void setQuestionText( String text ) { questionText = text; }

    public String getQuestionText() { return questionText; }

    public void setQuestionWeight( int newWeight ) { questionWeight = newWeight; };

    public int getQuestionWeight() { return questionWeight; }

    public void setQuestionID( long id ) { questionID = id; }

    public long getQuestionID() { return questionID; }
}
