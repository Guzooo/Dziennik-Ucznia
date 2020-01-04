package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.database.Cursor;

public class CategoryOfAssessment2020 extends DatabaseObject{
    private String name;
    private String color;
    private int defaultWeight;

    public final static String DATABASE_NAME = "CATEGORY_ASSESSMENT";
    public final static String[] ON_CURSOR = new String[] {
            "_id",
            "NAME",
            "COLOR",
            "WEIGHT"
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
                          String name,
                          String color,
                          int weight){
        setId(id);
        setName(name);
        setColor(color);
        setDefaultWeight(weight);
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
                "#000000",
                1);
    }

    @Override
    public ContentValues getContentValues() {
        return null;
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
        //TODO:color może nie być jako "Color" lecz jako "String"; należy doczytać
    }

    public int getDefaultWeight() {
        return defaultWeight;
    }

    public void setDefaultWeight(int defaultWeight) {
        this.defaultWeight = defaultWeight;
    }
}
