package pl.Guzooo.DziennikUcznia;

import android.view.View;
import android.view.ViewTreeObserver;

import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class UtilsFullScreen {

    public static void setUIVisibility(View v){
        v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    public static void setApplyWindowInsets(View v, OnApplyWindowInsetsListener listener){
        ViewCompat.setOnApplyWindowInsetsListener(v, listener);
    }

    public static void setPaddings(View v, Fragment fragment){
        GActivity activity = (GActivity) fragment.getActivity();
        WindowInsetsCompat insets = activity.getInsets();
        if(insets == null)
            activity.addViewWihoutPaddings(v);
        else
            setPaddings(v, insets, activity);
    }

    public static void setPaddings(View v, GActivity activity){
        ViewCompat.setOnApplyWindowInsetsListener(v, getApplyPaddingListener(activity));
    }

    public static void setPaddings(View v, WindowInsetsCompat insets, GActivity activity){
        v.setPadding(insets.getSystemWindowInsetLeft(),
                insets.getSystemWindowInsetTop(),
                insets.getSystemWindowInsetRight(),
                insets.getSystemWindowInsetBottom() + activity.getBottomPadding());

    }

    private static OnApplyWindowInsetsListener getApplyPaddingListener(final GActivity activity){
        return new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(final View v, final WindowInsetsCompat insets) {
                ViewTreeObserver viewTreeObserver = v.getViewTreeObserver();
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        setPaddings(v, insets, activity);
                    }
                });
                return insets;
            }
        };
    }
}
