package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterStatisticsSubject extends RecyclerView.Adapter<AdapterStatisticsSubject.ViewHolder> {

    private Context context;
    private Cursor cursor;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView semester1;
        private TextView semester2;
        private TextView semesterEnd;
        public ViewHolder (CardView v){
            super(v);
            title = v.findViewById(R.id.subject);
            semester1 = v.findViewById(R.id.semester1);
            semester2 = v.findViewById(R.id.semester2);
            semesterEnd = v.findViewById(R.id.semesterEnd);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.statistics_card_view, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(cursor.moveToPosition(position)) {
            Subject subject = Subject.getOfCursor(cursor);

            holder.title.setText(subject.getName());
            holder.semester1.setText(getSemesterAssessmentAndAverage(1, subject));
            holder.semester2.setText(getSemesterAssessmentAndAverage(2, subject));
            holder.semesterEnd.setText(getAverageEnd(subject));
        }
    }

    private String getSemesterAssessmentAndAverage(int semester, Subject subject){
        ArrayList<SubjectAssessment> assessments = subject.getAssessment(semester, context);
        String returned = /*String.format(Locale.US, "%.2f",*/ Float.toString(subject.getAverage(assessments, context));
        SharedPreferences sharedPreferences = context.getSharedPreferences(SettingActivity.PREFERENCE_NAME, Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean(SettingActivity.PREFERENCE_AVERAGE_TO_ASSESSMENT, SettingActivity.DEFAULT_AVERAGE_TO_ASSESSMENT)){
            returned += context.getResources().getString(R.string.separator) + subject.getRoundedAverage(assessments, sharedPreferences, context);
        }
        return returned + "\n\n" + subject.getStringAssessments(assessments, context);
    }

    private String getAverageEnd(Subject subject){
        ArrayList<SubjectAssessment> assessments1 = subject.getAssessment(1, context);
        ArrayList<SubjectAssessment> assessments2 = subject.getAssessment(2, context);
        String returned = /*String.format(Locale.US, "%.2f", */Float.toString(subject.getAverageEnd(assessments1, assessments2, context));
        SharedPreferences sharedPreferences = context.getSharedPreferences(SettingActivity.PREFERENCE_NAME, Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean(SettingActivity.PREFERENCE_AVERAGE_TO_ASSESSMENT, SettingActivity.DEFAULT_AVERAGE_TO_ASSESSMENT)){
            returned += context.getResources().getString(R.string.separator) + subject.getRoundedAverageEnd(assessments1, assessments2, sharedPreferences, context);
        }
        return returned;
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public AdapterStatisticsSubject (Cursor cursor, Context context){
        this.cursor = cursor;
        this.context = context;
    }
}
