package pl.Guzooo.DziennikUcznia;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class AddElementOfPlanFragment extends DialogFragment {
    private final String TAG = "ADD_ELEMENT_OF_PLAN";

    private ElementOfPlan2020 elementOfPlan;
    private SQLiteDatabase db;

    private View deleteIcon;
    private Spinner subject;
    private Spinner day;
    private View lessonStart;
    private TextView lessonStartText;
    private View lessonEnd;
    private TextView lessonEndText;
    private HoldEditText classroom;

    private Cursor subjectCursor;
    private AdapterSpinnerSubject subjectAdapter;
    private ArrayAdapter<CharSequence> dayAdapter;

    private MainMenuInsertListener insertListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View layout = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_element_of_plan, null);
        initialization(layout);
        setDeleteIcon();
        setDay();
        setLessonTime();
        setClassroom();
        try {
            setSubject();
        } catch (SQLiteException e){
            Database2020.errorToast(getContext());
        }
        return getAlertDialog(layout);
    }

    public void show(ElementOfPlan2020 elementOfPlan, MainMenuInsertListener insertListener, FragmentManager manager){
        super.show(manager, TAG);
        this.elementOfPlan = elementOfPlan;
        this.insertListener = insertListener;
    }

    private void initialization(View v){
        db = Database2020.getToReading(getContext());
        deleteIcon = v.findViewById(R.id.delete);
        subject = v.findViewById(R.id.subject);
        day = v.findViewById(R.id.day);
        lessonStart = v.findViewById(R.id.lesson_start);
        lessonStartText = v.findViewById(R.id.lesson_start_text);
        lessonEnd = v.findViewById(R.id.lesson_end);
        lessonEndText = v.findViewById(R.id.lesson_end_text);
        classroom = v.findViewById(R.id.classroom);
    }

    private void setDeleteIcon(){
        if(isNewElementOfPlan())
            deleteIcon.setVisibility(View.GONE);
        else
            deleteIcon.setOnClickListener(getOnDeleteClickListener());
    }

    private void setDay(){
        dayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.week, android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        day.setAdapter(dayAdapter);
        day.setSelection(getSelectedDay());
    }

    private void setLessonTime(){
        setLessonTimeOnClick();
        setLessonTimeText();
    }

    private void setClassroom(){
        classroom.getEditText().setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        String text = elementOfPlan.getClassroom();
        classroom.setText(text);
    }

    private void setSubject(){
        setSubjectData();
        setSubjectAdapter();
        setSubjectSpinner();
    }

    private AlertDialog getAlertDialog(View layout){
        return new AlertDialog.Builder(getContext())
                .setTitle(getDialogTitle())
                .setView(layout)
                .setPositiveButton(android.R.string.ok, getPositiveDialogListener())
                .setNegativeButton(android.R.string.cancel, null)
                .setNeutralButton(getNeutralButtonText(), getNeutralDialogListener())
                .create();
    }

    private View.OnClickListener getOnDeleteClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.want_delete_this_element_of_plan)
                        .setPositiveButton(android.R.string.yes, getDeleteListener())
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        };
    }

    private DialogInterface.OnClickListener getDeleteListener(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                elementOfPlan.delete(getContext());
                insertListener.beforeInsert();
                dismiss();
                Toast.makeText(getContext(), R.string.done_delete_this_element_of_plan, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private int getSelectedDay(){
        String dayStr = UtilsCalendar.getDayOfWeek(elementOfPlan.getDay(), getContext());
        for(int i = 0; i < day.getCount(); i++)
            if(day.getItemAtPosition(i).toString().equals(dayStr))
                return i;
        return 0;
    }

    private void setLessonTimeOnClick(){
        View.OnClickListener onLessonStartClick = getOnLessonClickListener(lessonStartText);
        View.OnClickListener onLessonEndClick = getOnLessonClickListener(lessonEndText);
        lessonStart.setOnClickListener(onLessonStartClick);
        lessonEnd.setOnClickListener(onLessonEndClick);
    }

    private View.OnClickListener getOnLessonClickListener(final TextView timeText){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeRead = timeText.getText().toString();
                int[] time = UtilsCalendar.getTimeToOperating(timeRead);
                new TimePickerDialog(getContext(), getTimeSetListener(timeText),
                        time[0], time[1], true)
                        .show();
            }
        };
    }

    private TimePickerDialog.OnTimeSetListener getTimeSetListener(final TextView timeText){
        return new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if(isLessonStartTime(timeText)) {
                    elementOfPlan.setTimeStart(hourOfDay, minute);
                    elementOfPlan.setTimeEndByTimeStart();
                } else
                    elementOfPlan.setTimeEnd(hourOfDay, minute);
                setLessonTimeText();
            }
        };
    }

    private boolean isLessonStartTime(TextView textView){
        if(textView == lessonStartText)
            return true;
        return false;
    }

    private void setLessonTimeText(){
        String start = elementOfPlan.getTimeStart();
        String end = elementOfPlan.getTimeEnd();
        lessonStartText.setText(start);
        lessonEndText.setText(end);
    }

    private void setSubjectData(){
        subjectCursor = db.query(Subject2020.DATABASE_NAME,
                Subject2020.ON_CURSOR,
                null, null, null, null,
                Subject2020.NAME);
    }

    private void setSubjectAdapter(){
        subjectAdapter = new AdapterSpinnerSubject(getContext(), subjectCursor);
    }

    private void setSubjectSpinner(){
        subject.setAdapter(subjectAdapter);
        int position = subjectAdapter.getItemPosition(elementOfPlan.getIdSubject());
        subject.setSelection(position);
    }

    private int getDialogTitle(){
        if(isNewElementOfPlan())
            return R.string.add_element_of_lesson_plan_title;
        return R.string.edit_element_of_lesson_plan_title;
    }

    private DialogInterface.OnClickListener getPositiveDialogListener(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveElementOfPlan();
            }
        };
    }

    private int getNeutralButtonText(){
        if(isNewElementOfPlan())
            return R.string.next;
        return R.string.next_new;
    }

    private DialogInterface.OnClickListener getNeutralDialogListener(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveElementOfPlan();
                dismiss();
                show(getNewElementOfPlan(), insertListener, getFragmentManager());
            }
        };
    }

    private ElementOfPlan2020 getNewElementOfPlan(){
        ElementOfPlan2020 newElement = new ElementOfPlan2020();
        if(isNewElementOfPlan()) {
            newElement.setDay(elementOfPlan.getDay());
            newElement.setTimeStart(elementOfPlan.getTimeEndHours(), elementOfPlan.getTimeEndMinutes() + 5);//TODO: jakaś zmienna w ustawieniach
            newElement.setTimeEndByTimeStart();
        } else {
            newElement.setIdSubject(elementOfPlan.getIdSubject());
            newElement.setClassroom(elementOfPlan.getClassroom());
        }

        return newElement;
    }

    private void saveElementOfPlan(){
        closeAllHoldEditText();
        if(!canSave())
            return;
        elementOfPlan.setIdSubject((int) subject.getSelectedItemId());
        elementOfPlan.setDay(getDayToSave());
        elementOfPlan.setClassroom(UtilsEditText.getString(classroom.getEditText()));
        if(isNewElementOfPlan())
            elementOfPlan.insert(getContext());
        else
            elementOfPlan.update(getContext());
        insertListener.beforeInsert();
    }

    private void closeAllHoldEditText(){
        classroom.hideEditMode();
    }

    private boolean canSave(){
        if(subject.getSelectedItemPosition() == 0){
            String text = getString(R.string.cant_save) + getString(R.string.separator) + getString(R.string.plan_edit_hint_subject);
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(day.getSelectedItemPosition() == 0){
            String text = getString(R.string.cant_save) + getString(R.string.separator) + getString(R.string.select_day_hint);
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private int getDayToSave() {
        String dayStr = day.getSelectedItem().toString();
        return UtilsCalendar.getDayOfWeek(dayStr, getContext());
    }

    private boolean isNewElementOfPlan(){
        if(elementOfPlan.getId() == 0)
            return true;
        return false;
    }
}