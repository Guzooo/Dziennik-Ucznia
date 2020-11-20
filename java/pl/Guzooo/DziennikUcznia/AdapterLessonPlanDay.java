package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class AdapterLessonPlanDay extends RecyclerView.Adapter<AdapterLessonPlanDay.ViewHolder> {
    private Listener listener;
    private Cursor cursor;

    public interface Listener{
        void onClick(int id);
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private View mainView;
        private TextView name;
        private TextView time;
        private TextView classroom;

        public ViewHolder(View v){
            super(v);
            mainView = v;
            name = v.findViewById(R.id.text1);
            time = v.findViewById(R.id.text2);
            classroom = v.findViewById(R.id.text3);
        }

        private Context getContext(){
            return mainView.getContext();
        }

        private void setName(String name){
            this.name.setText(name);
        }

        private void setTime(ElementOfPlan2020 elementOfPlan){
            String time = elementOfPlan.getTime();
            this.time.setText(time);
        }

        private void setClassroom(ElementOfPlan2020 elementOfPlan2020){
            String classroom = elementOfPlan2020.getClassroom();
            this.classroom.setText(classroom);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.three_text_card_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(cursor.moveToPosition(position)){
            ElementOfPlan2020 elementOfPlan = getElementOfPlan();
            holder.setName(getSubjectName());
            holder.setTime(elementOfPlan);
            holder.setClassroom(elementOfPlan);
            setOnClickThisView(holder, position);
        }
    }

    private ElementOfPlan2020 getElementOfPlan(){
        ElementOfPlan2020 elementOfPlan = new ElementOfPlan2020();
        elementOfPlan.setVariablesOfCursor(cursor);
        return elementOfPlan;
    }

    private String getSubjectName(){
        int columnSubjectNameIndex = cursor.getColumnIndex(Subject2020.DATABASE_NAME);
        return cursor.getString(columnSubjectNameIndex);
    }

    private void setOnClickThisView(ViewHolder holder, final int position){
        holder.mainView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(listener != null && cursor.moveToPosition(position))
                    listener.onClick(cursor.getInt(0));
            }
        });
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public AdapterLessonPlanDay(Cursor cursor){
        this.cursor = cursor;
    }

    public void changeCursor(Cursor cursor){
        this.cursor = cursor;
        notifyDataSetChanged();
    }
}