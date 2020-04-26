package pl.Guzooo.DziennikUcznia;

import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

public class UtilTheme {

    public static void setTheme(Context context){
        int savedTheme = MainSettingsFragment.getTheme(context);
        AppCompatDelegate.setDefaultNightMode(savedTheme);
    }
}
