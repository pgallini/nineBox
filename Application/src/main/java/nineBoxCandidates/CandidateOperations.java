package nineBoxCandidates;

/**
 * Created by ase408 on 3/30/16.
 */
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ninebox.nineboxapp.DatabaseOpenHelper;

public class CandidateOperations {
        // Database fields
        private DatabaseOpenHelper dbHelper;
        private String[] CANDIDATE_TABLE_COLUMNS = {DatabaseOpenHelper.CANDIDATE_ID, DatabaseOpenHelper.CANDIDATE_NAME, DatabaseOpenHelper.CANDIDATE_NOTES };
        private SQLiteDatabase database;

        public CandidateOperations(Context context) {
            dbHelper = new DatabaseOpenHelper(context);
        }

        public void open() throws SQLException {
            database = dbHelper.getWritableDatabase();
        }

        public void close() {
            dbHelper.close();
        }

        public Candidates addCandidate(String name, String notes) {
            ContentValues values = new ContentValues();
            values.put(DatabaseOpenHelper.CANDIDATE_NAME, name);
            values.put(DatabaseOpenHelper.CANDIDATE_NOTES, notes);
            long candId = database.insert(DatabaseOpenHelper.CANDIDATES, null, values);

            // see here for details on looking at the database outside of the app ...
            // http://stackoverflow.com/questions/18370219/how-to-use-adb-in-android-studio-to-view-an-sqlite-db

            // now that the student is created return it ...
            Cursor cursor = database.query(DatabaseOpenHelper.CANDIDATES,
                    CANDIDATE_TABLE_COLUMNS, DatabaseOpenHelper.CANDIDATE_ID + " = "
                            + candId, null, null, null, null);

            cursor.moveToFirst();

            Candidates newComment = parseCandidate(cursor);
            cursor.close();

            return newComment;
        }

        public void deleteCandidate(Candidates candidate) {
            long id = candidate.getCandidateID();
            System.out.println("Comment deleted with id: " + id);
            database.delete(DatabaseOpenHelper.CANDIDATES, DatabaseOpenHelper.CANDIDATE_ID
                    + " = " + id, null);
        }

        public ArrayList<Candidates> getAllCandidates() {
            ArrayList<Candidates> candidates = new ArrayList();
            Cursor cursor = database.query(DatabaseOpenHelper.CANDIDATES,
                    CANDIDATE_TABLE_COLUMNS, null, null, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                // TODO remove this ...
                System.out.println("inside getAllCandidates - looping " );

                Candidates candidate = parseCandidate(cursor);
                candidates.add(candidate);
                cursor.moveToNext();
            }
            cursor.close();
            return candidates;
        }

        private Candidates parseCandidate(Cursor cursor) {
            Candidates candidate = new Candidates();
            candidate.setCandidateID((cursor.getInt(0)));
            candidate.setCandidateName(cursor.getString(1));
            candidate.setCandidateNotes( cursor.getString(2));
            return candidate;
        }
    }
