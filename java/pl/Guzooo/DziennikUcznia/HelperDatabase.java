package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HelperDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "dziennikucznia";
    private static final int DB_VERSION = 1;

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
    }
}
