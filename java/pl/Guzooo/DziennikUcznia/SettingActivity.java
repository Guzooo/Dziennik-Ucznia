package pl.Guzooo.DziennikUcznia;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
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

    public static final Boolean defaulAverageToAssessment = false;
    public static final Float defaulAverageToSix = 5.1f;
    public static final Float defaulAverageToFive = 4.5f;
    public static final Float defaulAverageToFour = 3.6f;
    public static final Float defaulAverageToThree = 2.7f;
    public static final Float defaulAverageToTwo = 1.75f;
    public static final Float defaulAverageToBelt = 4.75f;

    private SharedPreferences sharedPreferences;

    private CheckBox checkBoxAverageToAssessment;
    private EditText editTextAverageToSix;
    private EditText editTextAverageToFive;
    private EditText editTextAverageToFour;
    private EditText editTextAverageToThree;
    private EditText editTextAverageToTwo;
    private EditText editTextAverageToBelt;

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

        sharedPreferences = getSharedPreferences(PREFERENCE_NAME_AVERAGE_TO, MODE_PRIVATE);
        checkBoxAverageToAssessment.setChecked(sharedPreferences.getBoolean(PREFERENCE_AVERAGE_TO_ASSESSMENT, defaulAverageToAssessment));
        editTextAverageToSix.setText(Float.toString(sharedPreferences.getFloat(PREFERENCE_AVERAGE_TO_SIX, defaulAverageToSix)));
        editTextAverageToFive.setText(Float.toString(sharedPreferences.getFloat(PREFERENCE_AVERAGE_TO_FIVE, defaulAverageToFive)));
        editTextAverageToFour.setText(Float.toString(sharedPreferences.getFloat(PREFERENCE_AVERAGE_TO_FOUR, defaulAverageToFour)));
        editTextAverageToThree.setText(Float.toString(sharedPreferences.getFloat(PREFERENCE_AVERAGE_TO_THREE, defaulAverageToThree)));
        editTextAverageToTwo.setText(Float.toString(sharedPreferences.getFloat(PREFERENCE_AVERAGE_TO_TWO, defaulAverageToTwo)));
        editTextAverageToBelt.setText(Float.toString(sharedPreferences.getFloat(PREFERENCE_AVERAGE_TO_BELT, defaulAverageToBelt)));

        ClickCheckAverageToAssessment(checkBoxAverageToAssessment);
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
            db.close();
            Toast.makeText(this, "Wyjebałeś lekcje hehehe", Toast.LENGTH_LONG).show(); //TODO: string
        } catch (SQLiteException e){
            Toast.makeText(this, "Jebut", Toast.LENGTH_LONG).show(); //TODO: string
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
        editor.apply();
    }
}
