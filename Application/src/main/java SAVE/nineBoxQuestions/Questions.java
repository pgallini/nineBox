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
    private ArrayList<String> questionText = new ArrayList<String>();
    private ArrayList<Integer> questionWeight = new ArrayList<Integer>();

    public void addQuestionText(String newText) {
        questionText.add(newText);
    }

    public void addQuestionText(int ord, String newText) {
        if( ord <= questionText.size()) {
            questionText.add(ord, newText);
        }
        else
        {
            System.out.println("Cannot insert Question Text at given position, it does not exist.  Position = " + ord);
        }
    }

    public void addQuestionWeight(int newResponse) {
        questionWeight.add(newResponse);
    }

    public void addQuestionWeight(int ord, int newText) {
        if( ord <= questionWeight.size()) {
            questionWeight.add(ord, newText);
        }
        else
        {
            System.out.println("Cannot insert Question Weight at given position, it does not exist.  Position = " + ord);
        }
    }

    public ArrayList<String> getQuestionText() {
        return questionText;
    }

    public ArrayList<Integer> getQuestionWeight() {
        return questionWeight;
    }
}
