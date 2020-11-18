package pl.Guzooo.DziennikUcznia;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LessonPlanEditActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "id";

    private ElementOfPlan2020 subjectPlan;

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
            db = Database2020.getToReading(this);
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
            Toast.makeText(this, R.string.select_day_hint, Toast.LENGTH_SHORT).show();
            return;
        }

        subjectPlan.setIdSubject((int) spinnerSubject.getSelectedItemId());
        subjectPlan.setDay((int) spinnerDay.getSelectedItemId());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            subjectPlan.setTimeStart(timePickerStart.getHour(), timePickerStart.getMinute());
            subjectPlan.setTimeEnd(timePickerEnd.getHour(), timePickerEnd.getMinute());
        } else {
            subjectPlan.setTimeStart(timePickerStart.getCurrentHour(), timePickerStart.getCurrentMinute());
            subjectPlan.setTimeEnd(timePickerEnd.getCurrentHour(), timePickerEnd.getCurrentMinute());
        }

        subjectPlan.setClassroom(editTextClassroom.getText().toString().trim());

        if(subjectPlan.getId() == 0){
            subjectPlan.insert(this);
        } else {
            subjectPlan.update(this);
        }
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
        cursor = db.query(Subject2020.DATABASE_NAME,
                Subject2020.ON_CURSOR,
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
        subjectPlan =  new ElementOfPlan2020();
    }

    private void readSubjectPlan(){
        Button buttonSave = findViewById(R.id.plan_edit_save);

        int id = getIntent().getIntExtra(EXTRA_ID, 0);
        subjectPlan = new ElementOfPlan2020();
        subjectPlan.setVariablesOfId(id, this);

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
        buttonSave.setText(android.R.string.ok);
        getSupportActionBar().setTitle(R.string.lesson_plan_edit);
    }

    private void deletePlan(){
        InterfaceUtils.getAlertDelete(this)
                .setPositiveButton(R.string.yes, new AlertDialog.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        subjectPlan.delete(getApplicationContext());
                        finish();
                    }
                })
                .show();
    }

    private int getPosition(int id, Cursor cursor){
        int position = 0;
        if(cursor.moveToFirst()){
            do{
                Subject2020 subject = new Subject2020();
                subject.setVariablesOfCursor(cursor);
                position++;
                if(subject.getId() == id){
                    return position;
                }
            }while (cursor.moveToNext());
        }
        return position;
    }
}
