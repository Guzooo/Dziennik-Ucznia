package pl.Guzooo.DziennikUcznia;

import android.widget.EditText;

public class UtilsEditText {

    public static String getString(EditText editText){
        return editText.getText().toString().trim();
    }
}
