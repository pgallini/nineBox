package com.ninebox.nineboxapp;
/**
 * Created by Paul Gallini on 3/30/16.
 *  Using SQLite as a means to store the candidates, questions, and ratings
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Steps to access database from terminal ..
 * adb devices
 * adb -s emulator-5554 shell   (where you specify one of the emulators listed)
 * cd /data/data/com.ninebox.nineboxapp/databases
 * sqlite3 ninebox.db
 *
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    public static final String CANDIDATES = "Candidates";
    public static final String CANDIDATE_ID = "_id";
    public static final String CANDIDATE_NAME = "_name";
    public static final String CANDIDATE_NOTES = "_notes";

    public static final String QUESTIONS = "Questions";
    public static final String QUESTIONS_ID = "_id";
    public static final String QUESTIONS_TEXT = "_text";
    public static final String QUESTIONS_WEIGHT = "_weight";
    public static final String QUESTIONS_AXIS = "_axis";

    public static final String RESPONSES = "Responses";
    public static final String RESP_ID = "_id";
    public static final String RESP_QUESTIONS_ID = "_question_id";
    public static final String RESP_CANDIDATE_ID = "_candidate_id";
    public static final String RESP_RESPONSE = "_response";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ninebox.db";
    private static final String CANDIDATES_TABLE_CREATE =
            "CREATE TABLE " + CANDIDATES + " (" +
                    CANDIDATE_ID  + " integer primary key autoincrement, " +
                    CANDIDATE_NAME + " TEXT NOT NULL, " +
                    CANDIDATE_NOTES + " TEXT);";

    // TODO add AXIS (X vs Y) ....
    private static final String QUESTIONS_TABLE_CREATE =
            "CREATE TABLE " + QUESTIONS + " (" +
                    QUESTIONS_ID  + " integer primary key autoincrement, " +
                    QUESTIONS_TEXT + " TEXT NOT NULL, " +
                    QUESTIONS_WEIGHT + " integer," +
                    QUESTIONS_AXIS + " TEXT NOT NULL);";

    private static final String RESPONSES_TABLE_CREATE =
            "CREATE TABLE " + RESPONSES + " (" +
                    RESP_ID  + " integer primary key autoincrement, " +
                    RESP_QUESTIONS_ID + " integer NOT NULL, " +
                    RESP_CANDIDATE_ID + " integer NOT NULL, " +
                    RESP_RESPONSE + " integer NOT NULL);";

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CANDIDATES_TABLE_CREATE);
        db.execSQL(QUESTIONS_TABLE_CREATE);
        db.execSQL(RESPONSES_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you should do some logging in here
        // ..
        db.execSQL("DROP TABLE IF EXISTS " + CANDIDATES);
        db.execSQL("DROP TABLE IF EXISTS " + QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + RESPONSES);
        onCreate(db);
    }
}
