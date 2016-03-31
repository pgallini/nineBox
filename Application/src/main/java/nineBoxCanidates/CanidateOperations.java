package nineBoxCanidates;

/**
 * Created by ase408 on 3/30/16.
 */
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ninebox.nineboxapp.DatabaseOpenHelper;

public class CanidateOperations {
        // Database fields
        private DatabaseOpenHelper dbHelper;
        private String[] CANIDATE_TABLE_COLUMNS = {DatabaseOpenHelper.CANIDATE_ID, DatabaseOpenHelper.CANIDATE_NAME, DatabaseOpenHelper.CANIDATE_NOTES };
        private SQLiteDatabase database;

        public CanidateOperations(Context context) {
            dbHelper = new DatabaseOpenHelper(context);
        }

        public void open() throws SQLException {
            database = dbHelper.getWritableDatabase();
        }

        public void close() {
            dbHelper.close();
        }

        public Canidates addCanidate(String name) {

            ContentValues values = new ContentValues();

            values.put(DatabaseOpenHelper.CANIDATE_NAME, name);

            long candId = database.insert(DatabaseOpenHelper.CANIDATES, null, values);

            // see here for details on looking at the database outside of the app ...
            // http://stackoverflow.com/questions/18370219/how-to-use-adb-in-android-studio-to-view-an-sqlite-db

            // now that the student is created return it ...
            Cursor cursor = database.query(DatabaseOpenHelper.CANIDATES,
                    CANIDATE_TABLE_COLUMNS, DatabaseOpenHelper.CANIDATE_ID + " = "
                            + candId, null, null, null, null);

            cursor.moveToFirst();

            Canidates newComment = parseCanidate(cursor);
            cursor.close();

            return newComment;
        }

        public void deleteCanidate(Canidates canidate) {
            long id = canidate.getCanidateID();
            System.out.println("Comment deleted with id: " + id);
            database.delete(DatabaseOpenHelper.CANIDATES, DatabaseOpenHelper.CANIDATE_ID
                    + " = " + id, null);
        }

        public ArrayList<String> getAllCanidates() {
            ArrayList<String> canidates = new ArrayList();

            Cursor cursor = database.query(DatabaseOpenHelper.CANIDATES,
                    CANIDATE_TABLE_COLUMNS, null, null, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Canidates canidate = parseCanidate(cursor);
                // TODO - is this even needed? 
                addCanidate(canidate.getCanidateName());
                canidates.add(canidate.getCanidateName());
                cursor.moveToNext();
            }

            System.out.println( "canidates.size() = ");
            System.out.println( canidates.size());

            cursor.close();
            return canidates;
        }

        private Canidates parseCanidate(Cursor cursor) {

            Canidates canidate = new Canidates();
            canidate.setCanidateID((cursor.getInt(0)));
            canidate.setCanidateName(cursor.getString(1));

            // TODO remove ..
            System.out.println(" cursor.getString(1)" ) ;
            System.out.println( cursor.getString(1) );
            // TODO Add Notes
            return canidate;
        }
    }
