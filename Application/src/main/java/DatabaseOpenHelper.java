package com.ninebox.nineboxapp;
/**
 * Created by ase408 on 3/30/16.
 *  Using SQLite as a means to store the canidates, questions, and ratings
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Steps to access database from terminal ..
 * adb devices
 * adb -s emulator-5554 shell   (where you specify one of the emulators listed)
 * cd /data/data/com.ninebox.nineboxapp/databases
 * sqlite3qlite3 candidates.db
 *
 */

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    public static final String CANDIDATES = "Candidates";
    public static final String CANDIDATE_ID = "_id";
    public static final String CANDIDATE_NAME = "_name";
    public static final String CANDIDATE_NOTES = "_notes";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "candidates.db";
    private static final String DICTIONARY_TABLE_CREATE =
            "CREATE TABLE " + CANDIDATES + " (" +
                    CANDIDATE_ID  + " integer primary key autoincrement, " +
                    CANDIDATE_NAME + " TEXT NOT NULL, " +
                    CANDIDATE_NOTES + " TEXT);";

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DICTIONARY_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you should do some logging in here
        // ..
        db.execSQL("DROP TABLE IF EXISTS " + CANDIDATES);
        onCreate(db);
    }
}
