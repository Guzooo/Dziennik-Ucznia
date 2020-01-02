package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class Assessment2020 extends DatabaseObject{
    private float assessment;
    private int weight;
    private String note;
    private int semester;
    private int idSubject;
    private int idCategory;
    private String date;

    public final static String DATABASE_NAME = "ASSESSMENTS";
    public final static String[] ON_CURSOR = new String[] {
            "_id",
            "ASSESSMENT",
            "WEIGHT",
            "NOTE",
            "SEMESTER",
            "TAB_SUBJECT",
            "TAB_CATEGORY_ASSESSMENT",
            "DATA"
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
                1,
                "",
                0,
                0,
                0,
                UtilsCalendar.getTodayForWriting());
    }

    @Override
    public ContentValues getContentValues(Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("ASSESSMENT", assessment);
        contentValues.put("WEIGHT", weight);
        contentValues.put("NOTE", note);
        contentValues.put("SEMESTER", semester);
        contentValues.put("TAB_SUBJECT", idSubject);
        contentValues.put("TAB_CATEGORY_ASSESSMENT", idCategory);
        contentValues.put("DATA", date);
        return contentValues;
    }

    public Float getAssessment(){
        return assessment;
    }

    public void setAssessment(float assessment) {
        this.assessment = assessment;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
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

    public String getDate() {
        return UtilsCalendar.getDateForReading(date);
    }

    public void setDate(String date) {
        this.date = date;
    }
}
