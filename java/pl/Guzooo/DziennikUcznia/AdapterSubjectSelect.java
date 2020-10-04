package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AdapterSubjectSelect extends ArrayAdapter<String> {
    private Cursor cursor;

    public AdapterSubjectSelect(Context context, Cursor cursor){
        super(context, 0);
        this.cursor = cursor;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, parent);
    }

    private View createItemView(int position, ViewGroup parent){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_one_text, parent, false);
        TextView text = v.findViewById(android.R.id.text1);

        if(position == 0)
            text.setText(getStartText());
        else if (cursor.moveToPosition(position-1))
            text.setText(getSubjectText());

        return v;
    }

    @Override
    public long getItemId(int position) {
        if(cursor.moveToPosition(position-1)){
            Subject2020 subject = new Subject2020();
            subject.setVariablesOfCursor(cursor);
            return subject.getId();
        }
        return 0;
    }

    @Override
    public int getCount() {
        return cursor.getCount() + 1;
    }

    private String getStartText(){
        if(getCount() == 1)
            return getContext().getString(R.string.plan_edit_null_subject);
        else
            return getContext().getString(R.string.plan_edit_hint_subject);
    }

    private String getSubjectText(){
        Subject2020 subject = new Subject2020();
        subject.setVariablesOfCursor(cursor);
        return subject.getName();
    }
}