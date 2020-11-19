package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.util.ArrayList;

public class Subject2020 extends DatabaseObject{
    public static final String NAME = "NAME";
    public static final String TEACHER = "TEACHER";
    public static final String UNPREPAREDNESS1 = "UNPREPAREDNESS1";
    public static final String UNPREPAREDNESS2 = "UNPREPAREDNESS2";
    public static final String UNPREPAREDNESS = "UNPREPAREDNESS";
    public static final String DESCRIPTION = "DESCRIPTION";

    private String name;
    private String teacher;
    private int unpreparednessOfSemesterI;
    private int unpreparednessOfSemesterII;
    private int unpreparednessDefault;
    private String description;

    public final static String DATABASE_NAME = "SUBJECTS";
    public final static String[] ON_CURSOR = new String[] {
            Database2020.ID,
            NAME,
            TEACHER,
            UNPREPAREDNESS1,
            UNPREPAREDNESS2,
            UNPREPAREDNESS,
            DESCRIPTION
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
                -1,
                -1,
                0,
                "");
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(TEACHER, teacher);
        contentValues.put(UNPREPAREDNESS1, unpreparednessOfSemesterI);
        contentValues.put(UNPREPAREDNESS2, unpreparednessOfSemesterII);
        contentValues.put(UNPREPAREDNESS, unpreparednessDefault);
        contentValues.put(DESCRIPTION, description);
        return contentValues;
    }

    @Override
    public void delete(Context context) {
        if(Database2020.delSubjectElements(getId(), context))
            super.delete(context);
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

    public int getRealUnpreparednessOfCurrentSemester(Context context){
        int unpreparedness = getUnpreparednessOfCurrentSemester(context);
        return getRealUnpreparedness(unpreparedness);
    }

    public int getUnpreparednessOfCurrentSemester(Context context){
        int semester = DataManager.getSemester(context);
        return getUnpreparednessOfSemester(semester);
    }

    public void setUnpreparednessOfCurrentSemester(int unpreparedness, Context context){
        int semester = DataManager.getSemester(context);
        setUnpreparednessOfSemester(unpreparedness, semester);
    }

    public int getUnpreparednessDefault(){
        return unpreparednessDefault;
    }

    public void setUnpreparednessDefault(int unpreparednessDefault){
        this.unpreparednessDefault = unpreparednessDefault;
    }

    public void useUnpreparedness(Context context){
        int unpreparedness = getRealUnpreparednessOfCurrentSemester(context);
        if(unpreparedness > 0)
            unpreparedness--;
        else
            Toast.makeText(context,R.string.unpreparedness_info_end, Toast.LENGTH_SHORT).show();
        setUnpreparednessOfCurrentSemester(unpreparedness, context);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getFinalAverage(Context context){
        return UtilsAverage.getSubjectFinalAverage(getId(), context);
    }

    public float getSemesterAverage(int semester, Context context){
        return UtilsAverage.getSubjectSemesterAverage(getId(), semester, context);
    }

    public ArrayList<Assessment2020> getAssessments(int semester, Context context){
        return UtilsAverage.getAssemssents(getId(), semester, context);
    }
 /*
    public void useUnpreparedness(Context context){
        if (StatisticsActivity.getSemester(context) == 1)
            unpreparednessOfSemesterI--;
        unpreparednessOfSemesterII--;
    }

    public void getAssessments(Context context){
        //TODO: stworzyć oceny z ich klasy
        // doprecyzować czy nazwe tej funkcji
    }*/
 //TODO: usuwanie tam ocen, notatek itp;

    private int getUnpreparednessOfSemester(int semester) {
        if (semester == 1)
            return getUnpreparednessOfSemesterI();
        return getUnpreparednessOfSemesterII();
    }

    private void setUnpreparednessOfSemester(int unpreparedness, int semester) {
        if (semester == 1)
            setUnpreparednessOfSemesterI(unpreparedness);
        else
            setUnpreparednessOfSemesterII(unpreparedness);
    }

    private int getRealUnpreparedness(int unpreparedness) {
        if (unpreparedness == -1)
            return unpreparednessDefault;
        return unpreparedness;
    }

    private int getUnpreparednessOfSemesterI() {
        return unpreparednessOfSemesterI;
    }

    private void setUnpreparednessOfSemesterI(int unpreparednessOfSemesterI) {
        this.unpreparednessOfSemesterI = unpreparednessOfSemesterI;
    }

    private int getUnpreparednessOfSemesterII() {
        return unpreparednessOfSemesterII;
    }

    private void setUnpreparednessOfSemesterII(int unpreparednessOfSemesterII) {
        this.unpreparednessOfSemesterII = unpreparednessOfSemesterII;
    }
}
