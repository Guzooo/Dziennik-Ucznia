package pl.Guzooo.DziennikUcznia;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class SubjectDetailsActivity extends GActivity {

    public static final String EXTRA_ID = "id";

    private final String BUNDLE_VISIBLE_NOTES = "visiblenotes";

    private Subject2020 subject;

    private ChangeTitle changeTitle;
    private HoldEditText teacher;
    private EditText assessment;
    private EditText assessmentWeight;
    private HoldEditText unpreparedness;
    private EditText currentUnpreparedness;
    private EditText startUnpreparedness;
    private HoldEditText description;

    private SQLiteDatabase db;

    private Cursor notesCursor;
    //Adapter
    private RecyclerView notesRecycler;

    private Cursor assessmentsCursor;
    //Adapter;
    private RecyclerView assessmentsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_details);
        initialization();
        loadInstanceState(savedInstanceState);
        setFullScreen();
        setActionBar();
        setChangeTitle();
        setNotes();
        setTeacher();
        setAssessment();
        setUnpreparedness();
        setDescription();

        //TODO: zczytać z zapisu czy otwarte notatki
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //TODO: odswiezyc pasek, oraz oceny i notatki
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.subject_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.edit:
                clickEdit();
                return true;

            case R.id.notes:
                clickNotes();
                return true;

            case R.id.delete:
                clickDelete();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(changeTitle.isVisible())
            changeTitle.hide();
        else if(isVisibilityNotes())
            setVisibilityNotes(false);
        else
            super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_VISIBLE_NOTES, isVisibilityNotes());
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeAllHoldEditText();
        updateSubject();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*notesCursor.close();//TODO: zamknąć to w innej metodzie
        assessmentsCursor.close();*/
        db.close();
    }

    private void initialization(){
        initialSubject();
        db = Database2020.getToWriting(this);
        changeTitle = findViewById(R.id.toolbar_change_title);
        teacher = findViewById(R.id.teacher);
        assessment = findViewById(R.id.assessment_edit_text);
        assessmentWeight = findViewById(R.id.assessment_weight_edit_text);
        initialUnpreparedness();
        description = findViewById(R.id.description);
    }

    private void loadInstanceState(Bundle savedState){
        if(savedState != null) {
            boolean visibilityNotes = savedState.getBoolean(BUNDLE_VISIBLE_NOTES, false);
            setVisibilityNotes(visibilityNotes);
        }
    }

    private void setFullScreen(){
        View nestScroll = findViewById(R.id.nest_scroll);
        UtilsFullScreen.setUIVisibility(nestScroll);
        UtilsFullScreen.setApplyWindowInsets(nestScroll, getWindowsInsetsListener());
        UtilsFullScreen.setPaddings(nestScroll, this);
    }

    private void setActionBar(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(subject.getName());
        getSupportActionBar().setSubtitle(getActionBarSubtitle());
    }

    private void setChangeTitle(){
        changeTitle.moveViewToActionBar(this);
        changeTitle.setDefaultValue(subject.getName());
    }

    private void setNotes(){

    }

    private void setTeacher(){
        String text = subject.getTeacher();
        teacher.setText(text);
        teacher.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
    }

    private void setAssessment(){
        setAssessmentWeight();
        setAssessmentButton();
    }

    private void setUnpreparedness(){
        setUnpreparednessText();
        setUnpreparednessTextAdditional();
        setUnpreparednessSecondEditText();
        setCurrentUnpreparednessHint();
        unpreparedness.setOnHoldEditTextChangeVisibilityListener(getOnUnpreparednessChangeViewListener());
        setUnpreparednessMinusButton();
    }

    private void setDescription(){
        String text = subject.getDescription();
        description.setText(text);
        description.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    }

    private void clickEdit(){
        changeTitle.show();
    }

    private void clickNotes(){

    }

    private void clickDelete(){
        new AlertDialog.Builder(this)//TODO: ogarnij tekstyyy
                .setTitle(getDeleteTitle())
                .setMessage(getDeleteMessage())
                .setPositiveButton(android.R.string.yes, getOnClickPositiveDeleteSubjectListener())
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private boolean isVisibilityNotes(){
        return false;//TODO:ofc zle
    }

    private void setVisibilityNotes(boolean open){

    }

    private void closeAllHoldEditText(){
        teacher.hideEditMode();
        unpreparedness.hideEditMode();
        description.hideEditMode();
    }

    private void updateSubject(){
        String name = getSupportActionBar().getTitle().toString();
        subject.setName(name);
        subject.setTeacher(teacher.getText());
        subject.setDescription(description.getText());
        subject.update(this);
    }

    private void initialSubject(){
        int id = getIntent().getIntExtra(EXTRA_ID, 0);
        subject = new Subject2020();
        subject.setVariablesOfId(id, this);
    }

    private void initialUnpreparedness(){
        FrameLayout main = findViewById(R.id.unpreparedness);
        ViewGroup editMode = findViewById(R.id.unpreparedness_edit);
        currentUnpreparedness = findViewById(R.id.unpreparedness_current);
        startUnpreparedness = findViewById(R.id.unpreparedness_start);
        ViewGroup normalMode = findViewById(R.id.unpreparedness_normal);
        View goToEdit = findViewById(R.id.unpreparedness_button_edit);
        TextView text = findViewById(R.id.unpreparedness_text);
        unpreparedness = HoldEditText.getCustomView(main, editMode, currentUnpreparedness, normalMode, goToEdit, text, this);
    }

    private OnApplyWindowInsetsListener getWindowsInsetsListener(){
        return new OnApplyWindowInsetsListener(){
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets){
                setInsets(insets);
                return insets;
            }
        };
    }

    private String getActionBarSubtitle(){
        int id = subject.getId();
        float average = UtilsAverage.getSubjectFinalAverage(id, this);
        return getString(R.string.final_average, average);
    }

    private void setAssessmentWeight(){
        if(!DataManager.isAverageWeight(this))
            assessmentWeight.setVisibility(View.GONE);
    }

    private void setAssessmentButton(){
        View assessmentAdd = findViewById(R.id.assessment_add);
        assessmentAdd.setOnClickListener(getAddAssessmentOnClickListener());
    }

    private View.OnClickListener getAddAssessmentOnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: wywalanie okna z oceną
            }
        };
    }

    private void setUnpreparednessText(){
        String text = subject.getRealUnpreparednessOfCurrentSemester(this) + "";
        unpreparedness.setText(text);
    }

    private void setUnpreparednessTextAdditional(){
        String prefix = getString(R.string.unpreparedness_prefix);
        String info = getString(R.string.unpreparedness);
        unpreparedness.setPrefix(prefix);
        unpreparedness.setInfo(info);
    }

    private void setUnpreparednessSecondEditText(){
        String unpreparednessDefault = subject.getUnpreparednessDefault() + "";
        startUnpreparedness.setText(unpreparednessDefault);
        startUnpreparedness.setHint("0");
        startUnpreparedness.addTextChangedListener(getChangedStartUnpreparednessListener());
        unpreparedness.addOtherEditors(startUnpreparedness);
    }

    private TextWatcher getChangedStartUnpreparednessListener(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String startUnpreparedness = s.toString();
                setCurrentUnpreparednessHint(startUnpreparedness);
            }
        };
    }

    private void setCurrentUnpreparednessHint(){
        String unpreparedness = UtilsEditText.getString(startUnpreparedness);
        setCurrentUnpreparednessHint(unpreparedness);
    }

    private void setCurrentUnpreparednessHint(String startUnpreparedness){
        if(!startUnpreparedness.isEmpty())
            currentUnpreparedness.setHint(startUnpreparedness);
        else
            currentUnpreparedness.setHint("0");
    }

    private HoldEditText.OnHoldEditTextChangeVisibilityListener getOnUnpreparednessChangeViewListener(){
        return new HoldEditText.OnHoldEditTextChangeVisibilityListener() {
            @Override
            public void onShowEditMode() {
                setCurrentUnpreparednessEditText();
            }

            @Override
            public void onHideEditMode() {
                setCurrentUnpreparedness();
                setStartUnpreparedness();
                setUnpreparednessText();
            }
        };
    }

    private void setCurrentUnpreparednessEditText(){
        int unpreparedness = subject.getUnpreparednessOfCurrentSemester(this);
        String string = "";
        if(unpreparedness > -1)
            string += unpreparedness;
        UtilsEditText.setText(currentUnpreparedness, string);
    }

    private void setCurrentUnpreparedness(){
        int unpreparedness = UtilsEditText.getInt(currentUnpreparedness, -1);
        subject.setUnpreparednessOfCurrentSemester(unpreparedness, this);
    }

    private void setStartUnpreparedness(){
        int unpreparedness = UtilsEditText.getInt(startUnpreparedness, 0);
        subject.setUnpreparednessDefault(unpreparedness);
    }

    private void setUnpreparednessMinusButton(){
        View minus = findViewById(R.id.unpreparedness_button_minus);
        minus.setOnClickListener(getOnClickMinusUnpreparednessListener());
    }

    private View.OnClickListener getOnClickMinusUnpreparednessListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subject.useUnpreparedness(getApplicationContext());
                setUnpreparednessText();
            }
        };
    }

    private String getDeleteTitle(){
        String subjectName = getSupportActionBar().getTitle().toString();
        return getString(R.string.delete_subject, subjectName);
    }
    //TODO: tutaj i w PreferenceSettingsFragment poprzątać te opisy bo tragedia...
    private String getDeleteMessage(){
        ArrayList<String> tableNames = getDelSubjectTableNames();
        ArrayList<String> columnNames = getDelSubjectColumnNames();
        ArrayList<Integer> plurals = getDelSubjectPlurals();
        ArrayList<Integer> counts = getDelSubjectCounts(tableNames, columnNames);
        String message = " " + getSummary(plurals.get(0), counts.get(0));
        message += ", " + getSummary(plurals.get(1), counts.get(1));
        message += ", " + getSummary(plurals.get(2), counts.get(2));
        message += " " + getString(R.string.in_lesson_plan);
        return getString(R.string.delete_subject_question, message);
    }

    private ArrayList<String> getDelSubjectTableNames(){
        return new ArrayList<>(Arrays.asList(
                Assessment2020.DATABASE_NAME,
                Note2020.DATABASE_NAME,
                ElementOfPlan2020.DATABASE_NAME
        ));
    }

    private ArrayList<String> getDelSubjectColumnNames(){
        return new ArrayList<>(Arrays.asList(
                Assessment2020.TAB_SUBJECT,
                Note2020.TAB_SUBJECT,
                ElementOfPlan2020.TAB_SUBJECT
        ));
    }

    private ArrayList<Integer> getDelSubjectPlurals(){
        return new ArrayList<>(Arrays.asList(
                R.plurals.summary_all_assessments,
                R.plurals.summary_all_notes,
                R.plurals.summary_lesson_plan
        ));
    }

    private ArrayList<Integer> getDelSubjectCounts(ArrayList<String> tableNames, ArrayList<String> columnNames){
        ArrayList<Integer> counts = new ArrayList<>();
        for(int i = 0; i < tableNames.size(); i++){
            String tableName = tableNames.get(i);
            String columnName = columnNames.get(i);
            int count = Database2020.getTableCountOnlyThisSubjectElement(tableName, columnName, subject.getId(), this);
            counts.add(count);
        }
        return counts;
    }

    private String getSummary(int plurals, int variable){
        return getResources().getQuantityString(plurals, variable, variable);
    }

    private DialogInterface.OnClickListener getOnClickPositiveDeleteSubjectListener(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String subjectName = getSupportActionBar().getTitle().toString();
                String doneToast = getString(R.string.delete_subject_done, subjectName);
                subject.delete(getApplicationContext());
                Toast.makeText(getApplicationContext(), doneToast, Toast.LENGTH_SHORT).show();
                finish();
            }
        };
    }
}
