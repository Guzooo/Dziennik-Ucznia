package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class UtilsKeyboard {

    public static void showKeyboard(EditText editText, Context context){
        if(editText.requestFocus()){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
