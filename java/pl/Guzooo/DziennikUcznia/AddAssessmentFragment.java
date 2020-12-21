package pl.Guzooo.DziennikUcznia;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class AddAssessmentFragment extends DialogFragment {
    private final String TAG = "ADD_ASSESSMENT";

    private Assessment2020 assessmentObj;
    private SQLiteDatabase db;

    private CheckBox autoShowBeforeAdd;
    private View deleteIcon;
    private TextView title;
    private Spinner categories;
    private HoldEditText assessment;
    private HoldEditText weight;
    private View dateContainer;
    private View dateEdit;
    private TextView dateText;
    private HoldEditText description;

    private Cursor categoriesCursor;
    private AdapterSpinnerCategoryOfAssessment categoryAdapter;

    private OnAssessmentChangeDataListener listener;

    public interface OnAssessmentChangeDataListener {
        void mustRefreshData();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View layout = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_assessment, null);
        if(assessmentObj == null){
            dismiss();
            assessmentObj = new Assessment2020();
        }
        initialization(layout);
        setAutoShowBeforeAdd();
        setDeleteIcon();
        setTitle();
        setAssessment();
        setWeight();
        setDate();
        setDescription();
        try {
            setCategories();
        } catch (SQLiteException e){
            Database2020.errorToast(getContext());
        }
        return getAlertDialog(layout);
    }

    public void show(Assessment2020 assessment, OnAssessmentChangeDataListener listener, FragmentManager manager){
        super.show(manager, TAG);
        this.assessmentObj = assessment;
        this.listener = listener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        closeDatabaseElements();
    }

    private void initialization(View v){
        db = Database2020.getToReading(getContext());
        autoShowBeforeAdd = v.findViewById(R.id.add_assessment_fragment_auto_show);
        deleteIcon = v.findViewById(R.id.delete);
        title = v.findViewById(R.id.title);
        categories = v.findViewById(R.id.categories);
        assessment = v.findViewById(R.id.assessment);
        weight = v.findViewById(R.id.weight);
        dateContainer = v.findViewById(R.id.date_container);
        dateEdit = v.findViewById(R.id.date_edit);
        dateText = v.findViewById(R.id.date_text);
        description = v.findViewById(R.id.description);
    }

    private void setAutoShowBeforeAdd(){
        boolean autoShow = DataManager.isAssessmentWindow(getContext());
        autoShowBeforeAdd.setChecked(autoShow);
        autoShowBeforeAdd.setOnClickListener(getOnClickAutoShowBeforeAddListener());
    }

    private void setDeleteIcon(){
        if(isNewAssessment())
            deleteIcon.setVisibility(View.GONE);
        else
            deleteIcon.setOnClickListener(getOnClickDeleteListener());
    }

    private void setTitle(){
        int semester = DataManager.getSemester(getContext());
        String text = getString(R.string.semester, semester);
        title.setText(text);
    }

    private void setAssessment(){
        assessment.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        String text = assessmentObj.getAssessment() + "";
        assessment.setText(text);
        if(!text.isEmpty())
            assessment.setEmptyValue(text);
    }

    private void setWeight(){
        boolean averageWeight = DataManager.isAverageWeight(getContext());
        if(!averageWeight)
            weight.setVisibility(View.GONE);
        else {
            weight.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
            int weightI = assessmentObj.getWeight();
            String text = "";
            if(weightI != -1)
                text = assessmentObj.getWeight() + "";
            weight.setText(text);
            weight.setDefaultValue(assessmentObj.getRealWeight(getContext())+"");
        }
    }

    private void setDate(){
        String text = assessmentObj.getDate();
        dateText.setText(text);
        dateEdit.setOnClickListener(getOnClickEditDateListener());
        dateContainer.setOnLongClickListener(getOnLongClickDateListener());
        if(!DataManager.isHoldEditTextHelpIcon(getContext()))
            dateEdit.setVisibility(View.GONE);
    }

    private void setDescription(){
        description.getEditText().setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        String text = assessmentObj.getNote();
        description.setText(text);
    }

    private void setCategories(){
        setData();
        setAdapter();
        setSpinner();
    }

    private AlertDialog getAlertDialog(View layout){
        return new AlertDialog.Builder(getContext())
                .setTitle(getDialogTitle())
                .setView(layout)
                .setPositiveButton(android.R.string.ok, getPositiveDialogListener())
                .setNegativeButton(android.R.string.cancel, null)
                .setNeutralButton(getNeutralButton(), getNeutralDialogListener())
                .create();
    }

    private void closeDatabaseElements(){
        categoriesCursor.close();
        db.close();
    }

    private View.OnClickListener getOnClickAutoShowBeforeAddListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean autoShow = autoShowBeforeAdd.isChecked();
                DataManager.setAssessmentWindow(autoShow, getContext());
            }
        };
    }

    private View.OnClickListener getOnClickDeleteListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.want_delete_this_assessment)
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
                assessmentObj.delete(getContext());
                listener.mustRefreshData();
                dismiss();
                Toast.makeText(getContext(), R.string.done_delete_this_assessment, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private View.OnClickListener getOnClickEditDateListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowPickDate();
            }
        };
    }

    private View.OnLongClickListener getOnLongClickDateListener(){
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onShowPickDate();
                return true;
            }
        };
    }

    private void onShowPickDate(){
        int[] date = assessmentObj.getDateElements();
        new DatePickerDialog(getContext(), getOnDateSetListener(), date[2], date[1]-1, date[0]).show();
    }

    private DatePickerDialog.OnDateSetListener getOnDateSetListener(){
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                assessmentObj.setDate(dayOfMonth, month+1, year);
                String text = assessmentObj.getDate();
                dateText.setText(text);
            }
        };
    }

    private void setData(){
        setCursor();
    }

    private void setCursor(){
        categoriesCursor = db.query(CategoryOfAssessment2020.DATABASE_NAME,
                CategoryOfAssessment2020.ON_CURSOR,
                null, null, null, null,
                CategoryOfAssessment2020.NAME);
    }

    private void setAdapter(){
        categoryAdapter = new AdapterSpinnerCategoryOfAssessment(getContext(), categoriesCursor);
    }

    private void setSpinner(){
       categories.setAdapter(categoryAdapter);
       categories.setOnItemSelectedListener(getOnCategorySelectedListener());
       int position = categoryAdapter.getItemPosition(assessmentObj.getIdCategory());
       categories.setSelection(position);
    }

    private AdapterView.OnItemSelectedListener getOnCategorySelectedListener(){
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                assessmentObj.setIdCategory((int) id);
                weight.setDefaultValue(assessmentObj.getDefaultWeight(getContext()) + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private int getDialogTitle(){
        if(isNewAssessment())
            return R.string.title_add_assessment;
        return R.string.title_edit_assessment;
    }

    private DialogInterface.OnClickListener getPositiveDialogListener(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveAssessment();
            }
        };
    }

    private int getNeutralButton(){
        if(isNewAssessment())
            return R.string.next;
        return R.string.next_new;
    }

    private DialogInterface.OnClickListener getNeutralDialogListener(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveAssessment();
                dismiss();
                show(getNewAssessment(), listener, getFragmentManager());
            }
        };
    }

    private Assessment2020 getNewAssessment(){
        Assessment2020 assessment = new Assessment2020();
        assessment.setIdSubject(assessmentObj.getIdSubject());
        assessment.setSemester(assessmentObj.getSemester());
        return assessment;
    }

    private void saveAssessment() {
        closeAllHoldEditText();
        if(!canSave())
            return;
        //TODO w holderach możan metody zwracające floata i inta dodac, bedzie łatwiej;
        assessmentObj.setAssessment(Float.parseFloat(assessment.getText()));//TODO czemu nie value;
        if(weight.getText().equals(""))
            assessmentObj.setWeight(-1);
        else
            assessmentObj.setWeight(Integer.valueOf(weight.getText()));//TODO czemu nie parse XDD;
        assessmentObj.setNote(description.getText());
        if(isNewAssessment())
            assessmentObj.insert(getContext());
        else
            assessmentObj.update(getContext());
        listener.mustRefreshData();
    }

    private void closeAllHoldEditText(){
        assessment.hideEditMode();
        weight.hideEditMode();
        description.hideEditMode();
    }

    private boolean canSave(){
        if(assessment.getText().isEmpty()){
            String text = getString(R.string.cant_save)
                    + getString(R.string.separator)
                    + getString(R.string.assessment_hint);
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isNewAssessment(){
        if(assessmentObj.getId() == 0)
            return true;
        return false;
    }
}
