package pl.Guzooo.DziennikUcznia;

import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

public class UtilTheme {

    public static void setTheme(Context context){
        int savedTheme = DataManager.getTheme(context);
        AppCompatDelegate.setDefaultNightMode(savedTheme);
    }
}
