package pl.Guzooo.DziennikUcznia;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class AdapterPlanCardView extends RecyclerView.Adapter<AdapterPlanCardView.ViewHolder> {

    private Cursor cursor;
    private Listener listener;

    private int monday;
    private int tuesday;
    private int wednesday;
    private int thursday;
    private int friday;
    private int saturday;
    private int sunday;

    private int day;

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
    public AdapterPlanCardView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cv = null;
        switch (viewType) {
            case 0:
                cv = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_card_view, parent, false);
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
                TextView name = cardView.findViewById(R.id.plan_name);
                TextView time = cardView.findViewById(R.id.plan_time);
                TextView classroom = cardView.findViewById(R.id.plan_classroom);

                int i = 0;
                if (position > sunday && sunday > -1) {
                    i++;
                    if(monday > -1){
                        i++;
                    }
                    if(tuesday > -1){
                        i++;
                    }
                    if(wednesday > -1){
                        i++;
                    }
                    if(thursday > -1){
                        i++;
                    }
                    if(friday > -1){
                        i++;
                    }
                    if(saturday > -1){
                        i++;
                    }
                } else if (position > saturday && saturday > -1) {
                    i++;
                    if(monday > -1){
                        i++;
                    }
                    if(tuesday > -1){
                        i++;
                    }
                    if(wednesday > -1){
                        i++;
                    }
                    if(thursday > -1){
                        i++;
                    }
                    if(friday > -1){
                        i++;
                    }
                } else if (position > friday && friday > -1){
                    i++;
                    if(monday > -1){
                        i++;
                    }
                    if(tuesday > -1){
                        i++;
                    }
                    if(wednesday > -1){
                        i++;
                    }
                    if(thursday > -1){
                        i++;
                    }
                } else if (position > thursday && thursday > -1){
                    i++;
                    if(monday > -1){
                        i++;
                    }
                    if(tuesday > -1){
                        i++;
                    }
                    if(wednesday > -1){
                        i++;
                    }
                } else if (position > wednesday && wednesday > -1){
                    i++;
                    if(monday > -1){
                        i++;
                    }
                    if(tuesday > -1){
                        i++;
                    }
                } else if (position > tuesday && tuesday > -1){
                    i++;
                    if(monday > -1){
                        i++;
                    }
                } else {
                    i++;
                }

                final int minus = i;

                if (cursor.moveToPosition(position - minus)) {
                    SubjectPlan subjectPlan = new SubjectPlan(cursor);

                    try {
                        SQLiteOpenHelper openHelper = new HelperDatabase(cardView.getContext());
                        SQLiteDatabase db = openHelper.getReadableDatabase();
                        Cursor cursor = db.query("SUBJECTS",
                                Subject.subjectOnCursor,
                                "_id = ?",
                                new String[]{Integer.toString(subjectPlan.getIdSubject())},
                                null, null, null);

                        if (cursor.moveToFirst()) {
                            Subject subject = new Subject(cursor);

                            name.setText(subject.getName());
                        }
                        cursor.close();
                        db.close();
                    } catch (SQLiteException e) {
                        Toast.makeText(cardView.getContext(), R.string.error_database, Toast.LENGTH_SHORT).show();
                    }

                    time.setText(subjectPlan.getTime());
                    classroom.setText(subjectPlan.getClassroom());
                }

                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null && cursor.moveToPosition(position - minus)) {
                            listener.onClick(cursor.getInt(0));
                        }
                    }
                });
                break;
            case 1:
                TextView title = cardView.findViewById(android.R.id.text1);

                if (position == monday) {
                    title.setText(R.string.monday);
                } else if (position == tuesday) {
                    title.setText(R.string.tuesday);
                } else if (position == wednesday){
                    title.setText(R.string.wednesday);
                } else if (position == thursday){
                    title.setText(R.string.thursday);
                } else if (position == friday){
                    title.setText(R.string.friday);
                } else if (position == saturday){
                    title.setText(R.string.saturday);
                } else {
                    title.setText(R.string.sunday);
                }
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == monday || position == tuesday || position == wednesday || position == thursday || position == friday || position == saturday || position == sunday){
            return 1;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return cursor.getCount() + day;
    }

    public AdapterPlanCardView(Cursor cursor, View nullCard) {
        this.cursor = cursor;

        if(cursor.moveToFirst()){
            do{
                SubjectPlan subjectPlan = new SubjectPlan(cursor);

                switch (subjectPlan.getDay()){
                    case 1:
                        monday++;
                        break;
                    case 2:
                        tuesday++;
                        break;
                    case 3:
                        wednesday++;
                        break;
                    case 4:
                        thursday++;
                        break;
                    case 5:
                        friday++;
                        break;
                    case 6:
                        saturday++;
                        break;
                    case 7:
                        sunday++;
                        break;
                }
            }while (cursor.moveToNext());

            if(sunday != 0){
                day++;
                sunday = 0;
                if(monday != 0){
                    sunday += monday + 1;
                }
                if(tuesday != 0){
                    sunday += tuesday + 1;
                }
                if(wednesday != 0){
                    sunday += wednesday + 1;
                }
                if(thursday != 0){
                    sunday += thursday + 1;
                }
                if(friday != 0){
                    sunday += friday + 1;
                }
                if(saturday != 0){
                    sunday += saturday + 1;
                }
            } else {
                sunday--;
            }
            if(saturday != 0){
                day++;
                saturday = 0;
                if(monday != 0){
                    saturday += monday + 1;
                }
                if(tuesday != 0){
                    saturday += tuesday + 1;
                }
                if(wednesday != 0){
                    saturday += wednesday + 1;
                }
                if(thursday != 0){
                    saturday += thursday + 1;
                }
                if(friday != 0){
                    saturday += friday + 1;
                }
            }else {
                saturday--;
            }
            if(friday != 0){
                day++;
                friday = 0;
                if(monday != 0){
                    friday += monday + 1;
                }
                if(tuesday != 0){
                    friday += tuesday + 1;
                }
                if(wednesday != 0){
                    friday += wednesday + 1;
                }
                if(thursday != 0){
                    friday += thursday + 1;
                }
            }else {
                friday--;
            }
            if(thursday != 0){
                day++;
                thursday = 0;
                if(monday != 0){
                    thursday += monday + 1;
                }
                if(tuesday != 0){
                    thursday += tuesday + 1;
                }
                if(wednesday != 0){
                    thursday += wednesday + 1;
                }
            }else {
                thursday--;
            }
            if(wednesday != 0){
                day++;
                wednesday = 0;
                if(monday != 0){
                    wednesday += monday + 1;
                }
                if(tuesday != 0){
                    wednesday += tuesday + 1;
                }
            }else {
                wednesday--;
            }
            if(tuesday != 0){
                day++;
                tuesday = 0;
                if(monday != 0){
                    tuesday += monday + 1;
                }
            }else {
                tuesday--;
            }
            if(monday != 0){
                day++;
                monday = 0;
            }else {
                monday--;
            }
        }

        if (nullCard != null) {
            if (cursor.getCount() == 0) {
                nullCard.setVisibility(View.VISIBLE);
            } else {
                nullCard.setVisibility(View.GONE);
            }
        }
    }
}
