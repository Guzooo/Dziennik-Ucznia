package pl.Guzooo.DziennikUcznia;

import android.widget.EditText;

public class UtilsEditText {

    public static int getInt(EditText editText, int defaultInt){
        String string = getString(editText);
        if(string.isEmpty())
            return defaultInt;
        return Integer.valueOf(string);
    }

    public static String getString(EditText editText){
        return editText.getText().toString().trim();
    }

    public static void setText(EditText editText, String text){
        editText.setText(text);
        editText.setSelection(text.length());
    }
}
