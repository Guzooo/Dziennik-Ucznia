package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterSpinnerCategoryOfAssessment extends ArrayAdapter<String> {

    private Cursor cursor;

    public AdapterSpinnerCategoryOfAssessment(Context context, Cursor cursor) {
        super(context, 0);

        this.cursor = cursor;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, parent);
    }

    private View createItemView(int position, ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_assessment_view_for_spinner, parent, false);
        TextView title = view.findViewById(R.id.title);
        ImageView label = view.findViewById(R.id.image);

        if(cursor.moveToPosition(position)){
            CategoryAssessment categoryAssessment = CategoryAssessment.getOfCursor(cursor);
            title.setText(categoryAssessment.getName());
            label.setColorFilter(Color.parseColor(categoryAssessment.getColor()));
        }
        return view;
    }

    @Override
    public long getItemId(int position) {
        if(cursor.moveToPosition(position)){
            CategoryAssessment categoryAssessment = CategoryAssessment.getOfCursor(cursor);
            return  categoryAssessment.getId();
        }
        return 0;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }
}
