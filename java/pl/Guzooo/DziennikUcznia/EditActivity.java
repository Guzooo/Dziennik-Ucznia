package pl.Guzooo.DziennikUcznia;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class EditActivity extends Activity {

    public static final String EXTRA_ID = "id";

    private Subject editSubject;

    private EditText editTextName;
    private EditText editTextTeacher;
    private EditText editTextAssessment;
    private EditText editTextUnpreparedness;
    private EditText editTextDescription;
    private TextView textViewEditAssessment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        editTextName = findViewById(R.id.edit_name);
        editTextTeacher = findViewById(R.id.edit_teacher);
        editTextAssessment = findViewById(R.id.edit_assessment);
        editTextUnpreparedness = findViewById(R.id.edit_unpreparedness);
        editTextDescription = findViewById(R.id.edit_description);
        TextView textViewEditAssessmentTitle = findViewById(R.id.edit_edit_assessment_title);
        textViewEditAssessment = findViewById(R.id.edit_edit_assessment);
        TextView textViewCurrentAssessment = findViewById(R.id.edit_current_assessment);
        View viewCurrentAssessmentBox = findViewById(R.id.edit_current_assessment_box);
        Button buttonSave = findViewById(R.id.edit_save);
        Button buttonDelete = findViewById(R.id.edit_delete_subject);
        Button buttonDuplicate = findViewById(R.id.edit_duplicate_subject);

        if(getIntent().getIntExtra(EXTRA_ID, 0) != 0){
            try {
                SQLiteOpenHelper openHelper = new HelperDatabase(this);
                SQLiteDatabase db = openHelper.getReadableDatabase();
                Cursor cursor = db.query("SUBJECTS",
                        Subject.subjectOnCursor,
                        "_id = ?",
                        new String[] {Integer.toString(getIntent().getIntExtra(EXTRA_ID, 0))},
                        null, null, null);

                if(cursor.moveToFirst()) {
                    editSubject = new Subject(cursor);
                    editTextName.setText(editSubject.getName());
                    editTextTeacher.setText(editSubject.getTeacher());
                    editTextUnpreparedness.setText(Integer.toString(editSubject.getUnpreparedness()));
                    editTextDescription.setText(editSubject.getDescription());
                    textViewEditAssessmentTitle.setText(R.string.edit_edit_assessment);
                    textViewEditAssessment.setText(editSubject.getStringAssessments());
                    textViewCurrentAssessment.setText(editSubject.getStringAssessments());
                    getActionBar().setTitle(R.string.edit_subject);
                    viewCurrentAssessmentBox.setVisibility(View.VISIBLE);
                    buttonSave.setText(R.string.save);
                    buttonDelete.setVisibility(View.VISIBLE);
                    buttonDuplicate.setVisibility(View.VISIBLE);
                }

                cursor.close();
                db.close();
            } catch (SQLiteException e){
                Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
            }

        } else {
            editSubject = new Subject(0, "", "", "", 0, "");
        }
    }

    public void ClickPlus(View v){
        if(editTextAssessment.getText().toString().trim().equals("")){
            Toast.makeText(this, R.string.hint_assessment, Toast.LENGTH_SHORT).show();
        } else {
            editSubject.getAssessments().add(Float.parseFloat(editTextAssessment.getText().toString().trim()));
            textViewEditAssessment.setText(editSubject.getStringAssessments());
        }
    }

    public void ClickMinus(View v){
        Float assessment = Float.parseFloat(editTextAssessment.getText().toString().trim());
        if(editTextAssessment.getText().toString().trim().equals("")) {
            Toast.makeText(this, R.string.hint_assessment, Toast.LENGTH_SHORT).show();
        } else if(editSubject.getAssessments().size() == 0) {
            Toast.makeText(this, R.string.subject_null_assessments, Toast.LENGTH_SHORT).show();
        } else if (!editSubject.getAssessments().remove(assessment)) {
            Toast.makeText(this, R.string.subject_null_this_assessment, Toast.LENGTH_SHORT).show();
        } else {
            textViewEditAssessment.setText(editSubject.getStringAssessments());
        }
    }

    public void ClickAllMinus(View v){
        editSubject.getAssessments().clear();
        textViewEditAssessment.setText(editSubject.getStringAssessments());
    }

    public void ClickDuplicateSubject(View v){
        try {
            SQLiteOpenHelper openHelper = new HelperDatabase(this);
            SQLiteDatabase db = openHelper.getWritableDatabase();
            db.insert("SUBJECTS", null, editSubject.saveSubject(this));
            db.close();
            Toast.makeText(this, R.string.edit_duplicate_subject_made, Toast.LENGTH_SHORT).show();
        } catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    public void ClickDeleteSubject(View v){
        try {
            SQLiteOpenHelper openHelper = new HelperDatabase(this);
            SQLiteDatabase db = openHelper.getWritableDatabase();
            db.delete("SUBJECTS",
                    "_id = ?",
                    new String[] {Integer.toString(editSubject.getId())});
            db.delete("NOTES",
                    "TAB_SUBJECT = ?",
                    new String[] {Integer.toString(editSubject.getId())});
            db.delete("LESSON_PLAN",
                    "TAB_SUBJECT = ?",
                    new String[] {Integer.toString(editSubject.getId())});
            db.close();
            finish();
        } catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    public void ClickSave(View v){
        if(editTextName.getText().toString().trim().equals("")){
            Toast.makeText(this, R.string.edit_hint_name, Toast.LENGTH_SHORT).show();
            return;
        }

        if(editTextUnpreparedness.getText().toString().trim().equals("")){
            editTextUnpreparedness.setText(Integer.toString(0));
        }

        editSubject.setName(editTextName.getText().toString().trim());
        editSubject.setTeacher(editTextTeacher.getText().toString().trim());
        editSubject.setUnpreparedness(Integer.parseInt(editTextUnpreparedness.getText().toString().trim()));
        editSubject.setDescription(editTextDescription.getText().toString().trim());

        try {
            SQLiteOpenHelper openHelper = new HelperDatabase(this);
            SQLiteDatabase db = openHelper.getWritableDatabase();

            if(editSubject.getId() == 0) {
                db.insert("SUBJECTS",null, editSubject.saveSubject(this));
            } else {
                db.update("SUBJECTS",
                        editSubject.saveSubject(this),
                        "_id = ?",
                        new String[] {Integer.toString(editSubject.getId())});
            }
            db.close();
        } catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public void ClickCancel(View v){
        finish();
    }
}
