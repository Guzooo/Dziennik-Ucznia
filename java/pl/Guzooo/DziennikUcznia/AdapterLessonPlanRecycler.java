package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterLessonPlanRecycler extends RecyclerView.Adapter<AdapterLessonPlanRecycler.ViewHolder> {
    private RecyclerView recyclerView;
    private Listener listener;
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<Cursor> cursors = new ArrayList<>();
    private ArrayList<AdapterLessonPlanDay> lessonPlanDayAdapters= new ArrayList<>();

    public interface Listener{
        void onClick(int id);
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private View mainView;
        private TextView title;
        private RecyclerView recycler;

        public ViewHolder(View v){
            super(v);
            mainView = v;
            title = v.findViewById(R.id.text1);
            recycler = v.findViewById(R.id.recycler);
        }

        private Context getContext(){
            return mainView.getContext();
        }

        private void setTitle(String title){
            this.title.setText(title);
        }

        private void setRecycler(AdapterLessonPlanDay adapter){
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recycler.setLayoutManager(layoutManager);
            recycler.setAdapter(adapter);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hub, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = getTitle(position);
        AdapterLessonPlanDay adapter = getAdapter(position);
        holder.setTitle(title);
        holder.setRecycler(adapter);
    }

    @Override
    public int getItemCount() {
        return cursors.size();
    }

    public AdapterLessonPlanRecycler(ArrayList<String> titles, ArrayList<Cursor> cursors){
        this.titles.addAll(titles);
        this.cursors.addAll(cursors);
        createAdapters();
    }

    public void changeData(ArrayList<String> newTitles, ArrayList<Cursor> newCursor){
        changeCursors(newCursor);
        if(!isTitlesSizeIsSame(newTitles)){
            notifyChangeThisAdapter(newTitles);
            return;
        }
        for(int i = 0; i < titles.size(); i++)
            if(!isTitlesTextIsSame(newTitles, i)){
                notifyChangeThisAdapter(newTitles);
                return;
            }
        notifyChangeInnerAdapter();
    }

    public void closeCursor(){
        for(Cursor cursor : cursors)
            cursor.close();
    }

    private String getTitle(int position){
        return titles.get(position);
    }

    private AdapterLessonPlanDay getAdapter(int position){
        return lessonPlanDayAdapters.get(position);
    }

    private Cursor getCursor(int position){
        return cursors.get(position);
    }

    private void notifyChangeThisAdapter(ArrayList<String> newTitles){
        scrollToTop();
        changeTitles(newTitles);
        changeAdapters();
        notifyDataSetChanged();
    }

    private void notifyChangeInnerAdapter(){
        for(int i = 0; i < lessonPlanDayAdapters.size(); i++)
            getAdapter(i).changeCursor(getCursor(i));
    }

    private void scrollToTop() {
        recyclerView.scrollToPosition(0);
    }

    private void changeCursors(ArrayList<Cursor> cursors){
        closeCursor();
        this.cursors.clear();
        this.cursors.addAll(cursors);
    }

    private void changeTitles(ArrayList<String> titles){
        this.titles.clear();
        this.titles.addAll(titles);
    }

    private void changeAdapters(){
        this.lessonPlanDayAdapters.clear();
        createAdapters();
    }

    private void createAdapters(){
        for(Cursor cursor : cursors){
            AdapterLessonPlanDay adapter = createAdapter(cursor);
            lessonPlanDayAdapters.add(adapter);
        }
    }

    private AdapterLessonPlanDay createAdapter(Cursor cursor){
        AdapterLessonPlanDay lessonPlanDayAdapter = new AdapterLessonPlanDay(cursor);
        lessonPlanDayAdapter.setListener(getListener());
        return lessonPlanDayAdapter;
    }

    private AdapterLessonPlanDay.Listener getListener(){
        return new AdapterLessonPlanDay.Listener(){
            @Override
            public void onClick(int id) {
                if (listener != null)
                    listener.onClick(id);
            }
        };
    }

    private boolean isTitlesSizeIsSame(ArrayList<String> newTitles){
        int sizeCurrentTitles = titles.size();
        int sizeNewTitles = newTitles.size();
        if(sizeCurrentTitles == sizeNewTitles)
            return true;
        return false;
    }

    private boolean isTitlesTextIsSame(ArrayList<String> newTitles, int position){
        String textCurrentTitles = titles.get(position);
        String textNewTitles = newTitles.get(position);
        if(textCurrentTitles.equals(textNewTitles))
            return true;
        return false;
    }
}