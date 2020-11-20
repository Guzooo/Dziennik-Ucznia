package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterSpinnerCategoryOfAssessment extends ArrayAdapter<String> {

    //TODO: przepisać na nowo;
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
            CategoryOfAssessment2020 categoryOfAssessment = new CategoryOfAssessment2020();
            categoryOfAssessment.setVariablesOfCursor(cursor);
            title.setText(categoryOfAssessment.getName());
            label.setColorFilter(categoryOfAssessment.getColor());
        }
        return view;
    }

    @Override
    public long getItemId(int position) {
        if(cursor.moveToPosition(position)){
            CategoryOfAssessment2020 categoryOfAssessment = new CategoryOfAssessment2020();
            categoryOfAssessment.setVariablesOfCursor(cursor);
            return  categoryOfAssessment.getId();
        }
        return 0;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    public int getItemPosition(int id){
        if(cursor.moveToFirst()){
            for(int i = 0; i < cursor.getCount(); i++, cursor.moveToNext())
                if(cursor.getInt(0) == id)
                    return i;
        }
        return 0;
    }

}
