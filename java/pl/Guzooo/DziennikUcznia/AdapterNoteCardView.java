package pl.Guzooo.DziennikUcznia;

import android.database.Cursor;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AdapterNoteCardView extends RecyclerView.Adapter<AdapterNoteCardView.ViewHolder> {

    private Cursor cursor;
    private Listener listener;

    public interface Listener{
        void onClick(int id);
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
            SubjectNote subjectNote = SubjectNote.getOfCursor(cursor);

            name.setText(subjectNote.getName());
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null && cursor.moveToPosition(position)){
                    listener.onClick(cursor.getInt(0));
                }
            }
        });
    }

    public void changeCursor(Cursor cursor){
        this.cursor.close();
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public AdapterNoteCardView(Cursor cursor){
        this.cursor = cursor;
    }
}

