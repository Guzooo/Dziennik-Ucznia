package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterStatisticsAverage extends RecyclerView.Adapter<AdapterStatisticsAverage.ViewHolder>{
    private Listener listener;
    private Cursor cursor;
    private View ad;

    public interface Listener{
        void onClick(int id);
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private View mainView;
        private TextView title;
        private TextView average1;
        private TextView average2;
        private TextView averageEnd;
        private TextView assessments1;
        private TextView assessments2;

        public ViewHolder(View v, boolean ad){
            super(v);
            mainView = v;
        }

        public ViewHolder(View v){
            super(v);
            mainView = v;
            title = v.findViewById(R.id.title);
            average1 = v.findViewById(R.id.text1);
            average2 = v.findViewById(R.id.text2);
            averageEnd = v.findViewById(R.id.text3);
            assessments1 = v.findViewById(R.id.text1_2);
            assessments2 = v.findViewById(R.id.text2_2);
        }

        private Context getContext(){
            return mainView.getContext();
        }

        private void setTitle(Subject2020 subject){
            String name = subject.getName();
            title.setText(name);
        }

        private void setAverageData(Subject2020 subject){
            ArrayList<Assessment2020> assessments1 = subject.getAssessments(1, getContext());
            ArrayList<Assessment2020> assessments2 = subject.getAssessments(2, getContext());
            setSemester1(assessments1);
            setSemester2(assessments2);
            setSemesterEnd(assessments1, assessments2);
        }

        private void setSemester1(ArrayList<Assessment2020> assessments){
            String averageStr = getAverage(assessments);
            if(averageStr.contains("0.0")) {
                averageStr = getContext().getResources().getString(R.string.lack_assessments);
                assessments1.setText("");
            }else{
                String assessmentsStr = getAssessments(assessments);
                assessments1.setText(assessmentsStr);
            }
            average1.setText(averageStr);
        }

        private void setSemester2(ArrayList<Assessment2020> assessments){
            String averageStr = getAverage(assessments);
            if(averageStr.contains("0.0")){
                averageStr = getContext().getResources().getString(R.string.lack_assessments);
                assessments2.setText("");
            } else {
                String assessmentsStr = getAssessments(assessments);
                assessments2.setText(assessmentsStr);
            }
            average2.setText(averageStr);
        }

        private void setSemesterEnd(ArrayList<Assessment2020> assessments1, ArrayList<Assessment2020> assessments2){
            float average = UtilsAverage.getSubjectFinalAverage(assessments1, assessments2, getContext());
            float roundedAverage = UtilsAverage.roundAverage(average, getContext());
            String set;
            String averageStr = Float.toString(average);
            String separator = getContext().getResources().getString(R.string.separator);
            String roundedAverageStr = String.valueOf(roundedAverage);
            roundedAverageStr = roundedAverageStr.replaceAll("\\.0", "");
            if(!DataManager.isAverageToAssessment(getContext()))
                set = averageStr;
            else
                set = averageStr + separator + roundedAverageStr;
            averageEnd.setText(set);
        }

        private String getAverage(ArrayList<Assessment2020> assessments){
            float average = UtilsAverage.getAverageFromAssessments(assessments, getContext());
            float roundedAverage = UtilsAverage.roundAverage(average, getContext());
            String averageStr = String.valueOf(average);
            String separator = getContext().getResources().getString(R.string.separator);
            String roundedAverageStr = String.valueOf(roundedAverage);
            roundedAverageStr = roundedAverageStr.replaceAll("\\.0", "");
            if(!DataManager.isAverageToAssessment(getContext()))
                return averageStr;
            return averageStr + separator + roundedAverageStr;
        }

        private String getAssessments(ArrayList<Assessment2020> assessments){
            String assessmentsStr = "";
            for(Assessment2020 assessment : assessments)
                assessmentsStr += assessment.getAssessmentToRead() + ", ";
            assessmentsStr = assessmentsStr.trim();
            int endIndex = assessmentsStr.length()-1;
            assessmentsStr = assessmentsStr.substring(0, endIndex);
            return assessmentsStr;//TODO: bajzel taki trzeba tu posprzaątać;
        }

        private void setView(View v){
            if(v.getParent() != null)
                ((ViewGroup) v.getParent()).removeAllViews();
            ((ViewGroup) mainView).addView(v);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 1) {
            View v = new FrameLayout(parent.getContext());
            v.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(v, true);
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.statistics_card_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(ad != null && position == 6){
            holder.setView(ad);
            return;
        }
        if(ad != null && position > 6)
            position--;

        if(cursor.moveToPosition(position)){
            Subject2020 subject = getSubject();
            holder.setTitle(subject);
            holder.setAverageData(subject);
            setOnClickThisView(holder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 6)
            return 1;
        return 0;
    }

    @Override
    public int getItemCount() {
        if(ad != null && cursor.getCount() > 5)
            return cursor.getCount() + 1;
        return cursor.getCount();
    }

    public AdapterStatisticsAverage(Cursor cursor){
        this.cursor = cursor;
    }

    public void setAd(View ad){
        this.ad = ad;
    }

    public void changeData(Cursor cursor){
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    private Subject2020 getSubject(){
        Subject2020 subject = new Subject2020();
        subject.setVariablesOfCursor(cursor);
        return subject;
    }

    private void setOnClickThisView(ViewHolder holder, final int position){
        holder.mainView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(listener != null && cursor.moveToPosition(position))
                    listener.onClick(cursor.getInt(0));
            }
        });
    }
}
