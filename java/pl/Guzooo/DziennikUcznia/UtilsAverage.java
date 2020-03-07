package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    public static float getSubjectFinalAverage(int idSubject, Context context){
        initialization(context);
        float result = getSubjectFinalAverage(idSubject);
        finalization();
        return result;
    }

    public static float getSubjectSemesterAverage(int idSubject, int semester, Context context){
        initialization(context);
        float result = getSubjectSemesterAverage(idSubject, semester);
        finalization();
        return result;
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
        float average = averageFromSemesterI + averageFromSemesterII;
        if(averageFromSemesterI != 0 && averageFromSemesterII != 0)
            average /= 2;
        return average;
    }

    private static float getSubjectSemesterAverage(int idSubject, int semester){
        Cursor cursor = getAssessments(idSubject, semester);
        float average = 0;
        int numberOfAssessments = 0;
        if(cursor.moveToFirst())
            do{
                Assessment2020 assessment = new Assessment2020();
                assessment.setVariablesOfCursor(cursor);
                int times = getWeight(assessment.getWeight());
                for(int i = 0; i < times; i++){
                    average += assessment.getAssessment();
                    numberOfAssessments++;
                }
            }while (cursor.moveToNext());
        if(numberOfAssessments != 0)
            average /= numberOfAssessments;
        cursor.close();
        return average;
    }

    private static Cursor getAssessments(int idSubject, int semester){
        return db.query(Assessment2020.DATABASE_NAME,
                Assessment2020.ON_CURSOR,
                Assessment2020.TAB_SUBJECT + " = ? AND " + Assessment2020.SEMESTER + " = ?",
                new String[]{Integer.toString(idSubject), Integer.toString(semester)},
                null, null, null);
    }

    private static int getWeight(int assessmentWeight){
        if(MainSettingsFragment.getAverageWeight(context))
            return assessmentWeight;
        return 1;
    }

    private static float roundAverage(float average) {
        if(!MainSettingsFragment.getAverageToAssessment(context))
            return average;
        if (average == 0)
            return 0;
        if (average >= MainSettingsFragment.getAverageToSix(context))
            return 6;
        if (average >= MainSettingsFragment.getAverageToFive(context))
            return 5;
        if (average >= MainSettingsFragment.getAverageToFour(context))
            return 4;
        if (average >= MainSettingsFragment.getAverageToThree(context))
            return 3;
        if (average >= MainSettingsFragment.getAverageToTwo(context))
            return 2;
        return 1;
    }
}