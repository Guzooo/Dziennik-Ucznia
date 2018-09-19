package pl.Guzooo.DziennikUcznia;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
public class MainActivity extends Activity {

    private final String PREFERENCE_NOTEPAD = "notepad";
    private final String PREFERENCE_DATABASE_1_TO_2 = "database1to2";
    private final String PREFERENCE_CURRENT_DAY = "day";

    private ArrayList<Cursor> cursors = new ArrayList<>();
    private SQLiteDatabase db;

    private TextView textViewSecond;
    private EditText editTextNotepad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.action_bar_two_text);

        View actionBar = getActionBar().getCustomView();

        TextView textViewTitle = actionBar.findViewById(R.id.action_bar_two_text_title);
        textViewSecond = actionBar.findViewById(R.id.action_bar_two_text_second);
        editTextNotepad = findViewById(R.id.main_notepad);

        editTextNotepad.setText(loadNotepad());
        textViewTitle.setText(R.string.app_name);

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

        if(sharedPreferences.getInt(PREFERENCE_DATABASE_1_TO_2, 0) == 0){
            database1to2();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            SQLiteOpenHelper openHelper = new HelperDatabase(this);
            db = openHelper.getWritableDatabase();
            Cursor cursor = db.query("SUBJECTS",
                    Subject.subjectOnCursorWithDay,
                    "DAY != ?",
                    new String[]{Integer.toString(0)},
                    null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    Subject subject = new Subject(cursor);
                    db.update("SUBJECTS",
                            subject.saveDay(this, 0),
                            "_id = ?",
                            new String[]{Integer.toString(subject.getId())});
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }

        RecyclerView recyclerView = findViewById(R.id.main_recycler);

        try {
            cursors.clear();
            for (int i = 0; i <= 7 ; i++){
                Cursor cursor = db.query("SUBJECTS",
                        Subject.subjectOnCursorWithDay,
                        "DAY = ?",
                        new String[]{Integer.toString(i)},
                        null, null,
                        "NOTES DESC");
                cursors.add(cursor);
            }

            textViewSecond.setText(getAverage());

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            AdapterSubjectCardView adapter = new AdapterSubjectCardView(cursors, findViewById(R.id.main_subject_null));
            recyclerView.setAdapter(adapter);

            adapter.setListener(new AdapterSubjectCardView.Listener() {
                @Override
                public void onClick(int id) {
                    Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                    intent.putExtra(DetailsActivity.EXTRA_ID, id);
                    startActivity(intent);
                }
            });
        } catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_notepad:
                View notepadBox = findViewById(R.id.main_notepad_box);
                if(notepadBox.getVisibility() == View.GONE) {
                    notepadBox.setVisibility(View.VISIBLE);
                } else {
                    notepadBox.setVisibility(View.GONE);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        for (int i = 0; i < cursors.size(); i++) {
        cursors.get(i).close();
        }
        db.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        saveNotepad();
    }

    public void ClickSetting(View v){
        Intent intent = new Intent(this, SettingActivity.class);
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

    private String getAverage() {
        SharedPreferences sharedPreferences = getSharedPreferences(SettingActivity.PREFERENCE_NAME_AVERAGE_TO, MODE_PRIVATE);
        float average = 0f;
        int number = 0;
        for (int i = 0; i < cursors.size(); i++) {
            if (cursors.get(i).moveToFirst()) {
                if (sharedPreferences.getBoolean(SettingActivity.PREFERENCE_AVERAGE_TO_ASSESSMENT, SettingActivity.defaultAverageToAssessment)) {
                    do {
                        number++;
                        average += new Subject(cursors.get(i)).getRoundedAverage(sharedPreferences);
                    } while (cursors.get(i).moveToNext());
                } else {
                    do {
                        number++;
                        average += new Subject(cursors.get(i)).getAverage();
                    } while (cursors.get(i).moveToNext());
                }
            }
        }
        if (number == 0){
            return "0.0";
        }
        average = average / number;
        if (average >= sharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_BELT, SettingActivity.defaultAverageToBelt)) {
            return Float.toString(average) + getResources().getString(R.string.separation) + getResources().getString(R.string.main_belt);
        }
        return Float.toString(average);
    }

    private void saveNotepad(){
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString(PREFERENCE_NOTEPAD, editTextNotepad.getText().toString().trim());
        editor.apply();
    }

    private String loadNotepad(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        return sharedPreferences.getString(PREFERENCE_NOTEPAD, "");
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
}
