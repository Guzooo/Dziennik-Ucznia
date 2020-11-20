package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class Assessment2020 extends DatabaseObject{
    public static final String ASSESSMENT = "ASSESSMENT";
    public static final String WEIGHT = "WEIGHT";
    public static final String NOTE = "NOTE";
    public static final String SEMESTER = "SEMESTER";
    public static final String TAB_SUBJECT = "TAB_SUBJECT";
    public static final String TAB_CATEGORY_ASSESSMENT = "TAB_CATEGORY_ASSESSMENT";
    public static final String DATA = "DATA";

    private float assessment;
    private int weight;
    private String note;
    private int semester;
    private int idSubject;
    private int idCategory;
    private String date;

    public final static String DATABASE_NAME = "ASSESSMENTS";
    public final static String[] ON_CURSOR = new String[] {
            Database2020.ID,
            ASSESSMENT,
            WEIGHT,
            NOTE,
            SEMESTER,
            TAB_SUBJECT,
            TAB_CATEGORY_ASSESSMENT,
            DATA
    };

    private void template(int id,
                          float assessment,
                          int weight,
                          String note,
                          int semester,
                          int idSubject,
                          int idCategory,
                          String date){
        setId(id);
        setAssessment(assessment);
        setWeight(weight);
        setNote(note);
        setSemester(semester);
        setIdSubject(idSubject);
        setIdCategory(idCategory);
        setDate(date);
    }

    @Override
    public String[] onCursor() {
        return ON_CURSOR;
    }

    @Override
    public String databaseName() {
        return DATABASE_NAME;
    }

    @Override
    public void setVariablesOfCursor(Cursor cursor) {
        template(cursor.getInt(0),
                cursor.getFloat(1),
                cursor.getInt(2),
                cursor.getString(3),
                cursor.getInt(4),
                cursor.getInt(5),
                cursor.getInt(6),
                cursor.getString(7));
    }

    @Override
    public void setVariablesEmpty() {
        template(0,
                0,
                -1,
                "",
                0,
                0,
                0,
                UtilsCalendar.getTodayToWrite());
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ASSESSMENT, assessment);
        contentValues.put(WEIGHT, weight);
        contentValues.put(NOTE, note);
        contentValues.put(SEMESTER, semester);
        contentValues.put(TAB_SUBJECT, idSubject);
        contentValues.put(TAB_CATEGORY_ASSESSMENT, idCategory);
        contentValues.put(DATA, date);
        return contentValues;
    }

    public String getAssessmentToRead(){
        String text = getAssessment() + "";
        text = text.replace(".0", "");
        text = text.replace(".5", "+");
        return text;
    }

    public Float getAssessment(){
        return assessment;
    }

    public void setAssessment(float assessment) {
        this.assessment = assessment;
    }

    public int getRealWeight(Context context){
        if(weight == -1)
            return getDefaultWeight(context);
        return weight;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getDefaultWeight(Context context){
        CategoryOfAssessment2020 categoryOfAssessment = new CategoryOfAssessment2020();
        categoryOfAssessment.setVariablesOfId(idCategory, context);
        return categoryOfAssessment.getDefaultWeight();
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getIdSubject() {
        return idSubject;
    }

    public void setIdSubject(int idSubject) {
        this.idSubject = idSubject;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public String getDateToRead() {
        return UtilsCalendar.getDateToRead(date);
    }

    public String getDate(){
        return date;
    }

    public int[] getDateElements(){
        String[] elements = date.split("/");
        return new int[] {Integer.parseInt(elements[0]),
                                Integer.parseInt(elements[1]),
                                Integer.parseInt(elements[2])};//TODO: czemu nie valueOf
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDate(int day, int month, int year){
        date = day + "/" + month + "/" + year;
    }
}
