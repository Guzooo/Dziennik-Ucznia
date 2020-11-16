package pl.Guzooo.DziennikUcznia;

import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

public class NoteActivity extends AppCompatActivity {

    public static final String EXTRA_ID_NOTE = "idnote";
    public static final String EXTRA_ID_SUBJECT = "idsubject";

    private final int NOTIFICATION_ID = 1000;

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

            case R.id.action_pin:
                pinNote();
                return true;

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
        getSupportActionBar().setDisplayOptions(androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_edit_text);

        editTextTitle = getSupportActionBar().getCustomView().findViewById(R.id.action_bar_edit_text_title);
        editTextTitle.setHint(R.string.title_hint);
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
        string += getString(R.string.share_notes_info);
        return string;
    }

    private void pinNote(){

        Notification.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationsChannels.CheckChannelNoteIsActive(this);
            builder = new Notification.Builder(this, NotificationsChannels.CHANNEL_NOTE_ID);
        }else {
            builder = new Notification.Builder(this);
        }
        builder = builder
                .setSmallIcon(R.drawable.pin)
                .setContentTitle(Subject.getOfId(subjectNote.getIdSubject(), this).getName())
                .setContentText(getNotificationText())
                .setStyle(new android.app.Notification.BigTextStyle()
                        .bigText(editTextNote.getText().toString().trim()))
                .setPriority(Notification.PRIORITY_LOW);
                //.setContentIntent(getNotifyIntent());
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID + subjectNote.getId(), builder.build());
    }

    private String getNotificationText(){
        String returned = editTextTitle.getText().toString().trim();
        if(!subjectNote.getNote().equals(""))
            return returned + getString(R.string.separator) + editTextNote.getText().toString().trim();
        return returned;
    }

  /*  private PendingIntent getNotifyIntent(){
        Log.d("bierze intenta", subjectNote.getName() + " " + subjectNote.getId());
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(EXTRA_ID_NOTE, subjectNote.getId());
        return PendingIntent.getActivity(this, 0, intent, 0);
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
            Toast.makeText(this, R.string.title_hint, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
