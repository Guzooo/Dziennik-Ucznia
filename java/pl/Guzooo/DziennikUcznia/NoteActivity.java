package pl.Guzooo.DziennikUcznia;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class NoteActivity extends Activity {

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_NUM_NOTE = "numnote";

    private EditText editTextTitle;
    private EditText editTextNote;

    private Subject subject;
    private ArrayList<SubjectNote> subjectNotes;
    private int numOfNote;
    private boolean del = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.action_bar_edit_text);

        editTextTitle = getActionBar().getCustomView().findViewById(R.id.action_bar_edit_text_title);
        editTextNote = findViewById(R.id.note_note);

        numOfNote = getIntent().getIntExtra(EXTRA_NUM_NOTE, -1);

        try {
            SQLiteOpenHelper openHelper = new HelperDatabase(this);
            SQLiteDatabase db = openHelper.getReadableDatabase();
            Cursor cursor = db.query("SUBJECTS",
                    new String[] {"OBJECT"},
                    "_id = ?",
                    new String[] {Integer.toString(getIntent().getIntExtra(EXTRA_ID, 0))},
                    null, null, null, null);
            if(cursor.moveToFirst()) {
                subject = new Subject(cursor.getString(0));
                subjectNotes = subject.getSubjectNotes();
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }

        editTextTitle.setHint(R.string.note_hint_name);
        editTextNote.setHint(R.string.note_hint_description);

        if(numOfNote == -1) {
            findViewById(R.id.note_button_box).setVisibility(View.VISIBLE);
        } else {
            editTextTitle.setText(subjectNotes.get(numOfNote).getName());
            editTextNote.setText(subjectNotes.get(numOfNote).getNote());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(numOfNote != -1){
            getMenuInflater().inflate(R.menu.note_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case  R.id.action_trash:
                subjectNotes.remove(numOfNote);
                del = true;
                saveSubject();
                finish();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(numOfNote == -1 || chechSave()){
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if((numOfNote != -1 && !del)) {
            subjectNotes.get(numOfNote).setName(editTextTitle.getText().toString().trim());
            subjectNotes.get(numOfNote).setNote(editTextNote.getText().toString().trim());
            saveSubject();
        }
    }

    public void ClickAdd(View v){
        if(chechSave()) {
            subjectNotes.add(new SubjectNote(editTextTitle.getText().toString().trim(), editTextNote.getText().toString().trim()));
            saveSubject();
            finish();
        }
    }

    public void ClickCancel(View v){
        finish();
    }

    private void saveSubject(){
        subject.setSubjectNotes(subjectNotes);

        try {
            SQLiteOpenHelper openHelper = new HelperDatabase(this);
            SQLiteDatabase db = openHelper.getWritableDatabase();

            db.update("SUBJECTS",
                    subject.subjectValues(),
                    "_id = ?",
                    new String[] {Integer.toString(getIntent().getIntExtra(EXTRA_ID, 0))});
            db.close();
        }catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean chechSave(){
        if(editTextTitle.getText().toString().trim().equals("") || editTextNote.getText().toString().trim().equals("")){
            Toast.makeText(this, R.string.note_null, Toast.LENGTH_SHORT).show();
            return false;
        }

        if(checkString(editTextTitle) || checkString(editTextNote)){
            Toast.makeText(this, R.string.error_prohibited_sign, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private Boolean checkString(EditText editText){
        Boolean bool = (new String(editText.getText().toString().trim()).indexOf("©") != -1);
        if(!bool){
            bool = (new String(editText.getText().toString().trim()).indexOf("®") != -1);
        }
        return bool;
    }
}
