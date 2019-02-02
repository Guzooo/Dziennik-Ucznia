package pl.Guzooo.DziennikUcznia;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.ResourceMismatchViolation;
import android.view.Display;

public class HelperDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "dziennikucznia";
    private static final int DB_VERSION = 3;

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
            db.execSQL("ALTER TABLE SUBJECTS ADD COLUMN DAY INTEGER");

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
                    + "DAY INTEGER,"
                    + "CLASSROOM TEXT)");
        }
        if(oldVersion < 3){
            db.execSQL("ALTER TABLE SUBJECTS ADD COLUMN ASSESSMENTS2 TEXT");
            
            db.update("SUBJECTS", updateDatabase2to3(), null, null);
        }
        if(oldVersion < 4){
            db.execSQL("CREATE TABLE ASSESSMENTS (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "ASSESSMENT REAL,"
                    + "NOTE TEXT,"
                    + "SEMESTER INTEGER,"
                    + "TAB_SUBJECT INTEGER,"
                    + "TAB_CATEGORY_ASSESSMENT)");

            db.execSQL("CREATE TABLE CATEGORY_ASSESSMENT (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "NAME TEXT,"
                    + "COLOR TEXT)");

            CreateDefaultCategoryOfAssessment();
        }
    }

    public void CreateDefaultCategoryOfAssessment(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("CATEGORY_ASSESSMENT", null, null);
        db.insert("CATEGORY_ASSESSMENT", null, ModelCategoryOfAssessment(Resources.getSystem().getResourceName(R.string.category_of_assessment_test), "#ff0000"));
        db.insert("CATEGORY_ASSESSMENT", null, ModelCategoryOfAssessment(Resources.getSystem().getResourceName(R.string.category_of_assessment_answer), "#006399"));
        db.insert("CATEGORY_ASSESSMENT", null, ModelCategoryOfAssessment(Resources.getSystem().getResourceName(R.string.category_of_assessment_homework), "#00ff11"));
        db.insert("CATEGORY_ASSESSMENT", null, ModelCategoryOfAssessment(Resources.getSystem().getResourceName(R.string.category_of_assessment_quiz), "#ff0000"));
        db.insert("CATEGORY_ASSESSMENT", null, ModelCategoryOfAssessment(Resources.getSystem().getResourceName(R.string.category_of_assessment_default), "#000000"));
    }

    private ContentValues ModelCategoryOfAssessment(String name, String color){
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("COLOR", color);
        return contentValues;
    }

    private ContentValues updateDatabase2to3(){
        ContentValues contentValues = new ContentValues();
        contentValues.put("ASSESSMENTS2", "");
        return contentValues;
    }
}
