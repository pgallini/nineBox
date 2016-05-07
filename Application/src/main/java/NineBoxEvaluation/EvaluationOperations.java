package nineBoxEvaluation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ninebox.nineboxapp.DatabaseOpenHelper;

import java.util.ArrayList;

import nineBoxQuestions.Questions;

/**
 * Created by Paul Gallini on 4/8/16.
 */
public class EvaluationOperations {

    // Database fields
        private DatabaseOpenHelper dbHelper;
        private String[] RESPONSES_TABLE_COLUMNS = {DatabaseOpenHelper.RESP_ID, DatabaseOpenHelper.RESP_QUESTIONS_ID, DatabaseOpenHelper.RESP_CANDIDATE_ID,  DatabaseOpenHelper.RESP_RESPONSE };
        private SQLiteDatabase database;

        public EvaluationOperations(Context context) {
            dbHelper = new DatabaseOpenHelper(context);
        }

        public void open() throws SQLException {
            database = dbHelper.getWritableDatabase();
            // TODO remove this
            System.out.println("database.getVersion() = ");
            System.out.println(database.getVersion());
        }

        public void close() {
            dbHelper.close();
        }

        public boolean addResponse(long candidate_id, long question_id, int response) {
            // TODO retest everything by dropping the database and make sure the app recreates it
            boolean wasSuccessful = true;
            ContentValues values = new ContentValues();
            values.put(DatabaseOpenHelper.RESP_QUESTIONS_ID, question_id );
            values.put(DatabaseOpenHelper.RESP_CANDIDATE_ID, candidate_id);
            values.put(DatabaseOpenHelper.RESP_RESPONSE, response);
            long respId = database.insert(DatabaseOpenHelper.RESPONSES, null, values);

            // TODO figure out if we need to do something like this for responses...
            //   why are we returning the Question or Candidate?
            // now that the question is created return it ...
//            Cursor cursor = database.query(DatabaseOpenHelper.QUESTIONS,
//                    QUESTIONS_TABLE_COLUMNS, DatabaseOpenHelper.QUESTIONS_ID + " = "
//                            + quesId, null, null, null, null);
//            cursor.moveToFirst();

//            Questions newQuestion = parseQuestion(cursor);
//            cursor.close();

            return wasSuccessful;
        }
    public boolean updateResponse( long resp_id, long candidate_id, long question_id, int response) {
        // this method will update an existing row instead of adding a new one
        boolean wasSuccessful = true;
        ContentValues values = new ContentValues();

        values.put(DatabaseOpenHelper.RESP_ID, resp_id);
        values.put(DatabaseOpenHelper.RESP_QUESTIONS_ID, question_id);
        values.put(DatabaseOpenHelper.RESP_CANDIDATE_ID, candidate_id);
        values.put(DatabaseOpenHelper.RESP_RESPONSE, response);
        long respId = database.replace(DatabaseOpenHelper.RESPONSES, null, values);
        return wasSuccessful;
    }

    public long getResponseID( long candidate_id, long question_id ) {
        long respID = -1;

        String[] tableColumns = new String[] {
                DatabaseOpenHelper.RESP_ID
        };
        String whereClause = DatabaseOpenHelper.RESP_CANDIDATE_ID + " = ? AND "+  DatabaseOpenHelper.RESP_QUESTIONS_ID + " = ?";
        String[] whereArgs = new String[] {
                Long.toString(candidate_id) ,
                Long.toString(question_id)
        };
        Cursor c = database.query(DatabaseOpenHelper.RESPONSES, tableColumns, whereClause, whereArgs,
                null, null, null);

        if( c != null && c.moveToFirst()) {
            String tmpRespID = c.getString(0);
            respID = Integer.parseInt(tmpRespID);
            c.close();
        };
        return respID;
    }


    public int getResponseValue( long candidate_id, long question_id ) {
        int respVal = -1;

        String[] tableColumns = new String[] {
                DatabaseOpenHelper.RESP_RESPONSE
        };
        String whereClause = DatabaseOpenHelper.RESP_CANDIDATE_ID + " = ? AND "+  DatabaseOpenHelper.RESP_QUESTIONS_ID + " = ?";
        String[] whereArgs = new String[] {
                Long.toString(candidate_id) ,
                Long.toString(question_id)
        };
        Cursor c = database.query(DatabaseOpenHelper.RESPONSES, tableColumns, whereClause, whereArgs,
                null, null, null);

        if( c != null && c.moveToFirst()) {
            String tmpRespVal = c.getString(0);
            respVal = Integer.parseInt(tmpRespVal);
            c.close();
        };
        return respVal;
    }

        public void deleteResponse(long respId) {
            System.out.println("Response deleted with id: " + respId);
            database.delete(DatabaseOpenHelper.RESPONSES, DatabaseOpenHelper.RESP_ID
                    + " = " + respId, null);
        }
    // TODO add a get type method to return a set of X responses for a given candidate (and another for Y)

//        public ArrayList<Questions> getAllQuestions() {
//            ArrayList<Questions> questionsList = new ArrayList();
//            Cursor cursor = database.query(DatabaseOpenHelper.QUESTIONS,
//                    QUESTIONS_TABLE_COLUMNS, null, null, null, null, null);
//
//            cursor.moveToFirst();
//            while (!cursor.isAfterLast()) {
//                Questions question = parseQuestion(cursor);
//                questionsList.add(question);
//                cursor.moveToNext();
//            }
//            cursor.close();
//            return questionsList;
//        }

    // TODO decide if we need a version of this ...
//        private Questions parseQuestion(Cursor cursor) {
//            Questions question = new Questions();
//            question.setQuestionID((cursor.getInt(0)));
//            question.setQuestionText(cursor.getString(1));
//            question.setQuestionWeight(cursor.getInt(2));
//            return question;
//        }
    }

