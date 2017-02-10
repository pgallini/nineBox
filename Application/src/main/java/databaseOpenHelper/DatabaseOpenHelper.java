package databaseOpenHelper;
/**
 * Created by Paul Gallini on 3/30/16.
 *  Using SQLite as a means to store the candidates, questions, and ratings
 */
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ninebox.nineboxapp.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import nineBoxCandidates.appColor;
import nineBoxMain.User;

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
    public static final String CANDIDATE_COLOR = "_color";
    public static final String CANDIDATE_INITIALS = "_initials";

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

    public static final String COLORS = "Colors";
    public static final String COLOR_ID = "_id";
    public static final String COLOR_TEXT = "_color_text";
    public static final String COLOR_NUMBER = "_color_number";
    public static final String COLOR_INUSE = "_color_inuse";

    public static final String USER = "User";
    public static final String USER_ID = "_id";
    public static final String USER_NUM = "_number";
    public static final String USER_NAME = "_name";
    public static final String USER_EMAIL = "_email";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ninebox.db";
    private static final String CANDIDATES_TABLE_CREATE =
            "CREATE TABLE " + CANDIDATES + " (" +
                    CANDIDATE_ID  + " integer primary key autoincrement, " +
                    CANDIDATE_NAME + " TEXT NOT NULL, " +
                    CANDIDATE_NOTES + " TEXT, " +
                    CANDIDATE_COLOR + " TEXT NOT NULL, " +
                    CANDIDATE_INITIALS + " TEXT NOT NULL );";

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

    private static final String COLORS_TABLE_CREATE =
            "CREATE TABLE " + COLORS + " (" +
                    COLOR_ID  + " integer primary key autoincrement, " +
                    COLOR_TEXT + " TEXT NOT NULL, " +
                    COLOR_NUMBER + " TEXT NOT NULL, " +
                    COLOR_INUSE + " integer NOT NULL);";

    private static final String USER_TABLE_CREATE =
            "CREATE TABLE " + USER + " (" +
                    USER_ID  + " integer primary key autoincrement, " +
                    USER_NUM + " integer NOT NULL, " +
                    USER_NAME + " TEXT NOT NULL, " +
                    USER_EMAIL + " TEXT );";

    public final Context fContext;
    public ArrayList<appColor> colorList = new ArrayList<appColor>();

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        fContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println(" in Database Open Helper onCreate()");
        db.execSQL(CANDIDATES_TABLE_CREATE);
        db.execSQL(QUESTIONS_TABLE_CREATE);
        db.execSQL(RESPONSES_TABLE_CREATE);
        db.execSQL(COLORS_TABLE_CREATE);
        db.execSQL(USER_TABLE_CREATE);
        loadColorsTable( db );
        loadQuestionsTable( db );
        initiateUserTable( db );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you should do some logging in here
        // ..
        db.execSQL("DROP TABLE IF EXISTS " + CANDIDATES);
        db.execSQL("DROP TABLE IF EXISTS " + QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + RESPONSES);
        db.execSQL("DROP TABLE IF EXISTS " + COLORS );
        db.execSQL("DROP TABLE IF EXISTS " + USER);
        onCreate(db);
    }

    public static void updateColorsTableToggleInUse(SQLiteDatabase db, String color, boolean inuse ) {
        int inuseVal = 1;
        if( inuse ) { inuseVal = 1; } else { inuseVal = 0; };

        final ContentValues values = new ContentValues();
        values.put(COLOR_INUSE, inuseVal);

        try {
            db.beginTransaction();
            final boolean state = db.update(COLORS, values, COLOR_NUMBER + " = " + "'"+ color + "'", null)>0;
            db.setTransactionSuccessful();

//            return state;
        } catch (SQLException e) {
            throw e;
        } finally {
            db.endTransaction();
        }
    }

    public void initiateUserTable(SQLiteDatabase db) {
        //Add default record
        ContentValues _Values = new ContentValues();
        // TODO - make usr num dynamic somehow
            _Values.put(USER_NUM, 1);
            _Values.put(USER_NAME, "Unknown User");
            _Values.put(USER_EMAIL, " ");
            long userID = db.insert(USER, null, _Values);
        User currentUser = new User(userID);

    }

    public void loadColorsTable(SQLiteDatabase db) {
        //Add default record
        ContentValues _Values = new ContentValues();
        //Get xml resource file
        Resources res = fContext.getResources();

        //Open xml file
        XmlResourceParser _xml = res.getXml(R.xml.colors_data);
        try
        {
            //Check for end of document
            int eventType = _xml.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                //Search for record tags
                if ((eventType == XmlPullParser.START_TAG) &&(_xml.getName().equals("record"))){
                    //Record tag found, now get values and insert record
                    String _Color_Text = _xml.getAttributeValue(null, "color_text");
                    String _Color_Number = _xml.getAttributeValue(null, "color_number");
                    String _Color_InUse = _xml.getAttributeValue(null, "color_inuse");
                    _Values.put(COLOR_TEXT, _Color_Text);
                    _Values.put(COLOR_NUMBER, _Color_Number);
                    _Values.put(COLOR_INUSE, _Color_InUse);
                    db.insert(COLORS, null, _Values);

                }
                eventType = _xml.next();
            }
        }
        //Catch errors
        catch (XmlPullParserException e)
        {
            // TODO - figure out this LOG thing - is that something I need throughout the app?
//            Log.e(TAG, e.getMessage(), e);
            System.out.println( e.getMessage() );
        }
        catch (IOException e)
        {
//            Log.e(TAG, e.getMessage(), e);
            System.out.println( e.getMessage() );

        }
        finally
        {
            //Close the xml file
            _xml.close();
        }
    }

    public void loadQuestionsTable(SQLiteDatabase db) {
        // This method will take the pre-defined questions in xml/questions_data.xml and load it into the questions table

        //Add default record
        ContentValues _Values = new ContentValues();
        //Get xml resource file
        Resources res = fContext.getResources();

        //Open xml file
        XmlResourceParser _xml = res.getXml(R.xml.questions_data);
        try
        {
            //Check for end of document
            int eventType = _xml.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                //Search for record tags
                if ((eventType == XmlPullParser.START_TAG) &&(_xml.getName().equals("record"))){
                    //Record tag found, now get values and insert record
                    String _Questions_Id = _xml.getAttributeValue(null, "question_id");
                    String _Questions_Text = _xml.getAttributeValue(null, "question_text");
                    String _Questions_Weight = _xml.getAttributeValue(null, "question_weight");
                    String _Questions_Axis = _xml.getAttributeValue(null, "question_axis");
                    _Values.put(QUESTIONS_ID, _Questions_Id);
                    _Values.put(QUESTIONS_TEXT, _Questions_Text);
                    _Values.put(QUESTIONS_WEIGHT, _Questions_Weight);
                    _Values.put(QUESTIONS_AXIS, _Questions_Axis);
                    db.insert(QUESTIONS, null, _Values);

                }
                eventType = _xml.next();
            }
        }
        //Catch errors
        catch (XmlPullParserException e)
        {
//            Log.e(TAG, e.getMessage(), e);
            System.out.println( e.getMessage() );
        }
        catch (IOException e)
        {
//            Log.e(TAG, e.getMessage(), e);
            System.out.println( e.getMessage() );

        }
        finally
        {
            //Close the xml file
            _xml.close();
        }
    }

    public ArrayList<appColor> getAllColors(){
        // Select All Query
        String selectQuery = "SELECT " + COLOR_TEXT + ", " + COLOR_NUMBER + ", " + COLOR_INUSE + " FROM " + COLORS + " WHERE _color_inuse = 0";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String tmpColorNumber = cursor.getString(1);
                int tmpColorInuse = Integer.parseInt(cursor.getString(2));
                colorList.add(new appColor(cursor.getString(0), tmpColorNumber, tmpColorInuse));
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return colorList;
    }
}
