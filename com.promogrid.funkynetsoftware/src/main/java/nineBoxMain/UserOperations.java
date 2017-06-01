package nineBoxMain;

/**
 * Created by Paul Gallini on 7/12/16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import databaseOpenHelper.DatabaseOpenHelper;

public class UserOperations {
    // Database fields
    private DatabaseOpenHelper dbHelper;
    private String[] USER_TABLE_COLUMNS = {DatabaseOpenHelper.USER_ID, DatabaseOpenHelper.USER_NUM, DatabaseOpenHelper.USER_NAME, DatabaseOpenHelper.USER_EMAIL };
    private SQLiteDatabase database;

    public UserOperations(Context context) {
        dbHelper = new DatabaseOpenHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public User addUser(int userNumber, String name, String email) {
        ContentValues values = new ContentValues();
        values.put(DatabaseOpenHelper.USER_NUM, userNumber );
        values.put(DatabaseOpenHelper.USER_NAME, name);
        values.put(DatabaseOpenHelper.USER_EMAIL, email);
        long userId = database.insert(DatabaseOpenHelper.USER, null, values);
        // now that the user record is created return it ...
        Cursor cursor = database.query(DatabaseOpenHelper.USER,
                USER_TABLE_COLUMNS, DatabaseOpenHelper.USER_ID + " = "
                        + userId, null, null, null, null);

        cursor.moveToFirst();

        User newUser = parseUser(cursor);
        cursor.close();
        return newUser;
    }

    public User getUser(long user_id ) {
        Cursor cursor = database.query(DatabaseOpenHelper.USER,
                USER_TABLE_COLUMNS, null, null, null, null, null);

        User user = new User();
        cursor.moveToFirst();
        // this is set to only return one user - may need to change it to handle multiple
        while (!cursor.isAfterLast()) {
            user = parseUser(cursor);
            cursor.moveToNext();
        }
        cursor.close();
        return user;
    }
    public int getUserID(long user_id ) {
        int respVal = -1;

        String[] tableColumns = new String[] {
                DatabaseOpenHelper.USER_ID
        };
        String whereClause = DatabaseOpenHelper.USER_ID + " = ? ";
        String[] whereArgs = new String[] {
                Long.toString(user_id)
        };
        Cursor c = database.query(DatabaseOpenHelper.USER, tableColumns, whereClause, whereArgs,
                null, null, null);

        if( c != null && c.moveToFirst()) {
            String tmpRespVal = c.getString(0);
            respVal = Integer.parseInt(tmpRespVal);
            c.close();
        };
        return respVal;
    }

    public boolean updateUserEmail(long userID, String email) {
        ContentValues values = new ContentValues();
        values.put(DatabaseOpenHelper.USER_EMAIL, email);

        long candId = database.update(DatabaseOpenHelper.USER, values, DatabaseOpenHelper.USER_ID + "=" + userID ,null);

        return (candId > 0);
    }

//    public void deleteUser(User user) {
//        long id = candidate.getCandidateID();
//        database.delete(DatabaseOpenHelper.CANDIDATES, DatabaseOpenHelper.CANDIDATE_ID
//                + " = " + id, null);
//        // update the color table to mark this color as in-use ...
//        DatabaseOpenHelper.updateColorsTableToggleInUse(database, candidate.getCandidateColor(), false);
//    }

    private User parseUser(Cursor cursor) {
        User user = new User();
        user.setUserID((cursor.getInt(0)));
        user.setUserNumber(cursor.getInt(1));
        user.setUserName(cursor.getString(2));
        user.setUserEmail(cursor.getString(3));

        return user;
    }
}

