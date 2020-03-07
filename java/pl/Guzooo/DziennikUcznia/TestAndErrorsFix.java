package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

public class TestAndErrorsFix {

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
                sum += UtilsAverage.getSubjectFinalAverage(cursor.getInt(0), context);
                count++;
            } while (cursor.moveToNext());
        }
        float average;
        if(count == 0)
            count = 1;
        int dodatkowe = 0;
        do {
            average = (sum + dodatkowe)/count;
            dodatkowe++;
        } while (isBelt(average, context));
        new AlertDialog.Builder(context)
                .setTitle("Testowe Beta")
                .setMessage("Potrzebujesz jeszcze " + dodatkowe-- + " ocen do poaska")
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
}
