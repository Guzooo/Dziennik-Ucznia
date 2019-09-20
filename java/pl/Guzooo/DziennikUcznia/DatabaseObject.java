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
        SetEmpty();
    }

    public abstract void SetOfCursor(Cursor cursor);

    public void SetOfId(int id, Context context){
        try {
            SQLiteDatabase db = DatabaseUtils.getReadableDatabase(context);
            Cursor cursor = db.query(databaseName(),
                    onCursor(),
                    "_id = ?",
                    new String[]{Integer.toString(id)},
                    null, null, null);

            if (cursor.moveToFirst())
                SetOfCursor(cursor);
            else
                SetEmpty();

            cursor.close();
            db.close();
        } catch (SQLiteException e){
            HelperDatabase.ErrorToast(context);
        }
    }

    public abstract void SetEmpty();

    public void Insert(Context context){
        try{
            SQLiteDatabase db = DatabaseUtils.getWritableDatabase(context);
            db.insert(databaseName(), null, getContentValues());
            db.close();
        } catch (SQLiteException e){
            HelperDatabase.ErrorToast(context);
        }
    }

    public void Update(Context context){
        try {
            SQLiteDatabase db = DatabaseUtils.getWritableDatabase(context);
            db.update(databaseName(), getContentValues(), "_id = ?", new String[]{Integer.toString(getId())});
            db.close();
        } catch (SQLiteException e){
            HelperDatabase.ErrorToast(context);
        }
    }

    public void Delete(Context context){
        try {
            SQLiteDatabase db = DatabaseUtils.getWritableDatabase(context);
            db.delete(databaseName(), "_id = ?", new String[]{Integer.toString(getId())});
            db.close();
        } catch (SQLiteException e){
            HelperDatabase.ErrorToast(context);
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
