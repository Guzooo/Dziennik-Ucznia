package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

public class SubjectNote {

    private int id;
    private String name;
    private String note;
    private int idSubject;
    private ContentValues contentValues = new ContentValues();

    public static final String[] subjectNoteOnCursor = {"_id", "NAME", "NOTE", "TAB_SUBJECT"};

    private SubjectNote(int id, String name, String note, int idSubject){
        this.id = id;
        setName(name);
        setNote(note);
        setIdSubject(idSubject);
    }

    public static SubjectNote newEmpty(){
        return new SubjectNote(0, "", "", 0);
    }

    public static SubjectNote getOfCursor(Cursor cursor){
        return new SubjectNote(cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3));
    }

    public static SubjectNote getOfId(int id, Context context){
        SubjectNote subjectNote;
        SQLiteDatabase db = StaticMethod.getReadableDatabase(context);
        Cursor cursor = db.query("NOTES",
                SubjectNote.subjectNoteOnCursor,
                "_id = ?",
                new String[]{Integer.toString(id)},
                null, null, null);

        if (cursor.moveToFirst()) {
            subjectNote = SubjectNote.getOfCursor(cursor);
        } else {
            subjectNote = SubjectNote.newEmpty();
        }

        cursor.close();
        db.close();
        return subjectNote;
    }

    public void insert(Context context){
        try {
            SQLiteDatabase db = StaticMethod.getWritableDatabase(context);
            db.insert("NOTES", null, contentValues);
            contentValues.clear();
            db.close();
        } catch (SQLiteException e){
            Toast.makeText(context, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    public void update(Context context){
        try {
            SQLiteDatabase db = StaticMethod.getWritableDatabase(context);
            db.update("NOTES",
                    contentValues,
                    "_id = ?",
                    new String[] {Integer.toString(getId())});
            contentValues.clear();
            db.close();
        } catch (SQLException e){
            Toast.makeText(context, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    public void delete(Context context){
        try {
            SQLiteDatabase db = StaticMethod.getWritableDatabase(context);
            db.delete("NOTES",
                    "_id = ?",
                    new String[]{Integer.toString(getId())});
            db.close();
        } catch (SQLiteException e){
            Toast.makeText(context, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    SubjectNote (Cursor cursor){ //old
        this.id = cursor.getInt(0);
        setName(cursor.getString(1));
        setNote(cursor.getString(2));
        this.idSubject = cursor.getInt(3);
    }

    SubjectNote(String name, String note, int idSubject) { //old
        setName(name);
        setNote(note);
        this.idSubject = idSubject;
    }

    public ContentValues saveSubjectNote(){ //old //TODO: Przeniesc cala metode z dodawanie, usuwania, edycji
        ContentValues contentValues = new ContentValues();

        contentValues.put("NAME", getName());
        contentValues.put("NOTE", getNote());
        contentValues.put("TAB_SUBJECT", idSubject);

        return contentValues;
    }

    @Override
    public String toString() { //old
        String string = getName() + "®" + getNote() + "®" + "®" + "®" + "®" + "®" + "®" + "®" + "®" + "®";
        return string;
    }

    public int getId(){
        return id;
    }

    public int getIdSubject(){
        return idSubject;
    }

    public String getName() {
        return name;
    }

    public String getNote() {
        return note;
    }

    public void setName(String name){
        this.name = name;
        contentValues.put("NAME", getName());
    }

    public void setNote(String note){
        this.note = note;
        contentValues.put("NOTE", getNote());
    }

    public void setIdSubject(int id){
        this.idSubject = id;
        contentValues.put("TAB_SUBJECT", getIdSubject());
    }
}
