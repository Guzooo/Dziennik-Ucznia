package pl.Guzooo.DziennikUcznia;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
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

        goFirstChangeView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(subject.getId() != 0) {
            getMenuInflater().inflate(R.menu.edit_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_trash:
                deleteSubject();
                return true;

            default:
                return super.onOptionsItemSelected(item);

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

    public void ClickDuplicateSubject(View v){
        if(subject.duplicate(this)) {
            Toast.makeText(this, R.string.edit_duplicate_subject_made, Toast.LENGTH_SHORT).show();
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

        subject.setName(editTextName.getText().toString().trim());
        subject.setTeacher(editTextTeacher.getText().toString().trim());
        subject.setUnpreparedness(Integer.parseInt(editTextUnpreparedness.getText().toString().trim()));
        subject.setDescription(editTextDescription.getText().toString().trim());

        if(subject.getId() == 0) {
            subject.insert(this);
        } else {
            subject.update(this);
        }
        finish();
    }

    public void ClickCancel(View v){
        finish();
    }

    private void goFirstChangeView(){
        final View view = findViewById(R.id.edit_scroll_view);
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();

        if (viewTreeObserver.isAlive()){
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    View bottomButtons = findViewById(R.id.edit_bottom_buttons);
                    view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), bottomButtons.getHeight());
                }
            });
        }
    }

    private void newSubject(){
        subject = Subject.newEmpty();
    }

    private void readSubject(){
        TextView textViewEditAssessmentTitle = findViewById(R.id.edit_edit_assessment_title);
        TextView textViewCurrentAssessment = findViewById(R.id.edit_current_assessment);
        View viewCurrentAssessmentBox = findViewById(R.id.edit_current_assessment_box);
        Button buttonSave = findViewById(R.id.edit_save);
        Button buttonDuplicate = findViewById(R.id.edit_duplicate_subject);

        subject = Subject.getOfId(getIntent().getIntExtra(EXTRA_ID, 0), this);

        getActionBar().setTitle(R.string.edit_subject);
        editTextName.setText(subject.getName());
        editTextTeacher.setText(subject.getTeacher());
        editTextDescription.setText(subject.getDescription());
        viewCurrentAssessmentBox.setVisibility(View.VISIBLE);
        textViewEditAssessmentTitle.setText(R.string.edit_edit_assessment);
        textViewEditAssessment.setText(subject.getStringAssessments(this));
        textViewCurrentAssessment.setText(subject.getStringAssessments(this));
        editTextUnpreparedness.setText(Integer.toString(subject.getUnpreparedness()));
        buttonSave.setText(R.string.save);
        buttonDuplicate.setVisibility(View.VISIBLE);
    }

    public void deleteSubject(){
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
}
