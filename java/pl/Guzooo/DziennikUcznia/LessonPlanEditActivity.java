package pl.Guzooo.DziennikUcznia;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class LessonPlanEditActivity extends Activity {

    public static final String EXTRA_ID = "id";

    private SubjectPlan subjectPlan;

    private EditText editTextClassroom;
    private Spinner spinnerSubject;
    private Spinner spinnerDay;
    private TimePicker timePickerStart;
    private TimePicker timePickerEnd;

    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_plan_edit);

        timePickerStart = findViewById(R.id.plan_edit_time_start);
        timePickerEnd = findViewById(R.id.plan_edit_time_end);
        spinnerSubject = findViewById(R.id.plan_edit_subject);
        spinnerDay = findViewById(R.id.plan_edit_day);
        editTextClassroom = findViewById(R.id.plan_edit_classroom);

        timePickerStart.setIs24HourView(true);
        timePickerEnd.setIs24HourView(true);

        try {
            db = DatabaseUtils.getReadableDatabase(this);
            cursorForSpinnerSubject();
            setAdapterForSpinnerSubject();
            setAdapterForSpinnerDay();

            if(getIntent().getIntExtra(EXTRA_ID, 0) == 0){
                newSubjectPlan();
            } else {
                readSubjectPlan();
            }
        } catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }

        goFirstChangeView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(subjectPlan.getId() != 0){
            getMenuInflater().inflate(R.menu.lesson_plan_edit_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.action_trash:
                deletePlan();
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

    public void ClickSave(View v){
        if(spinnerSubject.getSelectedItemId() == 0){
            Toast.makeText(this, R.string.plan_edit_hint_subject, Toast.LENGTH_SHORT).show();
            return;
        }

        if(spinnerDay.getSelectedItemId() == 0){
            Toast.makeText(this, R.string.week_hint, Toast.LENGTH_SHORT).show();
            return;
        }

        subjectPlan.setIdSubject((int) spinnerSubject.getSelectedItemId());
        subjectPlan.setDay((int) spinnerDay.getSelectedItemId());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            subjectPlan.setTimeStart(subjectPlan.convertFromTime(timePickerStart.getHour(), timePickerStart.getMinute()));
            subjectPlan.setTimeEnd(subjectPlan.convertFromTime(timePickerEnd.getHour(), timePickerEnd.getMinute()));
        } else {
            subjectPlan.setTimeStart(subjectPlan.convertFromTime(timePickerStart.getCurrentHour(), timePickerStart.getCurrentMinute()));
            subjectPlan.setTimeEnd(subjectPlan.convertFromTime(timePickerEnd.getCurrentHour(), timePickerEnd.getCurrentMinute()));
        }

        subjectPlan.setClassroom(editTextClassroom.getText().toString().trim());

        if(subjectPlan.getId() == 0){
            subjectPlan.insert(this);
        } else {
            subjectPlan.update(this);
        }
        currentSubject();
        finish();
    }

    public void ClickCancel(View v){
        finish();
    }

    private void goFirstChangeView(){
        final View view = findViewById(R.id.plan_edit_scroll_view);
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();

        if (viewTreeObserver.isAlive()){
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    View bottomButtons = findViewById(R.id.plan_edit_bottom_buttons);
                    view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), bottomButtons.getHeight());
                }
            });
        }
    }

    private void cursorForSpinnerSubject(){
        cursor = db.query("SUBJECTS",
                Subject.subjectOnCursor,
                null, null, null, null,
                "NAME");
    }

    private void setAdapterForSpinnerSubject(){
        AdapterSubjectSpinner adapterSubjectSpinner = new AdapterSubjectSpinner(this, cursor);
        spinnerSubject.setAdapter(adapterSubjectSpinner);
    }

    private void setAdapterForSpinnerDay(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.week, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(adapter);
    }

    private void newSubjectPlan(){
        subjectPlan =  SubjectPlan.newEmpty();
    }

    private void readSubjectPlan(){
        Button buttonSave = findViewById(R.id.plan_edit_save);

        subjectPlan = SubjectPlan.getOfId(getIntent().getIntExtra(EXTRA_ID, 0), this);

        spinnerSubject.setSelection(getPosition(subjectPlan.getIdSubject(), cursor));
        spinnerDay.setSelection(subjectPlan.getDay());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePickerStart.setHour(subjectPlan.getTimeStartHours());
            timePickerStart.setMinute(subjectPlan.getTimeStartMinutes());
            timePickerEnd.setHour(subjectPlan.getTimeEndHours());
            timePickerEnd.setMinute(subjectPlan.getTimeEndMinutes());
        } else {
            timePickerStart.setCurrentHour(subjectPlan.getTimeStartHours());
            timePickerStart.setCurrentMinute(subjectPlan.getTimeStartMinutes());
            timePickerEnd.setCurrentHour(subjectPlan.getTimeEndHours());
            timePickerEnd.setCurrentMinute(subjectPlan.getTimeEndMinutes());
        }

        editTextClassroom.setText(subjectPlan.getClassroom());
        buttonSave.setText(R.string.save);
        getActionBar().setTitle(R.string.lesson_plan_edit);
    }

    private void deletePlan(){
        InterfaceUtils.getAlert(this)
                .setPositiveButton(R.string.yes, new AlertDialog.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        subjectPlan.delete(getApplicationContext());
                        currentSubject();
                        finish();
                    }
                })
                .show();
    }

    private void currentSubject(){
        Subject subject = Subject.getOfId(subjectPlan.getIdSubject(), this);
        subject.putInfoDay(this);
        subject.update(this);
    }

    private int getPosition(int id, Cursor cursor){
        int position = 0;
        if(cursor.moveToFirst()){
            do{
                position++;
                if(Subject.getOfCursor(cursor).getId() == id){
                    return position;
                }
            }while (cursor.moveToNext());
        }
        return position;
    }
}
