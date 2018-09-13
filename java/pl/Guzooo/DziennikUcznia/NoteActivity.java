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

    public static final String EXTRA_ID_NOTE = "idnote";
    public static final String EXTRA_ID_SUBJECT = "idsubject";

    private EditText editTextTitle;
    private EditText editTextNote;

    private SubjectNote subjectNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.action_bar_edit_text);

        editTextTitle = getActionBar().getCustomView().findViewById(R.id.action_bar_edit_text_title);
        editTextNote = findViewById(R.id.note_note);

        if(getIntent().getIntExtra(EXTRA_ID_NOTE, 0) == 0) {
            subjectNote = new SubjectNote(0, "", "", getIntent().getIntExtra(EXTRA_ID_SUBJECT, 0));
        } else {
            try {
                SQLiteOpenHelper openHelper = new HelperDatabase(this);
                SQLiteDatabase db = openHelper.getReadableDatabase();
                Cursor cursor = db.query("NOTES",
                        SubjectNote.subjectNoteOnCursor,
                        "_id = ?",
                        new String[]{Integer.toString(getIntent().getIntExtra(EXTRA_ID_NOTE, 0))},
                        null, null, null, null);
                if (cursor.moveToFirst()) {
                    subjectNote = new SubjectNote(cursor);
                }
                cursor.close();
                db.close();
            } catch (SQLiteException e) {
                Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
            }
        }

        editTextTitle.setHint(R.string.note_hint_name);
        editTextNote.setHint(R.string.note_hint_description);

        if(subjectNote.getId() == 0) {
            findViewById(R.id.note_button_box).setVisibility(View.VISIBLE);
        } else {
            editTextTitle.setText(subjectNote.getName());
            editTextNote.setText(subjectNote.getNote());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(subjectNote.getId() != 0){
            getMenuInflater().inflate(R.menu.note_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case  R.id.action_trash:
                deleteNote();
                finish();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(subjectNote.getId() == 0 || chechSave()){
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(subjectNote.getId() != 0) {
            saveNote();
        }
    }

    public void ClickAdd(View v){
        if(chechSave()) {
            saveNote();
            finish();
        }
    }

    public void ClickCancel(View v){
        finish();
    }

    private void saveNote(){
        subjectNote.setName(editTextTitle.getText().toString().trim());
        subjectNote.setNote(editTextNote.getText().toString().trim());

        try {
            SQLiteOpenHelper openHelper = new HelperDatabase(this);
            SQLiteDatabase db = openHelper.getWritableDatabase();

            if(subjectNote.getId() == 0) {
                db.insert("NOTES", null, subjectNote.saveSubjectNote());

                currentNotes(db);
            } else {
                db.update("NOTES",
                        subjectNote.saveSubjectNote(),
                        "_id = ?",
                        new String[]{Integer.toString(subjectNote.getId())});
            }
            db.close();
        }catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteNote(){
        try {
            SQLiteOpenHelper openHelper = new HelperDatabase(this);
            SQLiteDatabase db = openHelper.getWritableDatabase();
            db.delete("NOTES",
                    "_id = ?",
                    new String[] {Integer.toString(subjectNote.getId())});

            currentNotes(db);

            db.close();
        } catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    private void currentNotes(SQLiteDatabase db){
        try {
            Cursor cursor = db.query("SUBJECTS",
                    Subject.subjectOnCursor,
                    "_id = ?",
                    new String[]{Integer.toString(subjectNote.getIdSubject())},
                    null, null, null);

            if (cursor.moveToFirst()) {
                Subject subject = new Subject(cursor);

                db.update("SUBJECTS",
                        subject.saveSubject(this),
                        "_id = ?",
                        new String[]{Integer.toString(subject.getId())});
            }
            cursor.close();
        }catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean chechSave(){
        if(editTextTitle.getText().toString().trim().equals("")){
            Toast.makeText(this, R.string.note_hint_name, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
