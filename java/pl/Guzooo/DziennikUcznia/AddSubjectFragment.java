package pl.Guzooo.DziennikUcznia;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class AddSubjectFragment extends DialogFragment {
    private final String TAG = "ADD_SUBJECT";

    private EditText name;
    private EditText teacher;
    private EditText unpreparedness;
    private EditText description;

    private MainMenuInsertListener insertListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View layout = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_subject, null);
        initialization(layout);
        return getAlertDialog(layout);
    }

    public void show(MainMenuInsertListener insertListener, FragmentManager manager){
        super.show(manager, TAG);
        this.insertListener = insertListener;
    }

    private void initialization(View v){
        name = v.findViewById(R.id.name);
        teacher = v.findViewById(R.id.teacher);
        unpreparedness = v.findViewById(R.id.unpreparedness);
        description = v.findViewById(R.id.description);
    }

    private AlertDialog getAlertDialog(View layout){
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.add_subject_title)
                .setView(layout)
                .setPositiveButton(android.R.string.ok, getInsertDialogListener())
                .setNegativeButton(android.R.string.cancel, null)
                .setNeutralButton(R.string.next, getNextDialogListener())
                .create();
    }

    private DialogInterface.OnClickListener getInsertDialogListener(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                insertSubject();
            }
        };
    }

    private DialogInterface.OnClickListener getNextDialogListener(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                insertSubject();
                dismiss();
                show(insertListener, getFragmentManager());
            }
        };
    }

    private void insertSubject(){
        if(!canSave())
            return;
        Subject2020 subject = new Subject2020();
        subject.setName(getName());
        subject.setTeacher(getTeacher());
        subject.setUnpreparednessDefault(getUnpreparedness());
        subject.setDescription(getDescription());
        subject.insert(getContext());
        insertListener.beforeInsert();
    }

    private boolean canSave(){
        if(getName().isEmpty()){
            String text = getString(R.string.cant_save)
                    + getString(R.string.separator)
                    + getString(R.string.enter_subject_name);
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String getName(){
        return UtilsEditText.getString(name);
    }

    private String getTeacher(){
        return UtilsEditText.getString(teacher);
    }

    private int getUnpreparedness(){
        String str = UtilsEditText.getString(unpreparedness);
        if(str.isEmpty())
            return 0;
        return Integer.parseInt(str);
    }

    private String getDescription(){
        return UtilsEditText.getString(description);
    }
}