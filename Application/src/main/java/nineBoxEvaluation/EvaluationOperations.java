package nineBoxEvaluation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;

import databaseOpenHelper.DatabaseOpenHelper;

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
        }

        public void close() {
            dbHelper.close();
        }

        public boolean addResponse(long candidate_id, long question_id, int response) {
            boolean wasSuccessful = true;
            ContentValues values = new ContentValues();
            values.put(DatabaseOpenHelper.RESP_QUESTIONS_ID, question_id );
            values.put(DatabaseOpenHelper.RESP_CANDIDATE_ID, candidate_id);
            values.put(DatabaseOpenHelper.RESP_RESPONSE, response);
            long respId = database.insert(DatabaseOpenHelper.RESPONSES, null, values);

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


    public boolean isResponseTableEmpty( ) {
        boolean recsFound = false;

        String[] tableColumns = new String[] {
                DatabaseOpenHelper.RESP_ID
        };

        Cursor c = database.query(DatabaseOpenHelper.RESPONSES, tableColumns, null, null,
                null, null, null);

        if( c.getCount() > 0 ) {
            recsFound = true;

        }

        return !recsFound;
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
    }

