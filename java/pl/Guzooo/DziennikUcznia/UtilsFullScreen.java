package pl.Guzooo.DziennikUcznia;

import android.view.View;
import android.view.ViewTreeObserver;

import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UtilsFullScreen {

    public static void setUIVisibility(View v){
        v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    public static void setApplyWindowInsets(View v, OnApplyWindowInsetsListener listener){
        ViewCompat.setOnApplyWindowInsetsListener(v, listener);
    }

    public static void setPaddings(View v){//Nie używana
        setPaddings(v, 0,0,0,0);
    }

    public static void setPaddings(View v, GActivity activity){
        ViewCompat.setOnApplyWindowInsetsListener(v, getApplyPaddingsListener(activity));
    }

    public static void setPaddings(View v, int left, int top, int right, int bottom){//Nie Uzywana
        ViewCompat.setOnApplyWindowInsetsListener(v, getApplyPaddingsListener(left, top, right, bottom));
    }

    private static OnApplyWindowInsetsListener getApplyPaddingsListener(final int left, final int top, final int right, final int bottom){//Nie Używana
        return new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                v.setPadding(insets.getSystemWindowInsetLeft() + left,
                        insets.getSystemWindowInsetTop() + top,
                        insets.getSystemWindowInsetRight() + right,
                        insets.getSystemWindowInsetBottom() + bottom);
                return insets;
            }
        };
    }

    private static OnApplyWindowInsetsListener getApplyPaddingsListener(final GActivity activity){
        return new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(final View v, final WindowInsetsCompat insets) {
                ViewTreeObserver viewTreeObserver = v.getViewTreeObserver();
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        v.setPadding(insets.getSystemWindowInsetLeft(),
                                insets.getSystemWindowInsetTop(),
                                insets.getSystemWindowInsetRight(),
                                insets.getSystemWindowInsetBottom() + activity.getBottomPadding());
                    }
                });
                return insets;
            }
        };
    }
}
