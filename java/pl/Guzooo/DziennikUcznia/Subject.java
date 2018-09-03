package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.ArrayList;

public class Subject {

    private String name;
    private float average;
    private String teacher;
    private ArrayList<Float> assessments = new ArrayList<>();
    private int unpreparedness;
    private String description;
    private ArrayList<SubjectNote> subjectNotes = new ArrayList<>();

    public Subject (String name, String teacher, ArrayList<Float> assessments, int unpreparedness, String description, ArrayList<SubjectNote> subjectNotes){
        setName(name);
        setTeacher(teacher);
        setAssessments(assessments);
        setAverage();
        setUnpreparedness(unpreparedness);
        setDescription(description);
        setSubjectNotes(subjectNotes);
    }

    public Subject (String object){
        String[] strings =  object.split("©");
        setName(strings[0]);
        setTeacher(strings[1]);
        fromStringAssessments(strings[2]);
        setAverage();
        setDescription(strings[3]);
        fromStringSubjectNotes(strings[4]);
        setUnpreparedness(Integer.parseInt(strings[15]));
    }

    @Override
    public String toString() {
        String string = getName() + "©" + getTeacher() + "©" + toStringAssessments() + "©" + getDescription() + "©" + toStringSubjectNotes() +  "©" + "©" + "©" + "©" + "©" + "©" + "©" + "©" + "©" + "©" + "©" + Integer.toString(getUnpreparedness());
        return string;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAverage(){
        average = 0;
        if(assessments.size() > 0) {
            for (int i = 0; i < assessments.size(); i++) {
                average += assessments.get(i);
            }
            average = average / assessments.size();
        }
    }

    public int getRoundedAverage(SharedPreferences sharedPreferences){
        int roundedAverage;
        if(average >= sharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_SIX, SettingActivity.defaulAverageToSix)){
            roundedAverage = 6;
        } else if(average >= sharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_FIVE, SettingActivity.defaulAverageToFive)){
            roundedAverage = 5;
        } else if(average >= sharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_FOUR, SettingActivity.defaulAverageToFour)){
            roundedAverage = 4;
        } else if(average >= sharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_THREE, SettingActivity.defaulAverageToThree)){
            roundedAverage = 3;
        } else if(average >= sharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_TWO, SettingActivity.defaulAverageToTwo)){
            roundedAverage = 2;
        } else {
            roundedAverage = 1;
        }
        return roundedAverage;
    }

    public float getAverage(){
        return average;
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

    public void addAssessment(float assessment){
        assessments.add(assessment);
    }

    public void removeAssessment(float assessment, Context context){
        if(assessments.size() == 0){
            Toast.makeText(context, R.string.subject_null_assessments, Toast.LENGTH_SHORT).show();
        } else if(!assessments.remove(assessment)){
            Toast.makeText(context, R.string.subject_null_this_assessment, Toast.LENGTH_SHORT).show();
        }
    }

    public void removeAllAssessments(){
        assessments.clear();
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

    public void setSubjectNotes(ArrayList<SubjectNote> subjectNotes) {
        this.subjectNotes = subjectNotes;
    }

    public void addSubjectNote(SubjectNote subjectNote){
        this.subjectNotes.add(subjectNote);
    }

    public void removeSubjectNote(SubjectNote subjectNote){
        this.subjectNotes.remove(subjectNote);
    }

    public ArrayList<SubjectNote> getSubjectNotes() {
        return subjectNotes;
    }

    public int sizeSubjectNotes(){
        return subjectNotes.size();
    }

    public String toStringSubjectNotes(){
        String string = "";
        for (int i = 0; i < subjectNotes.size(); i++) {
            string += subjectNotes.get(i).toString();
        }
        return string;
    }

    public void fromStringSubjectNotes(String subjectNotes){
        if(!subjectNotes.equals("")) {
            String[] strings = subjectNotes.split("®");
            for (int i = 0; i < strings.length; i += 10) {
                this.subjectNotes.add(new SubjectNote(strings[i], strings[i + 1]));
            }
        }
    } //TODO:ContentValues tutej
}
