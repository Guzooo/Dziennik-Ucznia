package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseUtils {

    public static SQLiteDatabase getWritableDatabase(Context context){
        SQLiteOpenHelper openHelper = new HelperDatabase(context);
        return openHelper.getWritableDatabase();
    }

    public static SQLiteDatabase getReadableDatabase(Context context){
        SQLiteOpenHelper openHelper = new HelperDatabase(context);
        return openHelper.getReadableDatabase();
    }

    public static Boolean destroyAllSubject(Context context){
        try {
            if (!destroyAllNotes("TAB_SUBJECT > ?", new String[]{Integer.toString(0)}, context)) return false;
            if (!destroyAllLessonPlan("TAB_SUBJECT > ?", new String[]{Integer.toString(0)}, context)) return false;
            SQLiteDatabase db = getWritableDatabase(context);
            db.delete("SUBJECTS", null, null);
            db.close();
            return true;
        } catch (SQLiteException e){
            return false;
        }
    }

    public static Boolean destroyAllNotes(Context context){
        return destroyAllNotes(null, null, context);
    }

    public static Boolean destroyAllNotes(String whereClause, String[] whereArgs, Context context){
        try {
            SQLiteDatabase db = getWritableDatabase(context);
            db.delete("NOTES", whereClause, whereArgs);
            db.close();
            return true;
        } catch (SQLException e){
            return false;
        }
    }

    public static Boolean destroyAllLessonPlan (Context context){
        return destroyAllLessonPlan(null, null, context);
    }

    public static Boolean destroyAllLessonPlan(String whereClause, String[] whereArgs, Context context){
        try {
            SQLiteDatabase db = getWritableDatabase(context);
            db.delete("LESSON_PLAN", whereClause, whereArgs);
            db.close();
            return true;
        } catch (SQLException e){
            return false;
        }
    }
}
