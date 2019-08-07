package pl.Guzooo.DziennikUcznia;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_ID = "id";

    private final String BUNDLE_VISIBLE_NOTES = "visiblenotes";

    private Subject subject;

    private EditText editTextAssessment;
    private TextView textViewAssessment;
    private TextView textViewUnpreparedness;

    private SQLiteDatabase db;
    private Cursor cursor;
    private AdapterNoteCardView adapter;

    private View viewNotesBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        /*
        Tylko do testu niestandardowego widoku
        */

        Intent intent = new Intent(this, DetailsAndEditActivity.class);
        intent.putExtra(DetailsAndEditActivity.EXTRA_ID, getIntent().getExtras().getInt(EXTRA_ID));
        startActivity(intent);

        editTextAssessment = findViewById(R.id.details_edit_assessment);
        textViewAssessment = findViewById(R.id.details_assessment);
        textViewUnpreparedness = findViewById(R.id.details_unpreparedness);

        viewNotesBox = findViewById(R.id.details_notes_box);

        findViewById(R.id.details_add_note).setOnClickListener(this);
        findViewById(R.id.details_share_all_notes).setOnClickListener(this);
        findViewById(R.id.details_delete_all_notes).setOnClickListener(this);

        try {
            if (!readSubject()) {
                finish();
                return;
            }

            db = DatabaseUtils.getReadableDatabase(this);

            refreshNotesCursor();
            setAdapter();
            refreshActionBarInfo();
        } catch (SQLException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }

        visibilityButtonsRelatedWithNotes();

        if (savedInstanceState == null || !savedInstanceState.getBoolean(BUNDLE_VISIBLE_NOTES))
            showNotes();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        try {
            if (!readSubject()) {
                finish();
                return;
            }
            refreshNotesCursor();
            refreshActionBarInfo();
        }catch (SQLException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }

        visibilityButtonsRelatedWithNotes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        DrawableCompat.setTint(menu.findItem(R.id.action_notes).getIcon(), ContextCompat.getColor(this, android.R.color.darker_gray));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_edit:
                goToEditActivity(getIntent().getIntExtra(EXTRA_ID, 0));
                return true;

            case R.id.action_notes:
                showNotes();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_VISIBLE_NOTES, (viewNotesBox.getVisibility() == View.VISIBLE));
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (subject.getSizeContentValues() != 0) subject.update(this);
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
                goToNoteActivity(getIntent().getIntExtra(EXTRA_ID, 0), 0);
                break;

            case R.id.details_share_all_notes:
                shareAllNotes();
                break;

            case R.id.details_delete_all_notes:
                deleteAllNotes();
                break;
        }
    }

    public void ClickPlus(View v){
        subject.addAssessment(editTextAssessment.getText().toString().trim(), this);
        textViewAssessment.setText(subject.getStringAssessments(subject.getAssessment(StatisticsActivity.getSemester(this), this), this));
        refreshActionBarInfo();
    }

    public void ClickMinus(View v){
        subject.removeAssessment(editTextAssessment.getText().toString().trim(), this);
        textViewAssessment.setText(subject.getStringAssessments(subject.getAssessment(StatisticsActivity.getSemester(this), this), this));
        refreshActionBarInfo();
    }

    public void ClickMinusUnpreparedness(View v){
        subject.removeUnpreparedness();
        textViewUnpreparedness.setText(getResources().getString(R.string.unpreparedness_with_variable, subject.getUnpreparedness()));
    }

    private Boolean readSubject(){
        subject = Subject.getOfId(getIntent().getIntExtra(EXTRA_ID, 0), this);

        if(subject.getId() == 0) return false;

        TextView textViewTeacher = findViewById(R.id.details_teacher);
        TextView textViewDescription = findViewById(R.id.details_description);

        textViewTeacher.setText(subject.getTeacher());
        textViewAssessment.setText(subject.getStringAssessments(subject.getAssessment(StatisticsActivity.getSemester(this), this), this));
        textViewUnpreparedness.setText(getResources().getString(R.string.unpreparedness_with_variable, subject.getUnpreparedness()));
        textViewDescription.setText(subject.getDescription());

        return true;
    }

    private void refreshNotesCursor(){
        cursor = db.query("NOTES",
                SubjectNote.subjectNoteOnCursor,
                "TAB_SUBJECT = ?",
                new String[] {Integer.toString(subject.getId())},
                null, null, null);
        if (adapter != null) adapter.changeCursor(cursor);
    }

    private void setAdapter(){
        RecyclerView recyclerView = findViewById(R.id.details_notes);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AdapterNoteCardView(cursor, this);
        recyclerView.setAdapter(adapter);

        adapter.setListener(new AdapterNoteCardView.Listener() {
            @Override
            public void onClick(int id) {
                goToNoteActivity(0, id);
            }

            @Override
            public void refreshCursor() {
                refreshNotesCursor();
            }

            @Override
            public String getSubjectName() {
                return subject.getName();
            }
        });
    }

    private void refreshActionBarInfo(){
        getSupportActionBar().setTitle(subject.getName());
        getSupportActionBar().setSubtitle(setAverage());
    }

    private String setAverage(){
        SharedPreferences sharedPreferences = getSharedPreferences(SettingActivity.PREFERENCE_NAME, MODE_PRIVATE);

        ArrayList<SubjectAssessment> assessments1 = subject.getAssessment(1, this);
        ArrayList<SubjectAssessment> assessments2 = subject.getAssessment(2, this);

        float average = subject.getAverageEnd(assessments1, assessments2);
        String strAverage = getResources().getString(R.string.statistics_semester_end) + ": " + String.format(Locale.US, "%.2f", average);
        if(sharedPreferences.getBoolean(SettingActivity.PREFERENCE_AVERAGE_TO_ASSESSMENT, SettingActivity.DEFAULT_AVERAGE_TO_ASSESSMENT))
            return strAverage + getResources().getString(R.string.separation) + Integer.toString(subject.getRoundedAverageEnd(assessments1, assessments2,sharedPreferences));
        else
            return strAverage;
    }

    private void showNotes(){
        if (viewNotesBox.getVisibility() == View.VISIBLE)
            viewNotesBox.setVisibility(View.GONE);
        else
            viewNotesBox.setVisibility(View.VISIBLE);
    }

    private void visibilityButtonsRelatedWithNotes(){
        if (subject.getSizeNotes(this) != 0) {
            findViewById(R.id.details_delete_all_notes).setVisibility(View.VISIBLE);
            findViewById(R.id.details_share_all_notes).setVisibility(View.VISIBLE);
            findViewById(R.id.details_separator_next_delete_all_notes).setVisibility(View.VISIBLE);
            findViewById(R.id.details_separator_next_share_all_notes).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.details_delete_all_notes).setVisibility(View.GONE);
            findViewById(R.id.details_share_all_notes).setVisibility(View.GONE);
            findViewById(R.id.details_separator_next_delete_all_notes).setVisibility(View.GONE);
            findViewById(R.id.details_separator_next_share_all_notes).setVisibility(View.GONE);
        }
    }

    private void shareAllNotes(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getShareText());
        Intent intentChose = Intent.createChooser(intent, getString(R.string.share_title));
        startActivity(intentChose);
    }

    private String getShareText(){
        String string = "❗" + subject.getName() + "❗";
        if(cursor.moveToFirst()) {
            do {
                SubjectNote subjectNote = SubjectNote.getOfCursor(cursor);
                string += "\n\n✔ " + subjectNote.getName();
                if(!subjectNote.getNote().equals("")){
                    string += ":\n\n" + subjectNote.getNote();
                }
            } while (cursor.moveToNext());
        }
        string += getString(R.string.share_info);
        return string;
    }

    private void deleteAllNotes(){
        InterfaceUtils.getAlertDelete(this)
                .setPositiveButton(R.string.yes, new androidx.appcompat.app.AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseUtils.destroyAllNotes("TAB_SUBJECT = ?", new String[]{Integer.toString(subject.getId())}, getApplicationContext());
                        subject.putInfoSizeNotes(getApplicationContext());
                        refreshNotesCursor();
                        visibilityButtonsRelatedWithNotes();
                    }
                })
                .show();
    }

    private void goToEditActivity(int id){
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(EditActivity.EXTRA_ID, id);
        startActivity(intent);
    }

    private void goToNoteActivity(int id, int idNote){
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(NoteActivity.EXTRA_ID_SUBJECT, id);
        intent.putExtra(NoteActivity.EXTRA_ID_NOTE, idNote);
        startActivity(intent);
    }
}
