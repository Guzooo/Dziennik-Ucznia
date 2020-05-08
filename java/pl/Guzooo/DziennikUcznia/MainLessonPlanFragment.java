package pl.Guzooo.DziennikUcznia;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MainLessonPlanFragment extends MainFragment {

    private SQLiteDatabase db;
    private AdapterLessonPlanRecycler adapter;
    private RecyclerView recycler;

    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<Cursor> elementOfLessonPlanCursors = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.only_recycler, container, false);
        initialization(layout);
        setFullScreen();
        try{
            setElementOfLessonPlanData();
            setAdapter();
            setRecycler();
        } catch (SQLiteException e){
            Database2020.errorToast(getContext());
        }
        goToToday();
        return layout;
    }

    @Override
    public void onRestart() {
        super.onRestart();
        try{
            refreshElementOfLessonPlanData();
            adapter.changeData(titles, elementOfLessonPlanCursors);
        } catch (SQLiteException e){
            Database2020.errorToast(getContext());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closeDatabaseElements();
    }

    @Override
    public int getIconActionFAB() {
        return R.drawable.today;
    }

    @Override
    public void clickIconActionFAB() {
        goToToday();
    }

    private void initialization(View v){
        db = Database2020.getToReading(getContext());
        recycler = v.findViewById(R.id.recycler);
    }

    private void setFullScreen(){
        UtilsFullScreen.setPaddings(recycler, this);
    }

    private void setElementOfLessonPlanData(){
        setElementOfLessonPlanCursors();
        setElementOfLessonPlanTitles();
        deleteEmptyElementOfLessonPlanArrays();
    }

    private void setAdapter(){
        adapter = new AdapterLessonPlanRecycler(titles, elementOfLessonPlanCursors);
        adapter.setListener(new AdapterLessonPlanRecycler.Listener() {
            @Override
            public void onClick(int id) {
                //TODO: zmienić na nowe okno
                Intent intent = new Intent(getContext(), LessonPlanEditActivity.class);
                intent.putExtra(LessonPlanEditActivity.EXTRA_ID, id);
                startActivity(intent);
            }
        });
    }

    private void setRecycler(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
    }

    private void refreshElementOfLessonPlanData(){
        resetElementOfLessonPlanVariables();
        setElementOfLessonPlanData();
    }

    private void closeDatabaseElements(){
        adapter.closeCursor();
        for(int i = 0; i < elementOfLessonPlanCursors.size(); i++)
            elementOfLessonPlanCursors.get(i).close();
        db.close();
    }

    private void goToToday(){
        int todayPosition = getTodayFromTitles();
        RecyclerView.SmoothScroller smoothScroller = getSmoothScroller();
        smoothScroller.setTargetPosition(todayPosition);
        recycler.getLayoutManager().startSmoothScroll(smoothScroller);
    }

    private void setElementOfLessonPlanCursors(){
        for(int day = 1; day <= 7; day++){
            Cursor cursor = getElementOfLessonPlanCursor(day);
            elementOfLessonPlanCursors.add(cursor);
        }
    }

    private void setElementOfLessonPlanTitles(){
        for(int day = 1; day <= 7; day++){
            String string = UtilsCalendar.getDayOfWeek(day, getContext());
            titles.add(string);
        }
    }

    private void deleteEmptyElementOfLessonPlanArrays(){
        int size = elementOfLessonPlanCursors.size();
        for(int i = 0; i < size; i++){
            if(elementOfLessonPlanCursors.get(i).getCount() == 0){
                elementOfLessonPlanCursors.remove(i);
                titles.remove(i);
                i--;
                size--;
            }
        }
    }

    private Cursor getElementOfLessonPlanCursor(int day){
        String query = queryElementOfLessonPlan(day);
        return db.rawQuery(query, null);
    }

    private String queryElementOfLessonPlan(int day){
        return selectElementOfLessonPlan()
                + selectSubjectName()
                + fromElementOfLessonPlan()
                + whereElementOfLessonPlan(day)
                + orderElementOfLessonPlan();
    }

    private String selectElementOfLessonPlan(){
        return "SELECT " + elementOfLessonPlanColumns();
    }

    private String elementOfLessonPlanColumns(){
        String columns = "";
        for(int i = 0; i < ElementOfPlan2020.ON_CURSOR.length; i++)
            columns += ElementOfPlan2020.DATABASE_NAME + "." + ElementOfPlan2020.ON_CURSOR[i] + ", ";
        return columns;
    }

    private String selectSubjectName(){
        return Subject2020.NAME + " AS " + Subject2020.DATABASE_NAME;
    }

    private String fromElementOfLessonPlan(){
        return " FROM " + ElementOfPlan2020.DATABASE_NAME
                +" LEFT JOIN " + Subject2020.DATABASE_NAME + " ON " + Subject2020.DATABASE_NAME + "." + Database2020.ID + " = " + ElementOfPlan2020.DATABASE_NAME + "." + ElementOfPlan2020.TAB_SUBJECT;
    }

    private String whereElementOfLessonPlan(int day){
        return " WHERE " + ElementOfPlan2020.DAY + " = " + day;
    }

    private String orderElementOfLessonPlan(){
        return " ORDER BY " + ElementOfPlan2020.TIME_START;
    }

    private void resetElementOfLessonPlanVariables(){
        titles.clear();
        elementOfLessonPlanCursors.clear();
    }

    private int getTodayFromTitles(){
        int today = UtilsCalendar.getTodaysDayOfWeek();
        String todayName = UtilsCalendar.getDayOfWeek(today, getContext());
        for(int i = 0; i < titles.size(); i++)
            if(titles.get(i).equals(todayName))
                return i;
        return 0;
    }

    private LinearSmoothScroller getSmoothScroller(){
        return new LinearSmoothScroller(getContext()) {
            @Override protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
    }
}
