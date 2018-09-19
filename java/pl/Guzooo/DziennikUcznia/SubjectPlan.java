package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.database.Cursor;

public class SubjectPlan {

    private int id;
    private int timeStart;
    private int timeEnd;
    private int idSubject;
    private int day;
    private String classroom;

    public static final String[] subjectPlanOnCursor = {"_id", "TIME_START", "TIME_END", "TAB_SUBJECT", "DAY", "CLASSROOM"};

    SubjectPlan() {

    }

    SubjectPlan(Cursor cursor){
        this.id = cursor.getInt(0);
        this.timeStart = cursor.getInt(1);
        this.timeEnd = cursor.getInt(2);
        this.idSubject = cursor.getInt(3);
        this.day = cursor.getInt(4);
        this.classroom = cursor.getString(5);
    }

    public ContentValues saveSubjectPlan(){
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
    }

    public void setTimeStart(int timeStart) {
        this.timeStart = timeStart;
    }

    public void setTimeEnd(int timeEnd){
        this.timeEnd = timeEnd;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
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
