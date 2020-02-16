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

public class AdapterMainRecycler extends RecyclerView.Adapter<AdapterMainRecycler.ViewHolder>{
    private RecyclerView recyclerView;
    private Listener listener;
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<Cursor> cursors = new ArrayList<>();
    private ArrayList<AdapterSubject> subjectAdapters = new ArrayList<>();

    public static interface Listener{
        void onClick(int id);
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private View main_view;
        private TextView title;
        private RecyclerView recycler;

        public ViewHolder(View v){
            super(v);
            main_view = v;
            title = v.findViewById(R.id.text1);
            recycler = v.findViewById(R.id.recycler);
        }

        private Context getContext(){
            return main_view.getContext();
        }

        private void setTile(String title){
            this.title.setText(title);
        }

        private void setRecycler(AdapterSubject adapter){
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_day_subjects, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = getTitle(position);
        AdapterSubject adapter = getAdapter(position);
        holder.setTile(title);
        holder.setRecycler(adapter);
    }

    @Override
    public int getItemCount() {
        return cursors.size();
    }

    public AdapterMainRecycler(ArrayList<String> titles, ArrayList<Cursor> cursors){
        this.titles.addAll(titles);
        this.cursors.addAll(cursors);
        createAdapters();
    }

    public void changeData(ArrayList<String> newTitles, ArrayList<Cursor> newCursors){
        changeCursors(newCursors);
        if(!isTitlesSizeIsSame(newTitles)) {
            notifyChangeThisAdapter(newTitles);
            return;
        }
        for (int i = 0; i < titles.size(); i++)
            if (!isTitlesTextIsSame(newTitles, i)) {
                notifyChangeThisAdapter(newTitles);
                return;
            }
        notifyChangeInnerAdapters();
    }

    public void closeCursors(){
        for(Cursor cursor : cursors)
            cursor.close();
    }

    private String getTitle(int position){
        return titles.get(position);
    }

    private AdapterSubject getAdapter(int position){
        return subjectAdapters.get(position);
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

    private void notifyChangeInnerAdapters(){
        for (int i = 0; i < subjectAdapters.size(); i++)
            getAdapter(i).changeCursor(getCursor(i));
    }

    private void scrollToTop(){
        recyclerView.scrollToPosition(0);
    }

    private void changeCursors(ArrayList<Cursor> cursors){
        closeCursors();
        this.cursors.clear();
        this.cursors.addAll(cursors);
    }

    private void changeTitles(ArrayList<String> titles){
        this.titles.clear();
        this.titles.addAll(titles);
    }

    private void changeAdapters(){
        this.subjectAdapters.clear();
        createAdapters();
    }

    private void createAdapters(){
        for(Cursor cursor : cursors){
            AdapterSubject adapter = createAdapter(cursor);
            subjectAdapters.add(adapter);
        }
    }

    private AdapterSubject createAdapter(Cursor cursor){
        AdapterSubject subjectAdapter = new AdapterSubject(cursor);
        subjectAdapter.setListener(getListener());
        return subjectAdapter;
    }

    private AdapterSubject.Listener getListener(){
        return new AdapterSubject.Listener() {
            @Override
            public void onClick(int id) {
                if(listener != null)
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