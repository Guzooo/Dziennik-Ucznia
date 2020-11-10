package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;

public class UtilsColor {

    public static int getColorFromAttrs(int styleable, Context context){
        Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(new int[]{styleable});
        return a.getColor(0, 0);
    }

    public static int getForegroundColor(int color){
        Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        double brightness = getBrightness(red, green, blue);
        if(isColorBright(brightness))
            return Color.parseColor("#000000");
        return Color.parseColor("#ffffff");
    }

    private static double getBrightness(int R, int G, int B){
        return Math.sqrt((0.241 * (R * R)) + (0.671 * (G * G)) + (0.068 * (B * B)));
    }

    private static boolean isColorBright(double brightness){
        if(brightness > 128)
            return true;
        return false;
    }
}