package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AdapterSpinnerSubject extends ArrayAdapter<String> {
    private Cursor cursor;

    public AdapterSpinnerSubject(Context context, Cursor cursor){
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
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
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

    public int getItemPosition(int id){
        if(cursor.moveToFirst()){
            for(int i = 0; i < cursor.getCount(); i++, cursor.moveToNext())
                if(cursor.getInt(0) == id)
                    return i+1;
        }
        return 0;
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