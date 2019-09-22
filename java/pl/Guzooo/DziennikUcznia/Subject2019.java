package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class Subject2019 extends DatabaseObject{
    private String name;
    private String teacher;
    private int unpreparednessDefault;
    private int unpreparedness;
    private String description;

    public static String databaseName = "SUBJECT";
    public static String[] onCursorStr(Context context){
        String unpreparedness;
        if(StatisticsActivity.getSemester(context) == 1)
            unpreparedness = "UNPREPAREDNESS1";
        else
            unpreparedness = "UNPREPAREDNESS2";
        return new String[] {"_id", "NAME", "TEACHER", "UNPREPAREDNESS", unpreparedness, "DESCRIPTION"/*, "TODAY", "DAY", "NUMBER_NOTES", "ASSESSMENTS_EXIST*/};
    }

    @Override
    public String[] onCursor(Context context){
        return onCursorStr(context);
    }

    @Override
    public String databaseName() {
        return databaseName;
    }

    private void Template(int id, String name, String teacher, int unpreparednessDefault, int unpreparedness, String description) {
        setId(id);
        setName(name);
        setTeacher(teacher);
        setUnpreparednessDefault(unpreparednessDefault);
        setUnpreparedness(unpreparedness);
        setDescription(description);
    }

    @Override
    public void SetOfCursor(Cursor cursor) {
        Template(cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getInt(4),
                cursor.getString(5));
    }

    @Override
    public void SetEmpty() {
        Template(0, "", "", 0, 0, "");
    }

    @Override
    public ContentValues getContentValues(Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("TEACHER", teacher);
        contentValues.put("UNPREPAREDNESS", unpreparednessDefault);
        contentValues.put("UNPREPAREDNESS" + StatisticsActivity.getSemester(context), unpreparedness);
        contentValues.put("DESCRIPTION", description);
        return contentValues;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public int getUnpreparednessDefault() {
        return unpreparednessDefault;
    }

    public void setUnpreparednessDefault(int unpreparednessDefault) {
        this.unpreparednessDefault = unpreparednessDefault;
    }

    public int getUnpreparedness() {
        return unpreparedness;
    }

    public void setUnpreparedness(int unpreparedness) {
        this.unpreparedness = unpreparedness;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void UseUnpreparedness(){
        unpreparedness--;
    }

    public String getAssessments(Context context){
        try{
            SQLiteDatabase db = DatabaseUtils.getReadableDatabase(context);
            Cursor cursor = db.query("ASSESSMENTS",
                    SubjectAssessment.subjectAssessmentOnCursor,
                    "TAB_SUBJECT = ? AND SEMESTER = ?",
                    new String[]{Integer.toString(getId()), Integer.toString(StatisticsActivity.getSemester(context))},
                    null, null, null);
            String assessments = "";
            if(cursor.moveToFirst()){
                do{
                    assessments += SubjectAssessment.getOfCursor(cursor).getAssessment() + " ";
                } while (cursor.moveToNext());
                assessments = assessments.replaceAll(".0", "");
                assessments = assessments.replaceAll(".5", "+");
            } else {
                assessments = context.getString(R.string.null_string);
            }
            cursor.close();
            db.close();
            return assessments;
        }catch (SQLiteException e){
            HelperDatabase.ErrorToast(context);
        }
        return context.getString(R.string.error_database);
    }

    public void SetDay(){

    }

    public void SetNumberNotes(){

    }

    public void SetAssessmentExist(){

    }
}
