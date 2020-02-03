package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.Calendar;

public class Database2020 extends SQLiteOpenHelper {

    public static final String ID = "_id";

    private static final String DB_NAME = "dziennikucznia";
    private static final int DB_VERSION = 8; //TODO: Już nowe

    private SQLiteDatabase db;

    Database2020(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        createTableSubjects();
        createTableNotes();
        createTableLessonPlan();
        createTableAssessments();
        createTableCategoryAssessment();
        createDefaultCategoryOfAssessment();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion ) {
        this.db = db;
        if(oldVersion < 8){
            upgradeDatabaseLessThan8();
        }
    }

    private void upgradeDatabaseLessThan8(){
        //Medoty naprawcze bazy danych dla urządzeń z zainstalowaną wersją 12 lub niższą;
        changeOldTableNames();
        createTableSubjects();
        moveDataToNewTable();
        deleteOldTable();
        upgradeTableCategoryAssessment();
        repairTableLessonPlanColumnDay();
    }

    private void changeOldTableNames(){
        db.execSQL("ALTER TABLE SUBJECTS RENAME TO oldSUBJECTS");
    }

    private void moveDataToNewTable(){
        db.execSQL("INSERT INTO " + Subject2020.DATABASE_NAME + " SELECT "
                + ID + ", "
                + Subject2020.NAME + ", "
                + Subject2020.DESCRIPTION + ", "
                + Subject2020.TEACHER + ", "
                + Subject2020.UNPREPAREDNESS + ", "
                + Subject2020.UNPREPAREDNESS1 + ", "
                + Subject2020.UNPREPAREDNESS2 + " FROM oldSUBJECTS");
    }

    private void deleteOldTable(){
        db.execSQL("DROP TABLE IF EXISTS oldSUBJECTS");
    }

    private void upgradeTableCategoryAssessment(){
        addTableWeight();
        setDefaultWeightForExistingElements();
    }

    private void addTableWeight(){
        db.execSQL("ALTER TABLE " + CategoryOfAssessment2020.DATABASE_NAME + " ADD COLUMN " + CategoryOfAssessment2020.WEIGHT + " INTEGER");
    }

    private void setDefaultWeightForExistingElements(){
        db.update(CategoryOfAssessment2020.DATABASE_NAME, defaultWeight(), null, null);
    }

    private ContentValues defaultWeight(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CategoryOfAssessment2020.WEIGHT, 1);
        return contentValues;
    }

    private void repairTableLessonPlanColumnDay(){
        Cursor cursor = db.query(ElementOfPlan2020.DATABASE_NAME,
                ElementOfPlan2020.ON_CURSOR,
                null, null, null, null, null);
        if(cursor.moveToFirst())
            do{
                ElementOfPlan2020 element = new ElementOfPlan2020();
                element.setVariablesOfCursor(cursor);
                int day = element.getDay();
                int correctDay = getChangeDayToSystemsDay(day);
                element.setDay(correctDay);
                int id = element.getId();
                db.update(ElementOfPlan2020.DATABASE_NAME, element.getContentValues(), "_id = ?", new String[]{Integer.toString(id)});
            }while (cursor.moveToNext());
    }

    private int getChangeDayToSystemsDay(int day){
        switch (day){
            case 1:
                return Calendar.MONDAY;
            case 2:
                return Calendar.TUESDAY;
            case 3:
                return Calendar.WEDNESDAY;
            case 4:
                return Calendar.THURSDAY;
            case 5:
                return Calendar.FRIDAY;
            case 6:
                return Calendar.SATURDAY;
            case 7:
                return Calendar.SUNDAY;
        }
        return 0;
    }

    private void createTableSubjects(){
        db.execSQL("CREATE TABLE " + Subject2020.DATABASE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Subject2020.NAME + " TEXT,"
                + Subject2020.DESCRIPTION + " TEXT,"
                + Subject2020.TEACHER + " TEXT,"
                + Subject2020.UNPREPAREDNESS + " INTEGER,"
                + Subject2020.UNPREPAREDNESS1 + " INTEGER,"
                + Subject2020.UNPREPAREDNESS2 + " INTEGER)");
    }

    private void createTableNotes(){
        db.execSQL("CREATE TABLE " + Note2020.DATABASE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Note2020.NAME + " TEXT,"
                + Note2020.NOTE + " TEXT,"
                + Note2020.TAB_SUBJECT + " INTEGER)");
    }

    private void createTableLessonPlan(){
        db.execSQL("CREATE TABLE " + ElementOfPlan2020.DATABASE_NAME + " (" +  ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ElementOfPlan2020.TIME_START + " INTEGER,"
                + ElementOfPlan2020.TIME_END + " INTEGER,"
                + ElementOfPlan2020.TAB_SUBJECT + " INTEGER,"
                + ElementOfPlan2020.DAY + " INTEGER,"
                + ElementOfPlan2020.CLASSROOM + " TEXT)");
    }

    private void createTableAssessments(){
        db.execSQL("CREATE TABLE " + Assessment2020.DATABASE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Assessment2020.ASSESSMENT + " REAL,"
                + Assessment2020.WEIGHT + " INTEGER,"
                + Assessment2020.NOTE + " TEXT,"
                + Assessment2020.SEMESTER + " INTEGER,"
                + Assessment2020.TAB_SUBJECT + " INTEGER,"
                + Assessment2020.TAB_CATEGORY_ASSESSMENT + " INTEGER,"
                + Assessment2020.DATA + " TEXT)");
    }

    private void createTableCategoryAssessment(){
        db.execSQL("CREATE TABLE " + CategoryOfAssessment2020.DATABASE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CategoryOfAssessment2020.NAME + " TEXT,"
                + CategoryOfAssessment2020.COLOR + " TEXT,"
                + CategoryOfAssessment2020.WEIGHT + " INTEGER)");
    }

    private void createDefaultCategoryOfAssessment(){
        CategoryOfAssessment2020 category = new CategoryOfAssessment2020();
        category.setName("DEFAULT");
        db.insert(CategoryOfAssessment2020.DATABASE_NAME, null, category.getContentValues());
    }

    public static SQLiteDatabase getToWriting(Context context){
        SQLiteOpenHelper openHelper = new Database2020(context);
        return openHelper.getWritableDatabase();
    }

    public static SQLiteDatabase getToReading(Context context){
        SQLiteOpenHelper openHelper = new Database2020(context);
        return openHelper.getReadableDatabase();
    }

    public static void errorToast(Context context){
        Toast.makeText(context, R.string.error_database, Toast.LENGTH_SHORT).show();
    }

    //STARE TODO:ogarnąć
    public static Boolean destroyAllSubject(Context context){
        try {
            if (!destroyAllNotes("TAB_SUBJECT > ?", new String[]{Integer.toString(0)}, context)) return false;
            if (!destroyAllLessonPlan("TAB_SUBJECT > ?", new String[]{Integer.toString(0)}, context)) return false;
            SQLiteDatabase db = getToWriting(context);
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
            SQLiteDatabase db = getToWriting(context);
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
            SQLiteDatabase db = getToWriting(context);
            db.delete("LESSON_PLAN", whereClause, whereArgs);
            db.close();
            return true;
        } catch (SQLException e){
            return false;
        }
    }

    public static Boolean destroyAllAssessment (Context context){
        return destroyAllAssessment(null, null, context);
    }

    public static Boolean destroyAllAssessment(String whereClause, String[] whereArgs, Context context){
        try {
            SQLiteDatabase db = getToWriting(context);
            db.delete("ASSESSMENTS", whereClause, whereArgs);
            db.close();
            return true;
        } catch (SQLException e){
            return false;
        }
    }
}