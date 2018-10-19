package pl.Guzooo.DziennikUcznia;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class LessonPlanEditActivity extends Activity {

    public static final String EXTRA_ID = "id";

    private SubjectPlan editSubjectPlan;

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
        Button buttonSave = findViewById(R.id.plan_edit_save);

        timePickerStart.setIs24HourView(true);
        timePickerEnd.setIs24HourView(true);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.week, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(adapter);

        try {
            SQLiteOpenHelper openHelper = new HelperDatabase(this);
            db = openHelper.getReadableDatabase();
            cursor = db.query("SUBJECTS",
                    Subject.subjectOnCursor,
                    null, null, null, null, null);

            AdapterSubjectSpinner adapterSubjectSpinner = new AdapterSubjectSpinner(this, cursor);
            spinnerSubject.setAdapter(adapterSubjectSpinner);
        } catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }

        if(getIntent().getIntExtra(EXTRA_ID, 0) != 0){
            try{
                SQLiteOpenHelper openHelper = new HelperDatabase(this);
                SQLiteDatabase db = openHelper.getReadableDatabase();
                Cursor cursor = db.query("LESSON_PLAN",
                        SubjectPlan.subjectPlanOnCursor,
                        "_id = ?",
                        new String[] {Integer.toString(getIntent().getIntExtra(EXTRA_ID, 0))},
                        null, null, null);

                if(cursor.moveToFirst()){
                    editSubjectPlan = new SubjectPlan(cursor);
                    spinnerSubject.setSelection(getPosition(editSubjectPlan.getIdSubject(), this.cursor));
                    spinnerDay.setSelection(editSubjectPlan.getDay());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        timePickerStart.setHour(editSubjectPlan.getTimeStartHours());
                        timePickerStart.setMinute(editSubjectPlan.getTimeStartMinutes());
                        timePickerEnd.setHour(editSubjectPlan.getTimeEndHours());
                        timePickerEnd.setMinute(editSubjectPlan.getTimeEndMinutes());
                    } else {
                        timePickerStart.setCurrentHour(editSubjectPlan.getTimeStartHours());
                        timePickerStart.setCurrentMinute(editSubjectPlan.getTimeStartMinutes());
                        timePickerEnd.setCurrentHour(editSubjectPlan.getTimeEndHours());
                        timePickerEnd.setCurrentMinute(editSubjectPlan.getTimeEndMinutes());
                    }

                    editTextClassroom.setText(editSubjectPlan.getClassroom());
                    buttonSave.setText(R.string.save);
                    getActionBar().setTitle(R.string.lesson_plan_edit);
                }
            } catch (SQLiteException e){
                Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
            }
        } else {
            editSubjectPlan = new SubjectPlan();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(editSubjectPlan.getId() != 0){
            getMenuInflater().inflate(R.menu.lesson_plan_edit_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.action_trash:
                deletePlan();
                finish();

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

        editSubjectPlan.setIdSubject((int) spinnerSubject.getSelectedItemId());
        editSubjectPlan.setDay((int) spinnerDay.getSelectedItemId());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            editSubjectPlan.setTimeStart(editSubjectPlan.convertFromTime(timePickerStart.getHour(), timePickerStart.getMinute()));
            editSubjectPlan.setTimeEnd(editSubjectPlan.convertFromTime(timePickerEnd.getHour(), timePickerEnd.getMinute()));
        } else {
            editSubjectPlan.setTimeStart(editSubjectPlan.convertFromTime(timePickerStart.getCurrentHour(), timePickerStart.getCurrentMinute()));
            editSubjectPlan.setTimeEnd(editSubjectPlan.convertFromTime(timePickerEnd.getCurrentHour(), timePickerEnd.getCurrentMinute()));
        }

        editSubjectPlan.setClassroom(editTextClassroom.getText().toString().trim());

        try {
            SQLiteOpenHelper openHelper = new HelperDatabase(this);
            SQLiteDatabase db = openHelper.getWritableDatabase();

            if(editSubjectPlan.getId() == 0){
                db.insert("LESSON_PLAN", null, editSubjectPlan.saveSubjectPlan());
            } else {
                db.update("LESSON_PLAN",
                        editSubjectPlan.saveSubjectPlan(),
                        "_id = ?",
                        new String[] {Integer.toString(editSubjectPlan.getId())});
            }
            currentSubject(db);
            db.close();
        }   catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public void ClickCancel(View v){
        finish();
    }

    private void deletePlan(){
        try {
            SQLiteOpenHelper openHelper = new HelperDatabase(this);
            SQLiteDatabase db = openHelper.getWritableDatabase();
            db.delete("LESSON_PLAN",
                    "_id = ?",
                    new String[] {Integer.toString(editSubjectPlan.getId())});

            currentSubject(db);

            db.close();
        } catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    private void currentSubject(SQLiteDatabase db){
        try {
            Cursor cursor = db.query("SUBJECTS",
                    Subject.subjectOnCursor,
                    "_id = ?",
                    new String[]{Integer.toString(editSubjectPlan.getIdSubject())},
                    null, null, null);

            if (cursor.moveToFirst()) {
                Subject subject = new Subject(cursor);

                db.update("SUBJECTS",
                        subject.saveSubject(this),
                        "_id = ?",
                        new String[]{Integer.toString(subject.getId())});
            }
            cursor.close();
        }catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    private int getPosition(int id, Cursor cursor){
        int position = 0;
        if(cursor.moveToFirst()){
            do{
                position++;
                if(new Subject(cursor).getId() == id){
                    return position;
                }
            }while (cursor.moveToNext());
        }
        return position;
    }
}
