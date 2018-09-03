package pl.Guzooo.DziennikUcznia;

import android.app.Activity;
import android.content.ContentValues;
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
    public static final String EXTRA_OBJECT = "object";

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
            editSubject = new Subject(getIntent().getStringExtra(EXTRA_OBJECT));
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
        } else {
            editSubject = new Subject("", "", new ArrayList<Float>(), 0, "", new ArrayList<SubjectNote>());
        }
    }

    public void ClickPlus(View v){
        if(editTextAssessment.getText().toString().trim().equals("")){
            Toast.makeText(this, R.string.hint_assessment, Toast.LENGTH_SHORT).show();
        } else {
            editSubject.addAssessment(Float.parseFloat(editTextAssessment.getText().toString().trim()));
            textViewEditAssessment.setText(editSubject.getStringAssessments());
        }
    }

    public void ClickMinus(View v){
        if(editTextAssessment.getText().toString().trim().equals("")) {
            Toast.makeText(this, R.string.hint_assessment, Toast.LENGTH_SHORT).show();
        } else {
            editSubject.removeAssessment(Float.parseFloat(editTextAssessment.getText().toString().trim()), this);
            textViewEditAssessment.setText(editSubject.getStringAssessments());
        }
    }

    public void ClickAllMinus(View v){
        editSubject.removeAllAssessments();
        textViewEditAssessment.setText(editSubject.getStringAssessments());
    }

    public void ClickDuplicateSubject(View v){
        ContentValues subjectValues = new ContentValues();
        subjectValues.put("OBJECT", editSubject.toString());
        subjectValues.put("NOTES", editSubject.sizeSubjectNotes());

        try {
            SQLiteOpenHelper openHelper = new HelperDatabase(this);
            SQLiteDatabase db = openHelper.getWritableDatabase();
            db.insert("SUBJECTS", null, subjectValues);
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
                    new String[] {Integer.toString(getIntent().getIntExtra(EXTRA_ID, 0))});
            db.close();
        } catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public void ClickSave(View v){
        if (editTextName.getText().toString().trim().equals("")){
            Toast.makeText(this, R.string.edit_hint_name, Toast.LENGTH_SHORT).show();
            return;
        }

        if (checkString(editTextName) || checkString(editTextTeacher) || checkString(editTextDescription)){
            Toast.makeText(this, R.string.error_prohibited_sign, Toast.LENGTH_SHORT).show();
            return;
        }

        if(editTextUnpreparedness.getText().toString().trim().equals("")){
            editTextUnpreparedness.setText(Integer.toString(0));
        }

        Subject subject = new Subject(editTextName.getText().toString().trim(), editTextTeacher.getText().toString().trim(), editSubject.getAssessments(), Integer.parseInt(editTextUnpreparedness.getText().toString().trim()), editTextDescription.getText().toString().trim(), editSubject.getSubjectNotes());

        ContentValues subjectValues = new ContentValues();
        subjectValues.put("OBJECT", subject.toString());
        subjectValues.put("NOTES", subject.sizeSubjectNotes());

        try {
            SQLiteOpenHelper openHelper = new HelperDatabase(this);
            SQLiteDatabase db = openHelper.getWritableDatabase();

            if(getIntent().getIntExtra(EXTRA_ID, 0) == 0) {
                db.insert("SUBJECTS",null, subjectValues);
            } else {
                db.update("SUBJECTS",
                        subjectValues,
                        "_id = ?",
                        new String[] {Integer.toString(getIntent().getIntExtra(EXTRA_ID, 0))});
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

    private Boolean checkString(TextView textView){
        Boolean bool = (new String(textView.getText().toString().trim()).indexOf("©") != -1);
        return bool;
    }
}
