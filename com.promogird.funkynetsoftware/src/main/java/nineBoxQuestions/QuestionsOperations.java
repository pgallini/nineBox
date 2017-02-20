package nineBoxQuestions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import databaseOpenHelper.DatabaseOpenHelper;

import java.util.ArrayList;

/**
 * Created by Paul Gallini on 4/8/16.
 */
public class QuestionsOperations {

    // Database fields
        private DatabaseOpenHelper dbHelper;
        private String[] QUESTIONS_TABLE_COLUMNS = {DatabaseOpenHelper.QUESTIONS_ID, DatabaseOpenHelper.QUESTIONS_TEXT, DatabaseOpenHelper.QUESTIONS_WEIGHT , DatabaseOpenHelper.QUESTIONS_AXIS};
        private SQLiteDatabase database;

        public QuestionsOperations(Context context) {
            dbHelper = new DatabaseOpenHelper(context);
        }

        public void open() throws SQLException {
            database = dbHelper.getWritableDatabase();
        }

        public void close() {
            dbHelper.close();
        }

    public Questions addQuestion(String questionText, int questionWeight, boolean x_axis) {
        ContentValues values = new ContentValues();
        values.put(DatabaseOpenHelper.QUESTIONS_TEXT, questionText);
        values.put(DatabaseOpenHelper.QUESTIONS_WEIGHT, questionWeight);
        if(x_axis) {
            values.put(DatabaseOpenHelper.QUESTIONS_AXIS, "X");
        } else {
            values.put(DatabaseOpenHelper.QUESTIONS_AXIS, "Y");
        }
        long quesId = database.insert(DatabaseOpenHelper.QUESTIONS, null, values);

        // now that the question is created return it ...
        Cursor cursor = database.query(DatabaseOpenHelper.QUESTIONS,
                QUESTIONS_TABLE_COLUMNS, DatabaseOpenHelper.QUESTIONS_ID + " = "
                        + quesId, null, null, null, null);

        cursor.moveToFirst();

        Questions newQuestion = parseQuestion(cursor);
        cursor.close();

        return newQuestion;
    }

        public boolean updateQuestion(long questionID, String questionText, int questionWeight, boolean x_axis) {
            ContentValues values = new ContentValues();
            values.put(DatabaseOpenHelper.QUESTIONS_TEXT, questionText);
            values.put(DatabaseOpenHelper.QUESTIONS_WEIGHT, questionWeight);
            if(x_axis) {
                values.put(DatabaseOpenHelper.QUESTIONS_AXIS, "X");
            } else {
                values.put(DatabaseOpenHelper.QUESTIONS_AXIS, "Y");
            }
            long quesId = database.update(DatabaseOpenHelper.QUESTIONS, values, DatabaseOpenHelper.QUESTIONS_ID + "=" + questionID ,null);

            return (quesId > 0);
        }

        public void deleteCQuestion(Questions question) {
            long id = question.getQuestionID();
            System.out.println("Question deleted with id: " + id);
            database.delete(DatabaseOpenHelper.QUESTIONS, DatabaseOpenHelper.QUESTIONS_ID
                    + " = " + id, null);
        }

        public ArrayList<Questions> getAllQuestions() {
            ArrayList<Questions> questionsList = new ArrayList();
            Cursor cursor = database.query(DatabaseOpenHelper.QUESTIONS,
                    QUESTIONS_TABLE_COLUMNS, null, null, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Questions question = parseQuestion(cursor);
                questionsList.add(question);
                cursor.moveToNext();
            }
            // TODO decide if we should or should not close the cursor here - seems to be cause null pointer second time
            cursor.close();
            return questionsList;
        }

        private Questions parseQuestion(Cursor cursor) {
            Questions question = new Questions();
            question.setQuestionID((cursor.getInt(0)));
            question.setQuestionText(cursor.getString(1));
            question.setQuestionWeight(cursor.getInt(2));
            question.setQuestionAxis(cursor.getString(3));
            return question;
        }
    }

