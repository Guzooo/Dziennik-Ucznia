package pl.Guzooo.DziennikUcznia;

import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterNoteCardView extends RecyclerView.Adapter<AdapterNoteCardView.ViewHolder> {

    private Cursor cursor;
    private Listener listener;

    public static interface Listener{
        public void onClick(int id);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        public ViewHolder (CardView v){
            super(v);
            cardView = v;
        }
    }

    @Override
    public AdapterNoteCardView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card_view, parent, false);
        return new ViewHolder(cv);
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;

        TextView name = cardView.findViewById(R.id.note_name);

        if(cursor.moveToPosition(position)) {
            SubjectNote subjectNote = new SubjectNote(cursor);

            name.setText(subjectNote.getName());
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    if (cursor.moveToPosition(position))
                    listener.onClick(cursor.getInt(0));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public AdapterNoteCardView(Cursor cursor){
        this.cursor = cursor;
    }

    public void CloseCursor(){
        if(cursor != null){
            cursor.close();
        }
    }
}

