package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

public class CategoryAssessment {
    private int id;
    private String name;
    private String color;

    public static final String[] onCursor = {"_id", "NAME", "COLOR"};

    public CategoryAssessment (int id, String name, String color){
        this.id = id;
        setName(name);
        setColor(color);
    }

    public static CategoryAssessment newEmpty() {
        return new CategoryAssessment(0, "", "");
    }

    public static CategoryAssessment getOfCursor(Cursor cursor){
        return new CategoryAssessment(cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2));
    }

    public static CategoryAssessment getOfId (int id, Context context){
        CategoryAssessment categoryAssessment;
        SQLiteDatabase db = DatabaseUtils.getReadableDatabase(context);
        Cursor cursor = db.query("CATEGORY_ASSESSMENT",
                CategoryAssessment.onCursor,
                "_id = ?",
                new String[]{Integer.toString(id)},
                null, null, null);

        if(cursor.moveToFirst()) {
            categoryAssessment = CategoryAssessment.getOfCursor(cursor);
        } else {
            categoryAssessment = CategoryAssessment.newEmpty();
        }

        cursor.close();
        db.close();
        return categoryAssessment;
    }

    public void insert(Context context){
        try {
            SQLiteDatabase db = DatabaseUtils.getWritableDatabase(context);
            db.insert("CATEGORY_ASSESSMENT", null, getContentValues());
            db.close();
        } catch (SQLiteException e){
            Toast.makeText(context, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    public void update(Context context){
        try {
            SQLiteDatabase db = DatabaseUtils.getWritableDatabase(context);
            db.update("CATEGORY_ASSESSMENT",
                    getContentValues(),
                    "_id = ?",
                    new String[]{Integer.toString(getId())});
            db.close();
        } catch (SQLiteException e){
            Toast.makeText(context, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    public void delete(Context context){
        try {
            SQLiteDatabase db = DatabaseUtils.getWritableDatabase(context);
            db.delete("CATEGORY_ASSESSMENT",
                    "_id = ?",
                    new String[]{Integer.toString(getId())});
            db.close();
        } catch (SQLiteException e){
            Toast.makeText(context, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    private ContentValues getContentValues (){
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", getName());
        contentValues.put("COLOR", getColor());
        return contentValues;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public static String getForegroundColor(String color){
        String colorOne = color.charAt(1) + "" + color.charAt(2);
        int red = Integer.parseInt(colorOne, 16);
        Log.d("a ", colorOne + " " + red);
        colorOne = color.charAt(3) + "" + color.charAt(4);
        int green = Integer.parseInt(colorOne, 16);
        colorOne = color.charAt(5) + "" + color.charAt(6);
        int blue = Integer.parseInt(colorOne, 16);
        Log.d("Kolorki", "R: " + red + ", G: " + green + ",b " + blue);
        double brightness = Math.sqrt((0.241 * (red * red)) + (0.671 * (green * green)) + (0.068 * (blue * blue)));

        Log.d("jasność czy coś", "oto: " + brightness);
        if(brightness > 128) {
            return "#000000";
        } else {
            return "#ffffff";
        }
    }
}
