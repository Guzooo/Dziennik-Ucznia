package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import androidx.core.app.NotificationManagerCompat;

public class Note2020 extends DatabaseObject {
    public static final String NAME = "NAME";
    public static final String NOTE = "NOTE";
    public static final String TAB_SUBJECT = "TAB_SUBJECT";

    private String title;
    private String note;
    private int idSubject;

    public final static String DATABASE_NAME = "NOTES";
    public final static String[] ON_CURSOR = new String[] {
            Database2020.ID,
            NAME,
            NOTE,
            TAB_SUBJECT
    };

    @Override
    public String[] onCursor() {
        return ON_CURSOR;
    }

    @Override
    public String databaseName() {
        return DATABASE_NAME;
    }

    private void template(int id,
                          String title,
                          String note,
                          int idSubject){
        setId(id);
        setTitle(title);
        setNote(note);
        setIdSubject(idSubject);
    }

    @Override
    public void setVariablesOfCursor(Cursor cursor) {
        template(cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3));
    }

    @Override
    public void setVariablesEmpty() {
        template(0,
                "",
                "",
                0);
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, title);
        contentValues.put(NOTE, note);
        contentValues.put(TAB_SUBJECT, idSubject);;
        return contentValues;
    }

    @Override
    public void delete(Context context) { //TODO: powinienem ogarnąć by zawsze działało
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(AddNoteFragment.NOTIFICATION_ID + getId());
        super.delete(context);
    }

    public String getShareText(Context context){
        String text = context.getString(R.string.share_notes_title, getTitle());
        if(!getNote().isEmpty())
            text += context.getString(R.string.share_notes_note, getNote());
        return text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getIdSubject() {
        return idSubject;
    }

    public void setIdSubject(int idSubject) {
        this.idSubject = idSubject;
    }
}
