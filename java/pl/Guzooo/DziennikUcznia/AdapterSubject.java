package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class AdapterSubject extends RecyclerView.Adapter<AdapterSubject.ViewHolder>{
    private Listener listener;
    private Cursor cursor;

    public static interface Listener{
        void onClick(int id);
    }

    public void setListener (Listener listener){
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private View main_view;
        private TextView name;
        private TextView unpreparedness;
        private TextView numberNotes;

        public ViewHolder(View v){
            super(v);
            main_view = v;
            name = v.findViewById(R.id.text1);
            unpreparedness = v.findViewById(R.id.text2);
            numberNotes = v.findViewById(R.id.text3);
        }

        private Context getContext(){
            return main_view.getContext();
        }

        private void setName(Subject2020 subject){
            String name = subject.getName();
            this.name.setText(name);
        }

        private void setUnpreparedness(Subject2020 subject){
            int unpreparedness = subject.getUnpreparednessOfCurrentSemester(getContext());
            String unpreparednessStr = getContext().getString(R.string.number_of_unpreparedness, unpreparedness);
            this.unpreparedness.setText(unpreparednessStr);
        }

        private void setNumberNotes(int numberNotes){
            String numberNotesStr = getContext().getString(R.string.number_of_notes, numberNotes);
            this.numberNotes.setText(numberNotesStr);
        }
    }

    @Override
    public AdapterSubject.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.three_text_card_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(cursor.moveToPosition(position)){
            Subject2020 subject = getSubject();
            holder.setName(subject);
            holder.setUnpreparedness(subject);
            holder.setNumberNotes(getNumberNotes());
            setOnClickThisView(holder, position);
        }
    }

    private int getNumberNotes(){
        int columnNumberNotesIndex = cursor.getColumnIndex(Note2020.DATABASE_NAME);
        return cursor.getInt(columnNumberNotesIndex);
    }

    private Subject2020 getSubject(){
        Subject2020 subject = new Subject2020();
        subject.setVariablesOfCursor(cursor);
        return subject;
    }

    private void setOnClickThisView(ViewHolder holder, final int position){
        holder.main_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null && cursor.moveToPosition(position))
                    listener.onClick(cursor.getInt(0));
            }
        });
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public AdapterSubject(Cursor cursor){
        this.cursor = cursor;
    }

    public void changeCursor(Cursor cursor){
        this.cursor = cursor;
        notifyDataSetChanged();
    }
}
