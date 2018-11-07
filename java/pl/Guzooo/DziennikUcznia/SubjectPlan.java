package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

public class SubjectPlan {

    private int id;
    private int timeStart;
    private int timeEnd;
    private int idSubject;
    private int day;
    private String classroom;
    private ContentValues contentValues = new ContentValues();

    public static final String[] subjectPlanOnCursor = {"_id", "TIME_START", "TIME_END", "TAB_SUBJECT", "DAY", "CLASSROOM"};

    private SubjectPlan(int id, int timeStart, int timeEnd, int idSubject, int day, String classroom) {
        this.id = id;
        setTimeStart(timeStart);
        setTimeEnd(timeEnd);
        setIdSubject(idSubject);
        setDay(day);
        setClassroom(classroom);
    }

    public static SubjectPlan newEmpty(){
        return new SubjectPlan(0, 0, 0, 0, 0, "");
    }

    public static SubjectPlan getOfCursor(Cursor cursor){
        return new SubjectPlan(cursor.getInt(0),
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getInt(3),
                cursor.getInt(4),
                cursor.getString(5));
    }

    public static SubjectPlan getOfId(int id, Context context){
        SubjectPlan subjectPlan;
        SQLiteDatabase db = StaticMethod.getReadableDatabase(context);
        Cursor cursor = db.query("LESSON_PLAN",
                SubjectPlan.subjectPlanOnCursor,
                "_id = ?",
                new String[]{Integer.toString(id)},
                null, null, null);

        if(cursor.moveToFirst()) {
            subjectPlan = SubjectPlan.getOfCursor(cursor);
        } else {
            subjectPlan = SubjectPlan.newEmpty();
        }

        cursor.close();
        db.close();
        return subjectPlan;
    }

    SubjectPlan(Cursor cursor){ //old
        this.id = cursor.getInt(0);
        this.timeStart = cursor.getInt(1);
        this.timeEnd = cursor.getInt(2);
        this.idSubject = cursor.getInt(3);
        this.day = cursor.getInt(4);
        this.classroom = cursor.getString(5);
    }

    public void insert(Context context){
        try {
            SQLiteDatabase db = StaticMethod.getWritableDatabase(context);
            db.insert("LESSON_PLAN", null, contentValues);
            contentValues.clear();
            db.close();
        } catch (SQLiteException e){
            Toast.makeText(context, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }


    public void update (Context context){
        try {
            SQLiteDatabase db = StaticMethod.getWritableDatabase(context);
            db.update("LESSON_PLAN",
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
            SQLiteDatabase db = StaticMethod.getWritableDatabase(context);
            db.delete("LESSON_PLAN",
                    "_id = ?",
                    new String[]{Integer.toString(getId())});
            db.close();
        } catch (SQLiteException e){
            Toast.makeText(context, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    public ContentValues saveSubjectPlan(){ //old
        ContentValues contentValues = new ContentValues();

        contentValues.put("TIME_START", timeStart);
        contentValues.put("TIME_END", timeEnd);
        contentValues.put("TAB_SUBJECT", idSubject);
        contentValues.put("DAY", day);
        contentValues.put("CLASSROOM", classroom);

        return contentValues;
    }

    public int getId(){
        return id;
    }

    public int getIdSubject(){
        return idSubject;
    }

    public int getTimeStartHours(){
        return timeStart / 60;
    }

    public int getTimeStartMinutes(){
        return timeStart % 60;
    }

    public int getTimeEndHours() {
        return timeEnd / 60;
    }

    public int getTimeEndMinutes() {
        return timeEnd % 60;
    }

    public String getTime(){
        return convertOnTime(timeStart) + " - " + convertOnTime(timeEnd);
    }

    public int getDay() {
        return day;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setIdSubject(int id){
        this.idSubject = id;
        contentValues.put("TAB_SUBJECT", getIdSubject());
    }

    public void setTimeStart(int timeStart) {
        this.timeStart = timeStart;
        contentValues.put("TIME_START", convertFromTime(getTimeStartHours(), getTimeStartMinutes()));
    }

    public void setTimeEnd(int timeEnd){
        this.timeEnd = timeEnd;
        contentValues.put("TIME_END", convertFromTime(getTimeEndHours(), getTimeEndMinutes()));
    }

    public void setDay(int day) {
        this.day = day;
        contentValues.put("DAY", getDay());
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
        contentValues.put("CLASSROOM", getClassroom());
    }

    public int convertFromTime(int hours, int minutes){
        return hours * 60 + minutes;
    }

    private String convertOnTime(int together){
        String string = "";
        int hours = together / 60;
        int minutes = together % 60;

        if(hours < 10){
            string += "0";
        }
        string += hours + ":";

        if(minutes < 10){
            string += "0";
        }
        string += minutes;

        return string;
    }
}
