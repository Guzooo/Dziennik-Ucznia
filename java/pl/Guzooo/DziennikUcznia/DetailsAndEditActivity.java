package pl.Guzooo.DziennikUcznia;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class DetailsAndEditActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "id";

    private final String BUNDLE_VISIBLE_NOTES = "visiblenotes";

    private Subject subject;

    private EditText editTextAssessment;//TODO: trza?
    private EditText editTextAssessmentWeight;
    private TextAndHoldEditView textAndHoldEditViewTeacher;
    private TextAndHoldEditView textAndHoldEditViewUnpreparedness;
    private EditText editTextStartUnpreparedness;
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
        editTextAssessmentWeight = findViewById(R.id.assessment_weight);
        textAndHoldEditViewTeacher = findViewById(R.id.teacher);
        textAndHoldEditViewUnpreparedness = findViewById(R.id.unpreparedness);
        editTextStartUnpreparedness = findViewById(R.id.unpreparedness_start_edit);
        textAndHoldEditViewDescription = findViewById(R.id.description);

        textAndHoldEditViewUnpreparedness.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        textAndHoldEditViewDescription.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        notesRecycler = findViewById(R.id.notes);
        assessmentsRecycler = findViewById(R.id.assessments);

        subject = Subject.getOfId(getIntent().getIntExtra(EXTRA_ID, 0), this);
        db = Database2020.getToWriting(this);

        if(savedInstanceState == null || !savedInstanceState.getBoolean(BUNDLE_VISIBLE_NOTES))
            ChangeVisibilityNotes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RefreshActionBarInfo();
        SetNotes();
        SetTeacher();
        SetAssessmentWeight();
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
                EditTitle();
                return true;

            case R.id.action_notes:
                ChangeVisibilityNotes();
                return true;

            case R.id.action_del:
                Delete();
                return true;

            case R.id.action_duplicate:
                Duplicate();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(checkVisibilityNotes())
            ChangeVisibilityNotes();
        else
            super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_VISIBLE_NOTES, (notesRecycler.getVisibility() == View.VISIBLE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        CloseAllTextAndEdit();
        subject.setTeacher(textAndHoldEditViewTeacher.getText());
        subject.setCurrentUnpreparedness(valueOf(textAndHoldEditViewUnpreparedness.getText()), this);
        subject.setUnpreparedness(valueOf(editTextStartUnpreparedness.getText().toString()));
        subject.setDescription(textAndHoldEditViewDescription.getText());
        subject.update(this);
    }

    private int valueOf(String string){
        if(string.equals("")){
            return 0;
        } else {
            return Integer.valueOf(string);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        notesCursor.close();
        assessmentsCursor.close();
        db.close();
    }

    private void EditTitle(){
        final EditText editText = new EditText(this);
        editText.setText(subject.getName());
        editText.setTextColor(Color.parseColor("#FFFFFF"));
        new AlertDialog.Builder(this)
                .setView(editText)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(editText.getText().toString().equals("")){
                            String text = getString(R.string.cant_save) + getString(R.string.separator) + getString(R.string.edit_hint_name);
                            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        subject.setName(editText.getText().toString());
                        RefreshActionBarInfo();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void Delete(){
        InterfaceUtils.getAlertDelete(this)
                .setPositiveButton(R.string.yes, new AlertDialog.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        subject.delete(getApplicationContext());
                        finish();
                    }
                })
                .show();
    }

    private void Duplicate(){
        if(subject.duplicate(this))
            Toast.makeText(this, R.string.edit_duplicate_subject_made, Toast.LENGTH_SHORT).show();
    }

    public void ClickPlusAssessment(View v){
        SubjectAssessment assessment = subject.addAssessment(editTextAssessment.getText().toString().trim(), editTextAssessmentWeight.getText().toString().trim(), this);
        if(assessment == null)
            RefreshAssessmentInfo();
        else
            OpenAssessmentWindows(assessment, true);
    }

    public void ClickMinusUnpreparedness(View v){
        int np = Integer.valueOf(textAndHoldEditViewUnpreparedness.getText());
        if(np > 0) {
            np--;
            textAndHoldEditViewUnpreparedness.setText(np + "");
        }
    }

    private void ChangeVisibilityNotes(){
        if(checkVisibilityNotes()){
            notesRecycler.setVisibility(View.GONE);
        } else {
            notesRecycler.setVisibility(View.VISIBLE);
        }
    }

    private boolean checkVisibilityNotes(){
        return notesRecycler.getVisibility() == View.VISIBLE;
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

    private void SetAssessmentWeight(){
        SharedPreferences sharedPreferences = getSharedPreferences(SettingActivityOLD.PREFERENCE_NAME, MODE_PRIVATE);
        if(!sharedPreferences.getBoolean(SettingActivityOLD.PREFERENCE_AVERAGE_WEIGHT, SettingActivityOLD.DEFAULT_AVERAGE_WEIGHT))
            editTextAssessmentWeight.setVisibility(View.GONE);
    }

    private void SetAssessments(){
        RefreshAssessmentsCursor();
        SetAssessmentsAdapter();
    }

    private void RefreshAssessmentInfo(){
        RefreshActionBarInfo();
        RefreshAssessmentsCursor();
    }

    private void RefreshAssessmentsCursor(){
        assessmentsCursor = db.query("ASSESSMENTS",
                SubjectAssessment.subjectAssessmentOnCursor,
                "TAB_SUBJECT = ? AND SEMESTER = ?",
                new String[]{Integer.toString(subject.getId()), Integer.toString(DataManager.getSemester(this))},
                null, null, null);
        if (assessmentsAdapter != null)
            assessmentsAdapter.changeCursor(assessmentsCursor);
    }

    private void SetAssessmentsAdapter(){
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int width = size.x;
        float count = width / getResources().getDimensionPixelSize(R.dimen.length_assessment);
        float margin = width % getResources().getDimensionPixelSize(R.dimen.length_assessment) / count / 2;

        GridLayoutManager layoutManage = new GridLayoutManager(this, (int) count);
        assessmentsRecycler.setLayoutManager(layoutManage);
        assessmentsAdapter = new AdapterAssessments(assessmentsCursor, (int) margin, db,this);
        assessmentsRecycler.setAdapter(assessmentsAdapter);

        assessmentsAdapter.setListener(new AdapterAssessments.Listener() {
            @Override
            public void onClick(SubjectAssessment subjectAssessment, final int position) {
                OpenAssessmentWindows(subjectAssessment, false);
            }
        });
    }

    private void OpenAssessmentWindows(SubjectAssessment assessment, boolean insert) {
        AssessmentOptionsFragment.ListenerDismiss listenerDismiss = new AssessmentOptionsFragment.ListenerDismiss() {
            @Override
            public void Refresh() {
                RefreshAssessmentInfo();
            }
        };
        new AssessmentOptionsFragment().show(assessment, listenerDismiss, insert, getSupportFragmentManager(), "assessment");
    }

    private void SetUnpreparedness() {
        textAndHoldEditViewUnpreparedness.setText(subject.getCurrentUnpreparedness(this) + "");
        editTextStartUnpreparedness.setText(subject.getUnpreparedness() + "");

        final View titles = findViewById(R.id.unpreparedness_titles);
        final View button = findViewById(R.id.unpreparedness_del_one);

        textAndHoldEditViewUnpreparedness.AddEditText(editTextStartUnpreparedness);
        textAndHoldEditViewUnpreparedness.setOnChangeViewListener(new TextAndHoldEditView.onChangeViewListener() {
            @Override
            public void onChangeView(boolean isVisibleText, boolean isEmptyText) {
                if (!isVisibleText) {
                    button.setVisibility(View.GONE);
                    titles.setVisibility(View.VISIBLE);
                    editTextStartUnpreparedness.setVisibility(View.VISIBLE);
                } else {
                    titles.setVisibility(View.GONE);
                    editTextStartUnpreparedness.setVisibility(View.GONE);
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
        SharedPreferences sharedPreferences = getSharedPreferences(SettingActivityOLD.PREFERENCE_NAME, MODE_PRIVATE);

        ArrayList<SubjectAssessment> assessments1 = subject.getAssessment(1, this);
        ArrayList<SubjectAssessment> assessments2 = subject.getAssessment(2, this);
        float average = subject.getAverageEnd(assessments1, assessments2, this);

        if(average == 0.0)
            return "";

        String strAverage = getResources().getString(R.string.final_average) + ": " + String.format(Locale.US, "%.2f", average);
        if(sharedPreferences.getBoolean(SettingActivityOLD.PREFERENCE_AVERAGE_TO_ASSESSMENT, SettingActivityOLD.DEFAULT_AVERAGE_TO_ASSESSMENT))
            return strAverage + getResources().getString(R.string.separator) + Integer.toString(subject.getRoundedAverageEnd(assessments1, assessments2,sharedPreferences, this)); //TODO: unnecessary delete ;)
        else
            return strAverage;
    }

    private void goToNoteActivity(int id, int idNote){
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(NoteActivity.EXTRA_ID_SUBJECT, id);
        intent.putExtra(NoteActivity.EXTRA_ID_NOTE, idNote);
        startActivity(intent);
    }

    private void CloseAllTextAndEdit(){
        textAndHoldEditViewTeacher.EndEdition();
        textAndHoldEditViewUnpreparedness.EndEdition();
        textAndHoldEditViewDescription.EndEdition();
    }
}
