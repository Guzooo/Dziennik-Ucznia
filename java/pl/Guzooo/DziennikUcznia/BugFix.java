package pl.Guzooo.DziennikUcznia;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class BugFix {

    private static Context context;
    private static int version;

    public static void startFixingBugs(Activity activity){
        initial(activity.getApplicationContext());
        if(!isNeedFix())
            return;
        if(version == 0)
            moveSavedData(activity);
        finalization();
    }

    private static void initial(Context context){
        BugFix.context = context;
        version = DataManager.getCurrentVersion(context);
        setInitialVersion();
    }

    private static void finalization(){
        setCurrentVersion();
    }

    private static boolean isNeedFix(){
        if(DataManager.getCurrentVersion(context) == BuildConfig.VERSION_CODE)
            return false;
        /*if(DataManager.getCurrentVersion(context) == 0)
            return false;*/
        //TODO: odkreskować Po usnięciu metody "moveSavedData()"
        return true;
    }

    private static void setInitialVersion(){
        if(DataManager.getInitialVersion(context) == 0)
            DataManager.setInitialVersion(context);
    }

    private static void setCurrentVersion(){
        if(DataManager.getCurrentVersion(context) == 0)
            DataManager.setCurrentVersion(context);
    }

    //Przenoszenie zapisanych ustawień i preferencji do nowego fragmentu ustawień
    //Dodany z 12 na 13 wersje
    public static final String PREFERENCE_STAT_NAME = "statistics";
    public static final String PREFERENCE_STAT_SEMESTER = "semester";

    public static final String PREFERENCE_SET_NAME = "averageto";
    public static final String PREFERENCE_SET_AVERAGE_WEIGHT = "averageweight";
    public static final String PREFERENCE_SET_AVERAGE_TO_ASSESSMENT = "averagetoassessment";
    public static final String PREFERENCE_SET_AVERAGE_TO_SIX = "averagetosix";
    public static final String PREFERENCE_SET_AVERAGE_TO_FIVE = "averagetofive";
    public static final String PREFERENCE_SET_AVERAGE_TO_FOUR = "averagetofour";
    public static final String PREFERENCE_SET_AVERAGE_TO_THREE = "averagetothree";
    public static final String PREFERENCE_SET_AVERAGE_TO_TWO = "averagetotwo";
    public static final String PREFERENCE_SET_AVERAGE_TO_BELT = "averagetobelt";

    private static final String PREFERENCE_ASSESS_WIN_NAME = "assessmentoptions";
    private static final String PREFERENCE_ASSESS_WIN_AUTO_SHOW = "autoshow";

    private static final String PREFERENCE_MAIN_NOTEPAD = "notepad";

    private static void moveSavedData(Activity activity){
        moveSemester();
        moveBelt();
        moveWeight();
        moveAssessmentAverage();
        moveSix();
        moveFive();
        moveFour();
        moveThree();
        moveTwo();
        moveAssessmentWindow();
        moveNotepad(activity);
    }

    private static void moveSemester(){
        String id = context.getString(R.string.ID_SEMESTER);
        String def = context.getString(R.string.DEFAULT_SEMESTER);
        int defInt = Integer.parseInt(def);
        int old = context.getSharedPreferences(PREFERENCE_STAT_NAME, Context.MODE_PRIVATE).getInt(PREFERENCE_STAT_SEMESTER, defInt);
        putString(id, old+"");
    }

    private static void moveBelt(){
        String id = context.getString(R.string.ID_AVERAGE_TO_BELT);
        String def = context.getString(R.string.DEFAULT_AVERAGE_TO_BELT);
        float defFloat = Float.parseFloat(def);
        float old = getFloatFromSet(PREFERENCE_SET_AVERAGE_TO_BELT, defFloat);
        putString(id, old+"");
    }

    private static void moveWeight(){
        String id = context.getString(R.string.ID_AVERAGE_WEIGHT);
        boolean def = context.getResources().getBoolean(R.bool.DEFAULT_AVERAGE_WEIGHT);
        boolean old = context.getSharedPreferences(PREFERENCE_SET_NAME, Context.MODE_PRIVATE).getBoolean(PREFERENCE_SET_AVERAGE_WEIGHT, def);
        putBool(id, old);
    }

    private static void moveAssessmentAverage(){
        String id = context.getString(R.string.ID_AVERAGE_TO_ASSESSMENT);
        boolean def = context.getResources().getBoolean(R.bool.DEFAULT_AVERAGE_TO_ASSESSMENT);
        boolean old = context.getSharedPreferences(PREFERENCE_SET_NAME, Context.MODE_PRIVATE).getBoolean(PREFERENCE_SET_AVERAGE_TO_ASSESSMENT, def);
        putBool(id, old);
    }

    private static void moveSix(){
        String id = context.getString(R.string.ID_AVERAGE_TO_SIX);
        String def = context.getString(R.string.DEFAULT_AVERAGE_TO_SIX);
        float defFloat = Float.parseFloat(def);
        float old = getFloatFromSet(PREFERENCE_SET_AVERAGE_TO_SIX, defFloat);
        putString(id, old+"");
    }

    private static void moveFive(){
        String id = context.getString(R.string.ID_AVERAGE_TO_FIVE);
        String def = context.getString(R.string.DEFAULT_AVERAGE_TO_FIVE);
        float defFloat = Float.parseFloat(def);
        float old = getFloatFromSet(PREFERENCE_SET_AVERAGE_TO_FIVE, defFloat);
        putString(id, old+"");
    }

    private static void moveFour(){
        String id = context.getString(R.string.ID_AVERAGE_TO_FOUR);
        String def = context.getString(R.string.DEFAULT_AVERAGE_TO_FOUR);
        float defFloat = Float.parseFloat(def);
        float old = getFloatFromSet(PREFERENCE_SET_AVERAGE_TO_FOUR, defFloat);
        putString(id, old+"");
    }

    private static void moveThree(){
        String id = context.getString(R.string.ID_AVERAGE_TO_THREE);
        String def = context.getString(R.string.DEFAULT_AVERAGE_TO_THREE);
        float defFloat = Float.parseFloat(def);
        float old = getFloatFromSet(PREFERENCE_SET_AVERAGE_TO_THREE, defFloat);
        putString(id, old+"");
    }

    private static void moveTwo(){
        String id = context.getString(R.string.ID_AVERAGE_TO_TWO);
        String def = context.getString(R.string.DEFAULT_AVERAGE_TO_TWO);
        float defFloat = Float.parseFloat(def);
        float old = getFloatFromSet(PREFERENCE_SET_AVERAGE_TO_TWO, defFloat);
        putString(id, old+"");
    }

    private static void moveAssessmentWindow(){
        String id = context.getString(R.string.ID_ASSESSMENT_WINDOW);
        boolean def = context.getResources().getBoolean(R.bool.DEFAULT_ASSESSMENT_WINDOW);
        boolean old = context.getSharedPreferences(PREFERENCE_ASSESS_WIN_NAME, Context.MODE_PRIVATE).getBoolean(PREFERENCE_ASSESS_WIN_AUTO_SHOW, def);
        putBool(id, old);
    }

    private static void moveNotepad(Activity activity){
        String id = context.getString(R.string.ID_NOTEPAD);
        String def = context.getString(R.string.DEFAULT_NOTEPAD);
        String old = activity.getPreferences(Context.MODE_PRIVATE).getString(PREFERENCE_MAIN_NOTEPAD, def);
        putString(id, old);
    }

    private static float getFloatFromSet(String id, float def){
        return context.getSharedPreferences(PREFERENCE_SET_NAME, Context.MODE_PRIVATE).getFloat(id, def);
    }

    private static void putString(String id, String value){
        SharedPreferences.Editor editor = getEditor();
        editor.putString(id, value);
        editor.apply();
    }

    private static void putBool(String id, boolean value){
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean(id, value);
        editor.apply();
    }

    private static SharedPreferences.Editor getEditor(){
        return getPref().edit();
    }

    private static SharedPreferences getPref(){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    //
}
