package pl.Guzooo.DziennikUcznia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private final String PREFERENCE_NOTEPAD = "notepad";

    //preference for errors and new save
    private final String PREFERENCE_DATABASE_1_TO_2 = "database1to2";
    private final String PREFERENCE_ERROR_VERSION_0_2_5 = "errorversion0.2.5";

    private final String BUNDLE_VISIBLE_NOTEPAD = "visiblenotepad";

    private ArrayList<Cursor> cursors = new ArrayList<>();
    private SQLiteDatabase db;
    private AdapterSubjectCardView adapter;

    private TextView textViewSecond;
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

        if(sharedPreferences.getInt(PREFERENCE_DATABASE_1_TO_2, 0) == 0){
            database1to2();
        }

        if(sharedPreferences.getInt(PREFERENCE_ERROR_VERSION_0_2_5, 0) == 0){
            errorSaveSubjectOfVersion0_2_5();
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
                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.EXTRA_ID, id);
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
        SharedPreferences statisticsSharePreferences = getSharedPreferences(StatisticsActivity.PREFERENCE_NAME, MODE_PRIVATE);
        float average = 0f;
        float assessment;
        int number = 0;
        boolean roundedAverage = settingSharedPreferences.getBoolean(SettingActivity.PREFERENCE_AVERAGE_TO_ASSESSMENT, SettingActivity.DEFAULT_AVERAGE_TO_ASSESSMENT);
        for (int i = 0; i < cursors.size(); i++) {
            if (cursors.get(i).moveToFirst()) {
                do {
                    if (roundedAverage) {
                        assessment = Subject.getOfCursor(cursors.get(i)).getRoundedAverage(settingSharedPreferences, this);
                    } else {
                        assessment = Subject.getOfCursor(cursors.get(i)).getAverage(this);
                    }

                    if(assessment != 0) {
                        number++;
                        average += assessment;
                    }
                } while (cursors.get(i).moveToNext());
            }
        }
        String subtitle = getResources().getString(R.string.statistics_semester, statisticsSharePreferences.getInt(StatisticsActivity.PREFERENCE_SEMESTER, StatisticsActivity.DEFAULT_SEMESTER)) + getResources().getString(R.string.separation);
        if (number == 0){
            return subtitle + "0.0";
        }
        average = average / number;
        if (average >= settingSharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_BELT, SettingActivity.DEFAULT_AVERAGE_TO_BELT)) {
            return subtitle + Float.toString(average) + getResources().getString(R.string.separation) + getResources().getString(R.string.main_belt);
        }
        return subtitle + Float.toString(average);
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

    private void database1to2 (){ // dodano w wersji 1 na 2
        try {
            SQLiteOpenHelper openHelper = new HelperDatabase(MainActivity.this);
            SQLiteDatabase db = openHelper.getWritableDatabase();
            Cursor cursor = db.query("SUBJECTS",
                    new String[]{"_id", "OBJECT"},
                    null, null, null, null, null);
            if(cursor.moveToFirst()){
                do{
                    Subject subject = new Subject(cursor.getString(1), cursor.getInt(0));

                    for(int i = 0; i < subject.getSubjectNotes().size(); i++){
                        db.insert("NOTES", null, subject.getSubjectNotes().get(i).saveSubjectNote());
                    }

                    db.update("SUBJECTS",
                            subject.saveSubject(getApplicationContext()),
                            "_id = ?",
                            new String[] {Integer.toString(cursor.getInt(0))});

                }while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
            editor.putInt(PREFERENCE_DATABASE_1_TO_2, 1);
            editor.apply();
        } catch (SQLiteException e){
            Toast.makeText(MainActivity.this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    private void errorSaveSubjectOfVersion0_2_5 (){ // dodano w wersji 4 na 5
        try {
            SQLiteOpenHelper openHelper = new HelperDatabase(this);
            SQLiteDatabase db = openHelper.getWritableDatabase();
            Cursor cursor = db.query("SUBJECTS",
                    new String[]{"_id", "DAY"},
                    null, null, null, null, null);

            if(cursor.moveToFirst()){
                do{
                    if(cursor.getType(1) == 0){
                        db.delete("SUBJECTS", "_id = ?", new String[]{Integer.toString(cursor.getInt(0))});
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
            editor.putInt(PREFERENCE_ERROR_VERSION_0_2_5, 1);
            editor.apply();
        } catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }
}
