package com.ninebox.nineboxapp;
/**
 * Created by ase408 on 3/30/16.
 *  Using SQLite as a means to store the canidates, questions, and ratings
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    public static final String CANIDATES = "Canidates";
    public static final String CANIDATE_ID = "_id";
    public static final String CANIDATE_NAME = "_name";
    public static final String CANIDATE_NOTES = "_notes";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "canidates.db";
    private static final String DICTIONARY_TABLE_CREATE =
            "CREATE TABLE " + CANIDATES + " (" +
                    CANIDATE_ID  + " integer primary key autoincrement, " +
                    CANIDATE_NAME + " TEXT NOT NULL, " +
                    CANIDATE_NOTES + " TEXT);";

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
        db.execSQL("DROP TABLE IF EXISTS " + CANIDATES);
        onCreate(db);
    }
}
