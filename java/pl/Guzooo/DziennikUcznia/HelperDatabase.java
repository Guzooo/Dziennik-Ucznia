package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class HelperDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "dziennikucznia";
    private static final int DB_VERSION = 2;

    HelperDatabase(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0 ,DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion){
        if(oldVersion < 1){
            db.execSQL("CREATE TABLE SUBJECTS (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "OBJECT TEXT,"
                    + "NOTES INTEGER)");
        }
        if(oldVersion < 2){
            db.execSQL("ALTER TABLE SUBJECTS ADD COLUMN MONDAY INTEGER");
            db.execSQL("ALTER TABLE SUBJECTS ADD COLUMN TUESDAY INTEGER");
            db.execSQL("ALTER TABLE SUBJECTS ADD COLUMN WEDNESDAY INTEGER");
            db.execSQL("ALTER TABLE SUBJECTS ADD COLUMN THURSDAY INTEGER");
            db.execSQL("ALTER TABLE SUBJECTS ADD COLUMN FRIDAY INTEGER");
            db.execSQL("ALTER TABLE SUBJECTS ADD COLUMN SATURDAY INTEGER");
            db.execSQL("ALTER TABLE SUBJECTS ADD COLUMN SUNDAY INTEGER");

            db.execSQL("ALTER TABLE SUBJECTS ADD COLUMN NAME TEXT");
            db.execSQL("ALTER TABLE SUBJECTS ADD COLUMN TEACHER TEXT");
            db.execSQL("ALTER TABLE SUBJECTS ADD COLUMN ASSESSMENTS TEXT");
            db.execSQL("ALTER TABLE SUBJECTS ADD COLUMN UNPREPAREDNESS INTEGER");
            db.execSQL("ALTER TABLE SUBJECTS ADD COLUMN DESCRIPTION TEXT");

            db.execSQL("CREATE TABLE NOTES (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "NAME TEXT,"
                    + "NOTE TEXT,"
                    + "TAB_SUBJECT INTEGER)");

            db.execSQL("CREATE TABLE LESSON_PLAN (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "TIME_START INTEGER,"
                    + "TIME_END INTEGER,"
                    + "TAB_SUBJECT INTEGER,"
                    + "DAY INTEGER)");
        }
    }
}
