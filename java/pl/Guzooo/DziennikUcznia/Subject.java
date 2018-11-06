package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
    private ContentValues contentValues = new ContentValues();

    public static final String[] subjectOnCursor = {"_id", "NAME", "TEACHER", "ASSESSMENTS", "UNPREPAREDNESS", "DESCRIPTION"};
    public static final String[] subjectOnCursorWithDay = {"_id", "NAME", "TEACHER", "ASSESSMENTS", "UNPREPAREDNESS", "DESCRIPTION", "DAY"};

    private Subject (int id, String name, String teacher, String assessments, int unpreparedness, String description){
        this.id = id;
        setName(name);
        setTeacher(teacher);
        fromStringAssessments(assessments);
        setUnpreparedness(unpreparedness);
        setDescription(description);
    }

    public static Subject newEmpty (){
        Subject subject = new Subject (0, "", "", "", 0, "");
        subject.contentValues.put("NOTES", 0);
        subject.contentValues.put("DAY", 0);
        return subject;
    }

    public static Subject getOfCursor(Cursor cursor){
        return new Subject(cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getInt(4),
                cursor.getString(5));
    }

    public static Subject getOfId (int id, Context context){
        Subject subject;
        SQLiteDatabase db = StaticMethod.getReadableDatabase(context);
        Cursor cursor = db.query("SUBJECTS",
                Subject.subjectOnCursor,
                "_id = ?",
                new String[]{Integer.toString(id)},
                null, null, null);

        if (cursor.moveToFirst()) {
            subject = Subject.getOfCursor(cursor);
        } else {
            subject = Subject.newEmpty();
        }

        cursor.close();
        db.close();
        return subject;
    }

    private Subject getOfSubject (Subject subject){
        Subject newSubject = Subject.newEmpty();
        newSubject.setName(subject.getName());
        newSubject.setTeacher(subject.getTeacher());
        newSubject.setUnpreparedness(subject.getUnpreparedness());
        newSubject.setDescription(subject.getDescription());
        newSubject.fromStringAssessments(subject.toStringAssessments());
        return newSubject;
    }

    public void insert(Context context){
        try {
            SQLiteDatabase db = StaticMethod.getWritableDatabase(context);
            db.insert("SUBJECTS", null, contentValues);
            contentValues.clear();
            db.close();
        } catch (SQLiteException e){
            Toast.makeText(context, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    public void update(Context context){
        try {
            SQLiteDatabase db = StaticMethod.getWritableDatabase(context);
            db.update("SUBJECTS",
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
            db.delete("SUBJECTS",
                    "_id = ?",
                    new String[]{Integer.toString(getId())});
            db.close();
        } catch (SQLiteException e){
            Toast.makeText(context, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
        StaticMethod.destroyAllLessonPlan("TAB_SUBJECT = ?", new String[]{Integer.toString(getId())}, context);
        StaticMethod.destroyAllNotes("TAB_SUBJECT = ?", new String[]{Integer.toString(getId())}, context);
    }

    public boolean duplicate(Context context){
        try {
            SQLiteDatabase db = StaticMethod.getWritableDatabase(context);
            db.insert("SUBJECTS", null, getOfSubject(this).contentValues);
            return true;
        } catch (SQLiteException e){
            Toast.makeText(context, R.string.error_database, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public Subject (Cursor cursor){ //old
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

    public void putInfoSizeNotes(Context context){
        contentValues.put("NOTES", getSizeNotes(context));
    }

    public void putInfoDay(Context context){
        contentValues.put("DAY", getDay(context, 0));
    }

    public int getSizeContentValues(){
        return contentValues.size();
    }

    public ContentValues saveSubject(Context context){// old
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
        if(average >= sharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_SIX, SettingActivity.DEFAULT_AVERAGE_TO_SIX)){
            roundedAverage = 6;
        } else if(average >= sharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_FIVE, SettingActivity.DEFAULT_AVERAGE_TO_FIVE)){
            roundedAverage = 5;
        } else if(average >= sharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_FOUR, SettingActivity.DEFAULT_AVERAGE_TO_FOUR)){
            roundedAverage = 4;
        } else if(average >= sharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_THREE, SettingActivity.DEFAULT_AVERAGE_TO_THREE)){
            roundedAverage = 3;
        } else if(average >= sharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_TWO, SettingActivity.DEFAULT_AVERAGE_TO_TWO)){
            roundedAverage = 2;
        } else {
            roundedAverage = 1;
        }
        return roundedAverage;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getStringAssessments(Context context){
        String assessmentsString = "";
        if(assessments.size() > 0) {
            for (int i = 0; i < assessments.size(); i++) {
                assessmentsString += Float.toString(assessments.get(i)) + " ";
            }
        } else {
            assessmentsString = context.getResources().getString(R.string.null_string);
        }
        return assessmentsString;
    }

    public ArrayList<Float> getAssessments(){
        return assessments;
    }

    public int getUnpreparedness(){
        return unpreparedness;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<SubjectNote> getSubjectNotes() {
        return subjectNotes;
    }

    public void setName (String name) {
        this.name = name;
        contentValues.put("NAME", getName());
    }

    public void setTeacher(String teacher){
        this.teacher = teacher;
        contentValues.put("TEACHER", getTeacher());
    }

    public void setUnpreparedness(int unpreparedness){
        this.unpreparedness = unpreparedness;
        contentValues.put("UNPREPAREDNESS", getUnpreparedness());
    }

    public void removeUnpreparedness(){
        if (unpreparedness > 0) {
            unpreparedness--;
            contentValues.put("UNPREPAREDNESS", getUnpreparedness());
        }
    }

    public void setDescription(String description){
        this.description = description;
        contentValues.put("DESCRIPTION", getDescription());
    }

    private String toStringAssessments(){
        String string = "";
        for (int i = 0; i < assessments.size(); i++) {
            string += Float.toString(assessments.get(i)) + "®";
        }
        return string;
    }

    private void fromStringAssessments(String assessments){
        if(!assessments.equals("")) {
            String[] strings = assessments.split("®");
            for (int i = 0; i < strings.length; i++) {
                this.assessments.add(Float.parseFloat(strings[i]));
            }
        }
        contentValues.put("ASSESSMENTS", toStringAssessments());
    }

    public void addAssessment(String string, Context context){
        if (string.equals("")) Toast.makeText(context, R.string.hint_assessment, Toast.LENGTH_SHORT).show();
        else {
            getAssessments().add(Float.parseFloat(string));
            contentValues.put("ASSESSMENTS", toStringAssessments());
        }
    }

    public void removeAssessment(String string, Context context){
        if (string.equals("")){
            Toast.makeText(context, R.string.hint_assessment, Toast.LENGTH_SHORT).show();
            return;
        }
        Float assessment = Float.parseFloat(string);
        if (getAssessments().size() == 0) Toast.makeText(context, R.string.subject_null_assessments, Toast.LENGTH_SHORT).show();
        else if (!getAssessments().remove(assessment)) Toast.makeText(context, R.string.subject_null_this_assessment, Toast.LENGTH_SHORT).show();
        else contentValues.put("ASSESSMENTS", toStringAssessments());
    }

    private void fromStringSubjectNotes(String subjectNotes) { //old
        if (!subjectNotes.equals("")) {
            String[] strings = subjectNotes.split("®");
            for (int i = 0; i < strings.length; i += 10) {
                this.subjectNotes.add(new SubjectNote(strings[i], strings[i + 1], this.id));
            }
        }
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
