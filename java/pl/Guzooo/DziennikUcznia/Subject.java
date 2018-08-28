package pl.Guzooo.DziennikUcznia;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class Subject {

    private String name;
    private float average;
    private String teacher;
    private ArrayList<Float> assessments = new ArrayList<>();
    private int unpreparedness;
    private String description;

    public Subject (String name, String teacher, ArrayList<Float> assessments, int unpreparedness, String description){
        setName(name);
        setTeacher(teacher);
        setAssessments(assessments);
        setAverage();
        setUnpreparedness(unpreparedness);
        setDescription(description);
    }

    public Subject (String object){
        String[] strings =  object.split("©");
        this.name = strings[0];
        this.teacher = strings[1];
        fromStringAssessments(strings[2]);
        setAverage();
        this.description = strings[3];
        this.unpreparedness = Integer.parseInt(strings[4]);
    }

    @Override
    public String toString() {
        String string = name + "©" + teacher + "©" + toStringAssessments() + "©" + description + "©" + Integer.toString(unpreparedness);
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
            Toast.makeText(context, "Nie ma ocen do usunięcia", Toast.LENGTH_SHORT).show();
        } else if(!assessments.remove(assessment)){
            Toast.makeText(context, "Nie znaleźiono takiej oceny do usunięcia", Toast.LENGTH_SHORT).show(); //TODO: stringi
        }
    }

    public void removeAllAssessments(){
        assessments.clear();
    }

    public String getStringAssessments(){
        String assessmentsString = "Brak"; //TODO: String
        if(assessments.size() > 0) {
            assessmentsString = "";
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
}
