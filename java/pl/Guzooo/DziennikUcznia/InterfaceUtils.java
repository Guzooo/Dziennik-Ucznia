package pl.Guzooo.DziennikUcznia;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class InterfaceUtils {

    public static AlertDialog.Builder getAlertDelete(Context context){
        return new AlertDialog.Builder(context, R.style.AppTheme_Dialog_Alarm)
                .setMessage(R.string.you_are_sure)
                .setNegativeButton(R.string.no, null);
    }

    public static AlertDialog.Builder getAlertEmpty(Context context){
        return new AlertDialog.Builder(context, R.style.AppTheme_Dialog_Alarm);
    }
}
