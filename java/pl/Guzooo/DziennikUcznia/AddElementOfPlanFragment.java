package pl.Guzooo.DziennikUcznia;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class AddElementOfPlanFragment extends DialogFragment {
    private final String TAG = "ADD_ELEMENT_OF_PLAN";

    private Spinner subject;
    private Spinner day;
    private TextView lessonStart;
    private TextView lessonEnd;
    private EditText classroom;

    private MainMenuInsertListener insertListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View layout = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_element_of_plan, null);
        initialization(layout);
        setOnClickLessonTime(layout);
        setLessonTime();
        return getAlertDialog(layout);
    }

    public void show(MainMenuInsertListener insertListener, FragmentManager manager){
        super.show(manager, TAG);
        this.insertListener = insertListener;
    }

    private void initialization(View v){
        subject = v.findViewById(R.id.subject);
        day = v.findViewById(R.id.day);
        lessonStart = v.findViewById(R.id.lesson_start_text);
        lessonEnd = v.findViewById(R.id.lesson_end_text);
        classroom = v. findViewById(R.id.classroom);
    }

    private void setOnClickLessonTime(View v){
        View.OnClickListener onLessonStartClick = getOnLessonTimeClickListener(lessonStart);
        View.OnClickListener onLessonEndClick = getOnLessonTimeClickListener(lessonEnd);
        v.findViewById(R.id.lesson_start).setOnClickListener(onLessonStartClick);
        v.findViewById(R.id.lesson_end).setOnClickListener(onLessonEndClick);
    }

    private void setLessonTime(){
        lessonStart.setText("8:00");
        lessonEnd.setText("8:45");
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
                show(insertListener, getFragmentManager());
            }
        };
    }

    private void insertElement(){

    }
}
