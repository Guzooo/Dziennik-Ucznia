package pl.Guzooo.DziennikUcznia;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsActivity extends Activity implements View.OnClickListener {

    public static final String EXTRA_ID = "id";

    private Subject subject;
    private EditText editTextAssessment;
    private TextView textViewAssessment;
    private TextView textViewUnpreparedness;
    private TextView textViewSecond;

    SQLiteDatabase db;
    Cursor cursor;

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

        findViewById(R.id.details_add_note).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        try{
            SQLiteOpenHelper openHelper = new HelperDatabase(this);
            db = openHelper.getReadableDatabase();
            cursor = db.query("SUBJECTS",
                    Subject.subjectOnCursor,
                    "_id = ?",
                    new String[] {Integer.toString(getIntent().getIntExtra(EXTRA_ID, 0))},
                    null, null, null);

            if(cursor.moveToFirst()){
                subject = new Subject(cursor);
            } else {
                cursor.close();
                db.close();
                finish();
                return;
            }

            cursor = db.query("NOTES",
                    SubjectNote.subjectNoteOnCursor,
                    "TAB_SUBJECT = ?",
                    new String[] {Integer.toString(subject.getId())},
                    null, null, null);

            View actionBar = getActionBar().getCustomView();

            TextView textViewTitle = actionBar.findViewById(R.id.action_bar_two_text_title);
            textViewSecond = actionBar.findViewById(R.id.action_bar_two_text_second);

            TextView textViewTeacher = findViewById(R.id.details_teacher);
            TextView textViewDescription = findViewById(R.id.details_description);

            RecyclerView recyclerView = findViewById(R.id.details_notes);

            textViewTitle.setText(subject.getName());
            setAverage();

            textViewTeacher.setText(subject.getTeacher());
            textViewAssessment.setText(subject.getStringAssessments());
            if(textViewAssessment.getText().toString().trim().equals("")){
                textViewAssessment.setText(R.string.null_string);
            }
            textViewUnpreparedness.setText(getResources().getString(R.string.unpreparedness, subject.getUnpreparedness()));
            textViewDescription.setText(subject.getDescription());

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            AdapterNoteCardView adapter = new AdapterNoteCardView(cursor);
            recyclerView.setAdapter(adapter);

            adapter.setListener(new AdapterNoteCardView.Listener() {
                @Override
                public void onClick(int id) {
                    Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                    intent.putExtra(NoteActivity.EXTRA_ID_NOTE, id);
                    startActivity(intent);
                }
            });
        } catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }

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
                startActivity(intent);
                return true;

            case R.id.action_notes:
                View notesBox = findViewById(R.id.details_notes_box);
                if(notesBox.getVisibility() == View.GONE){
                    notesBox.setVisibility(View.VISIBLE);
                } else {
                    notesBox.setVisibility(View.GONE);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cursor.close();
        db.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.details_add_note:
                Intent intent = new Intent(this, NoteActivity.class);
                intent.putExtra(NoteActivity.EXTRA_ID_SUBJECT, getIntent().getIntExtra(EXTRA_ID, 0));
                startActivity(intent);
                break;
        }
    }

    public void ClickPlus(View v){
        if(editTextAssessment.getText().toString().trim().equals("")){
            Toast.makeText(this, R.string.hint_assessment, Toast.LENGTH_SHORT).show();
        } else {
            subject.getAssessments().add(Float.parseFloat(editTextAssessment.getText().toString().trim()));
            textViewAssessment.setText(subject.getStringAssessments());
        }
        setAverage();
        saveSubject();
    }

    public void ClickMinus(View v){
        Float assessment = Float.parseFloat(editTextAssessment.getText().toString().trim());
        if(editTextAssessment.getText().toString().trim().equals("")) {
            Toast.makeText(this, R.string.hint_assessment, Toast.LENGTH_SHORT).show();
        } else if(subject.getAssessments().size() == 0) {
            Toast.makeText(this, R.string.subject_null_assessments, Toast.LENGTH_SHORT).show();
        } else if (!subject.getAssessments().remove(assessment)) {
            Toast.makeText(this, R.string.subject_null_this_assessment, Toast.LENGTH_SHORT).show();
        } else {
            subject.getAssessments().remove(assessment);
            textViewAssessment.setText(subject.getStringAssessments());
        }
        setAverage();
        saveSubject();
    }

    public void ClickMinusUnpreparedness(View v){
        subject.removeUnpreparedness();
        textViewUnpreparedness.setText(getResources().getString(R.string.unpreparedness, subject.getUnpreparedness()));
        saveSubject();
    }

    private void setAverage(){
        SharedPreferences sharedPreferences = getSharedPreferences(SettingActivity.PREFERENCE_NAME, MODE_PRIVATE);
        if(sharedPreferences.getBoolean(SettingActivity.PREFERENCE_AVERAGE_TO_ASSESSMENT, SettingActivity.defaultAverageToAssessment)){
            textViewSecond.setText(Float.toString(subject.getAverage()) + getResources().getString(R.string.separation) + Integer.toString(subject.getRoundedAverage(sharedPreferences)));
        } else {
            textViewSecond.setText(Float.toString(subject.getAverage()));
        }
    }

    private void saveSubject(){
        try {
            SQLiteOpenHelper openHelper = new HelperDatabase(this);
            SQLiteDatabase db = openHelper.getWritableDatabase();
            db.update("SUBJECTS",
                    subject.saveSubject(this),
                    "_id = ?",
                    new String[] {Integer.toString(subject.getId())});
            db.close();
        } catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database,Toast.LENGTH_SHORT).show();
        }
    }
}
