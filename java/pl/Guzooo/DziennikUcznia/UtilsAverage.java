package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class UtilsAverage {
    private static Context context;
    private static SQLiteDatabase db;

    public static float getFinalAverage(Context context){
        initialization(context);
        Cursor cursor = getIdOfSubjectContainingAssessment();
        float average = 0;
        int numberOfSubjects = 0;
        if(cursor.moveToFirst())
            do{
                int idSubject = cursor.getInt(0);
                float subjectAverage = getSubjectFinalAverage(idSubject);
                subjectAverage = roundAverage(subjectAverage);
                average += subjectAverage;
                numberOfSubjects++;
            }while (cursor.moveToNext());
        if(numberOfSubjects != 0)
            average /= numberOfSubjects;
        cursor.close();
        finalization();
        return average;
    }

    public static float getSemesterAverage(int semester, Context context){
        initialization(context);
        Cursor cursor = getIdOfSubjectContainingAssessment();
        float average = 0;
        int numberOfSubjects = 0;
        if(cursor.moveToFirst())
            do{
                int idSubject = cursor.getInt(0);
                float subjectAverage = getSubjectSemesterAverage(idSubject, semester);
                subjectAverage = roundAverage(subjectAverage);
                average += subjectAverage;
                numberOfSubjects++;
            }while(cursor.moveToNext());
        if(numberOfSubjects != 0)
            average /= numberOfSubjects;
        cursor.close();
        finalization();
        return average;
    }

    public static float getSubjectFinalAverage(int idSubject, Context context){
        initialization(context);
        float result = getSubjectFinalAverage(idSubject);
        finalization();
        return result;
    }

    public static float getSubjectFinalAverage(ArrayList<Assessment2020> assessments1, ArrayList<Assessment2020> assessments2, Context context){
        initialization(context);
        float result = getSubjectFinalAverage(assessments1, assessments2);
        finalization();
        return result;
    }

    public static float getSubjectSemesterAverage(int idSubject, int semester, Context context){
        initialization(context);
        float result = getSubjectSemesterAverage(idSubject, semester);
        finalization();
        return result;
    }

    public static ArrayList<Assessment2020> getAssemssents(int idSubject, int semester, Context context){
        initialization(context);
        ArrayList<Assessment2020> assessments = getAssessments(idSubject, semester);
        finalization();
        return assessments;
    }

    public static float getAverageFromAssessments(ArrayList<Assessment2020> assessments, Context context){
        initialization(context);
        float result = getAverageFromAssessments(assessments);
        finalization();
        return result;
    }

    public static float roundAverage(float average, Context context){
        initialization(context);
        float result = roundAverage(average);
        finalization();
        return result;
    }

    public static boolean isBelt(float average, Context context){
        if(average >= DataManager.getAverageToBelt(context))
            return true;
        return false;
    }

    private static void initialization(Context context){
        if(context != null){
            UtilsAverage.context = context;
            db = Database2020.getToWriting(context);
        }
    }

    private static void finalization(){
        db.close();
    }

    private static Cursor getIdOfSubjectContainingAssessment(){
        return db.rawQuery("SELECT " + Database2020.ID
                        + " FROM " + Subject2020.DATABASE_NAME
                        + " WHERE " + Database2020.ID + " IN ("
                            + "SELECT DISTINCT " + Assessment2020.TAB_SUBJECT
                            + " FROM " + Assessment2020.DATABASE_NAME
                        + " )"
                        , null);
    }

    private static float getSubjectFinalAverage(int idSubject){
        float averageFromSemesterI = getSubjectSemesterAverage(idSubject, 1);
        float averageFromSemesterII = getSubjectSemesterAverage(idSubject, 2);
        return getAverageFromTwo(averageFromSemesterI, averageFromSemesterII);
    }

    private static float getSubjectFinalAverage(ArrayList<Assessment2020> assessments1, ArrayList<Assessment2020> assessments2){
        float averageFromAssessmentsI = getAverageFromAssessments(assessments1);
        float averageFromAssessmentsII = getAverageFromAssessments(assessments2);
        return getAverageFromTwo(averageFromAssessmentsI, averageFromAssessmentsII);
    }

    private static float getSubjectSemesterAverage(int idSubject, int semester){
        ArrayList<Assessment2020> assessments = getAssessments(idSubject, semester);
        return getAverageFromAssessments(assessments);
    }

    private static ArrayList<Assessment2020> getAssessments(int idSubject, int semester){
        ArrayList<Assessment2020> assessments = new ArrayList<>();
        Cursor cursor = UtilsAverage.getAssessmentsCursor(idSubject, semester);
        if(cursor.moveToFirst())
            do{
                Assessment2020 assessment = new Assessment2020();
                assessment.setVariablesOfCursor(cursor);
                assessments.add(assessment);
            }while (cursor.moveToNext());
        cursor.close();
        return assessments;
    }

    private static Cursor getAssessmentsCursor(int idSubject, int semester){
        return db.query(Assessment2020.DATABASE_NAME,
                Assessment2020.ON_CURSOR,
                Assessment2020.TAB_SUBJECT + " = ? AND " + Assessment2020.SEMESTER + " = ?",
                new String[]{Integer.toString(idSubject), Integer.toString(semester)},
                null, null, null);
    }

    private static float getAverageFromTwo(float averageI, float averageII){
        float average = averageI + averageII;
        if(averageI != 0 && averageII != 0)
            average /= 2;
        return average;
    }

    private static float getAverageFromAssessments(ArrayList<Assessment2020> assessments){
        float average = 0;
        int numberOfAssessments = 0;
        for(Assessment2020 assessment : assessments) {
            int times = getWeight(assessment);
            for (int i = 0; i < times; i++) {
                average += assessment.getAssessment();
                numberOfAssessments++;
            }
        }
        if(numberOfAssessments != 0)
            average /= numberOfAssessments;
        return average;
    }

    private static int getWeight(Assessment2020 assessment){
        if(DataManager.getAverageWeight(context))
            return assessment.getWeight();
        return 1;
    }

    private static float roundAverage(float average) {
        if(!DataManager.getAverageToAssessment(context))
            return average;
        if (average == 0)
            return 0;
        if (average >= DataManager.getAverageToSix(context))
            return 6;
        if (average >= DataManager.getAverageToFive(context))
            return 5;
        if (average >= DataManager.getAverageToFour(context))
            return 4;
        if (average >= DataManager.getAverageToThree(context))
            return 3;
        if (average >= DataManager.getAverageToTwo(context))
            return 2;
        return 1;
    }
}