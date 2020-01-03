package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.database.Cursor;

public class ElementOfPlan2020 extends DatabaseObject {
    private int timeStart;
    private int timeEnd;
    private int idSubject;
    private int day;
    private String classroom;

    public final static String DATABASE_NAME = "LESSON_PLAN";
    public final static String[] ON_CURSOR = new String[] {
            "TIME_START",
            "TIME_END",
            "TAB_SUBJECT",
            "DAY",
            "CLASSROOM"
    };

    @Override
    public String[] onCursor() {
        return ON_CURSOR;
    }

    @Override
    public String databaseName() {
        return DATABASE_NAME;
    }

    private void template(int id,
                          int timeStart,
                          int timeEnd,
                          int idSubject,
                          int day,
                          String classroom){
        setId(id);
        setTimeStart(timeStart);
        setTimeEnd(timeEnd);
        setIdSubject(idSubject);
        setDay(day);
        setClassroom(classroom);
    }

    @Override
    public void setVariablesOfCursor(Cursor cursor) {
        template(cursor.getInt(0),
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getInt(3),
                cursor.getInt(4),
                cursor.getString(5));
    }

    @Override
    public void setVariablesEmpty() {
        template(0,
                0,
                0,
                0,
                0,
                "");
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("TIME_START", timeStart);
        contentValues.put("TIME_END", timeEnd);
        contentValues.put("TAB_SUBJECT", idSubject);
        contentValues.put("DAY", day);
        contentValues.put("CLASSROOM", classroom);
        return null;
    }

    public String getTime(){
        String timeStart = UtilsCalendar.getTimeToReadFromWriteOnlyMinutes(this.timeStart);
        String timeEnd = UtilsCalendar.getTimeToReadFromWriteOnlyMinutes(this.timeEnd);
        return timeStart + " - " + timeEnd;
    }

    public int getTimeStartHours() {
        return UtilsCalendar.getHoursFromWriteOnlyMinutes(timeStart);
    }

    public int getTimeStartMinutes(){
        return UtilsCalendar.getMinutesFromWriteOnlyMinutes(timeStart);
    }

    public void setTimeStart(int allMinutes) {
        timeStart = allMinutes;
    }

    public void setTimeStart(int hours, int minutes){
        timeStart = UtilsCalendar.getWriteOnlyMinutes(hours, minutes);
    }

    public int getTimeEndHours() {
        return UtilsCalendar.getHoursFromWriteOnlyMinutes(timeEnd);
    }

    public int getTimeEndMinutes(){
        return UtilsCalendar.getMinutesFromWriteOnlyMinutes(timeEnd);
    }

    public void setTimeEnd(int allMinutes) {
        timeEnd = allMinutes;
    }

    public void setTimeEnd(int hours, int minutes){
        timeEnd = UtilsCalendar.getWriteOnlyMinutes(hours, minutes);
    }

    public int getIdSubject() {
        return idSubject;
    }

    public void setIdSubject(int idSubject) {
        this.idSubject = idSubject;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }
}