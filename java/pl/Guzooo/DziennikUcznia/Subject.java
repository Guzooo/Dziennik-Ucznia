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
    private ArrayList<ArrayList<Float>> assessments =  new ArrayList<> (); // z 6 na 7
    private int unpreparedness;
    private int unpreparedness1;
    private int unpreparedness2;
    private String description;
    private ContentValues contentValues = new ContentValues();

    public static final String[] subjectOnCursor = {"_id", "NAME", "TEACHER", "ASSESSMENTS", "UNPREPAREDNESS", "DESCRIPTION", "ASSESSMENTS2", "UNPREPAREDNESS1", "UNPREPAREDNESS2"};      //jak z 6 uciekną pozbyć się łocen

    private Subject (int id, String name, String teacher, String assessments, int unpreparedness, String description, String assessments2, int unpreparedness1, int unpreparedness2){
        this.id = id;
        setName(name);
        setTeacher(teacher);
        this.assessments.add(new ArrayList<Float>());
        this.assessments.add(new ArrayList<Float>());
        fromStringAssessments(0, assessments);
        fromStringAssessments(1, assessments2);
        contentValues.put("ASSESSMENTS", "");
        contentValues.put("ASSESSMENTS2", "");
        setUnpreparedness(unpreparedness);
        setDescription(description);
        setUnpreparedness1(unpreparedness1);
        setUnpreparedness2(unpreparedness2);
    }

    public static Subject newEmpty (){
        Subject subject = new Subject (0, "", "", "", 0, "", "", -1, -1);
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
                cursor.getString(5),
                cursor.getString(6),
                cursor.getInt(7),
                cursor.getInt(8));
    }

    public static Subject getOfId (int id, Context context){
        Subject subject;
        SQLiteDatabase db = DatabaseUtils.getReadableDatabase(context);
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
        newSubject.setUnpreparedness1(subject.unpreparedness1);
        newSubject.setUnpreparedness2(subject.unpreparedness2);
        newSubject.setDescription(subject.getDescription());
        return newSubject;
    }

    public void insert(Context context){
        try {
            SQLiteDatabase db = DatabaseUtils.getWritableDatabase(context);
            db.insert("SUBJECTS", null, contentValues);
            contentValues.clear();
            db.close();
        } catch (SQLiteException e){
            Toast.makeText(context, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    public void update(Context context){
        try {
            SQLiteDatabase db = DatabaseUtils.getWritableDatabase(context);
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
            SQLiteDatabase db = DatabaseUtils.getWritableDatabase(context);
            db.delete("SUBJECTS",
                    "_id = ?",
                    new String[]{Integer.toString(getId())});
            db.close();
        } catch (SQLiteException e){
            Toast.makeText(context, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
        DatabaseUtils.destroyAllLessonPlan("TAB_SUBJECT = ?", new String[]{Integer.toString(getId())}, context);
        DatabaseUtils.destroyAllNotes("TAB_SUBJECT = ?", new String[]{Integer.toString(getId())}, context);
        DatabaseUtils.destroyAllAssessment("TAB_SUBJECT = ?", new String[]{Integer.toString(getId())}, context);
    }

    public boolean duplicate(Context context){
        try {
            SQLiteDatabase db = DatabaseUtils.getWritableDatabase(context);
            db.insert("SUBJECTS", null, getOfSubject(this).contentValues);
            return true;
        } catch (SQLiteException e){
            Toast.makeText(context, R.string.error_database, Toast.LENGTH_SHORT).show();
            return false;
        }
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


    public int getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    private float average(ArrayList<SubjectAssessment> assessments){
        float average = 0;
        if (assessments.size() > 0){
            for (int i = 0; i < assessments.size(); i++){
                average += assessments.get(i).getAssessment();
            }

            average = average / assessments.size();
        }
        return average;
    }

    public float getAverage(ArrayList<SubjectAssessment> assessments){
        return average(assessments);
    }

    public float getAverageEnd(ArrayList<SubjectAssessment> assessments1, ArrayList<SubjectAssessment> assessments2){
        float average1 = average(assessments1);
        float average2 = average(assessments2);

        if(average1 == 0)
            return average2;

        if(average2 == 0)
            return average1;

        return (average1 + average2)/2;
    }

    private int roundAverage(float average, SharedPreferences sharedPreferences){
        if(average == 0){
            return 0;
        }
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

    public int getRoundedAverage(ArrayList<SubjectAssessment> assessments, SharedPreferences sharedPreferences){
        return roundAverage(average(assessments), sharedPreferences);
    }

    public int getRoundedAverageEnd(ArrayList<SubjectAssessment> assessments1, ArrayList<SubjectAssessment> assessments2, SharedPreferences sharedPreferences){
        float averageEnd = getAverageEnd(assessments1, assessments2);
        return roundAverage(averageEnd, sharedPreferences);
    }

    public ArrayList<SubjectAssessment> getAssessment(int semester, Context context){
        ArrayList<SubjectAssessment> assessments = new ArrayList<>();
        SQLiteDatabase db = DatabaseUtils.getWritableDatabase(context);
        Cursor cursor = db.query("ASSESSMENTS",
                SubjectAssessment.subjectAssessmentOnCursor,
                "TAB_SUBJECT = ? AND SEMESTER = ?",
                new String[]{Integer.toString(getId()), Integer.toString(semester)},
                null ,null, null);
        if(cursor.moveToFirst()){
            do {
                assessments.add(SubjectAssessment.getOfCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return assessments;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getStringAssessments(ArrayList<SubjectAssessment> assessments, Context context){
        String assessmentsString = "";
        if(assessments.size() > 0) {
            for (int i = 0; i < assessments.size(); i++) {
                assessmentsString += Float.toString(assessments.get(i).getAssessment()) + " ";
            }
        } else {
            assessmentsString = context.getResources().getString(R.string.null_string);
        }
        return assessmentsString;
    }

    public ArrayList<Float> getAssessment(int i){ //stare
        return assessments.get(i);
    }

    public int getUnpreparedness(){
        return unpreparedness;
    }

    public int getCurrentUnpreparedness(Context context){
        int i;
        if(StatisticsActivity.getSemester(context) == 1){
            i = unpreparedness1;
        } else {
            i = unpreparedness2;
        }
        if(i == -1)
            return getUnpreparedness();
        return i;
    }

    public String getDescription() {
        return description;
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

    public void setCurrentUnpreparedness(int unpreparedness, Context context){
        if(StatisticsActivity.getSemester(context) == 1){
            setUnpreparedness1(unpreparedness);
        } else {
            setUnpreparedness2(unpreparedness);
        }
    }

    private void setUnpreparedness1(int unpreparedness1) {
        this.unpreparedness1 = unpreparedness1;
        contentValues.put("UNPREPAREDNESS1", unpreparedness1);
    }

    private void setUnpreparedness2(int unpreparedness2) {
        this.unpreparedness2 = unpreparedness2;
        contentValues.put("UNPREPAREDNESS2", unpreparedness2);
    }

    public void setDescription(String description){
        this.description = description;
        contentValues.put("DESCRIPTION", getDescription());
    }

    private void fromStringAssessments(int num, String assessments){ //old
        if(!assessments.equals("")) {
            String[] strings = assessments.split("®");
            for (int i = 0; i < strings.length; i++) {
                this.assessments.get(num).add(Float.parseFloat(strings[i]));
            }
        }
    }

    public SubjectAssessment addAssessment(String string, Context context){
        if (string.equals(""))
            Toast.makeText(context, R.string.hint_assessment, Toast.LENGTH_SHORT).show();
        else if (AssessmentOptionsFragment.getPreferenceAutoShow(context)){
            return getNewAssessment(string, context);
        } else {
            getNewAssessment(string, context).insert(context);
        }
        return null;
    }

    private SubjectAssessment getNewAssessment(String string, Context context){
        SubjectAssessment assessment = SubjectAssessment.newEmpty();
        assessment.setAssessment(Float.parseFloat(string));
        assessment.setSubjectId(getId());
        assessment.setSemester(StatisticsActivity.getSemester(context));
        return assessment;
    }

    public void removeAssessment(String string, Context context){
        if (string.equals("")){
            Toast.makeText(context, R.string.hint_assessment, Toast.LENGTH_SHORT).show();
            return;
        }
        Float assessment = Float.parseFloat(string);

        ArrayList<SubjectAssessment> assessments = getAssessment(StatisticsActivity.getSemester(context),context);


        if(assessments.size() == 0)
            Toast.makeText(context, R.string.subject_null_assessments, Toast.LENGTH_SHORT).show();
        else {
            for(int i = assessments.size()-1; i > -1; i--){
                if(assessments.get(i).getAssessment() == assessment){
                    assessments.get(i).delete(context);
                    return;
                }
            }
            Toast.makeText(context, R.string.subject_null_this_assessment, Toast.LENGTH_SHORT).show();
        }
    }

    public void removeAllAssessments(Context context){
        DatabaseUtils.destroyAllAssessment("TAB_SUBJECT = ? AND SEMESTER =?", new String[]{Integer.toString(getId()), Integer.toString(StatisticsActivity.getSemester(context))}, context);
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
