package pl.Guzooo.DziennikUcznia;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NoteActivity extends Activity {

    public static final String EXTRA_ID_NOTE = "idnote";
    public static final String EXTRA_ID_SUBJECT = "idsubject";

//    private final int NOTIFICATION_ID = 035;

    private EditText editTextTitle;
    private EditText editTextNote;

    private SubjectNote subjectNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        editTextNote = findViewById(R.id.note_note);

        setCustomActionBar();

        if(getIntent().getIntExtra(EXTRA_ID_NOTE, 0) == 0) {
            newSubjectNote();
            findViewById(R.id.note_button_box).setVisibility(View.VISIBLE);
        } else {
            readSubjectNote();
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

            case R.id.action_share:
                shareNote();
                return true;

            /*case R.id.action_pin:
                pinNote();
                return true;*/

            case  R.id.action_trash:
                deleteNote();
                return true;

            default:
                shareNote();
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(subjectNote.getId() == 0 || checkSave()){
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
        if(checkSave()) {
            saveNote();
            finish();
        }
    }

    public void ClickCancel(View v){
        finish();
    }

    private void setCustomActionBar(){
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(R.layout.action_bar_edit_text);

        editTextTitle = getActionBar().getCustomView().findViewById(R.id.action_bar_edit_text_title);
        editTextTitle.setHint(R.string.note_hint_name);
    }

    private void newSubjectNote(){
        subjectNote = SubjectNote.newEmpty();
        subjectNote.setIdSubject(getIntent().getIntExtra(EXTRA_ID_SUBJECT, 0));
    }

    private void readSubjectNote(){
        try {
            subjectNote = SubjectNote.getOfId(getIntent().getIntExtra(EXTRA_ID_NOTE, 0), this);
        } catch (SQLiteException e) {
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    private void shareNote(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getShareText());
        Intent intentChose = Intent.createChooser(intent, getString(R.string.share_title));
        startActivity(intentChose);
    }

    private String getShareText(){
        String string = "❗" + Subject.getOfId(subjectNote.getIdSubject(), this).getName() + "❗\n\n✔ " + editTextTitle.getText().toString().trim();
        if(!editTextNote.getText().toString().trim().equals("")){
            string += ":\n\n" + editTextNote.getText().toString().trim();
        }
        string += getString(R.string.share_info);
        return string;
    }

   /* private void pinNote(){
        Notification.Builder builder = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //builder = new Notification.Builder(this, CHANEL_ID);
        }else {
            builder = new Notification.Builder(this);
        }
        Notification notification = builder
                .setSmallIcon(R.drawable.ic_pin)
                .setContentTitle(Subject.getOfId(subjectNote.getIdSubject(), this).getName() + " - " + editTextTitle.getText().toString().trim())
                .setContentText(editTextNote.getText().toString().trim())
                .setPriority(Notification.PRIORITY_MAX)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }*/

    private void saveNote(){
        subjectNote.setName(editTextTitle.getText().toString().trim());
        subjectNote.setNote(editTextNote.getText().toString().trim());

        if(subjectNote.getId() == 0) {
            subjectNote.insert(this);
            currentNotes();
        } else {
            subjectNote.update(this);
        }
    }

    private void deleteNote(){
        InterfaceUtils.getAlertDelete(this)
                .setPositiveButton(R.string.yes, new AlertDialog.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        subjectNote.delete(getApplicationContext());
                        currentNotes();
                        finish();
                    }
                })
                .show();
    }

    private void currentNotes(){ //TODO: Subject zmiana metody zapisu
        Subject subject = Subject.getOfId(subjectNote.getIdSubject(), this);
        subject.putInfoSizeNotes(this);
        subject.update(this);
    }

    private Boolean checkSave(){
        if(editTextTitle.getText().toString().trim().equals("")){
            Toast.makeText(this, R.string.note_hint_name, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
