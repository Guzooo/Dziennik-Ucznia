package pl.Guzooo.DziennikUcznia;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterPlanCardView extends RecyclerView.Adapter<AdapterPlanCardView.ViewHolder> {

    private Cursor cursor;
    private Listener listener;

    private ArrayList<Integer> days = new ArrayList<>();

    private View nullView;

    public static interface Listener{
        public void onClick(int id);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private View view;
        public ViewHolder (View v){
            super(v);
            view = v;
        }
    }

    @Override
    public AdapterPlanCardView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_card_view, parent, false);
                break;
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_one_text, parent, false);
                break;
        }
        return new ViewHolder(view);
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        View view = holder.view;

        switch (getItemViewType(position)) {
            case 0:
                TextView name = view.findViewById(R.id.plan_name);
                TextView time = view.findViewById(R.id.plan_time);
                TextView classroom = view.findViewById(R.id.plan_classroom);

                final int minus = getAmountTitleOn(position);

                if (cursor.moveToPosition(position - minus)) {
                    SubjectPlan subjectPlan = SubjectPlan.getOfCursor(cursor);
                    Subject subject = Subject.getOfId(subjectPlan.getIdSubject(), view.getContext());

                    name.setText(subject.getName());
                    time.setText(subjectPlan.getTime());
                    classroom.setText(subjectPlan.getClassroom());
                }

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null && cursor.moveToPosition(position - minus)) {
                            listener.onClick(cursor.getInt(0));
                        }
                    }
                });
                break;
            case 1:
                TextView title = view.findViewById(android.R.id.text1);

                if (position == days.get(0)) {
                    title.setText(R.string.monday);
                } else if (position == days.get(1)) {
                    title.setText(R.string.tuesday);
                } else if (position == days.get(2)){
                    title.setText(R.string.wednesday);
                } else if (position == days.get(3)){
                    title.setText(R.string.thursday);
                } else if (position == days.get(4)){
                    title.setText(R.string.friday);
                } else if (position == days.get(5)){
                    title.setText(R.string.saturday);
                } else if (position == days.get(6)){
                    title.setText(R.string.sunday);
                }
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        for (int i = 0; i < days.size(); i++){
            if (position == days.get(i)){
                return 1;
            }
        }
        return 0;
    }

    public void changeCursor(Cursor cursor){
        this.cursor.close();
        this.cursor = cursor;
        days.clear();
        CreateCurrentDaySize();

        if (cursor.moveToFirst()) {
            NumberAllLessonInEveryDay();
        }
        PositionTitles();
        VisibilityNullView();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        int size = cursor.getCount();

        for (int i = 0; i < days.size(); i++){
            if (days.get(i) > -1){
                size ++;
            }
        }
        return size;
    }

    public AdapterPlanCardView(Cursor cursor, View nullCard) {
        this.cursor = cursor;
        nullView = nullCard;

        CreateCurrentDaySize();

        if (cursor.moveToFirst()) {
            NumberAllLessonInEveryDay();
        }
        PositionTitles();
        VisibilityNullView();
    }

    private void CreateCurrentDaySize(){
        for (int i = 0; i < 7; i++) {
            days.add(0);
        }
    }

    private void NumberAllLessonInEveryDay(){
        do {
            SubjectPlan subjectPlan = SubjectPlan.getOfCursor(cursor);
            days.set(subjectPlan.getDay() - 1, days.get(subjectPlan.getDay() - 1) + 1);
        } while (cursor.moveToNext());
    }

    private void PositionTitles(){
        for (int i = 6; i >= 0; i--) {
            if (days.get(i) != 0) {
                days.set(i, 0);
                for (int j = 0; j < i; j++) {
                    if (days.get(j) != 0) {
                        days.set(i, days.get(i) + days.get(j) + 1);
                    }
                }
            } else {
                days.set(i, -1);
            }
        }
    }

    private void VisibilityNullView(){
        if (nullView != null) {
            if (cursor.getCount() == 0) {
                nullView.setVisibility(View.VISIBLE);
            } else {
                nullView.setVisibility(View.GONE);
            }
        }
    }

    private int getAmountTitleOn(int position){
        int m = 0;
        for(int i = 6; i >= 0; i--){
            if(position >= days.get(i) && days.get(i) > -1){
                m++;
                for (int j = 0; j < i; j++){
                    if(days.get(j) > -1){
                        m++;
                    }
                }
                i = -1;
            }
        }
        return m;
    }
}