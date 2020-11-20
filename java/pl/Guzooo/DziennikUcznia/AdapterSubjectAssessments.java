package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterSubjectAssessments extends RecyclerView.Adapter<AdapterSubjectAssessments.ViewHolder> {

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
        private CardView cardView;
        private TextView weight;
        private TextView assessment;
        private TextView data;

        public ViewHolder(View v){
            super(v);
            mainView = v;
            cardView = v.findViewById(R.id.card_view);
            weight = v.findViewById(R.id.weight);
            assessment = v.findViewById(R.id.assessment);
            data = v.findViewById(R.id.date);

            setWeightVisibility();
        }

        private void setWeightVisibility(){
            boolean averageWeight = DataManager.isAverageWeight(getContext());
            if(!averageWeight)
                weight.setVisibility(View.INVISIBLE);
        }

        private Context getContext(){
            return mainView.getContext();
        }

        private void setColor(Assessment2020 assessment){
            int idCategoryOfAssessment = assessment.getIdCategory();
            CategoryOfAssessment2020 categoryOfAssessment = getCategoryOfAssessment(idCategoryOfAssessment);
            int backgroundColor = categoryOfAssessment.getColor();
            int foregroundColor = UtilsColor.getForegroundColor(backgroundColor);
            cardView.setCardBackgroundColor(backgroundColor);
            weight.setTextColor(foregroundColor);
            this.assessment.setTextColor(foregroundColor);
            data.setTextColor(foregroundColor);
        }

        private void setWeight(Assessment2020 assessment){
            String text = assessment.getRealWeight(getContext()) + "";
            weight.setText(text);
        }

        private void setAssessment(Assessment2020 assessment){
            String text = assessment.getAssessmentToRead();
            this.assessment.setText(text);
        }

        private void setData(Assessment2020 assessment){
            String text = assessment.getDateToRead();
            data.setText(text);
        }

        private CategoryOfAssessment2020 getCategoryOfAssessment(int id){
            CategoryOfAssessment2020 categoryOfAssessment = new CategoryOfAssessment2020();
            categoryOfAssessment.setVariablesOfId(id, getContext());
            return categoryOfAssessment;
        }
    }

    @Override
    public AdapterSubjectAssessments.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.assessment_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        if(cursor.moveToPosition(position)){
            Assessment2020 assessment = getAssessment();
            holder.setColor(assessment);
            holder.setWeight(assessment);
            holder.setAssessment(assessment);
            holder.setData(assessment);
            setOnClickThisView(holder, position);
        }
    }

    @Override
    public int getItemCount(){
        return cursor.getCount();
    }

    public AdapterSubjectAssessments(Cursor cursor){
        this.cursor = cursor;
    }

    public void changeCursor(Cursor cursor){
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    private Assessment2020 getAssessment(){
        Assessment2020 assessment = new Assessment2020();
        assessment.setVariablesOfCursor(cursor);
        return assessment;
    }

    private void setOnClickThisView(ViewHolder holder, final int position){
        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null && cursor.moveToPosition(position))
                    listener.onClick(cursor.getInt(0));
            }
        });
    }
}
