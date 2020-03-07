package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

public class TestAndErrorsFix {

    //Prodżekt "Road To Belt";
    public static View.OnLongClickListener assessmentToBeltLongListener(final Context context){
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                assessmentToBelt(context);
                return true;
            }
        };
    }

    public static void assessmentToBelt(Context context){
        Cursor cursor = getIdOfSubjectContainingAssessment(context);
        int sum = 0;
        int count = 0;
        if(cursor.moveToFirst()) {
            do {
                sum += roundAverage(UtilsAverage.getSubjectFinalAverage(cursor.getInt(0), context), context);
                count++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        float average;
        if(count == 0)
            count = 1;
        int dodatkowe = -1;
        do {
            dodatkowe++;
            average = (float) (sum + dodatkowe)/count;
        } while (!isBelt(average, context));
        new AlertDialog.Builder(context)
                .setTitle("Testowe Beta")
                .setMessage("Potrzebujesz jeszcze " + dodatkowe + " ocen do paska")
                .show();
    }

    private static Cursor getIdOfSubjectContainingAssessment(Context context){
        return Database2020.getToReading(context).rawQuery("SELECT " + Database2020.ID
                        + " FROM " + Subject2020.DATABASE_NAME
                        + " WHERE " + Database2020.ID + " IN ("
                        + "SELECT DISTINCT " + Assessment2020.TAB_SUBJECT
                        + " FROM " + Assessment2020.DATABASE_NAME
                        + " )"
                , null);
    }

    private static boolean isBelt(float average, Context context){
        if(average >= MainSettingsFragment.getAverageToBelt(context))
            return true;
        return false;
    }

    private static float roundAverage(float average, Context context) {
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
