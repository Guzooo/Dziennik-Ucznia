package pl.Guzooo.DziennikUcznia;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditActivity extends Activity {

    public static final String EXTRA_ID = "id";

    private Subject subject;

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
        textViewEditAssessment = findViewById(R.id.edit_edit_assessment);

        if(getIntent().getIntExtra(EXTRA_ID, 0) == 0){
            newSubject();
        } else {
            readSubject();
        }
    }

    public void ClickPlus(View v){
        subject.addAssessment(editTextAssessment.getText().toString().trim(), this);
        textViewEditAssessment.setText(subject.getStringAssessments(this));
    }

    public void ClickMinus(View v){
        subject.removeAssessment(editTextAssessment.getText().toString().trim(), this);
        textViewEditAssessment.setText(subject.getStringAssessments(this));
    }

    public void ClickAllMinus(View v){
        subject.getAssessments().clear();
        textViewEditAssessment.setText(subject.getStringAssessments(this));
    }

    public void ClickDuplicateSubject(View v){ //TODO: duplikowanie w Subject
        try {
            SQLiteOpenHelper openHelper = new HelperDatabase(this);
            SQLiteDatabase db = openHelper.getWritableDatabase();
            db.insert("SUBJECTS", null, subject.saveSubject(this));
            db.close();
            Toast.makeText(this, R.string.edit_duplicate_subject_made, Toast.LENGTH_SHORT).show();
        } catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    public void ClickDeleteSubject(View v){
        StaticMethod.getAlert(this)
                .setPositiveButton(R.string.yes, new AlertDialog.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        subject.delete(getApplicationContext());
                        finish();
                    }
                })
                .show();
    }

    public void ClickSave(View v){
        if(editTextName.getText().toString().trim().equals("")){
            Toast.makeText(this, R.string.edit_hint_name, Toast.LENGTH_SHORT).show();
            return;
        }

        if(editTextUnpreparedness.getText().toString().trim().equals("")){
            editTextUnpreparedness.setText(Integer.toString(0));
        }

        subject.setName(editTextName.getText().toString().trim());
        subject.setTeacher(editTextTeacher.getText().toString().trim());
        subject.setUnpreparedness(Integer.parseInt(editTextUnpreparedness.getText().toString().trim()));
        subject.setDescription(editTextDescription.getText().toString().trim());

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

    private void newSubject(){
        subject = new Subject();
    }

    private void readSubject(){
        TextView textViewEditAssessmentTitle = findViewById(R.id.edit_edit_assessment_title);
        TextView textViewCurrentAssessment = findViewById(R.id.edit_current_assessment);
        View viewCurrentAssessmentBox = findViewById(R.id.edit_current_assessment_box);
        Button buttonSave = findViewById(R.id.edit_save);
        Button buttonDelete = findViewById(R.id.edit_delete_subject);
        Button buttonDuplicate = findViewById(R.id.edit_duplicate_subject);

        subject = Subject.getOfId(getIntent().getIntExtra(EXTRA_ID, 0), this);

        editTextName.setText(subject.getName());
        editTextTeacher.setText(subject.getTeacher());
        editTextUnpreparedness.setText(Integer.toString(subject.getUnpreparedness()));
        editTextDescription.setText(subject.getDescription());
        textViewEditAssessmentTitle.setText(R.string.edit_edit_assessment);
        textViewEditAssessment.setText(subject.getStringAssessments(this));
        textViewCurrentAssessment.setText(subject.getStringAssessments(this));
        getActionBar().setTitle(R.string.edit_subject);
        viewCurrentAssessmentBox.setVisibility(View.VISIBLE);
        buttonSave.setText(R.string.save);
        buttonDelete.setVisibility(View.VISIBLE);
        buttonDuplicate.setVisibility(View.VISIBLE);
    }
}
