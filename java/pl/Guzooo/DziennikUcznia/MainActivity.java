package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private final String PREFERENCE_NOTEPAD = "notepad";

    private final String PREFERENCE_CATEGORY_OF_ASSESSMENT = "categoryofassessment";
    //preference for errors and new save
    private final String PREFERENCE_DATABASE_3_TO_4 = "database3to4";

    private final String BUNDLE_VISIBLE_NOTEPAD = "visiblenotepad";

    private ArrayList<Cursor> cursors = new ArrayList<>();
    private SQLiteDatabase db;
    private AdapterSubjectCardView adapter;

    private EditText editTextNotepad;
    private View notepadBox;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextNotepad = findViewById(R.id.main_notepad);
        recyclerView = findViewById(R.id.main_recycler);
        notepadBox = findViewById(R.id.main_notepad_box);

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

        if(sharedPreferences.getInt(PREFERENCE_CATEGORY_OF_ASSESSMENT, 0) == 0){
            HelperDatabase.CreateDefaultCategoryOfAssessment(this);
            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
            editor.putInt(PREFERENCE_CATEGORY_OF_ASSESSMENT, 1);
            editor.apply();
        }

        if(sharedPreferences.getInt(PREFERENCE_DATABASE_3_TO_4, 0) == 0){
            database3to4();
        }

        goFirstChangeView(savedInstanceState);
        try {
            db = DatabaseUtils.getWritableDatabase(this);
            setDayOfSubject();
            refreshSubjectsCursors();
            setAdapter();
            refreshActionBarInfo();
        } catch (SQLiteException e) {
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }

        loadNotepad();

        if(CheckInformationOnline.getWifiConnecting(this)) {
            CheckInformationOnline checkInformationOnline = new CheckInformationOnline(this);
            checkInformationOnline.execute();
        }
        NotificationsChannels.CreateNotificationsChannels(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        try {
            refreshSubjectsCursors();
            refreshActionBarInfo();
        } catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(isNotepadEmpty()){
            DrawableCompat.setTint(menu.findItem(R.id.action_notepad).getIcon(), ContextCompat.getColor(this, android.R.color.darker_gray));
        } else {
            DrawableCompat.setTint(menu.findItem(R.id.action_notepad).getIcon(), ContextCompat.getColor(this, android.R.color.holo_red_light));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_notepad:
                showNotepad();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(notepadBox.getTranslationY() == 0){
            showNotepad();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_VISIBLE_NOTEPAD, (notepadBox.getTranslationY() == 0));
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveNotepad();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < cursors.size(); i++) {
            cursors.get(i).close();
        }
        db.close();
    }

    public void ClickSetting(View v){
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    public void ClickStatistics(View v){
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    public void ClickPlan(View v){
        Intent intent = new Intent(this, LessonPlanActivity.class);
        startActivity(intent);
    }

    public void ClickPlus(View v){
        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);
    }

    private void goFirstChangeView(final Bundle bundle){
        ViewTreeObserver viewTreeObserver = recyclerView.getViewTreeObserver();

        if (viewTreeObserver.isAlive()){
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    View bottomButtons = findViewById(R.id.main_bottom_buttons);
                    recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerView.getPaddingTop(), recyclerView.getPaddingRight(), bottomButtons.getHeight());

                    if(bundle == null || !bundle.getBoolean(BUNDLE_VISIBLE_NOTEPAD)) showNotepad();
                }
            });
        }
    }

    private void setDayOfSubject(){
        Cursor cursor = db.query("SUBJECTS",
                Subject.subjectOnCursor,
                "DAY != ?",
                new String[]{Integer.toString(0)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Subject subject = Subject.getOfCursor(cursor);
                db.update("SUBJECTS",
                        subject.saveDay(this, 0),
                        "_id = ?",
                        new String[]{Integer.toString(subject.getId())});
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public void setAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AdapterSubjectCardView(cursors, findViewById(R.id.main_subject_null));
        recyclerView.setAdapter(adapter);

        adapter.setListener(new AdapterSubjectCardView.Listener() {
            @Override
            public void onClick(int id) {
                Intent intent = new Intent(getApplicationContext(), DetailsAndEditActivity.class);
                intent.putExtra(DetailsAndEditActivity.EXTRA_ID, id);
                startActivity(intent);
            }
        });
    }

    private void refreshActionBarInfo(){
        getSupportActionBar().setSubtitle(getAverage());
    }

    private void refreshSubjectsCursors(){
        cursors.clear();
        for (int i = 0; i <= 7 ; i++){
            Cursor cursor = db.query("SUBJECTS",
                    Subject.subjectOnCursor,
                    "DAY = ?",
                    new String[]{Integer.toString(i)},
                    null, null,
                    "NOTES DESC, NAME");

            cursors.add(cursor);
        }
        if(adapter != null){
            adapter.changeCursors(cursors);
        }
    }

    private String getAverage() {
        SharedPreferences settingSharedPreferences = getSharedPreferences(SettingActivity.PREFERENCE_NAME, MODE_PRIVATE);
        float average = 0f;
        float assessment;
        int number = 0;
        boolean roundedAverage = settingSharedPreferences.getBoolean(SettingActivity.PREFERENCE_AVERAGE_TO_ASSESSMENT, SettingActivity.DEFAULT_AVERAGE_TO_ASSESSMENT);
        for (int i = 0; i < cursors.size(); i++) {
            if (cursors.get(i).moveToFirst()) {
                do {
                    Subject subject = Subject.getOfCursor(cursors.get(i));
                    ArrayList<SubjectAssessment> assessments1 = subject.getAssessment(1, this);
                    ArrayList<SubjectAssessment> assessments2 = subject.getAssessment(2, this);
                    if (roundedAverage) {
                        assessment = subject.getRoundedAverageEnd(assessments1, assessments2, settingSharedPreferences);
                    } else {
                        assessment = subject.getAverageEnd(assessments1, assessments2);
                    }

                    if(assessment != 0) {
                        number++;
                        average += assessment;
                    }
                } while (cursors.get(i).moveToNext());
            }
        }
        String subtitle = getResources().getString(R.string.statistics_semester, StatisticsActivity.getSemester(this)) + getResources().getString(R.string.separation) + getResources().getString(R.string.statistics_semester_end) + ": ";
        if (number == 0){
            return subtitle + "0.0";
        }
        average = average / number;
        String strAverage = String.format(Locale.US, "%.2f", average);
        if (average >= settingSharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_BELT, SettingActivity.DEFAULT_AVERAGE_TO_BELT)) {
            return subtitle + strAverage + getResources().getString(R.string.separation) + getResources().getString(R.string.main_belt);
        }
        return subtitle + strAverage;
    }

    private void showNotepad() {
        if (notepadBox.getTranslationY() != 0) {
            notepadBox.animate()
                    .translationY(0);
            recyclerView.setPadding(recyclerView.getPaddingLeft(), notepadBox.getHeight() + getResources().getDimensionPixelSize(R.dimen.card_margin) * 2, recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());
        } else {
            notepadBox.animate()
                    .translationY(notepadBox.getHeight() * -1 - getResources().getDimensionPixelSize(R.dimen.card_margin) * 2);
            recyclerView.setPadding(recyclerView.getPaddingLeft(), 0, recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());
        }
        invalidateOptionsMenu();
    }

    private void saveNotepad(){
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString(PREFERENCE_NOTEPAD, editTextNotepad.getText().toString().trim());
        editor.apply();
    }

    private void loadNotepad(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        editTextNotepad.setText(sharedPreferences.getString(PREFERENCE_NOTEPAD, ""));
    }

    private boolean isNotepadEmpty(){
        if(editTextNotepad.getText().toString().trim().equals("")){
            return true;
        } else {
            return false;
        }
    }

    private void database3to4 (){ //dodano w wersji 6 na 7
        try {
            SQLiteDatabase db = DatabaseUtils.getWritableDatabase(this);

            createDefaultCategoryOfAssessment(db);

            Cursor cursor = db.query("SUBJECTS",
                    Subject.subjectOnCursor,
                    null, null, null, null, null);
            if(cursor.moveToFirst()){
                do {
                    Subject subject = Subject.getOfCursor(cursor);
                    ArrayList<Float> assessment = subject.getAssessment(0);

                    for(int i = 0; i < assessment.size(); i++){
                        db.insert("ASSESSMENTS", null, AssessmentContent(assessment.get(i), 1, subject.getId()));
                    }
                    assessment = subject.getAssessment(1);

                    for(int i = 0; i < assessment.size(); i++){
                        db.insert("ASSESSMENTS", null, AssessmentContent(assessment.get(i), 2, subject.getId()));
                    }

                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
            editor.putInt(PREFERENCE_DATABASE_3_TO_4, 1);
            editor.apply();
        } catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    private ContentValues AssessmentContent(float assessment, int semester, int subject){
        ContentValues contentValues = new ContentValues();
        contentValues.put("ASSESSMENT", assessment);
        contentValues.put("NOTE", "");
        contentValues.put("SEMESTER", semester);
        contentValues.put("TAB_SUBJECT", subject);
        contentValues.put("TAB_CATEGORY_ASSESSMENT", 1);
        return contentValues;
    }

    private void createDefaultCategoryOfAssessment(SQLiteDatabase db){
        db.delete("CATEGORY_ASSESSMENT", null, null);
        db.insert("CATEGORY_ASSESSMENT", null, ModelCategoryOfAssessment(getResources().getString(R.string.category_of_assessment_default), "#000000"));
        db.insert("CATEGORY_ASSESSMENT", null, ModelCategoryOfAssessment(getResources().getString(R.string.category_of_assessment_test), "#ff0000"));
        db.insert("CATEGORY_ASSESSMENT", null, ModelCategoryOfAssessment(getResources().getString(R.string.category_of_assessment_answer), "#006399"));
        db.insert("CATEGORY_ASSESSMENT", null, ModelCategoryOfAssessment(getResources().getString(R.string.category_of_assessment_homework), "#00ff11"));
        db.insert("CATEGORY_ASSESSMENT", null, ModelCategoryOfAssessment(getResources().getString(R.string.category_of_assessment_quiz), "#ff0000"));
    }

    private ContentValues ModelCategoryOfAssessment(String name, String color){
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("COLOR", color);
        return contentValues;
    }
}
