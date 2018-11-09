package pl.Guzooo.DziennikUcznia;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends Activity {

    public static final String PREFERENCE_NAME = "averageto";
    public static final String PREFERENCE_AVERAGE_TO_ASSESSMENT = "averagetoassessment";
    public static final String PREFERENCE_AVERAGE_TO_SIX = "averagetosix";
    public static final String PREFERENCE_AVERAGE_TO_FIVE = "averagetofive";
    public static final String PREFERENCE_AVERAGE_TO_FOUR = "averagetofour";
    public static final String PREFERENCE_AVERAGE_TO_THREE = "averagetothree";
    public static final String PREFERENCE_AVERAGE_TO_TWO = "averagetotwo";
    public static final String PREFERENCE_AVERAGE_TO_BELT = "averagetobelt";

    public static final Boolean DEFAULT_AVERAGE_TO_ASSESSMENT = false;
    public static final Float DEFAULT_AVERAGE_TO_SIX = 5.1f;
    public static final Float DEFAULT_AVERAGE_TO_FIVE = 4.5f;
    public static final Float DEFAULT_AVERAGE_TO_FOUR = 3.6f;
    public static final Float DEFAULT_AVERAGE_TO_THREE = 2.7f;
    public static final Float DEFAULT_AVERAGE_TO_TWO = 1.75f;
    public static final Float DEFAULT_AVERAGE_TO_BELT = 4.75f;

    private SharedPreferences sharedPreferences;

    private CheckBox checkBoxAverageToAssessment;
    private EditText editTextAverageToSix;
    private EditText editTextAverageToFive;
    private EditText editTextAverageToFour;
    private EditText editTextAverageToThree;
    private EditText editTextAverageToTwo;
    private EditText editTextAverageToBelt;

    private View viewAverageToAssessmentBox;

    private final int positionAverageToAssessmentBox = 4;
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

        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        checkBoxAverageToAssessment.setChecked(sharedPreferences.getBoolean(PREFERENCE_AVERAGE_TO_ASSESSMENT, DEFAULT_AVERAGE_TO_ASSESSMENT));
        editTextAverageToSix.setText(Float.toString(sharedPreferences.getFloat(PREFERENCE_AVERAGE_TO_SIX, DEFAULT_AVERAGE_TO_SIX)));
        editTextAverageToFive.setText(Float.toString(sharedPreferences.getFloat(PREFERENCE_AVERAGE_TO_FIVE, DEFAULT_AVERAGE_TO_FIVE)));
        editTextAverageToFour.setText(Float.toString(sharedPreferences.getFloat(PREFERENCE_AVERAGE_TO_FOUR, DEFAULT_AVERAGE_TO_FOUR)));
        editTextAverageToThree.setText(Float.toString(sharedPreferences.getFloat(PREFERENCE_AVERAGE_TO_THREE, DEFAULT_AVERAGE_TO_THREE)));
        editTextAverageToTwo.setText(Float.toString(sharedPreferences.getFloat(PREFERENCE_AVERAGE_TO_TWO, DEFAULT_AVERAGE_TO_TWO)));
        editTextAverageToBelt.setText(Float.toString(sharedPreferences.getFloat(PREFERENCE_AVERAGE_TO_BELT, DEFAULT_AVERAGE_TO_BELT)));

        viewAverageToAssessmentBox = findViewById(R.id.setting_average_to_assessment_box);

        ClickCheckAverageToAssessment(checkBoxAverageToAssessment);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(PREFERENCE_AVERAGE_TO_ASSESSMENT, checkBoxAverageToAssessment.isChecked());
        editor.putFloat(PREFERENCE_AVERAGE_TO_SIX, getFloatFromText(editTextAverageToSix, DEFAULT_AVERAGE_TO_SIX));
        editor.putFloat(PREFERENCE_AVERAGE_TO_FIVE, getFloatFromText(editTextAverageToFive, DEFAULT_AVERAGE_TO_FIVE));
        editor.putFloat(PREFERENCE_AVERAGE_TO_FOUR, getFloatFromText(editTextAverageToFour, DEFAULT_AVERAGE_TO_FOUR));
        editor.putFloat(PREFERENCE_AVERAGE_TO_THREE, getFloatFromText(editTextAverageToThree, DEFAULT_AVERAGE_TO_THREE));
        editor.putFloat(PREFERENCE_AVERAGE_TO_TWO, getFloatFromText(editTextAverageToTwo, DEFAULT_AVERAGE_TO_TWO));
        editor.putFloat(PREFERENCE_AVERAGE_TO_BELT, getFloatFromText(editTextAverageToBelt, DEFAULT_AVERAGE_TO_BELT));

        editor.apply();
    }

    private Intent getIntentPage(String url){
        Uri uri = Uri.parse(url);
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    private Float getFloatFromText(EditText editText, Float defaultF){
        String string = editText.getText().toString().trim();
        if (string.equals("")) return defaultF;
        return Float.parseFloat(string);
    }

    public void ClickFacebook(View v){
        startActivity(getIntentPage("https://www.facebook.com/GuzoooApps"));
    }

    public void ClickMessenger(View v){
        startActivity(getIntentPage("https://www.messenger.com/t/GuzoooApps"));
    }

    public void ClickCheckAverageToAssessment(View v){
        ViewGroup viewGroup = findViewById(R.id.setting_home_layout);
        if(checkBoxAverageToAssessment.isChecked() && viewGroup.findViewById(viewAverageToAssessmentBox.getId()) == null){
            viewGroup.addView(viewAverageToAssessmentBox, positionAverageToAssessmentBox);
        } else if(!checkBoxAverageToAssessment.isChecked()) {
            viewGroup.removeView(viewAverageToAssessmentBox);
        }
    }

    public void ClickDestroyAllSubjects(View v){
        StaticMethod.getAlert(this)
                .setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (StaticMethod.destroyAllSubject(getApplicationContext())) {
                            Toast.makeText(getApplicationContext(), R.string.setting_delete_all_subjects_made, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.error_database, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();

    }

    public void ClickDestroyAllNotes(View v){
        StaticMethod.getAlert(this)
                .setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (StaticMethod.destroyAllNotes(getApplicationContext())) {
                            Toast.makeText(getApplicationContext(), R.string.setting_delete_all_notes_made, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.error_database, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }

    public void ClickDestroyAllPlanLesson(View v){
        StaticMethod.getAlert(this)
                .setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (StaticMethod.destroyAllLessonPlan(getApplicationContext())) {
                            Toast.makeText(getApplicationContext(), R.string.setting_delete_all_lesson_plan_made, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.error_database, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }
}
