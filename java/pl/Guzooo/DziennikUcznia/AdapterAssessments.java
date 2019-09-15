package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterAssessments extends RecyclerView.Adapter<AdapterAssessments.ViewHolder> {

    private Context context;
    private SQLiteDatabase db;
    private Cursor cursor;
    private Listener listener;

    private int margin;

    public interface Listener{
        void onClick(SubjectAssessment subjectAssessment, int position);
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CardView background;
        private TextView assessment;

        private ViewHolder (CardView cv, int margin, Context context){
            super(cv);
            background = cv;
            assessment = cv.findViewById(R.id.assessment);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    context.getResources().getDimensionPixelSize(R.dimen.assessment_length),
                    context.getResources().getDimensionPixelSize(R.dimen.assessment_length));
            params.setMargins(margin, margin, margin, margin);
            background.setLayoutParams(params);
        }
    }

    @Override
    public AdapterAssessments.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.assessment_view, parent, false);
        return new ViewHolder(cardView, margin, context);
    }

    @Override
    public void onBindViewHolder(final AdapterAssessments.ViewHolder holder, int position) {
        if(cursor.moveToPosition(position)){
            final SubjectAssessment assessment = SubjectAssessment.getOfCursor(cursor);
            holder.assessment.setText(assessment.getAssessment() + "");
            CategoryAssessment categoryAssessment = CategoryAssessment.getOfId(assessment.getCategoryId(), context);
            if(categoryAssessment.getId() == 0)
                categoryAssessment = CategoryAssessment.getOfId(CategoryAssessment.getPreferenceDefaultCategory(context), context);

            holder.background.setCardBackgroundColor(Color.parseColor(categoryAssessment.getColor()));
            holder.assessment.setTextColor(Color.parseColor(categoryAssessment.getForegroundColor()));

            holder.background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null)
                        listener.onClick(assessment, holder.getAdapterPosition());
                }
            });
        }
    }

    public void changeCursor(Cursor cursor){
        this.cursor.close();
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public AdapterAssessments(Cursor cursor, int margin, SQLiteDatabase db, Context context) {
        this.cursor = cursor;
        this.margin = margin;
        this.db = db;
        this.context = context;
    }
}
