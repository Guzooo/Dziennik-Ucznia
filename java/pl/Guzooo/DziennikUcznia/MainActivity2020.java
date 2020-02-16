package pl.Guzooo.DziennikUcznia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity2020 extends GActivity {

    //  TODO: usunąć zakomentowanie po usunięciu dopiska 2020 z nazwy klasy
    //  private final String PREFERENCE_NOTEPAD = "notepad";

    private final String BUNDLE_VISIBLE_NOTEPAD = "visiblenotepad";

    private EditText notepad;

    private SQLiteDatabase db;
    private AdapterMainRecycler mainAdapter;

    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<Cursor> subjectCursors = new ArrayList<>();

    private RecyclerView mainRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialization();
        //FirstChangeView();
        try{
            setSubjectData();
            setMainAdapter();
            setMainRecycler();
            setActionBarSubtitle();
        } catch (SQLiteException e){
            Database2020.errorToast(this);
        }
        setNotepad();
        NotificationOnline.checkAutomatically(this);
        NotificationsChannels.CreateNotificationsChannels(this);//TODO: czy to musi się wykonywać za każdym uruchomieniem aplikacji
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try{
            refreshSubjectData();
            mainAdapter.changeData(titles,subjectCursors);
            setActionBarSubtitle();
        } catch (SQLiteException e){
            Database2020.errorToast(this);
        }
        //TODO: jak ikony nie będą się zmieniać (notatnika) to dodać: invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //TODO: jak notatnik pusty
        //ustaw notatnik
        //else
        //ustaw notatnik z zawartością
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_notepad:
                //show/hide notepad;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //TODO:jak notatnik otwarty
        //zamknij go
        //else
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //TODO: zapis czy notatnik jest zamknięty czy otwarty
    }

    @Override
    protected void onPause() {
        super.onPause();
        //TODO:zapisz notatnik
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDatabaseElements();
    }

    public void ClickSetting(View v){
        //TODO: nowa wersja aktywności
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    public void ClickStatistics(View v){
        //TODO: nowa wersja aktywności
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    public void ClickPlan(View v){
        //TODO: nowa wersja aktywności
        Intent intent = new Intent(this, LessonPlanActivity.class);
        startActivity(intent);
    }

    public void ClickPlus(View v){
        //TODO: nowa wersja aktywności
        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);
    }

    private void initialization() {
        db = Database2020.getToReading(this);
        mainRecycler = findViewById(R.id.recycler);
    }

    private void refreshSubjectData(){
        resetSubjectVariables();
        setSubjectData();
    }

    private void setSubjectData(){
        setSubjectCursors();
        setSubjectTitles();
        deleteEmptySubjectArrays();
    }

    private void setMainAdapter(){
        mainAdapter = new AdapterMainRecycler(titles, subjectCursors);
        mainAdapter.setListener(new AdapterMainRecycler.Listener() {
            @Override
            public void onClick(int id) {
                //TODO: zmienić na nowe okno szczegółów
                Intent intent = new Intent(getApplicationContext(), DetailsAndEditActivity.class);
                intent.putExtra(DetailsAndEditActivity.EXTRA_ID, id);
                startActivity(intent);
            }
        });
    }

    private void setMainRecycler(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mainRecycler.setLayoutManager(layoutManager);
        mainRecycler.setAdapter(mainAdapter);
    }

    private void setActionBarSubtitle(){
        String semester = getSemester();
        String separator = getString(R.string.separator);
        String average = getFinalAverage();
        getSupportActionBar().setSubtitle(semester + separator + average);
    }

    private void setNotepad(){
        //TODO: notatnik w głównym Activity
    }

    private void closeDatabaseElements(){
        mainAdapter.closeCursors();
        for(int i = 0; i < subjectCursors.size(); i++)
            subjectCursors.get(i).close();
        db.close();
    }

    private void setSubjectCursors(){
        int today = UtilsCalendar.getTodaysDayOfWeek();
        subjectCursors.add(subjectDayIsNull());
        subjectCursors.add(subjectToday());
        for(int day = today+1; day <= 7; day++)
            subjectCursors.add(subjectThisWeek(day));
        for(int day = 1; day <= today; day++)
            subjectCursors.add(subjectNextWeek(day));
    }

    private void setSubjectTitles(){
        int today = UtilsCalendar.getTodaysDayOfWeek();
        titles.add(getString(R.string.not_in_plan));
        for(int day = today; day <= 7; day++)
            titles.add(UtilsCalendar.getDayOfWeek(day, this));
        for(int day = 1; day < today; day++)
            titles.add(UtilsCalendar.getDayOfWeek(day, this));
        titles.add(getString(R.string.in_a_week));
        titles.set(1, getString(R.string.today));
        titles.set(2, getString(R.string.tomorrow));
    }

    private void deleteEmptySubjectArrays(){
        int size = subjectCursors.size();
        for(int i = 0; i < size; i++) {
            if (subjectCursors.get(i).getCount() == 0) {
                subjectCursors.remove(i);
                titles.remove(i);
                i--;
                size--;
            }
        }
    }

    private Cursor subjectDayIsNull(){
        String query = querySubject(" IS NULL", 0);
        return db.rawQuery(query, null);
    }

    private Cursor subjectToday(){
        int today = UtilsCalendar.getTodaysDayOfWeek();
        String query = querySubject(" = " + today, 0);
        return db.rawQuery(query, null);
    }

    private Cursor subjectThisWeek(int day){
        int today = UtilsCalendar.getTodaysDayOfWeek();
        String query = querySubject(" > " + today, day);
        return db.rawQuery(query, null);
    }

    private Cursor subjectNextWeek(int day){
        String query = querySubject(" IS NOT 0 ", day);
        return db.rawQuery(query, null);
    }

    private String querySubject(String where, int day){
        return selectSubject()
                + selectNumberNotesOfSubject()
                + fromSubject()
                + whereSubject(where)
                + groupSubject()
                + havingSubject(day)
                + orderSubject();
    }

    private String selectSubject(){
        return "SELECT DISTINCT " + subjectColumns();
    }

    private String subjectColumns(){
        String columns = "";
        for(int i = 0; i < Subject2020.ON_CURSOR.length; i++)
            columns += Subject2020.DATABASE_NAME + "." + Subject2020.ON_CURSOR[i] + ", ";
        return columns;
    }

    private String selectNumberNotesOfSubject(){
        return "COUNT(DISTINCT " + Note2020.DATABASE_NAME + "." + Database2020.ID + ") AS " + Note2020.DATABASE_NAME;
    }

    private String fromSubject(){
        return " FROM " + Subject2020.DATABASE_NAME
                +" LEFT JOIN " + ElementOfPlan2020.DATABASE_NAME + " ON " + ElementOfPlan2020.DATABASE_NAME + "." + ElementOfPlan2020.TAB_SUBJECT + " = " + Subject2020.DATABASE_NAME + "." + Database2020.ID
                +" LEFT JOIN " + Note2020.DATABASE_NAME + " ON " + Note2020.DATABASE_NAME + "." + Note2020.TAB_SUBJECT + " = " + Subject2020.DATABASE_NAME + "." + Database2020.ID;
    }

    private String whereSubject(String where){
        return " WHERE " + ElementOfPlan2020.DAY + where;
    }

    private String groupSubject(){
        return " GROUP BY " + Subject2020.DATABASE_NAME + "." + Database2020.ID;
    }

    private String havingSubject(int day){
        if(day == 0)
            return "";
        int today = UtilsCalendar.getTodaysDayOfWeek();
        if(day > today)
            return  " HAVING MIN(" + ElementOfPlan2020.DAY + ") = " + day;
        return " HAVING MAX(" + ElementOfPlan2020.DAY + ") <= " + today + " AND " + "MIN(" + ElementOfPlan2020.DAY + ") = " + day;
    }

    private String orderSubject(){
        return " ORDER BY " + Note2020.DATABASE_NAME + " DESC, " + Subject2020.DATABASE_NAME + "." + Subject2020.NAME;
    }

    private String getSemester(){
        int semester = StatisticsActivity.getSemester(this);
        return getString(R.string.semester_with_colon, semester);
    }

    private String getFinalAverage(){
        float average = UtilsAverage.getFinalAverage(this);
        if(isBelt(average))
            return getString(R.string.final_average, average) + getString(R.string.separator) + getString(R.string.belt);
        return getString(R.string.final_average, average);
    }

    private boolean isBelt(float average){
        if(average >= getMinimumToBelt())
            return true;
        return false;
    }

    private float getMinimumToBelt(){
        //TODO: usunąć i dodać taką medote do ustawień;
        SharedPreferences sharedPreferences = getSharedPreferences(SettingActivity.PREFERENCE_NAME, MODE_PRIVATE);
        return sharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_BELT, SettingActivity.DEFAULT_AVERAGE_TO_BELT);
    }

    private void resetSubjectVariables(){
        titles.clear();
        subjectCursors.clear();
    }
}