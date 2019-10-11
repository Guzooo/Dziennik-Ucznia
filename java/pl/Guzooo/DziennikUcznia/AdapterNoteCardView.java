package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterNoteCardView extends RecyclerView.Adapter<AdapterNoteCardView.ViewHolder> {

    private Cursor cursor;
    private Listener listener;
    private Context context;

    private ViewHolderControl holderControl;
    private boolean selectedMode;
    private ArrayList<Integer> selectNotes = new ArrayList<>();
    private ArrayList<SubjectNote> deletedNotes = new ArrayList<>();

    public interface Listener{
        void onClick(int id);
        void refreshCursor();
        String getSubjectName();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private ViewHolder (CardView v){
            super(v);
            cardView = v;
        }

        private boolean changeSelected(){
            cardView.setSelected(!cardView.isSelected());
            if(cardView.isSelected()){
                cardView.setCardBackgroundColor(Color.GRAY);
            } else {
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            return cardView.isSelected();
        }
    }

    private static class ViewHolderControl extends ViewHolder implements View.OnClickListener{
        private View undo;
        private View close;
        private View delete;
        private View share;
        private View add;
        private View undoSeparator;
        private View closeSeparator;
        private View deleteSeparator;
        private View shareSeparator;
        private CheckBox allNotes;
        private TextView selectInfo;

        private ButtonsListener buttonsListener;

        private interface ButtonsListener{
            void Undo();
            void Close();
            void Delete();
            void Share();
            void Add();
            void AllNotes(boolean b);
        }

        private ViewHolderControl (CardView v, ButtonsListener buttonsListener){
            super(v);
            this.buttonsListener = buttonsListener;
            undo = v.findViewById(R.id.undo);
            close = v.findViewById(R.id.close);
            delete = v.findViewById(R.id.delete);
            share = v.findViewById(R.id.share);
            add = v.findViewById(R.id.add);
            undoSeparator = v.findViewById(R.id.undo_separator);
            closeSeparator = v.findViewById(R.id.close_separator);
            deleteSeparator = v.findViewById(R.id.delete_separator);
            shareSeparator = v.findViewById(R.id.share_separator);
            allNotes = v.findViewById(R.id.select_all);
            selectInfo = v.findViewById(R.id.select_info);
            undo.setOnClickListener(this);
            close.setOnClickListener(this);
            delete.setOnClickListener(this);
            share.setOnClickListener(this);
            add.setOnClickListener(this);
            allNotes.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.undo:
                    buttonsListener.Undo();
                    break;

                case R.id.close:
                    buttonsListener.Close();
                    break;

                case R.id.delete:
                    buttonsListener.Delete();
                    break;

                case R.id.share:
                    buttonsListener.Share();
                    break;

                case R.id.add:
                    buttonsListener.Add();
                    break;

                case R.id.select_all:
                    buttonsListener.AllNotes(allNotes.isChecked());
            }
        }

        public void setNumberSelectedNotes(int number, Context context){
            selectInfo.setText(context.getString(R.string.selected_item, number));
        }

        public void VisibilityButtons(int numberNotes, int numberDeletedNotes, boolean selectedMode){
            if(!selectedMode) {
                VisibilityByNumberNotes(numberNotes);
                VisibilityByUndoNotes(numberDeletedNotes);
            }
        }

        private void VisibilityByNumberNotes(int numberNotes){
            if(numberNotes == 0){
                close.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
                share.setVisibility(View.GONE);
                add.setVisibility(View.VISIBLE);
                closeSeparator.setVisibility(View.GONE);
                deleteSeparator.setVisibility(View.GONE);
                shareSeparator.setVisibility(View.GONE);
                allNotes.setVisibility(View.GONE);
                selectInfo.setVisibility(View.GONE);
            } else {
                close.setVisibility(View.GONE);
                delete.setVisibility(View.VISIBLE);
                share.setVisibility(View.VISIBLE);
                add.setVisibility(View.VISIBLE);
                closeSeparator.setVisibility(View.GONE);
                deleteSeparator.setVisibility(View.VISIBLE);
                shareSeparator.setVisibility(View.VISIBLE);
                allNotes.setVisibility(View.GONE);
                selectInfo.setVisibility(View.GONE);
            }
        }

        private void VisibilityByUndoNotes(int numberDeletedNotes){
            if(numberDeletedNotes != 0){
                undo.setVisibility(View.VISIBLE);
                undoSeparator.setVisibility(View.VISIBLE);
            } else {
                undo.setVisibility(View.GONE);
                undoSeparator.setVisibility(View.GONE);
            }
        }

        public final String TYPE_DELETE = "delete";
        public final String TYPE_SHARE = "share";

        public void SetVisibleSelectedMode(String selectType){
            close.setVisibility(View.VISIBLE);
            undo.setVisibility(View.GONE);
            add.setVisibility(View.GONE);
            closeSeparator.setVisibility(View.VISIBLE);
            undoSeparator.setVisibility(View.GONE);
            deleteSeparator.setVisibility(View.GONE);
            shareSeparator.setVisibility(View.GONE);
            allNotes.setVisibility(View.VISIBLE);
            selectInfo.setVisibility(View.VISIBLE);
            allNotes.setChecked(false);
            switch (selectType) {
                case TYPE_DELETE:
                    share.setVisibility(View.GONE);
                    break;
                case TYPE_SHARE:
                    delete.setVisibility(View.GONE);
                    break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return 0;
        return 1;
    }

    @Override
    public AdapterNoteCardView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv;
        if(viewType == 1)
            cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card_view, parent, false);
        else
            cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.note_cotrol_card_view, parent, false);
        return new ViewHolder(cv);
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;

        if(getItemViewType(position) == 0){
            if(holderControl != null)
                cardView = ((ViewHolder) holderControl).cardView;
            else
                holderControl = new ViewHolderControl(cardView, buttonsListener());
            holderControl.VisibilityButtons(cursor.getCount(), deletedNotes.size(), selectedMode);
        } else {
            TextView name = cardView.findViewById(R.id.note_name);
            if (cursor.moveToPosition(position-1)) {
                SubjectNote subjectNote = SubjectNote.getOfCursor(cursor);
                name.setText(subjectNote.getName());
            }

            if((selectNotes.contains(position-1) && !holder.cardView.isSelected()) || (!selectNotes.contains(position -1) && holder.cardView.isSelected()))
                holder.changeSelected();

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null && cursor.moveToPosition(position-1)) {
                        if (selectedMode) {
                            if(holder.changeSelected()){
                                selectNotes.add(position-1);
                                if(cursor.getCount() == selectNotes.size())
                                    holderControl.allNotes.setChecked(true);
                            } else {
                                selectNotes.remove((Integer) (position-1));
                                holderControl.allNotes.setChecked(false);
                            }
                            holderControl.setNumberSelectedNotes(selectNotes.size(), context);
                        } else {
                            listener.onClick(cursor.getInt(0));
                        }
                    }
                }
            });
        }
    }

    private ViewHolderControl.ButtonsListener buttonsListener(){
        return new ViewHolderControl.ButtonsListener() {
            @Override
            public void Undo() {
                for(SubjectNote note : deletedNotes){
                    note.insert(context);
                }
                deletedNotes.clear();
                listener.refreshCursor();
                holderControl.VisibilityButtons(cursor.getCount(), deletedNotes.size(), selectedMode);
                CurentNotesInSubject();
            }

            @Override
            public void Close() {
                selectedMode = false;
                selectNotes.clear();
                notifyDataSetChanged();
                holderControl.VisibilityButtons(cursor.getCount(), deletedNotes.size(), selectedMode);
            }

            @Override
            public void Delete() {
                if(selectedMode){
                    DeleteNotes();
                    selectNotes.clear();
                    listener.refreshCursor();
                    holderControl.VisibilityButtons(cursor.getCount(), deletedNotes.size(), selectedMode);
                } else {
                    holderControl.SetVisibleSelectedMode(holderControl.TYPE_DELETE);
                    holderControl.setNumberSelectedNotes(selectNotes.size(), context);
                }
                selectedMode = !selectedMode;
            }

            private void DeleteNotes(){
                for(int i : selectNotes)
                    if(cursor.moveToPosition(i)) {
                        SubjectNote subjectNote = SubjectNote.getOfCursor(cursor);
                        deletedNotes.add(subjectNote);
                        subjectNote.delete(context);
                    }
                CurentNotesInSubject();
            }

            @Override
            public void Share() {
                if(selectedMode){
                    ShareIntent();
                    selectNotes.clear();
                    listener.refreshCursor();
                    holderControl.VisibilityButtons(cursor.getCount(), deletedNotes.size(), selectedMode);
                } else {
                    holderControl.SetVisibleSelectedMode(holderControl.TYPE_SHARE);
                    holderControl.setNumberSelectedNotes(selectNotes.size(), context);
                }
                selectedMode = !selectedMode;
            }

            private void ShareIntent(){
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getShareText());
                Intent intentChose = Intent.createChooser(intent, context.getString(R.string.share_title));
                context.startActivity(intentChose);
            }

            private String getShareText(){
                String string = "❗" + listener.getSubjectName() + "❗";
                for(int i : selectNotes){
                    if(cursor.moveToPosition(i)){
                        SubjectNote subjectNote = SubjectNote.getOfCursor(cursor);//TODO:w klasie o notatkach statyczna
                        string += "\n\n✔ " + subjectNote.getName();
                        if(!subjectNote.getNote().equals(""))
                            string += ":\n\n" + subjectNote.getNote();
                    }
                }
                return string + context.getString(R.string.share_info);
            }

            @Override
            public void Add() {
                listener.onClick(0);
            }

            @Override
            public void AllNotes(boolean b) {
                for(int i = 0; i < cursor.getCount(); i++){
                    if(b && !selectNotes.contains(i)) {
                        selectNotes.add(i);
                    } else if (!b){
                        selectNotes.clear();
                        break;
                    }
                }
                holderControl.setNumberSelectedNotes(selectNotes.size(), context);
                notifyDataSetChanged();
            }

            private void CurentNotesInSubject(){
                if(cursor.moveToFirst()) {
                    Subject subject = Subject.getOfId(cursor.getInt(0), context);
                    subject.putInfoSizeNotes(context);
                    subject.update(context);
                }
            }
        };
    }

    public void changeCursor(Cursor cursor){
        this.cursor.close();
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cursor.getCount() + 1;
    }

    public AdapterNoteCardView(Cursor cursor, Context context){
        this.cursor = cursor;
        this.context = context;
    }
}

