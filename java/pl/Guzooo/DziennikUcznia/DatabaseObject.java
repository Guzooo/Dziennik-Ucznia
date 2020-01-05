package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public abstract class DatabaseObject {

    private int id;

    public abstract String[] onCursor();
    public abstract String databaseName();

    public DatabaseObject(){
        setVariablesEmpty();
    }

    public abstract void setVariablesOfCursor(Cursor cursor);

    public void setVariablesOfId(int id, Context context){
        try {
            SQLiteDatabase db = Database2020.getToReading(context);
            Cursor cursor = db.query(databaseName(),
                    onCursor(),
                    "_id = ?",
                    new String[]{Integer.toString(id)},
                    null, null, null);

            if (cursor.moveToFirst())
                setVariablesOfCursor(cursor);
            else
                setVariablesEmpty();

            cursor.close();
            db.close();
        } catch (SQLiteException e){
            Database2020.ErrorToast(context);
        }
    }

    public abstract void setVariablesEmpty();

    public void insert(Context context){
        try{
            SQLiteDatabase db = Database2020.getToWriting(context);
            db.insert(databaseName(), null, getContentValues());
            db.close();
        } catch (SQLiteException e){
            Database2020.ErrorToast(context);
        }
    }

    public void update(Context context){
        try {
            SQLiteDatabase db = Database2020.getToWriting(context);
            db.update(databaseName(), getContentValues(), "_id = ?", new String[]{Integer.toString(getId())});
            db.close();
        } catch (SQLiteException e){
            Database2020.ErrorToast(context);
        }
    }

    public void delete(Context context){
        try {
            SQLiteDatabase db = Database2020.getToWriting(context);
            db.delete(databaseName(), "_id = ?", new String[]{Integer.toString(getId())});
            db.close();
        } catch (SQLiteException e){
            Database2020.ErrorToast(context);
        }
    }

    public abstract ContentValues getContentValues();

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }
}
