package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterSubjectNotes extends RecyclerView.Adapter<AdapterSubjectNotes.ViewHolder> {

    private Listener listener;
    private Cursor cursor;

    private ArrayList<Integer> selectedPositions = new ArrayList<>();

    public interface Listener{
        boolean onClick(int id);
        boolean onLongClick(int id);
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CardView mainView;
        private TextView title;
        private TextView note;

        public ViewHolder(View v){
            super(v);
            mainView = (CardView) v;
            title = v.findViewById(R.id.title);
            note = v.findViewById(R.id.text);
        }

        private Context getContext(){
            return mainView.getContext();
        }

        private void setTitle(Note2020 note){
            String title = note.getTitle();
            this.title.setText(title);
        }

        private void setNote(Note2020 note){
            String noteStr = note.getNote();
            if(noteStr.isEmpty())
                this.note.setVisibility(View.GONE);
            else
                this.note.setVisibility(View.VISIBLE);//TODO: jak wszystko ogarne sprawdzić jak działa bez tego
            this.note.setText(noteStr);
        }

        private void setSelected(boolean selected){
            if(selected)
                setSelected();
            else
                setUnselected();
        }

        private void setSelected(){
            mainView.setSelected(false);
            int backgroundColor = UtilsColor.getColorFromAttrs(R.attr.colorAccentG, getContext());
            int foregroundColor = UtilsColor.getForegroundColor(backgroundColor);
            setColors(backgroundColor, foregroundColor);
        }

        private void setUnselected(){
            mainView.setSelected(false);
            int backgroundColor = UtilsColor.getColorFromAttrs(R.attr.colorPrimaryG, getContext());
            int foregroundColor = UtilsColor.getColorFromAttrs(R.attr.colorSecondaryG, getContext());
            setColors(backgroundColor, foregroundColor);
        }

        private void setColors(int background, int foreground){
            mainView.setCardBackgroundColor(background);
            title.setTextColor(foreground);
            note.setTextColor(foreground);
        }
    }

    @Override
    public AdapterSubjectNotes.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        if(cursor.moveToPosition(position)){
            holder.setSelected(selectedPositions.contains(position));

            Note2020 note = getNote();
            holder.setTitle(note);
            holder.setNote(note);
            setOnClickThisView(holder, position);
            setOnLongClickThisView(holder, position);
        }
    }

    @Override
    public int getItemCount(){
        return cursor.getCount();
    }

    public AdapterSubjectNotes(Cursor cursor){
        this.cursor = cursor;
    }

    public void changeCursor(Cursor cursor){
        this.cursor = cursor;//TODO: czyżby nie wystraczy zmienić zaaktualizować kursora i wysłać notifyDataSetChanged() z poziomu oktywności
        notifyDataSetChanged();
    }

    public void selectAll(boolean checked){
        selectedPositions.clear();
        if(checked)
            for(int i = 0; i < getItemCount(); i++)
                selectedPositions.add(i);
        notifyItemRangeChanged(0, getItemCount());
    }

    private Note2020 getNote(){
        Note2020 note = new Note2020();
        note.setVariablesOfCursor(cursor);
        return note;
    }

    private void setOnClickThisView(final ViewHolder holder, final int position){
        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null && cursor.moveToPosition(position)) {
                    boolean selected = listener.onClick(cursor.getInt(0));
                    holder.setSelected(selected);
                }
            }
        });
    }

    private void setOnLongClickThisView(final ViewHolder holder, final int position){
        holder.mainView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(listener != null && cursor.moveToPosition(position)) {
                    boolean selected = listener.onLongClick(cursor.getInt(0));
                    holder.setSelected(selected);
                    return true;
                }
                return false;
            }
        });
    }
}
