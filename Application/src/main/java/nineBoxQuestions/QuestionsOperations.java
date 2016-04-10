package nineBoxQuestions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ninebox.nineboxapp.DatabaseOpenHelper;

import java.util.ArrayList;

/**
 * Created by ase408 on 4/8/16.
 */
public class QuestionsOperations {

    // Database fields
        private DatabaseOpenHelper dbHelper;
        private String[] QUESTIONS_TABLE_COLUMNS = {DatabaseOpenHelper.QUESTIONS_ID, DatabaseOpenHelper.QUESTIONS_TEXT, DatabaseOpenHelper.QUESTIONS_WEIGHT };
        private SQLiteDatabase database;

        public QuestionsOperations(Context context) {
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

        public Questions addQuestion(String questionText, int questionWeight) {
            ContentValues values = new ContentValues();
            values.put(DatabaseOpenHelper.QUESTIONS_TEXT, questionText);
            values.put(DatabaseOpenHelper.QUESTIONS_WEIGHT, questionWeight);
            long quesId = database.insert(DatabaseOpenHelper.QUESTIONS, null, values);

            // see here for details on looking at the database outside of the app ...
            // http://stackoverflow.com/questions/18370219/how-to-use-adb-in-android-studio-to-view-an-sqlite-db

            // now that the question is created return it ...
            Cursor cursor = database.query(DatabaseOpenHelper.QUESTIONS,
                    QUESTIONS_TABLE_COLUMNS, DatabaseOpenHelper.QUESTIONS_ID + " = "
                            + quesId, null, null, null, null);

            cursor.moveToFirst();

            Questions newQuestion = parseQuestion(cursor);
            cursor.close();

            return newQuestion;
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

                // TODO remove this ...
                System.out.println("inside getAllQuestions - looping " );

                Questions question = parseQuestion(cursor);
                questionsList.add(question);
                cursor.moveToNext();
            }
            cursor.close();
            return questionsList;
        }

        private Questions parseQuestion(Cursor cursor) {
            Questions question = new Questions();
            question.setQuestionID((cursor.getInt(0)));
            question.setQuestionText(cursor.getString(1));
            question.setQuestionWeight(cursor.getInt(2));
            return question;
        }
    }

