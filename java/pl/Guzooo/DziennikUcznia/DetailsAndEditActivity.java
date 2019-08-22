package pl.Guzooo.DziennikUcznia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.Locale;

public class DetailsAndEditActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "id";

    private final String BUNDLE_VISIBLE_NOTES = "visiblenotes";

    private Subject subject;

    private EditText editTextAssessment;
    private TextAndHoldEditView textAndHoldEditViewTeacher;
    private TextAndHoldEditView textAndHoldEditViewUnpreparedness;
    private TextAndHoldEditView textAndHoldEditViewDescription;

    private SQLiteDatabase db;

    private Cursor notesCursor;
    private AdapterNoteCardView notesAdapter;
    private RecyclerView notesRecycler;

    private Cursor assessmentsCursor;
    private RecyclerView assessmentsRecycler;
    private AdapterAssessments assessmentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_and_edit);

        editTextAssessment = findViewById(R.id.assessment);
        textAndHoldEditViewTeacher = findViewById(R.id.teacher);
        textAndHoldEditViewUnpreparedness = findViewById(R.id.unpreparedness);
        textAndHoldEditViewDescription = findViewById(R.id.description);

        notesRecycler = findViewById(R.id.notes);
        assessmentsRecycler = findViewById(R.id.assessments);

        subject = Subject.getOfId(getIntent().getIntExtra(EXTRA_ID, 0), this);
        db = DatabaseUtils.getWritableDatabase(this);

        if(savedInstanceState == null || !savedInstanceState.getBoolean(BUNDLE_VISIBLE_NOTES))
            ChangeVisibilityNotes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RefreshActionBarInfo();
        SetNotes();
        SetTeacher();
        SetAssessments();
        SetUnpreparedness();
        SetDescription();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_and_edit_menu, menu);
        DrawableCompat.setTint(menu.findItem(R.id.action_notes).getIcon(), ContextCompat.getColor(this, android.R.color.darker_gray));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.action_edit:

                return true;

            case R.id.action_notes:
                ChangeVisibilityNotes();
                return true;

            case R.id.action_del:

                return true;

            case R.id.action_duplicate:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_VISIBLE_NOTES, (notesRecycler.getVisibility() == View.VISIBLE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        notesCursor.close();
        assessmentsCursor.close();
        db.close();
    }

    private void ChangeVisibilityNotes(){
        if(notesRecycler.getVisibility() == View.VISIBLE){
            notesRecycler.setVisibility(View.GONE);
        } else {
            notesRecycler.setVisibility(View.VISIBLE);
        }
    }

    private void SetNotes(){
        RefreshNotesCursor();
        SetNotesAdapter();
    }

    private void RefreshNotesCursor(){
        notesCursor = db.query("NOTES",
                SubjectNote.subjectNoteOnCursor,
                "TAB_SUBJECT = ?",
                new String[] {Integer.toString(subject.getId())},
                null, null, null);
        if(notesAdapter != null)
            notesAdapter.changeCursor(notesCursor);
    }

    private void SetNotesAdapter(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        notesRecycler.setLayoutManager(layoutManager);
        notesAdapter = new AdapterNoteCardView(notesCursor, this);
        notesRecycler.setAdapter(notesAdapter);

        notesAdapter.setListener(new AdapterNoteCardView.Listener() {
            @Override
            public void onClick(int id) {
                goToNoteActivity(subject.getId(), id);
            }

            @Override
            public void refreshCursor() {
                RefreshNotesCursor();
            }

            @Override
            public String getSubjectName() {
                return subject.getName();
            }
        });
    }

    private void SetTeacher(){
        textAndHoldEditViewTeacher.setText(subject.getTeacher());
    }

    private void SetAssessments(){
        RefreshAssessmentsCursor();
        SetAssessmentsAdapter();
    }

    private void RefreshAssessmentsCursor(){
        assessmentsCursor = db.query("ASSESSMENTS",
                SubjectAssessment.subjectAssessmentOnCursor,
                "TAB_SUBJECT = ? AND SEMESTER = ?",
                new String[] {Integer.toString(subject.getId()), Integer.toString(StatisticsActivity.getSemester(this))},
                null, null, null);
        if(assessmentsAdapter != null)
          assessmentsAdapter.changeCursor(assessmentsCursor);
    }

    private void SetAssessmentsAdapter(){
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int width = size.x;
        float count = width / getResources().getDimensionPixelSize(R.dimen.assessment_length);
        float margin = width % getResources().getDimensionPixelSize(R.dimen.assessment_length) / count / 2;

        GridLayoutManager layoutManage = new GridLayoutManager(this, (int) count);
        assessmentsRecycler.setLayoutManager(layoutManage);
        assessmentsAdapter = new AdapterAssessments(assessmentsCursor, (int) margin, db,this);
        assessmentsRecycler.setAdapter(assessmentsAdapter);

        //TODO; listener;
    }

    private void SetUnpreparedness() {
        textAndHoldEditViewUnpreparedness.setText(Integer.toString(subject.getUnpreparedness()));

        final View titles = findViewById(R.id.unpreparedness_titles);
        final View button = findViewById(R.id.unpreparedness_del_one);
        final EditText editStart = findViewById(R.id.unpreparedness_start_edit);

        textAndHoldEditViewUnpreparedness.AddEditText(editStart);
        textAndHoldEditViewUnpreparedness.setOnChangeViewListener(new TextAndHoldEditView.onChangeViewListener() {
            @Override
            public void onChangeView(boolean isVisibleText, boolean isEmptyText) {
                if (!isVisibleText) {
                    button.setVisibility(View.GONE);
                    titles.setVisibility(View.VISIBLE);
                    editStart.setVisibility(View.VISIBLE);
                } else {
                    titles.setVisibility(View.GONE);
                    editStart.setVisibility(View.GONE);
                    if (isEmptyText)
                        button.setVisibility(View.GONE);
                    else
                        button.setVisibility(View.VISIBLE);
                }
            }
        });
        textAndHoldEditViewUnpreparedness.callChangeView();
    }

    private void SetDescription(){
        textAndHoldEditViewDescription.setText(subject.getDescription());
    }

    private void RefreshActionBarInfo(){
        getSupportActionBar().setTitle(subject.getName());
        getSupportActionBar().setSubtitle(SetAverage());
    }

    private String SetAverage(){
        SharedPreferences sharedPreferences = getSharedPreferences(SettingActivity.PREFERENCE_NAME, MODE_PRIVATE);

        ArrayList<SubjectAssessment> assessments1 = subject.getAssessment(1, this);
        ArrayList<SubjectAssessment> assessments2 = subject.getAssessment(2, this);

        float average = subject.getAverageEnd(assessments1, assessments2);
        String strAverage = getResources().getString(R.string.statistics_semester_end) + ": " + String.format(Locale.US, "%.2f", average);
        if(sharedPreferences.getBoolean(SettingActivity.PREFERENCE_AVERAGE_TO_ASSESSMENT, SettingActivity.DEFAULT_AVERAGE_TO_ASSESSMENT))
            return strAverage + getResources().getString(R.string.separation) + Integer.toString(subject.getRoundedAverageEnd(assessments1, assessments2,sharedPreferences)); //TODO: unnecessary delete ;)
        else
            return strAverage;
    }

    private void goToNoteActivity(int id, int idNote){
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(NoteActivity.EXTRA_ID_SUBJECT, id);
        intent.putExtra(NoteActivity.EXTRA_ID_NOTE, idNote);
        startActivity(intent);
    }
}
