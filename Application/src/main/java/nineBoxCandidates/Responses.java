package nineBoxCandidates;

import java.util.ArrayList;

/**
 * Created by Paul Gallini on 2/22/16.
 */
public class Responses {
    private ArrayList<Integer> questionResponse = new ArrayList<Integer>();

    public void addQuestionResponse(int newResponse) {
        questionResponse.add(newResponse);
    }

    public void addQuestionResponse(int ord, int newText) {
        if( ord <= questionResponse.size()) {
            questionResponse.add(ord, newText);
        }
        else
        {
            System.out.println("Cannot insert Question Response at given position, it does not exist.  Position = " + ord);
        }
    }

    public int getQuestionResponse(int i) {
        return questionResponse.get(i);
    }
}
