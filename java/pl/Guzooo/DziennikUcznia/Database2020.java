package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class Database2020 extends SQLiteOpenHelper {

    public static final String ID = "_id";

    private static final String DB_NAME = "dziennikucznia";
    private static final int DB_VERSION = 8; //TODO: Już nowe

    Database2020(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableSubjects(db);
        createTableNotes(db);
        createTableLessonPlan(db);
        createTableAssessments(db);
        createTableCategoryAssessment(db);
        createDefaultCategoryOfAssessment(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion ) {
        if(oldVersion < 8){
            upgradeDatabaseLessThan8(db);
        }
    }

    private void upgradeDatabaseLessThan8(SQLiteDatabase db){
        //Medoty naprawcze bazy danych dla urządzeń z zainstalowaną wersją 12 lub niższą;
        changeOldTableNames(db);
        createTableSubjects(db);
        moveDataToNewTable(db);
        deleteOldTable(db);
        upgradeTableCategoryAssessment(db);
    }

    private void changeOldTableNames(SQLiteDatabase db){
        db.execSQL("ALTER TABLE SUBJECTS RENAME TO oldSUBJECTS");
    }

    private void moveDataToNewTable(SQLiteDatabase db){
        db.execSQL("INSERT INTO " + Subject2020.DATABASE_NAME + " SELECT "
                + ID + ", "
                + Subject2020.NAME + ", "
                + Subject2020.DESCRIPTION + ", "
                + Subject2020.TEACHER + ", "
                + Subject2020.UNPREPAREDNESS + ", "
                + Subject2020.UNPREPAREDNESS1 + ", "
                + Subject2020.UNPREPAREDNESS2 + " FROM oldSUBJECTS");
    }

    private void deleteOldTable(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS oldSUBJECTS");
    }

    private void upgradeTableCategoryAssessment(SQLiteDatabase db){
        addTableWeight(db);
        setDefaultWeightForExistingElements(db);
    }

    private void addTableWeight(SQLiteDatabase db){
        db.execSQL("ALTER TABLE " + CategoryOfAssessment2020.DATABASE_NAME + " ADD COLUMN " + CategoryOfAssessment2020.WEIGHT + " INTEGER");
    }

    private void setDefaultWeightForExistingElements(SQLiteDatabase db){
        db.update(CategoryOfAssessment2020.DATABASE_NAME, defaultWeight(), null, null);
    }

    private ContentValues defaultWeight(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CategoryOfAssessment2020.WEIGHT, 1);
        return contentValues;
    }

    private void createTableSubjects(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + Subject2020.DATABASE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Subject2020.NAME + " TEXT,"
                + Subject2020.DESCRIPTION + " TEXT,"
                + Subject2020.TEACHER + " TEXT,"
                + Subject2020.UNPREPAREDNESS + " INTEGER,"
                + Subject2020.UNPREPAREDNESS1 + " INTEGER,"
                + Subject2020.UNPREPAREDNESS2 + " INTEGER)");
    }

    private void createTableNotes(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + Note2020.DATABASE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Note2020.NAME + " TEXT,"
                + Note2020.NOTE + " TEXT,"
                + Note2020.TAB_SUBJECT + " INTEGER)");
    }

    private void createTableLessonPlan(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + ElementOfPlan2020.DATABASE_NAME + " (" +  ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ElementOfPlan2020.TIME_START + " INTEGER,"
                + ElementOfPlan2020.TIME_END + " INTEGER,"
                + ElementOfPlan2020.TAB_SUBJECT + " INTEGER,"
                + ElementOfPlan2020.DAY + " INTEGER,"
                + ElementOfPlan2020.CLASSROOM + " TEXT)");
    }

    private void createTableAssessments(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + Assessment2020.DATABASE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Assessment2020.ASSESSMENT + " REAL,"
                + Assessment2020.WEIGHT + " INTEGER,"
                + Assessment2020.NOTE + " TEXT,"
                + Assessment2020.SEMESTER + " INTEGER,"
                + Assessment2020.TAB_SUBJECT + " INTEGER,"
                + Assessment2020.TAB_CATEGORY_ASSESSMENT + " INTEGER,"
                + Assessment2020.DATA + " TEXT)");
    }

    private void createTableCategoryAssessment(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + CategoryOfAssessment2020.DATABASE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CategoryOfAssessment2020.NAME + " TEXT,"
                + CategoryOfAssessment2020.COLOR + " TEXT,"
                + CategoryOfAssessment2020.WEIGHT + " INTEGER)");
    }

    public static void createDefaultCategoryOfAssessment(SQLiteDatabase db){
        CategoryOfAssessment2020 category = new CategoryOfAssessment2020();
        category.setName("DEFAULT");
        db.insert(CategoryOfAssessment2020.DATABASE_NAME, null, category.getContentValues());
        //TODO: stara wersja aplikacji i przerzuta na nowa
        //TODO:kod który ustawia domyślną kategorie dla ocen
       /*      Cursor cursor = db.query("CATEGORY_ASSESSMENT",
                new String[] {"_id"},
                null, null, null, null, null);
        if(cursor.moveToFirst())
            CategoryAssessment.setPreferenceDefaultCategory(cursor.getInt(0), context);
        cursor.close();
        db.close();*/
    }

    public static SQLiteDatabase getToWriting(Context context){
        SQLiteOpenHelper openHelper = new Database2020(context);
        return openHelper.getWritableDatabase();
    }

    public static SQLiteDatabase getToReading(Context context){
        SQLiteOpenHelper openHelper = new Database2020(context);
        return openHelper.getReadableDatabase();
    }

    public static void ErrorToast(Context context){
        Toast.makeText(context, R.string.error_database, Toast.LENGTH_SHORT).show();
    }
}