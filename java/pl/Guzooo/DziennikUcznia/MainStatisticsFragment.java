package pl.Guzooo.DziennikUcznia;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainStatisticsFragment extends MainFragment {

    private SQLiteDatabase db;
    private AdapterStatisticsAverage adapter;
    private RecyclerView recycler;

    private Cursor cursor;

    private View nestScroll;
    private TextView semester1;
    private TextView semester2;
    private TextView semesterEnd;

    @Override
    public int getNoDataText() {
        return R.string.no_stat;
    }

    @Override
    public boolean isNoDateVisible() {
        if(cursor.getCount() == 0)
            return true;
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_main_statistics, container, false);
        initialization(layout);
        setFullScreen();
        setAverages();
        try{
            setData();
            setAdapter();
            setRecycler();
        }catch (SQLiteException e){
            Database2020.errorToast(getContext());
        }
        mainFragmentListener.setNoDataVisibility();
        return layout;
    }

    @Override
    public void onRestart() {
        super.onRestart();
        setAverages();
        try {
            refreshData();
            adapter.changeData(cursor);
        }catch (SQLException e){
            Database2020.errorToast(getContext());
        }
        mainFragmentListener.setNoDataVisibility();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closeDatabaseElements();
    }

    private void initialization(View v){
        db = Database2020.getToReading(getContext());
        recycler = v.findViewById(R.id.recycler);
        nestScroll = v.findViewById(R.id.nest_scroll);
        semester1 = v.findViewById(R.id.text1);
        semester2 = v.findViewById(R.id.text2);
        semesterEnd = v.findViewById(R.id.semester_end);
    }

    private void setFullScreen(){
        UtilsFullScreen.setPaddings(nestScroll, this);
    }

    private void setAverages(){
        setSemester1();
        setSemester2();
        setSemesterEnd();
    }

    private void setData(){
        setCursor();
    }

    private void setAdapter(){
        adapter = new AdapterStatisticsAverage(cursor);
        adapter.setListener(new AdapterStatisticsAverage.Listener() {
            @Override
            public void onClick(int id) {
                Intent intent = new Intent(getContext(), SubjectDetailsActivity.class);
                intent.putExtra(SubjectDetailsActivity.EXTRA_ID, id);
                startActivity(intent);
            }
        });
    }

    private void setRecycler(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
    }

    private void refreshData(){
        setData();
    }

    private void closeDatabaseElements(){
        cursor.close();
        db.close();
    }

    private void setSemester1(){
        float average = UtilsAverage.getSemesterAverage(1, getContext());
        String averageString = String.valueOf(average);
        semester1.setText(averageString);
    }

    private void setSemester2(){
        float average = UtilsAverage.getSemesterAverage(2, getContext());
        String averageString = String.valueOf(average);
        semester2.setText(averageString);
    }

    private void setSemesterEnd(){
        float average = UtilsAverage.getFinalAverage(getContext());
        String averageString = String.valueOf(average);
        semesterEnd.setText(averageString);
    }

    private void setCursor(){
        String query = querySubjectAverage();
        cursor = db.rawQuery(query, null);
    }

    private String querySubjectAverage(){
        return selectSubjectAverage()
                + fromSubjectAverage()
                + whereSubjectAverage()
                + orderSubjectAverage();
    }

    private String selectSubjectAverage(){
        return "SELECT " + elementOfSubjectAverageColumns();
    }

    private String elementOfSubjectAverageColumns(){
        String columns = "";
        for(String column : Subject2020.ON_CURSOR)
            columns += Subject2020.DATABASE_NAME + "." + column + ", ";
        columns = columns.trim();
        int endIndex = columns.length()-1;
        return columns.substring(0, endIndex);
    }

    private String fromSubjectAverage(){
        return " FROM " + Subject2020.DATABASE_NAME;
    }

    private String whereSubjectAverage(){
        return " WHERE " + Database2020.ID + " IN ("
                    + " SELECT DISTINCT " + Assessment2020.TAB_SUBJECT
                    + " FROM " + Assessment2020.DATABASE_NAME
                + " )";
    }

    private String orderSubjectAverage(){
        return " ORDER BY " + Subject2020.NAME;
    }
}