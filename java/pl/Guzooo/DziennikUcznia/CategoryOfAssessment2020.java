package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.widget.Toast;

public class CategoryOfAssessment2020 extends DatabaseObject{
    private final String PREFERENCE_NAME = "categoryassessment";
    private final String PREFERENCE_DEFAULT_CATEGORY = "defaultcategory";

    public static final String NAME = "NAME";
    public static final String COLOR = "COLOR";
    public static final String WEIGHT = "WEIGHT";

    private String name;
    private String color;
    private int defaultWeight;

    public final static String DATABASE_NAME = "CATEGORY_ASSESSMENT";
    public final static String[] ON_CURSOR = new String[] {
            Database2020.ID,
            NAME,
            COLOR,
            WEIGHT
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
    public void setVariablesOfId(int id, Context context) {
        if(id == 0)
            id = getDefault(context);
        super.setVariablesOfId(id, context);
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
    public void delete(Context context) {
        if(!isDefault(context))
            super.delete(context);
        else
            showToastCantDeleteDefault(context);
    }

    private boolean isDefault(Context context){
        if(getDefault(context) == getId())
            return true;
        return false;
    }

    private int getDefault(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(PREFERENCE_DEFAULT_CATEGORY, 1);
    }

    private void showToastCantDeleteDefault(Context context){
        Toast.makeText(context, R.string.cant_delete_default_assessments_category, Toast.LENGTH_SHORT).show();
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(COLOR, color);
        contentValues.put(WEIGHT, defaultWeight);
        return contentValues;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return Color.parseColor(color);
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getDefaultWeight() {
        return defaultWeight;
    }

    public void setDefaultWeight(int defaultWeight) {
        this.defaultWeight = defaultWeight;
    }
}