package pl.Guzooo.DziennikUcznia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {

    public static final String PREFERENCE_NAME = "statistics";
    public static final String PREFERENCE_SEMESTER = "semester";

    public static final int DEFAULT_SEMESTER = 1;

    private SharedPreferences sharedPreferences;

    private SQLiteDatabase db;
    private Cursor cursor;

    public static int getSemester(Context context){
        return context.getSharedPreferences(StatisticsActivity.PREFERENCE_NAME, Context.MODE_PRIVATE).getInt(StatisticsActivity.PREFERENCE_SEMESTER, StatisticsActivity.DEFAULT_SEMESTER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        TextView textViewSemesterI = findViewById(R.id.statistics_semesterI);
        TextView textViewSemesterII = findViewById(R.id.statistics_semesterII);
        TextView textViewSemesterEnd = findViewById(R.id.statistics_semester_end);
        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);

        textViewSemesterI.setText(getResources().getString(R.string.semester, 1) + " - " + getAverage(1));
        textViewSemesterII.setText(getResources().getString(R.string.semester, 2) + " - " + getAverage(2));
        textViewSemesterEnd.setText(getResources().getString(R.string.final_average) + " - " + getAverageEnd());

        setActionBarSubtitle();
        Cursor();
        RecyclerView();
    }

    public void ClickISemester(View v){
        editPreferenceSemester(1);
        setActionBarSubtitle();
    }

    public void ClickIISemester(View v){
        editPreferenceSemester(2);
        setActionBarSubtitle();
    }

    public void ClickMessenger(View v){
        Uri uri = Uri.parse("https://www.messenger.com/t/GuzoooApps");
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    private void setActionBarSubtitle(){
        getSupportActionBar().setSubtitle(getResources().getString(R.string.semester, sharedPreferences.getInt(PREFERENCE_SEMESTER, DEFAULT_SEMESTER)));
    }

    private void editPreferenceSemester(int semester){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREFERENCE_SEMESTER, semester);
        editor.apply();
    }

    private String getAverage(int num) {
        SQLiteDatabase db = Database2020.getToReading(this);
        Cursor cursor = db.query("SUBJECTS",
                Subject.subjectOnCursor,
                null, null, null, null, null);

        SharedPreferences sharedPreferences = getSharedPreferences(SettingActivity.PREFERENCE_NAME, MODE_PRIVATE);
        float average = 0f;
        float assessment;
        int number = 0;
        boolean roundedAverage = sharedPreferences.getBoolean(SettingActivity.PREFERENCE_AVERAGE_TO_ASSESSMENT, SettingActivity.DEFAULT_AVERAGE_TO_ASSESSMENT);

        if (cursor.moveToFirst()) {
            do {
                Subject subject = Subject.getOfCursor(cursor);
                ArrayList<SubjectAssessment> assessments;
                if(num == 1){
                    assessments = subject.getAssessment(1, this);
                } else {
                    assessments = subject.getAssessment(2, this);
                }

                if (roundedAverage) {
                    assessment = subject.getRoundedAverage(assessments, sharedPreferences, this);
                } else {
                    assessment = Subject.getOfCursor(cursor).getAverage(assessments, this);
                }

                if(assessment != 0) {
                    number++;
                    average += assessment;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        if (number == 0){
            return "0.0";
        }
        average = average / number;
        if (average >= sharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_BELT, SettingActivity.DEFAULT_AVERAGE_TO_BELT)) {
            return Float.toString(average) + getResources().getString(R.string.separator) + getResources().getString(R.string.belt);
        }
        return Float.toString(average);
    }

    private String getAverageEnd() {
        SQLiteDatabase db = Database2020.getToReading(this);
        Cursor cursor = db.query("SUBJECTS",
                Subject.subjectOnCursor,
                null, null, null, null, null);

        SharedPreferences sharedPreferences = getSharedPreferences(SettingActivity.PREFERENCE_NAME, MODE_PRIVATE);
        float average = 0f;
        float assessment;
        int number = 0;
        boolean roundedAverage = sharedPreferences.getBoolean(SettingActivity.PREFERENCE_AVERAGE_TO_ASSESSMENT, SettingActivity.DEFAULT_AVERAGE_TO_ASSESSMENT);

        if (cursor.moveToFirst()) {
            do {
                Subject subject = Subject.getOfCursor(cursor);
                ArrayList<SubjectAssessment> assessments1 = subject.getAssessment(1, getApplicationContext());
                ArrayList<SubjectAssessment> assessments2 = subject.getAssessment(2, getApplicationContext());

                if (roundedAverage) {
                    assessment = Subject.getOfCursor(cursor).getRoundedAverageEnd(assessments1, assessments2, sharedPreferences, this);
                } else {
                    assessment = Subject.getOfCursor(cursor).getAverageEnd(assessments1, assessments2, this);
                }

                if(assessment != 0) {
                    number++;
                    average += assessment;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        if (number == 0){
            return "0.0";
        }
        average = average / number;
        if (average >= sharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_BELT, SettingActivity.DEFAULT_AVERAGE_TO_BELT)) {
            return Float.toString(average) + getResources().getString(R.string.separator) + getResources().getString(R.string.belt);
        }
        return Float.toString(average);
    }

    private void Cursor(){
        db = Database2020.getToReading(this);
        cursor = db.query("SUBJECTS",
                Subject.subjectOnCursor,
                null, null, null, null,
                "NAME");
    }

    private void RecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new AdapterStatisticsSubject(cursor, this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cursor.close();
        db.close();
    }
}
