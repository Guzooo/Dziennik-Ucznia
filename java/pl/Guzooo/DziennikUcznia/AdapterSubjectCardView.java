package pl.Guzooo.DziennikUcznia;

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

        if(cursor.moveToPosition(position)){
            Subject subject = new Subject(cursor.getString(1));

            name.setText(subject.getName());
            np.setText("NP: " + subject.getUnpreparedness()); //TODO:string
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

    public AdapterSubjectCardView(Cursor cursor, View nullCard){
        CloseCursor();
        this.cursor = cursor;

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
