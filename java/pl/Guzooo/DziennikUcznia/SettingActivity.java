package pl.Guzooo.DziennikUcznia;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends Activity {

    public static final String PREFERENCE_NAME_AVERAGE_TO = "averageto";
    public static final String PREFERENCE_AVERAGE_TO_ASSESSMENT = "averagetoassessment";
    public static final String PREFERENCE_AVERAGE_TO_SIX = "averagetosix";
    public static final String PREFERENCE_AVERAGE_TO_FIVE = "averagetofive";
    public static final String PREFERENCE_AVERAGE_TO_FOUR = "averagetofour";
    public static final String PREFERENCE_AVERAGE_TO_THREE = "averagetothree";
    public static final String PREFERENCE_AVERAGE_TO_TWO = "averagetotwo";
    public static final String PREFERENCE_AVERAGE_TO_BELT = "averagetobelt";

//    public static final String PREFERENCE_SUBJECT_VIEW_TODAY_TO = "subjectviewtodayto";

    public static final Boolean defaultAverageToAssessment = false;
    public static final Float defaultAverageToSix = 5.1f;
    public static final Float defaultAverageToFive = 4.5f;
    public static final Float defaultAverageToFour = 3.6f;
    public static final Float defaultAverageToThree = 2.7f;
    public static final Float defaultAverageToTwo = 1.75f;
    public static final Float defaultAverageToBelt = 4.75f;

//    public static final int defaultSubjectViewTodayTo = 840;

    private SharedPreferences sharedPreferences;

    private CheckBox checkBoxAverageToAssessment;
    private EditText editTextAverageToSix;
    private EditText editTextAverageToFive;
    private EditText editTextAverageToFour;
    private EditText editTextAverageToThree;
    private EditText editTextAverageToTwo;
    private EditText editTextAverageToBelt;

//    private TimePicker timePickerSubjectViewTodayTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        checkBoxAverageToAssessment = findViewById(R.id.setting_average_to_assessment);
        editTextAverageToSix = findViewById(R.id.setting_average_to_six);
        editTextAverageToFive = findViewById(R.id.setting_average_to_five);
        editTextAverageToFour = findViewById(R.id.setting_average_to_four);
        editTextAverageToThree = findViewById(R.id.setting_average_to_three);
        editTextAverageToTwo = findViewById(R.id.setting_average_to_two);
        editTextAverageToBelt = findViewById(R.id.setting_average_to_belt);
//        timePickerSubjectViewTodayTo = findViewById(R.id.setting_end_day);

        sharedPreferences = getSharedPreferences(PREFERENCE_NAME_AVERAGE_TO, MODE_PRIVATE);
        checkBoxAverageToAssessment.setChecked(sharedPreferences.getBoolean(PREFERENCE_AVERAGE_TO_ASSESSMENT, defaultAverageToAssessment));
        editTextAverageToSix.setText(Float.toString(sharedPreferences.getFloat(PREFERENCE_AVERAGE_TO_SIX, defaultAverageToSix)));
        editTextAverageToFive.setText(Float.toString(sharedPreferences.getFloat(PREFERENCE_AVERAGE_TO_FIVE, defaultAverageToFive)));
        editTextAverageToFour.setText(Float.toString(sharedPreferences.getFloat(PREFERENCE_AVERAGE_TO_FOUR, defaultAverageToFour)));
        editTextAverageToThree.setText(Float.toString(sharedPreferences.getFloat(PREFERENCE_AVERAGE_TO_THREE, defaultAverageToThree)));
        editTextAverageToTwo.setText(Float.toString(sharedPreferences.getFloat(PREFERENCE_AVERAGE_TO_TWO, defaultAverageToTwo)));
        editTextAverageToBelt.setText(Float.toString(sharedPreferences.getFloat(PREFERENCE_AVERAGE_TO_BELT, defaultAverageToBelt)));

//        timePickerSubjectViewTodayTo.setIs24HourView(true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            timePickerSubjectViewTodayTo.setHour(sharedPreferences.getInt(PREFERENCE_SUBJECT_VIEW_TODAY_TO, defaultSubjectViewTodayTo) / 60);
//            timePickerSubjectViewTodayTo.setMinute(sharedPreferences.getInt(PREFERENCE_SUBJECT_VIEW_TODAY_TO, defaultSubjectViewTodayTo) % 60);
//        } else {
//            timePickerSubjectViewTodayTo.setCurrentHour(sharedPreferences.getInt(PREFERENCE_SUBJECT_VIEW_TODAY_TO, defaultSubjectViewTodayTo) / 60);
//            timePickerSubjectViewTodayTo.setCurrentMinute(sharedPreferences.getInt(PREFERENCE_SUBJECT_VIEW_TODAY_TO, defaultSubjectViewTodayTo) % 60);
//        }

        ClickCheckAverageToAssessment(checkBoxAverageToAssessment);
    }

    public void ClickFacebook(View v){
        Uri uri = Uri.parse("https://www.facebook.com/GuzoooApps");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void ClickCheckAverageToAssessment(View v){
        if(checkBoxAverageToAssessment.isChecked()){
            findViewById(R.id.setting_average_to_assessment_box).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.setting_average_to_assessment_box).setVisibility(View.GONE);
        }
    }

    public void ClickDestroyAllSubjects(View v){
        try {
            SQLiteOpenHelper openHelper = new HelperDatabase(this);
            SQLiteDatabase db = openHelper.getWritableDatabase();
            db.delete("SUBJECTS", null, null);
            db.delete("NOTES", null, null);
            db.delete("LESSON_PLAN", null, null);
            db.close();
            Toast.makeText(this, R.string.setting_delete_all_subjects_made, Toast.LENGTH_SHORT).show();
        } catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(PREFERENCE_AVERAGE_TO_ASSESSMENT, checkBoxAverageToAssessment.isChecked());
        editor.putFloat(PREFERENCE_AVERAGE_TO_SIX, Float.parseFloat(editTextAverageToSix.getText().toString().trim()));
        editor.putFloat(PREFERENCE_AVERAGE_TO_FIVE, Float.parseFloat(editTextAverageToFive.getText().toString().trim()));
        editor.putFloat(PREFERENCE_AVERAGE_TO_FOUR, Float.parseFloat(editTextAverageToFour.getText().toString().trim()));
        editor.putFloat(PREFERENCE_AVERAGE_TO_THREE, Float.parseFloat(editTextAverageToThree.getText().toString().trim()));
        editor.putFloat(PREFERENCE_AVERAGE_TO_TWO, Float.parseFloat(editTextAverageToTwo.getText().toString().trim()));
        editor.putFloat(PREFERENCE_AVERAGE_TO_BELT, Float.parseFloat(editTextAverageToBelt.getText().toString().trim()));

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            editor.putInt(PREFERENCE_SUBJECT_VIEW_TODAY_TO, timePickerSubjectViewTodayTo.getHour() * 60 + timePickerSubjectViewTodayTo.getMinute());
//        } else {
//            editor.putInt(PREFERENCE_SUBJECT_VIEW_TODAY_TO, timePickerSubjectViewTodayTo.getCurrentHour() * 60 + timePickerSubjectViewTodayTo.getCurrentMinute());
//        }

        editor.apply();
    }
}
