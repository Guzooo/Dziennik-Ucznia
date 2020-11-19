package pl.Guzooo.DziennikUcznia;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class MainHomeNotepadFragment extends DialogFragment {
    private final String TAG = "NOTEPAD";

    private HoldEditText notepad;
    private String oldNote;

    private NotepadListener listener;

    public interface NotepadListener{
        void setNotepad(boolean is);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View layout = getActivity().getLayoutInflater().inflate(R.layout.fragment_main_home_notepad, null);
        initialization(layout);
        setOldNote();
        setNotepad();
        return getAlertDialog(layout);
    }

    public void show(NotepadListener listener, FragmentManager manager){
        super.show(manager, TAG);
        this.listener = listener;
    }

    private void initialization(View v){
        notepad = v.findViewById(R.id.notepad);
    }

    private void setOldNote(){
        oldNote = DataManager.getNotepad(getContext());
    }

    private void setNotepad(){
        notepad.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        notepad.setText(oldNote);
        if(oldNote.isEmpty())
            notepad.showEditMode();
    }

    private AlertDialog getAlertDialog(View layout){
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.notepad)
                .setView(layout)
                .setPositiveButton(android.R.string.ok, getSaveNotepadListener())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private DialogInterface.OnClickListener getSaveNotepadListener(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                notepad.hideEditMode();
                String newNote = notepad.getText();
                if(newNote.equals(oldNote))
                    return;
                DataManager.setNotepad(newNote, getContext());
                if(newNote.isEmpty())
                    listener.setNotepad(false);
                else if(oldNote.isEmpty())
                    listener.setNotepad(true);
            }
        };
    }
}