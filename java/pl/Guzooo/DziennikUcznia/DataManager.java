package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.preference.PreferenceManager;

public class DataManager {

    private static final String DEFAULT_THEME(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            return "3";
        return "-1";
    }

    //Preferences from settings fragment
    public static int getTheme(Context context){
        String id = context.getString(R.string.ID_THEME);
        String theme = getPref(context).getString(id, DEFAULT_THEME());
        return Integer.valueOf(theme);
    }

    public static boolean isHardDarkTheme(Context context){
        String id = context.getString(R.string.ID_HARD_DARK);
        boolean def = context.getResources().getBoolean(R.bool.DEFAULT_HARD_DARK);
        return getPref(context).getBoolean(id, def);
    }

    public static boolean isAverageWeight(Context context){
        String id = context.getString(R.string.ID_AVERAGE_WEIGHT);
        boolean def = context.getResources().getBoolean(R.bool.DEFAULT_AVERAGE_WEIGHT);
        return getPref(context).getBoolean(id, def);
    }

    public static boolean isAverageToAssessment(Context context){
        String id = context.getString(R.string.ID_AVERAGE_TO_ASSESSMENT);
        boolean def = context.getResources().getBoolean(R.bool.DEFAULT_AVERAGE_TO_ASSESSMENT);
        return getPref(context).getBoolean(id, def);
    }

    public static float getAverageToSix(Context context){
        String id = context.getString(R.string.ID_AVERAGE_TO_SIX);
        String def = context.getString(R.string.DEFAULT_AVERAGE_TO_SIX);
        String result = getPref(context).getString(id, def);
        return Float.valueOf(result);
    }

    public static float getAverageToFive(Context context){
        String id = context.getString(R.string.ID_AVERAGE_TO_FIVE);
        String def = context.getString(R.string.DEFAULT_AVERAGE_TO_FIVE);
        String result = getPref(context).getString(id, def);
        return Float.valueOf(result);
    }

    public static float getAverageToFour(Context context){
        String id = context.getString(R.string.ID_AVERAGE_TO_FOUR);
        String def = context.getString(R.string.DEFAULT_AVERAGE_TO_FOUR);
        String result = getPref(context).getString(id, def);
        return Float.valueOf(result);
    }

    public static float getAverageToThree(Context context){
        String id = context.getString(R.string.ID_AVERAGE_TO_THREE);
        String def = context.getString(R.string.DEFAULT_AVERAGE_TO_THREE);
        String result = getPref(context).getString(id, def);
        return Float.valueOf(result);
    }

    public static float getAverageToTwo(Context context){
        String id = context.getString(R.string.ID_AVERAGE_TO_TWO);
        String def = context.getString(R.string.DEFAULT_AVERAGE_TO_TWO);
        String result = getPref(context).getString(id, def);
        return Float.valueOf(result);
    }

    public static float getAverageToBelt(Context context){
        String id = context.getString(R.string.ID_AVERAGE_TO_BELT);
        String def = context.getString(R.string.DEFAULT_AVERAGE_TO_BELT);
        String result = getPref(context).getString(id, def);
        return Float.valueOf(result);
    }

    public static int getSemester(Context context){
        String id = context.getString(R.string.ID_SEMESTER);
        String def = context.getString(R.string.DEFAULT_SEMESTER);
        String result = getPref(context).getString(id, def);
        return Integer.valueOf(result);
    }

    public static boolean isAssessmentWindow(Context context){
        String id = context.getString(R.string.ID_ASSESSMENT_WINDOW);
        boolean def = context.getResources().getBoolean(R.bool.DEFAULT_ASSESSMENT_WINDOW);
        return getPref(context).getBoolean(id, def);
    }

    public static void setAssessmentWindow(boolean autoShow, Context context){
        String id = context.getString(R.string.ID_ASSESSMENT_WINDOW);
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(id, autoShow);
        editor.apply();
    }

    public static boolean isHoldEditTextHelpIcon(Context context){
        String id = context.getString(R.string.ID_HOLD_EDIT_TEXT_HELP_ICON);
        boolean def = context.getResources().getBoolean(R.bool.DEFAULT_HOLD_EDIT_TEXT_HELP_ICON);
        return getPref(context).getBoolean(id, def);
    }

    //preferencje from other place

    public static String getNotepad(Context context){
        String id = context.getString(R.string.ID_NOTEPAD);
        String def = context.getString(R.string.DEFAULT_NOTEPAD);
        return getPref(context).getString(id, def);
    }

    //preferencje for compliance management

    public static int getInitialVersion(Context context){
        String id = context.getString(R.string.ID_INITIAL_VERSION);
        return getPref(context).getInt(id, 0);
    }

    public static void setInitialVersion(Context context){
        String id = context.getString(R.string.ID_INITIAL_VERSION);
        int currentVersion = BuildConfig.VERSION_CODE;
        SharedPreferences.Editor editor = getEditor(context);
        editor.putInt(id, currentVersion);
        editor.apply();
    }

    public static int getCurrentVersion(Context context){
        String id = context.getString(R.string.ID_CURRENT_VERSION);
        return getPref(context).getInt(id, 0);
    }

    public static void setCurrentVersion(Context context){
        String id = context.getString(R.string.ID_CURRENT_VERSION);
        int currentVersion = BuildConfig.VERSION_CODE;
        SharedPreferences.Editor editor = getEditor(context);
        editor.putInt(id, currentVersion);
        editor.apply();
    }

    private static SharedPreferences.Editor getEditor(Context context){
        return getPref(context).edit();
    }

    private static SharedPreferences getPref(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
