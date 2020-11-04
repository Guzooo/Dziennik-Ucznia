package pl.Guzooo.DziennikUcznia;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

public class SubjectDetailsActivity extends GActivity {

    public static final String EXTRA_ID = "id";

    private final String BUNDLE_VISIBLE_NOTES = "visiblenotes";

    private Subject2020 subject;

    private ChangeTitle changeTitle;
    private HoldEditText teacher;
    private EditText assessment;
    private EditText assessmentWeight;
    private HoldEditText unpreparedness;
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
        unpreparedness = HoldEditText.getCustomView((FrameLayout) findViewById(R.id.unpreparedness),//TODO:raczej inna metoda;
                                                    (ViewGroup) findViewById(R.id.unpreparedness_edit),
                                                    (EditText) findViewById(R.id.unpreparedness_current),
                                                    (ViewGroup)findViewById(R.id.unpreparedness_normal),
                                                    findViewById(R.id.unpreparedness_button_edit),
                                                    (TextView)findViewById(R.id.unpreparedness_text),
                                                    this);
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
        getSupportActionBar().setTitle(subject.getName());//todo:gowno
        getSupportActionBar().setSubtitle(""+UtilsAverage.getSubjectFinalAverage(subject.getId(), this));
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
    }

    private void setAssessment(){
        setAssessmentWeight();
        setAssessmentButton();
    }

    private void setUnpreparedness(){
        String prefix = getString(R.string.unpreparedness_prefix);
        String text = subject.getUnpreparednessOfCurrentSemester(this) + "";
        String info = getString(R.string.unpreparedness);
        unpreparedness.setPrefix(prefix);
        unpreparedness.setText(text);
        unpreparedness.setInfo(info);
        EditText unpreparednessStart = findViewById(R.id.unpreparedness_start);
        unpreparedness.addOtherEditors(unpreparednessStart);
    }

    private void setDescription(){
        description.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        String text = subject.getDescription();
        description.setText(text);
    }

    private void clickEdit(){
        changeTitle.show();
    }

    private void clickNotes(){

    }

    private void clickDelete(){

    }

    private boolean isVisibilityNotes(){
        return false;
    }

    private void setVisibilityNotes(boolean open){

    }

    private void closeAllHoldEditText(){

    }

    private void updateSubject(){

    }

    private void initialSubject(){
        int id = getIntent().getIntExtra(EXTRA_ID, 0);
        subject = new Subject2020();
        subject.setVariablesOfId(id, this);
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
}
