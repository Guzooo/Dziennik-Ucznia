package pl.Guzooo.DziennikUcznia;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterSubjectCardView extends RecyclerView.Adapter<AdapterSubjectCardView.ViewHolder> {

    private ArrayList<Cursor> cursors = new ArrayList<>();
    private Listener listener;

    private ArrayList<Integer> days = new ArrayList<>();

    public static interface Listener{
        public void onClick(int id);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private View cardView;
        public ViewHolder (View v){
            super(v);
            cardView = v;
        }
    }

    @Override
    public AdapterSubjectCardView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cv = null;
        switch (viewType) {
            case 0:
                cv = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_card_view, parent, false);
                break;
            case 1:
                cv = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_one_text, parent, false);
                break;
        }
        return new ViewHolder(cv);
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        View cardView = holder.cardView;

        switch (getItemViewType(position)) {
            case 0:
                TextView name = cardView.findViewById(R.id.subject_name);
                TextView np = cardView.findViewById(R.id.subject_np);
                TextView note = cardView.findViewById(R.id.subject_note);

                int m = 0;
                int c = 0;

                for (int i = 7; i >= 0; i--){
                    if(position >= days.get(i) && days.get(i) > -1){
                        m++;
                        for (int j = 0; j < i; j++){
                            if (days.get(j) > -1) {
                                m++;
                                m += cursors.get(j).getCount();
                            }
                        }
                        c = i;
                        i = -1;
                    }
                }

                final int minus = m;
                final int cursor = c;

                if (cursors.get(cursor).moveToPosition(position - minus)) {
                    Subject subject = new Subject(cursors.get(cursor));

                    name.setText(subject.getName());
                    np.setText(cardView.getContext().getResources().getString(R.string.unpreparedness, subject.getUnpreparedness()));
                    note.setText(cardView.getContext().getResources().getString(R.string.notes, subject.getSizeNotes(cardView.getContext())));
                }

                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null && cursors.get(cursor).moveToPosition(position - minus)) {
                            listener.onClick(cursors.get(cursor).getInt(0));
                        }
                    }
                });
                break;

            case 1:
                TextView title = cardView.findViewById(android.R.id.text1);

                if(position == days.get(0)){
                    title.setText(R.string.null_day);
                } else if(position == days.get(1)){
                    title.setText("Dziś");
                } else if(position == days.get(2)){
                    title.setText("Jutro");
                } else if(position == days.get(3)){
                    title.setText("Pojutrze");
                } else if(position == days.get(4)){
                    title.setText("Za 3 dni");
                } else if(position == days.get(5)){
                    title.setText("Za 4 dni");
                } else if(position == days.get(6)){
                    title.setText("Za 5 dni");
                } else if(position == days.get(7)){
                    title.setText("Za 6 dni");
                }
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        for(int i = 0; i < days.size(); i++) {
            if (position == days.get(i)) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        int size = 0;

        for(int i = 0; i < cursors.size(); i++){
            size += cursors.get(i).getCount();
            if(days.get(i) >= 0){
                size++;
            }
        }
        return size;
    }

    public AdapterSubjectCardView(ArrayList<Cursor> cursors, View nullCard){
        this.cursors.add(cursors.get(0));

        for (int i = 0; i <= 7; i++){
            days.add(0);
        }

        int c = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) -1;
        if(c == 0){
            c = 7;
        }

        for (int i = c; i <= 7; i++){
            this.cursors.add(cursors.get(i));
        }

        for (int i = 1; i < c; i++) {
            this.cursors.add(cursors.get(i));
        }

        for (int i = 0; i < this.cursors.size(); i++){
            days.set(i, this.cursors.get(i).getCount());
        }

        for (int i = 7; i >= 0 ; i--) {
            if (days.get(i) != 0) {
                days.set(i, 0);
                for (int j = 0; j < i; j++) {
                    if (days.get(j) != 0) {
                        days.set(i, days.get(i) + days.get(j) + 1);
                    }
                }
            } else {
                days.set(i, days.get(i) - 1);
            }
        }

        if(nullCard != null) {
            if (getItemCount() == 0) {
                nullCard.setVisibility(View.VISIBLE);
            } else {
                nullCard.setVisibility(View.GONE);
            }
        }
    }
}
