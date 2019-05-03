package pl.Guzooo.DziennikUcznia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Locale;

public class DetailsAndEditActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "id";

    private Subject subject;

    private EditText editTextAssessment;
    private TextAndHoldEditView textAndHoldEditViewTeacher;
    private TextAndHoldEditView textAndHoldEditViewUnpreparedness;
    private TextAndHoldEditView textAndHoldEditViewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_and_edit);

        textAndHoldEditViewTeacher = findViewById(R.id.teacher);
        textAndHoldEditViewUnpreparedness = findViewById(R.id.unpreparedness);
        textAndHoldEditViewDescription = findViewById(R.id.description);

        subject = Subject.getOfId(getIntent().getIntExtra(EXTRA_ID, 0), this);

        refreshActionBarInfo();
        setNotes();
        setTeacher();
        setAssessments();
        setUnpreparedness();
        setDescription();
    }

    @Override
    protected void onRestart() {
        super.onRestart();


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

                return true;

            case R.id.action_del:

                return true;

            case R.id.action_rename:
                //TODO: w xml weż na stringa ;)
                return true;

            case R.id.action_duplicate:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setNotes(){

    }

    private void setTeacher(){
        textAndHoldEditViewTeacher.setText(subject.getTeacher());
    }

    private void setAssessments(){

    }

    private void setUnpreparedness() {
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

    private void setDescription(){
        textAndHoldEditViewDescription.setText(subject.getDescription());
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
            return strAverage + getResources().getString(R.string.separation) + Integer.toString(subject.getRoundedAverageEnd(assessments1, assessments2,sharedPreferences)); //TODO: unnecessary delete ;)
        else
            return strAverage;
    }
}
