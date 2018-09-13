package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.database.Cursor;

public class SubjectNote {

    private int id;
    private String name;
    private String note;
    private int idSubject;

    public static final String[] subjectNoteOnCursor = {"_id", "NAME", "NOTE", "TAB_SUBJECT"};


    SubjectNote(int id, String name, String note, int idSubject){
        this.id = id;
        setName(name);
        setNote(note);
        this.idSubject = idSubject;
    }

    SubjectNote (Cursor cursor){
        this.id = cursor.getInt(0);
        setName(cursor.getString(1));
        setNote(cursor.getString(2));
        this.idSubject = cursor.getInt(3);
    }

    SubjectNote(String name, String note, int idSubject) {
        setName(name);
        setNote(note);
        this.idSubject = idSubject;
    }

    public ContentValues saveSubjectNote(){ //TODO: Przeniesc cala metode z dodawanie, usuwania, edycji
        ContentValues contentValues = new ContentValues();

        contentValues.put("NAME", getName());
        contentValues.put("NOTE", getNote());
        contentValues.put("TAB_SUBJECT", idSubject);

        return contentValues;
    }

    @Override
    public String toString() {
        String string = getName() + "®" + getNote() + "®" + "®" + "®" + "®" + "®" + "®" + "®" + "®" + "®";
        return string;
    }

    public int getId(){
        return id;
    }

    public int getIdSubject(){
        return idSubject;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setNote(String note){
        this.note = note;
    }

    public String getNote() {
        return note;
    }
}
