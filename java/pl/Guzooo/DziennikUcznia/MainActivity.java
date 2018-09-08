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

public class MainActivity extends Activity {

    private final String PREFERENCE_NOTEPAD = "notepad";

    private Cursor cursor;
    private SQLiteDatabase db;
    private AdapterSubjectCardView adapter;

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
    }

    @Override
    protected void onStart() {
        super.onStart();

        RecyclerView recyclerView = findViewById(R.id.main_recycler);

        try {
            SQLiteOpenHelper openHelper = new HelperDatabase(this);
            db = openHelper.getReadableDatabase();
            cursor = db.query("SUBJECTS",
                    new String[] {"_id", "OBJECT"},
                    null, null, null, null,
                    "NOTES DESC");
        } catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }

        textViewSecond.setText(getAverage());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AdapterSubjectCardView(cursor, findViewById(R.id.main_subject_null), this);
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

        adapter.CloseCursor();
        cursor.close();
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
        Toast.makeText(this, R.string.main_soon_plan, Toast.LENGTH_SHORT).show();
    }

    public void ClickPlus(View v){
        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);
    }

    private String getAverage() {
        SharedPreferences sharedPreferences = getSharedPreferences(SettingActivity.PREFERENCE_NAME_AVERAGE_TO, MODE_PRIVATE);
        float average = 0f;
        int number = 0;
        if (cursor.moveToFirst()) {
             if(sharedPreferences.getBoolean(SettingActivity.PREFERENCE_AVERAGE_TO_ASSESSMENT, SettingActivity.defaulAverageToAssessment)) {
                do {
                    number++;
                    average += new Subject(cursor.getString(1)).getRoundedAverage(sharedPreferences);
                } while (cursor.moveToNext());
            } else {
                do {
                    number++;
                    average += new Subject(cursor.getString(1)).getAverage();
                } while (cursor.moveToNext());
            }
        } else {
            return "0.0";
        }
        average = average / number;
        if (average >= sharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_BELT, SettingActivity.defaulAverageToBelt)) {
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
}
