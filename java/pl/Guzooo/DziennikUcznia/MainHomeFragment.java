package pl.Guzooo.DziennikUcznia;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainHomeFragment extends MainFragment {

    private SQLiteDatabase db;
    private AdapterMainRecycler mainAdapter;
    private RecyclerView mainRecycler;

    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<Cursor> subjectCursors = new ArrayList<>();

    @Override
    public boolean isHome() {
        return true;
    }

    @Override
    public int getIconActionFAB() {
        return R.drawable.notepad;
    }

    @Override
    public void clickIconActionFAB() {
        openNotepad();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.only_recycler, container, false);
        initialization(layout);
        setFullScreen();
        try{
            setSubjectData();
            setMainAdapter();
            setMainRecycler();
        } catch (SQLiteException e){
            Database2020.errorToast(getContext());
        }
        return layout;
    }

    @Override
    public void onRestart() {
        super.onRestart();
        try{
            refreshSubjectData();
            mainAdapter.changeData(titles, subjectCursors);
        } catch (SQLiteException e){
            Database2020.errorToast(getContext());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closeDatabaseElements();
    }

    private void initialization(View v){
        db = Database2020.getToReading(getContext());
        mainRecycler = v.findViewById(R.id.recycler);
        //TODO:notepad = settings.getNotepad();
    }

    private void setFullScreen(){
        UtilsFullScreen.setPaddings(mainRecycler, this);
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
                Intent intent = new Intent(getContext(), DetailsAndEditActivity.class);
                intent.putExtra(DetailsAndEditActivity.EXTRA_ID, id);
                startActivity(intent);
            }
        });
    }

    private void setMainRecycler(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mainRecycler.setLayoutManager(layoutManager);
        mainRecycler.setAdapter(mainAdapter);
    }

    private void refreshSubjectData(){
        resetSubjectVariables();
        setSubjectData();
    }

    private void closeDatabaseElements(){
        mainAdapter.closeCursors();
        for(int i = 0; i < subjectCursors.size(); i++)
            subjectCursors.get(i).close();
        db.close();
    }

    private void openNotepad(){
        TextView editText = new TextView(getContext(), null);
        editText.setText(DataManager.getNotepad(getContext()));
        //editText.setSingleLine(false);
        //FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(editText.getLayoutParams());
        //params.setMarginStart(getResources().getDimensionPixelOffset(R.dimen.margin_biggest));
        //params.setMarginEnd(getResources().getDimensionPixelOffset(R.dimen.margin_biggest));
        //editText.setLayoutParams(params);
        new AlertDialog.Builder(getContext())
                .setTitle("Notatnik \u2022 czasowo bez edycji")
                .setView(editText)
                .setPositiveButton("Ok", null)
                .setNegativeButton("Anuluj", null)
                .show();

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
            titles.add(UtilsCalendar.getDayOfWeek(day, getContext()));
        for(int day = 1; day < today; day++)
            titles.add(UtilsCalendar.getDayOfWeek(day, getContext()));
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

    private void resetSubjectVariables(){
        titles.clear();
        subjectCursors.clear();
    }
}