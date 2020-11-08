package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class AdapterSubjectNotes extends RecyclerView.Adapter<AdapterSubjectNotes.ViewHolder> {

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
        private TextView title;
        private TextView note;

        public ViewHolder(View v){
            super(v);
            mainView = v;
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
    }

    @Override
    public AdapterSubjectNotes.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        if(cursor.moveToPosition(position)){
            Note2020 note = getNote();
            holder.setTitle(note);
            holder.setNote(note);
            setOnClickThisView(holder, position);
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

    private Note2020 getNote(){
        Note2020 note = new Note2020();
        note.setVariablesOfCursor(cursor);
        return note;
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
