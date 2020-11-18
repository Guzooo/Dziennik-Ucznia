package pl.Guzooo.DziennikUcznia;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddElementOfPlanFragment extends DialogFragment {
    private final String TAG = "ADD_ELEMENT_OF_PLAN";

    private String timeLessonStart = "08:00";

    private Spinner subject;
    private Spinner day;
    private TextView lessonStart;
    private TextView lessonEnd;
    private EditText classroom;

    private SQLiteDatabase db;
    private Cursor cursor;

    private ArrayAdapter<CharSequence> dayAdapter;

    private MainMenuInsertListener insertListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View layout = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_element_of_plan, null);
        initialization(layout);
        setOnClickLessonTime(layout);
        setLessonTime();
        try{
            setData();
            setSubjectAdapter();
        } catch (SQLiteException e){
            Database2020.errorToast(getContext());
        }
        setDayAdapter();
        return getAlertDialog(layout);
    }

    public void show(MainMenuInsertListener insertListener, FragmentManager manager){
        super.show(manager, TAG);
        this.insertListener = insertListener;
    }

    public void show(MainMenuInsertListener insertListener, String timeLessonStart, FragmentManager manager){
        super.show(manager, TAG);
        this.insertListener = insertListener;
        this.timeLessonStart = timeLessonStart;
    }

    private void initialization(View v){
        subject = v.findViewById(R.id.subject);
        day = v.findViewById(R.id.day);
        lessonStart = v.findViewById(R.id.lesson_start_text);
        lessonEnd = v.findViewById(R.id.lesson_end_text);
        classroom = v. findViewById(R.id.classroom);
        db = Database2020.getToReading(getContext());
    }

    private void setOnClickLessonTime(View v){
        View.OnClickListener onLessonStartClick = getOnLessonTimeClickListener(lessonStart);
        View.OnClickListener onLessonEndClick = getOnLessonTimeClickListener(lessonEnd);
        v.findViewById(R.id.lesson_start).setOnClickListener(onLessonStartClick);
        v.findViewById(R.id.lesson_end).setOnClickListener(onLessonEndClick);
    }

    private void setLessonTime(){
        lessonStart.setText(timeLessonStart);
        autoSetLessonEndTime();
    }

    private void setData(){
        setCursor();
    }

    private void setSubjectAdapter(){
        AdapterSubjectSelect adapter = new AdapterSubjectSelect(getContext(), cursor);
        subject.setAdapter(adapter);
    }

    private void setDayAdapter(){
        dayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.week, android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        day.setAdapter(dayAdapter);
    }

    private AlertDialog getAlertDialog(View layout){
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.add_element_of_lesson_plan_title)
                .setView(layout)
                .setPositiveButton(android.R.string.ok, getInsertDialogListener())
                .setNegativeButton(android.R.string.cancel, null)
                .setNeutralButton(R.string.next, getNextDialogListener())
                .create();
    }

    private View.OnClickListener getOnLessonTimeClickListener(final TextView textTime){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeRead = textTime.getText().toString();
                int[] time = UtilsCalendar.getTimeToOperating(timeRead);
                new TimePickerDialog(getContext(), getTimeSetListener(textTime),
                                    time[0], time[1], true)
                                        .show();
            }
        };
    }

    private TimePickerDialog.OnTimeSetListener getTimeSetListener(final TextView textTime){
        return new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String timeRead = UtilsCalendar.getTimeToRead(hourOfDay, minute);
                textTime.setText(timeRead);
                if (isLessonStartTime(textTime))
                    autoSetLessonEndTime();
            }
        };
    }

    private boolean isLessonStartTime(TextView textView){
        if(textView == lessonStart)
            return true;
        return false;
    }

    private void autoSetLessonEndTime(){
        String startTime = lessonStart.getText().toString();
        int[] time = UtilsCalendar.getTimeToOperating(startTime);
        time[1] += 45;
        if(time[1] >= 60){
            time[0]++;
            time[1] -= 60;
        }
        String endTime = UtilsCalendar.getTimeToRead(time[0], time[1]);
        lessonEnd.setText(endTime);
    }

    private DialogInterface.OnClickListener getInsertDialogListener(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                insertElement();
            }
        };
    }

    private DialogInterface.OnClickListener getNextDialogListener(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                insertElement();
                dismiss();
                int[] start = UtilsCalendar.getTimeToOperating(lessonEnd.getText().toString());
                start[1] += 5;
                if(start[1] >= 60){
                    start[0]++;
                    start[1] -= 60;
                }
                String newStart = UtilsCalendar.getTimeToRead(start[0], start[1]);
                show(insertListener, newStart, getFragmentManager());
            }
        };
    }

    private void insertElement(){
        if(!canSave())
            return;
        ElementOfPlan2020 element = new ElementOfPlan2020();
        element.setIdSubject((int) subject.getSelectedItemId());
        int dayPosition = day.getSelectedItemPosition();
        int day = UtilsCalendar.getDayOfWeek((String) dayAdapter.getItem(dayPosition), getContext());
        element.setDay(day);
        int[] start = UtilsCalendar.getTimeToOperating(lessonStart.getText().toString());
        int[] end = UtilsCalendar.getTimeToOperating(lessonEnd.getText().toString());
        element.setTimeStart(start[0], start[1]);
        element.setTimeEnd(end[0], end[1]);
        element.setClassroom(UtilsEditText.getString(classroom));
        element.insert(getContext());
        insertListener.beforeInsert();
    }

    private boolean canSave(){
        if(subject.getSelectedItemId() == 0){
            String text = getString(R.string.cant_save)
                    + getString(R.string.separator)
                    + getString(R.string.plan_edit_hint_subject);
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            return false;
        } else if(day.getSelectedItemId() == 0) {
            String text = getString(R.string.cant_save)
                    + getString(R.string.separator)
                    + getString(R.string.select_day_hint);
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setCursor(){
        cursor = db.query(Subject2020.DATABASE_NAME,
                Subject2020.ON_CURSOR,
                null, null, null, null,
                Subject2020.NAME);
    }
}
