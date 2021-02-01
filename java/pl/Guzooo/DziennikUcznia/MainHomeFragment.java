package pl.Guzooo.DziennikUcznia;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainHomeFragment extends MainFragment {

    private SQLiteDatabase db;
    private AdapterMainRecycler mainAdapter;
    private RecyclerView mainRecycler;

    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<Cursor> subjectCursors = new ArrayList<>();

    private boolean notepad;

    @Override
    public boolean isHome() {
        return true;
    }

    @Override
    public int getIconActionFAB() {
        if(notepad)
            return R.drawable.notepad_with_content;
        return R.drawable.notepad;
    }

    @Override
    public void clickIconActionFAB() {
        openNotepad();
    }

    @Override
    public int getNoDataText() {
        return R.string.no_subject;
    }

    @Override
    public boolean isNoDateVisible() {
        if(subjectCursors.size() == 0)
            return true;
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.only_recycler, container, false);
        initialization(layout);
        setFullScreen();
        setNotepad();
        try{
            setSubjectData();
            setMainAdapter();
            setMainRecycler();
            setAd();
        } catch (SQLiteException e){
            Database2020.errorToast(getContext());
        }
        mainFragmentListener.setNoDataVisibility();
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
        mainFragmentListener.setNoDataVisibility();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closeDatabaseElements();
        UtilsAds.destroyAds(getContext());
    }

    private void initialization(View v){
        db = Database2020.getToReading(getContext());
        mainRecycler = v.findViewById(R.id.recycler);
    }

    private void setFullScreen(){
        UtilsFullScreen.setPaddings(mainRecycler, this);
    }

    private void setNotepad(){
        if(DataManager.getNotepad(getContext()).isEmpty())
            notepad = false;
        else {
            notepad = true;
            mainFragmentListener.setAgainActionFAB();
        }
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
                Intent intent = new Intent(getContext(), SubjectDetailsActivity.class);
                intent.putExtra(SubjectDetailsActivity.EXTRA_ID, id);
                startActivity(intent);
            }
        });
    }

    private void setMainRecycler(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mainRecycler.setLayoutManager(layoutManager);
        mainRecycler.setAdapter(mainAdapter);
    }

    private void setAd(){
        if(subjectCursors.size() > 0) {
            ViewGroup place = new FrameLayout(getContext());
            place.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            UtilsAds.showAd(getString(R.string.ad_before_today), place, getContext());
            mainAdapter.addAd(place);
        }
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
        new MainHomeNotepadFragment().show(getNotepadListener(), getFragmentManager());
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

    private MainHomeNotepadFragment.NotepadListener getNotepadListener(){
        return new MainHomeNotepadFragment.NotepadListener() {
            @Override
            public void setNotepad(boolean is) {
                notepad = is;
                mainFragmentListener.setAgainActionFAB();
            }
        };
    }
}