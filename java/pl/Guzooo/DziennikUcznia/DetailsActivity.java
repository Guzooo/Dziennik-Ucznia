package pl.Guzooo.DziennikUcznia;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class DetailsActivity extends Activity {

    public static final String EXTRA_ID = "id";

    private Subject subject;

    private EditText editTextAssessment;
    private TextView textViewAssessment;
    private TextView textViewUnpreparedness;
    private TextView textViewSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.action_bar_two_text);

        editTextAssessment = findViewById(R.id.details_edit_assessment);
        textViewAssessment = findViewById(R.id.details_assessment);
        textViewUnpreparedness = findViewById(R.id.details_unpreparedness);
    }

    @Override
    protected void onStart() {
        super.onStart();

        try{
            SQLiteOpenHelper openHelper = new HelperDatabase(this);
            SQLiteDatabase db = openHelper.getReadableDatabase();
            Cursor cursor = db.query("SUBJECTS",
                    new String[]{"OBJECT"},
                    "_id = ?",
                    new String[] {Integer.toString(getIntent().getIntExtra(EXTRA_ID, 0))},
                    null, null, null);

            if(cursor.moveToFirst()){
                subject = new Subject(cursor.getString(0));
            } else {
                cursor.close();
                db.close();
                finish();
                return;
            }

            cursor.close();
            db.close();
        } catch (SQLiteException e){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();//TODO: String
        }

        View actionBar = getActionBar().getCustomView();

        TextView textViewTitle = actionBar.findViewById(R.id.action_bar_two_text_title);
        textViewSecond = actionBar.findViewById(R.id.action_bar_two_text_second);

        TextView textViewTeacher = findViewById(R.id.details_teacher);
        TextView textViewDescription = findViewById(R.id.details_description);

        textViewTitle.setText(subject.getName());
        setAverage();

        textViewTeacher.setText(subject.getTeacher());
        textViewAssessment.setText(subject.getStringAssessments());
        textViewUnpreparedness.setText("NP: " + subject.getUnpreparedness());//TODO:string
        textViewDescription.setText(subject.getDescription());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_edit:
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra(EditActivity.EXTRA_ID, getIntent().getIntExtra(EXTRA_ID, 0));
                intent.putExtra(EditActivity.EXTRA_OBJECT, subject.toString());
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void ClickPlus(View v){
        if(editTextAssessment.getText().toString().trim().equals("")){
            Toast.makeText(this, "Wpisz ocenę", Toast.LENGTH_LONG).show(); //TODO:stringi
        } else {
            subject.addAssessment(Float.parseFloat(editTextAssessment.getText().toString().trim()));
            textViewAssessment.setText(subject.getStringAssessments());
        }
        setAverage();
        saveSubject();
    }

    public void ClickMinus(View v){
        if(editTextAssessment.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Wpisz ocenę", Toast.LENGTH_LONG).show();
        } else {
            subject.removeAssessment(Float.parseFloat(editTextAssessment.getText().toString().trim()), this);
            textViewAssessment.setText(subject.getStringAssessments());
        }
        setAverage();
        saveSubject();
    }

    public void ClickMinusUnpreparedness(View v){
        subject.removeUnpreparedness();
        textViewUnpreparedness.setText("NP: " + subject.getUnpreparedness());//TODO:string
        saveSubject();
    }

    private void setAverage(){
        subject.setAverage();
        SharedPreferences sharedPreferences = getSharedPreferences(SettingActivity.PREFERENCE_NAME_AVERAGE_TO, MODE_PRIVATE);
        if(sharedPreferences.getBoolean(SettingActivity.PREFERENCE_AVERAGE_TO_ASSESSMENT, SettingActivity.defaulAverageToAssessment)){
            textViewSecond.setText(Float.toString(subject.getAverage()) + " | " + Integer.toString(subject.getRoundedAverage(sharedPreferences)));
        } else {
            textViewSecond.setText(Float.toString(subject.getAverage()));
        }
    }

    private void saveSubject(){
        String subjectString = subject.toString();

        ContentValues subjectValues = new ContentValues();
        subjectValues.put("OBJECT", subjectString);

        try {
            SQLiteOpenHelper openHelper = new HelperDatabase(this);
            SQLiteDatabase db = openHelper.getWritableDatabase();
            db.update("SUBJECTS",
                    subjectValues,
                    "_id = ?",
                    new String[] {Integer.toString(getIntent().getIntExtra(EXTRA_ID, 0))});
            db.close();
        } catch (SQLiteException e){
            Toast.makeText(this, "Błąddd", Toast.LENGTH_SHORT).show(); //TODO:string
        }
    }
}
