package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import java.util.Calendar;

public class SubjectAssessment {

    private int id;
    private float assessment;
    private String note;
    private int semester;
    private int subjectId;
    private int categoryId;
    private String data;

    private ContentValues contentValues = new ContentValues();

    public static final String[] subjectAssessmentOnCursor = {"_id", "ASSESSMENT", "NOTE", "SEMESTER", "TAB_SUBJECT", "TAB_CATEGORY_ASSESSMENT", "DATA"};

    private SubjectAssessment (int id, float assessment, String note, int semester, int idSubject, int idCategoryAssessment, String data){
        this.id = id;
        setAssessment(assessment);
        setNote(note);
        setSemester(semester);
        setSubjectId(idSubject);
        setCategoryId(idCategoryAssessment);
        setData(data);
    }

    public static SubjectAssessment newEmpty (){
        return new SubjectAssessment(0, 0, "", 0, 0, 0, getToday());
    }

    public static SubjectAssessment getOfCursor(Cursor cursor){
        return new SubjectAssessment(cursor.getInt(0),
                cursor.getFloat(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getInt(4),
                cursor.getInt(5),
                cursor.getString(6));
    }

    public static SubjectAssessment getOfId (int id, Context context){
        SubjectAssessment assessment;
        SQLiteDatabase db = DatabaseUtils.getReadableDatabase(context);
        Cursor cursor = db.query("ASSESSMENTS",
                SubjectAssessment.subjectAssessmentOnCursor,
                "_id = ?",
                new String[]{Integer.toString(id)},
                null, null, null);

        if (cursor.moveToFirst()) {
            assessment = SubjectAssessment.getOfCursor(cursor);
        } else {
            assessment = SubjectAssessment.newEmpty();
        }

        cursor.close();
        db.close();
        return assessment;
    }

    public void insert(Context context){
        try {
            SQLiteDatabase db = DatabaseUtils.getWritableDatabase(context);
            db.insert("ASSESSMENTS", null, contentValues);
            contentValues.clear();
            db.close();
        } catch (SQLiteException e){
            Toast.makeText(context, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    public void update(Context context){
        try {
            SQLiteDatabase db = DatabaseUtils.getWritableDatabase(context);
            db.update("ASSESSMENTS",
                    contentValues,
                    "_id = ?",
                    new String[]{Integer.toString(getId())});
            contentValues.clear();
            db.close();
        } catch (SQLiteException e){
            Toast.makeText(context, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    public void delete(Context context){
        try {
            SQLiteDatabase db = DatabaseUtils.getWritableDatabase(context);
            db.delete("ASSESSMENTS",
                    "_id = ?",
                    new String[]{Integer.toString(getId())});
            db.close();
        } catch (SQLiteException e){
            Toast.makeText(context, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    public int getId() {
        return id;
    }

    public float getAssessment() {
        return assessment;
    }

    public String getNote() {
        return note;
    }

    public int getSemester() {
        return semester;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getData(){
        return data;
    }

    public void setAssessment(float assessment) {
        this.assessment = assessment;
        contentValues.put("ASSESSMENT", getAssessment());
    }

    public void setNote(String note) {
        this.note = note;
        contentValues.put("NOTE", getNote());
    }

    public void setSemester(int semester) {
        this.semester = semester;
        contentValues.put("SEMESTER", getSemester());
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
        contentValues.put("TAB_SUBJECT", getSubjectId());
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
        contentValues.put("TAB_CATEGORY_ASSESSMENT", getCategoryId());
    }

    public void setData(String data){
        this.data = data;
        contentValues.put("DATA", getData());
    }

    public static String getToday(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        return  day + "/" + month + "/" + year;
    }
}
