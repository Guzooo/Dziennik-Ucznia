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
    private Listener listener;
    private ArrayList<String> titles;
    private ArrayList<Cursor> cursors;

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
        private AdapterSubject subjectAdapter;

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

        private void setAdapter(Cursor cursor, AdapterSubject.Listener listener){
            subjectAdapter = new AdapterSubject(cursor);
            subjectAdapter.setListener(listener);
        }

        private void setRecycler(){
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recycler.setLayoutManager(layoutManager);
            recycler.setAdapter(subjectAdapter);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_day_subjects, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = getTitle(position);
        Cursor cursor = getCursor(position);
        AdapterSubject.Listener listener = getListener();
        holder.setTile(title);
        holder.setAdapter(cursor, listener);
        holder.setRecycler();
    }

    private String getTitle(int position){
        return titles.get(position);
    }

    private Cursor getCursor(int position){
        return cursors.get(position);
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

    @Override
    public int getItemCount() {
        return cursors.size();
    }

    public AdapterMainRecycler(ArrayList<String> titles, ArrayList<Cursor> cursors){
        this.titles = titles;
        this.cursors = cursors;
    }
}