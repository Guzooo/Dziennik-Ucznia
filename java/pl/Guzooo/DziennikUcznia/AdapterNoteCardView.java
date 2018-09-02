package pl.Guzooo.DziennikUcznia;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterNoteCardView extends RecyclerView.Adapter<AdapterNoteCardView.ViewHolder> {

    private ArrayList<SubjectNote> subjectNotes = new ArrayList<>();
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

        name.setText(subjectNotes.get(position).getName());

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectNotes.size();
    }

    public AdapterNoteCardView(ArrayList<SubjectNote> subjectNotes, View nullCard){
        this.subjectNotes = subjectNotes;

        if(nullCard != null) {
            if (subjectNotes.size() == 0) {
                nullCard.setVisibility(View.VISIBLE);
            } else {
                nullCard.setVisibility(View.GONE);
            }
        }
    }
}

