package pl.Guzooo.DziennikUcznia;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AssessmentOptionsFragment extends DialogFragment {

    private static final String PREFERENCE_NAME = "assessmentoptions";
    private static final String PREFERENCE_AUTO_SHOW = "autoshow";

    private SubjectAssessment subjectAssessment;
    private boolean insert;

    private ListenerDismiss listenerDismiss;

    private SQLiteDatabase db;

    private Spinner categorySpinner;
    private Cursor categoryCursor;
    private AdapterSpinnerCategoryOfAssessment categoryAdapter;

    private TextAndHoldEditView assessment;
    private TextAndHoldEditView weight;
    private TextView textViewData;
    private TextAndHoldEditView description;

    public interface ListenerDismiss{
        void Refresh();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        db = Database2020.getToReading(getContext());
        View layout = getActivity().getLayoutInflater().inflate(R.layout.fragment_assessment_options, null);
        SetAutoShowChecked(layout);
        SetDeleteIcon(layout);
        SetTitle(layout);
        SetEditIcon(layout);
        SetSpinner(layout);
        SetAssessment(layout);
        SetWeight(layout);
        SetData(layout);
        SetDescription(layout);

        return new AlertDialog.Builder(getContext())
                .setView(layout)
                .setPositiveButton(getPositiveButtonText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PositiveButton();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    private int getPositiveButtonText(){
        if (insert)
            return R.string.add;
        return R.string.ok;
    }

    private void PositiveButton(){
        assessment.EndEdition();
        weight.EndEdition();
        description.EndEdition();
        if(assessment.getText().equals("")){
            String text = getString(R.string.cant_save) + getString(R.string.separation) + getString(R.string.hint_assessment);
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        } else {
            if(weight.getText().equals(""))
                weight.setText("1");
            subjectAssessment.setAssessment(Float.parseFloat(assessment.getText()));
            subjectAssessment.setWeight(Integer.valueOf(weight.getText()));
            subjectAssessment.setNote(description.getText().trim());
            if(insert)
                subjectAssessment.insert(getContext());
            else
                subjectAssessment.update(getContext());
            listenerDismiss.Refresh();
        }
    }

    public void show(SubjectAssessment subjectAssessment, ListenerDismiss listenerDismiss, boolean insert, FragmentManager manager, String tag) {
        super.show(manager, tag);
        this.subjectAssessment = subjectAssessment;
        this.listenerDismiss = listenerDismiss;
        this.insert = insert;
    }

    private void ClickDelete(){
        subjectAssessment.delete(getContext());
        listenerDismiss.Refresh();
        dismiss();
    }

    private void ClickEdit(){
        Toast.makeText(getContext(), R.string.hold_to_edit, Toast.LENGTH_SHORT).show();
    }

    private void SetAutoShowChecked(View v){
        final CheckBox checkBox = v.findViewById(R.id.assessment_option_auto_show);
        checkBox.setChecked(getPreferenceAutoShow(getContext()));

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPreferenceAutoShow(checkBox.isChecked(), getContext());
            }
        });
    }

    private void SetDeleteIcon(View v){
        v.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickDelete();
            }
        });
    }

    private void SetTitle(View v){
        TextView textViewOne = v.findViewById(R.id.title);
        textViewOne.setText(getContext().getString(R.string.semester, StatisticsActivity.getSemester(getContext())));
    }

    private void SetEditIcon(View v){
        v.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickEdit();
            }
        });
    }

    private void SetSpinner(View v){
        categorySpinner = v.findViewById(R.id.spinner_category);
        RefreshCategoryCursor();
        SetCategoryAdapter();
        categorySpinner.setSelection(getCurrentCategory());
    }

    private void RefreshCategoryCursor(){
        categoryCursor = db.query("CATEGORY_ASSESSMENT",
            CategoryAssessment.onCursor,
            null, null, null, null, null);
    }

    private void SetCategoryAdapter(){
        categoryAdapter = new AdapterSpinnerCategoryOfAssessment(getContext(), categoryCursor);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Long lo = categoryAdapter.getItemId(i);
                subjectAssessment.setCategoryId(lo.intValue());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private int getCurrentCategory(){
        if(categoryCursor.moveToFirst())
            for(int i = 0; i < categoryCursor.getCount(); i ++, categoryCursor.moveToNext()) {
                if (categoryCursor.getInt(0) == subjectAssessment.getCategoryId())
                    return i;
            }
        return 0;
    }

    private void SetAssessment(View v){
        assessment = v.findViewById(R.id.assessment);
        assessment.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        assessment.setText(subjectAssessment.getAssessment() + "");
    }

    private void SetWeight(View v){
        weight = v.findViewById(R.id.weight);
        SharedPreferences preferences = getContext().getSharedPreferences(SettingActivity.PREFERENCE_NAME, Context.MODE_PRIVATE);
        if(!preferences.getBoolean(SettingActivity.PREFERENCE_AVERAGE_WEIGHT, SettingActivity.DEFAULT_AVERAGE_WEIGHT))
            weight.setVisibility(View.GONE);
        weight.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        weight.setText(subjectAssessment.getWeight() + "");
        //TODO:set params edit texta
    }

    private void SetData(View v){
        View view = v.findViewById(R.id.data_container);
        textViewData = v.findViewById(R.id.data);
        String data = subjectAssessment.getData();
        setDataText(subjectAssessment.getData());
        if(subjectAssessment.getData().equals("")){
            data = SubjectAssessment.getToday();
        }
        String[] strings = data.split("/");

        final ArrayList<Integer> ints = new ArrayList<>();
        for(String s : strings)
            ints.add(Integer.valueOf(s));


        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                       subjectAssessment.setData(i2 + "/" + (i1+1) + "/" + i);
                       setDataText(subjectAssessment.getData());
                    }
                };
                new DatePickerDialog(getContext(), onDateSetListener, ints.get(2), ints.get(1)-1, ints.get(0)).show();
                return true;
            }
        });
    }

    private void setDataText(String s){
       textViewData.setText(s);
    }

    private void SetDescription(View v){
        description = v.findViewById(R.id.description);
        description.setText(subjectAssessment.getNote());
    }

    public static void setPreferenceAutoShow (boolean autoShow, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(PREFERENCE_AUTO_SHOW, autoShow);
        editor.apply();
    }

    public static boolean getPreferenceAutoShow(Context context){
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getBoolean(PREFERENCE_AUTO_SHOW, true);
    }
}
