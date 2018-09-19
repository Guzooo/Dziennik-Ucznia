package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;


public class Subject {

    private int id;
    private String name;
    private String teacher;
    private ArrayList<Float> assessments = new ArrayList<>();
    private int unpreparedness;
    private String description;
    private ArrayList<SubjectNote> subjectNotes = new ArrayList<>();

    public static final String[] subjectOnCursor = {"_id", "NAME", "TEACHER", "ASSESSMENTS", "UNPREPAREDNESS", "DESCRIPTION"};
    public static final String[] subjectOnCursorWithDay = {"_id", "NAME", "TEACHER", "ASSESSMENTS", "UNPREPAREDNESS", "DESCRIPTION", "DAY"};

    public Subject (int id, String name, String teacher, ArrayList<Float> assessments, int unpreparedness, String description){
        this.id = id;
        setName(name);
        setTeacher(teacher);
        setAssessments(assessments);
        setUnpreparedness(unpreparedness);
        setDescription(description);
    }

    public Subject (int id, String name, String teacher, String assessments, int unpreparedness, String description){
        this.id = id;
        setName(name);
        setTeacher(teacher);
        fromStringAssessments(assessments);
        setUnpreparedness(unpreparedness);
        setDescription(description);
    }

    public Subject (Cursor cursor){
        this.id = cursor.getInt(0);
        setName(cursor.getString(1));
        setTeacher(cursor.getString(2));
        fromStringAssessments(cursor.getString(3));
        setUnpreparedness(cursor.getInt(4));
        setDescription(cursor.getString(5));
    }

    public Subject (String object , int id){ //old method
        this.id = id;
        String[] strings =  object.split("©");
        setName(strings[0]);
        setTeacher(strings[1]);
        fromStringAssessments(strings[2]);
        setDescription(strings[3]);
        fromStringSubjectNotes(strings[4]);
        setUnpreparedness(Integer.parseInt(strings[15]));
    }

    public ContentValues saveSubject(Context context){
        ContentValues contentValues = new ContentValues();

        contentValues.put("NAME", getName());
        contentValues.put("TEACHER", getTeacher());
        contentValues.put("ASSESSMENTS", toStringAssessments());
        contentValues.put("UNPREPAREDNESS", getUnpreparedness());
        contentValues.put("DESCRIPTION", getDescription());
        contentValues.put("NOTES", getSizeNotes(context));
        contentValues.put("DAY", getDay(context, 0));

        return contentValues;
    }

    public int getId(){
        return id;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public float getAverage(){
        float average = 0;
        if(assessments.size() > 0) {
            for (int i = 0; i < assessments.size(); i++) {
                average += assessments.get(i);
            }
            average = average / assessments.size();
        }
        return average;
    }

    public int getRoundedAverage(SharedPreferences sharedPreferences){
        float average = getAverage();
        int roundedAverage;
        if(average >= sharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_SIX, SettingActivity.defaultAverageToSix)){
            roundedAverage = 6;
        } else if(average >= sharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_FIVE, SettingActivity.defaultAverageToFive)){
            roundedAverage = 5;
        } else if(average >= sharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_FOUR, SettingActivity.defaultAverageToFour)){
            roundedAverage = 4;
        } else if(average >= sharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_THREE, SettingActivity.defaultAverageToThree)){
            roundedAverage = 3;
        } else if(average >= sharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_TWO, SettingActivity.defaultAverageToTwo)){
            roundedAverage = 2;
        } else {
            roundedAverage = 1;
        }
        return roundedAverage;
    }

    public void setTeacher(String teacher){
        this.teacher = teacher;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setAssessments (ArrayList<Float> assessments){
        this.assessments.clear();
        this.assessments.addAll(assessments);
    }

    public String getStringAssessments(){
        String assessmentsString = "";
        if(assessments.size() > 0) {
            for (int i = 0; i < assessments.size(); i++) {
                assessmentsString += Float.toString(assessments.get(i)) + " ";
            }
        }
        return assessmentsString;
    }

    public ArrayList<Float> getAssessments(){
        return assessments;
    }

    public String toStringAssessments(){
        String string = "";
        for (int i = 0; i < assessments.size(); i++) {
            string += Float.toString(assessments.get(i)) + "®";
        }
        return string;
    }

    public void fromStringAssessments(String assessments){
        if(!assessments.equals("")) {
            String[] strings = assessments.split("®");
            for (int i = 0; i < strings.length; i++) {
                this.assessments.add(Float.parseFloat(strings[i]));
            }
        }
    }

    public void setUnpreparedness(int unpreparedness){
        this.unpreparedness = unpreparedness;
    }

    public void removeUnpreparedness(){
        unpreparedness--;
        if(unpreparedness < 0){
            unpreparedness = 0;
        }
    }

    public int getUnpreparedness(){
        return unpreparedness;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void fromStringSubjectNotes(String subjectNotes) {
        if (!subjectNotes.equals("")) {
            String[] strings = subjectNotes.split("®");
            for (int i = 0; i < strings.length; i += 10) {
                this.subjectNotes.add(new SubjectNote(strings[i], strings[i + 1], this.id));
            }
        }
    }

    public ArrayList<SubjectNote> getSubjectNotes() {
        return subjectNotes;
    }

    public int getSizeNotes(Context context){
        int size = 0;
        try{
            SQLiteOpenHelper openHelper = new HelperDatabase(context);
            SQLiteDatabase db = openHelper.getReadableDatabase();
            Cursor cursor = db.query("NOTES",
                    new String[] {"COUNT(_id) AS count"},
                    "TAB_SUBJECT = ?",
                    new String[] {Integer.toString(getId())},
                    null, null, null);
            if(cursor.moveToFirst()){
                size = cursor.getInt(0);
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e){
            Toast.makeText(context, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
        return size;
    }

    private int getDay(Context context, int notThis) {
        int i = 0;
        try {
            SQLiteOpenHelper openHelper = new HelperDatabase(context);
            SQLiteDatabase db = openHelper.getReadableDatabase();
            Cursor cursor = db.query("LESSON_PLAN",
                    new String[]{"DAY"},
                    "TAB_SUBJECT = ?",
                    new String[]{Integer.toString(getId())},
                    null, null,
                    "DAY");
            i = getDayFromCursor(cursor, notThis);
            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            Toast.makeText(context, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
        return i;
    }

    public ContentValues saveDay(Context context, int notThis){
        ContentValues contentValues = new ContentValues();
        contentValues.put("DAY", getDay(context, notThis));
        return contentValues;
    }

    private int getDayFromCursor(Cursor cursor, int notThis){
        int c = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) -1;
        int z = 0;

        if(c == 0){
            c = 7;
        }

        for (int i = c; i <= 7; i++) {
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getInt(0) >= c) {
                        if(cursor.getInt(0) == notThis){
                            z = notThis;
                        } else {
                            return cursor.getInt(0);
                        }
                    }
                } while (cursor.moveToNext());
            }
        }
        for (int i = 1; i < c; i++){
            if(cursor.moveToFirst()) {
                if (cursor.getInt(0) == notThis) {
                    z = notThis;
                } else
                    return cursor.getInt(0);
            }
        }
        return z;
    }
}
