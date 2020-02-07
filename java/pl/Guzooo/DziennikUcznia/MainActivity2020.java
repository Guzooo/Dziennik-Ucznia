package pl.Guzooo.DziennikUcznia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
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
            setSubjectCursors();
            setSubjectTitles();
            deleteEmptySubjectArrays();
            setMainAdapter();
            setMainRecycler();
            setActionBarSubtitle();
        } catch (Exception e){
            Database2020.errorToast(this);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void ClickSetting(View v){

    }

    public void ClickStatistics(View v){

    }

    public void ClickPlan(View v){

    }

    public void ClickPlus(View v){

    }

    private void initialization() {
        db = Database2020.getToReading(this);
        mainRecycler = findViewById(R.id.recycler);
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

    private void setMainAdapter(){
        mainAdapter = new AdapterMainRecycler(titles, subjectCursors);
        mainAdapter.setListener(new AdapterMainRecycler.Listener() {
            @Override
            public void onClick(int id) {
                //TODO: zmienić przejście do okna szczegółów
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

    /*private void refreshSubjectCursor(){
        //TODO: jeśli użyje czegoś na wzór:
           setSubjectCursors();
           subjectAdapter.changeCursor(subjectCursor);
    }*/
}