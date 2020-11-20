package pl.Guzooo.DziennikUcznia;

import android.os.Build;

public class UtilsFragmentation {

    public static boolean isMinimumLollipop(){
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }
}
