package pl.Guzooo.DziennikUcznia;

import android.app.AlertDialog;
import android.content.Context;

public class InterfaceUtils {

    //TODO: stare

    public static AlertDialog.Builder getAlertDelete(Context context){
        return new AlertDialog.Builder(context)
                .setMessage(R.string.you_are_sure)
                .setNegativeButton(R.string.no, null);
    }

    public static AlertDialog.Builder getAlertEmpty(Context context){
        return new AlertDialog.Builder(context);
    }
}
