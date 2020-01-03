package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.database.Cursor;

public class Note2020 extends DatabaseObject {
    private String title;
    private String note;
    private int idSubject;

    public final static String DATABASE_NAME = "NOTES";
    public final static String[] ON_CURSOR = new String[] {
            "_id",
            "NAME",
            "NOTE",
            "TAB_SUBJECT"
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
        contentValues.put("NAME", title);
        contentValues.put("NOTE", note);
        contentValues.put("TAB_SUBJECT", idSubject);
        return null;
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
