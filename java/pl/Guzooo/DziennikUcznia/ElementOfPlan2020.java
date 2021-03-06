package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.database.Cursor;

public class ElementOfPlan2020 extends DatabaseObject {
    public static final String TIME_START = "TIME_START";
    public static final String TIME_END = "TIME_END";
    public static final String TAB_SUBJECT = "TAB_SUBJECT";
    public static final String DAY = "DAY";
    public static final String CLASSROOM = "CLASSROOM";

    private int timeStart;
    private int timeEnd;
    private int idSubject;
    private int day;
    private String classroom;

    public final static String DATABASE_NAME = "LESSON_PLAN";
    public final static String[] ON_CURSOR = new String[] {
            Database2020.ID,
            TIME_START,
            TIME_END,
            TAB_SUBJECT,
            DAY,
            CLASSROOM
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
                480, //TODO: takie cuda jak: poczatek lekcji; dlugosc lekcji; dlugosc przerwy
                525,
                0,
                0,
                "");
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TIME_START, timeStart);
        contentValues.put(TIME_END, timeEnd);
        contentValues.put(TAB_SUBJECT, idSubject);
        contentValues.put(DAY, day);
        contentValues.put(CLASSROOM, classroom);
        return contentValues;
    }

    public String getTime(){
        return getTimeStart() + " - " + getTimeEnd();
    }

    public String getTimeStart(){
        return UtilsCalendar.getTimeToReadFromWriteOnlyMinutes(timeStart);
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

    public String getTimeEnd(){
        return UtilsCalendar.getTimeToReadFromWriteOnlyMinutes(timeEnd);
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

    public void setTimeEndByTimeStart(){
        setTimeEnd(timeStart + 45); //TODO:może uwzglednić w jakims ustawieniu
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