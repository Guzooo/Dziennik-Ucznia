package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class Subject2020 extends DatabaseObject{
    private String name;
    private String teacher;
    private int unpreparednessOfSemesterI;
    private int unpreparednessOfSemesterII;
    private int unpreparednessDefault;
    private String description;

    public final static String DATABASE_NAME = "SUBJECT";
    public final static String[] ON_CURSOR = new String[] {
            "_id",
            "NAME",
            "TEACHER",
            "UNPREPAREDNESS1",
            "UNPREPAREDNESS2",
            "UNPREPAREDNESS",
            "DESCRIPTION"/*,
            "TODAY",
            "DAY",
            "NUMBER_NOTES",
            "ASSESSMENTS_EXIST*/
    };


    @Override
    public String[] onCursor(){
        return ON_CURSOR;
    }

    @Override
    public String databaseName() {
        return DATABASE_NAME;
    }

    private void template(int id,
                          String name,
                          String teacher,
                          int unpreparednessOfSemesterI,
                          int unpreparednessOfSemesterII,
                          int unpreparednessDefault,
                          String description) {
        setId(id);
        setName(name);
        setTeacher(teacher);
        setUnpreparednessOfSemesterI(unpreparednessOfSemesterI);
        setUnpreparednessOfSemesterII(unpreparednessOfSemesterII);
        setUnpreparednessDefault(unpreparednessDefault);
        setDescription(description);
    }

    @Override
    public void setVariablesOfCursor(Cursor cursor) {
        template(cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getInt(4),
                cursor.getInt(5),
                cursor.getString(6));
    }

    @Override
    public void setVariablesEmpty() {
        template(0,
                "",
                "",
                0,
                0,
                0,
                "");
    }

    @Override
    public ContentValues getContentValues(Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("TEACHER", teacher);
        contentValues.put("UNPREPAREDNESS1", unpreparednessOfSemesterI);
        contentValues.put("UNPREPAREDNESS2", unpreparednessOfSemesterII);
        contentValues.put("UNPREPAREDNESS", unpreparednessDefault);
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

    public void setUnpreparednessOfSemesterI(int unpreparednessOfSemesterI) {
        this.unpreparednessOfSemesterI = unpreparednessOfSemesterI;
    }

    public void setUnpreparednessOfSemesterII(int unpreparednessOfSemesterII) {
        this.unpreparednessOfSemesterII = unpreparednessOfSemesterII;
    }

    public int getUnpreparednessDefault() {
        return unpreparednessDefault;
    }

    public void setUnpreparednessDefault(int unpreparednessDefault) {
        this.unpreparednessDefault = unpreparednessDefault;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

 /* public int getUnpreparednessOfCurrentSemester(Context context){
        getUnpreparednessOfSemester(StatisticsActivity.getSemester(context));
    }

    public int getUnpreparednessOfSemester(int semester){
        if(semester == 1)
            return unpreparednessOfSemesterI;
        return unpreparednessOfSemesterII;
    }

    public void useUnpreparedness(Context context){
        if (StatisticsActivity.getSemester(context) == 1)
            unpreparednessOfSemesterI--;
        unpreparednessOfSemesterII--;
    }

    public void getAssessments(Context context){
        //TODO: stworzyć oceny z ich klasy
        // doprecyzować czy nazwe tej funkcji
    }

    public void SetDay(){

    }

    public void SetNumberNotes(){

    }

    public void SetAssessmentExist(){

    }*/
}
