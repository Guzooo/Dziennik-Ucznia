package pl.Guzooo.DziennikUcznia;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsActivity extends Activity implements View.OnClickListener {

    public static final String EXTRA_ID = "id";

    private final String BUNDLE_VISIBLE_NOTES = "visiblenotes";

    private Subject subject;

    private EditText editTextAssessment;
    private TextView textViewAssessment;
    private TextView textViewUnpreparedness;
    private ViewGroup viewGroupHomeLayout;

    private SQLiteDatabase db;
    private Cursor cursor;
    private AdapterNoteCardView adapter;

    private View viewNotesBox;

    private final int positionNoteBox = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        editTextAssessment = findViewById(R.id.details_edit_assessment);
        textViewAssessment = findViewById(R.id.details_assessment);
        textViewUnpreparedness = findViewById(R.id.details_unpreparedness);

        viewGroupHomeLayout = findViewById(R.id.details_home_layout);
        viewNotesBox = findViewById(R.id.details_notes_box);

        findViewById(R.id.details_add_note).setOnClickListener(this);

        try {
            if (!readSubject()) {
                finish();
                return;
            }

            db = DatabaseUtils.getReadableDatabase(this);

            refreshNotesCursor();
            setAdapter();
            setCustomActionBar();
            refreshActionBarInfo();
        } catch (SQLException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }

        if (savedInstanceState == null || !savedInstanceState.getBoolean(BUNDLE_VISIBLE_NOTES)) showNotes();
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
        outState.putBoolean(BUNDLE_VISIBLE_NOTES, (viewGroupHomeLayout.findViewById(viewNotesBox.getId()) != null));
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
        }
    }

    public void ClickPlus(View v){
        subject.addAssessment(editTextAssessment.getText().toString().trim(), this);
        textViewAssessment.setText(subject.getStringAssessments(this));
        refreshActionBarInfo();
    }

    public void ClickMinus(View v){
        subject.removeAssessment(editTextAssessment.getText().toString().trim(), this);
        textViewAssessment.setText(subject.getStringAssessments(this));
        refreshActionBarInfo();
    }

    public void ClickMinusUnpreparedness(View v){
        subject.removeUnpreparedness();
        textViewUnpreparedness.setText(getResources().getString(R.string.unpreparedness, subject.getUnpreparedness()));
    }

    private void setCustomActionBar() {
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.action_bar_two_text);
    }

    private Boolean readSubject(){
        subject = Subject.getOfId(getIntent().getIntExtra(EXTRA_ID, 0), this);

        if(subject.getId() == 0) return false;

        TextView textViewTeacher = findViewById(R.id.details_teacher);
        TextView textViewDescription = findViewById(R.id.details_description);

        textViewTeacher.setText(subject.getTeacher());
        textViewAssessment.setText(subject.getStringAssessments(this));
        textViewUnpreparedness.setText(getResources().getString(R.string.unpreparedness, subject.getUnpreparedness()));
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
        adapter = new AdapterNoteCardView(cursor);
        recyclerView.setAdapter(adapter);

        adapter.setListener(new AdapterNoteCardView.Listener() {
            @Override
            public void onClick(int id) {
                goToNoteActivity(0, id);
            }
        });
    }

    private void refreshActionBarInfo(){
        View actionBar = getActionBar().getCustomView();

        TextView textViewTitle = actionBar.findViewById(R.id.action_bar_two_text_title);
        TextView textViewSecond = actionBar.findViewById(R.id.action_bar_two_text_second);

        textViewTitle.setText(subject.getName());
        textViewSecond.setText(setAverage());
    }

    private String setAverage(){
        SharedPreferences sharedPreferences = getSharedPreferences(SettingActivity.PREFERENCE_NAME, MODE_PRIVATE);
        if(sharedPreferences.getBoolean(SettingActivity.PREFERENCE_AVERAGE_TO_ASSESSMENT, SettingActivity.DEFAULT_AVERAGE_TO_ASSESSMENT))
            return Float.toString(subject.getAverage()) + getResources().getString(R.string.separation) + Integer.toString(subject.getRoundedAverage(sharedPreferences));
        else return Float.toString(subject.getAverage());
    }

    private void showNotes(){
        if ((viewGroupHomeLayout.findViewById(viewNotesBox.getId()) == null)) viewGroupHomeLayout.addView(viewNotesBox, positionNoteBox);
        else viewGroupHomeLayout.removeView(viewNotesBox);
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
