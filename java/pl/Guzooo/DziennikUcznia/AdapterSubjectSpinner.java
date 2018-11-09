package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AdapterSubjectSpinner extends ArrayAdapter<String> {

    private Cursor cursor;

    public AdapterSubjectSpinner(Context context, Cursor cursor) {
        super(context, 0);

        this.cursor = cursor;
    }

    @Override
    public void setDropDownViewResource(int resource) {
        super.setDropDownViewResource(resource);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent){
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_one_text, parent, false);

        TextView name = view.findViewById(android.R.id.text1);

        if(position == 0){
            if(cursor.getCount() == 0){
                name.setText(R.string.plan_edit_null_subject);
            } else {
                name.setText(R.string.plan_edit_hint_subject);
            }
        }

        if(cursor.moveToPosition(position - 1)){
            Subject subject = Subject.getOfCursor(cursor);

            name.setText(subject.getName());
        }

        return view;
    }

    @Override
    public long getItemId(int position) {
        if(cursor.moveToPosition(position - 1)){
            Subject subject = Subject.getOfCursor(cursor);
            return subject.getId();
        }
        return 0;
    }

    @Override
    public int getCount() {
        return cursor.getCount() + 1;
    }
}
