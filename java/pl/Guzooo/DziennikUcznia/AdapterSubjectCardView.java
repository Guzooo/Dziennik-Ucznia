package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.sip.SipSession;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AdapterSubjectCardView extends RecyclerView.Adapter<AdapterSubjectCardView.ViewHolder> {

    private Cursor cursor;
    private Listener listener;
    private Context context;

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
    public AdapterSubjectCardView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_card_view, parent, false);
        return new ViewHolder(cv);
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;

        TextView name = cardView.findViewById(R.id.subject_name);
        TextView np = cardView.findViewById(R.id.subject_np);
        TextView note = cardView.findViewById(R.id.subject_note);

        if(cursor.moveToPosition(position)){
            Subject subject = new Subject(cursor);

            name.setText(subject.getName());
            np.setText(context.getResources().getString(R.string.unpreparedness, subject.getUnpreparedness()));
            note.setText(context.getResources().getString(R.string.notes, subject.getSizeNotes(context)));
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    if(cursor.moveToPosition(position)){
                        listener.onClick(cursor.getInt(0));
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public AdapterSubjectCardView(Cursor cursor, View nullCard, Context context){
        CloseCursor();
        this.cursor = cursor;
        this.context = context;

        if(nullCard != null) {
            if (cursor.getCount() == 0) {
                nullCard.setVisibility(View.VISIBLE);
            } else {
                nullCard.setVisibility(View.GONE);
            }
        }
    }

    public void CloseCursor(){
        if(cursor != null){
            cursor.close();
        }
    }
}
