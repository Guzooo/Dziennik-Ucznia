package pl.Guzooo.DziennikUcznia;

import android.app.AlertDialog;
import android.content.Context;

public class InterfaceUtils {

    public static AlertDialog.Builder getAlert(Context context){
        return new AlertDialog.Builder(context, R.style.AppTheme_Dialog_Alarm)
                .setMessage(R.string.you_are_sure)
                .setNegativeButton(R.string.no, null);
    }
}
