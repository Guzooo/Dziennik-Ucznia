package pl.Guzooo.DziennikUcznia;

import android.view.View;
import android.view.ViewTreeObserver;

import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class UtilsFullScreen {

    public static void setUIVisibility(View v){
        if(UtilsFragmentation.isMinimumLollipop())
            v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    public static void setPaddings(View v, Fragment fragment){
        GActivity activity = (GActivity) fragment.getActivity();
        WindowInsetsCompat insets = activity.getInsets();
        if(insets == null)
            setPaddings(v, activity);
        else
            setPaddings(v, insets, activity);
    }

    public static void setPaddings(View v, WindowInsetsCompat insets, GActivity activity){
        v.setPadding(insets.getSystemWindowInsetLeft(),
                insets.getSystemWindowInsetTop(),
                insets.getSystemWindowInsetRight(),
                insets.getSystemWindowInsetBottom() + activity.getBottomPadding());

    }

    public static void setPaddings(View v, GActivity activity){
        setApplyWindowInsets(v, getApplyPaddingListener(activity));
    }

    public static void setApplyWindowInsets(View v, OnApplyWindowInsetsListener listener){
        ViewCompat.setOnApplyWindowInsetsListener(v, listener);
    }

    private static OnApplyWindowInsetsListener getApplyPaddingListener(final GActivity activity){
        return new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(final View v, final WindowInsetsCompat insets) {
                setPaddingsOnlyInsert(v, insets);
                ViewTreeObserver.OnGlobalLayoutListener listener = getLayoutListenerPaddingsOfActivity(v, activity);
                ViewTreeObserver viewTreeObserver = v.getViewTreeObserver();
                viewTreeObserver.addOnGlobalLayoutListener(listener);
                return insets;
            }
        };
    }

    private static void setPaddingsOnlyInsert(View v, WindowInsetsCompat insets){
        v.setPadding(insets.getSystemWindowInsetLeft(),
                insets.getSystemWindowInsetTop(),
                insets.getSystemWindowInsetRight(),
                insets.getSystemWindowInsetBottom());
    }

    private static ViewTreeObserver.OnGlobalLayoutListener getLayoutListenerPaddingsOfActivity(final View v, final GActivity activity){
        return new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                v.setPadding(v.getPaddingLeft(),
                        v.getPaddingTop(),
                        v.getPaddingRight(),
                        v.getPaddingBottom() + activity.getBottomPadding());
                v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        };
    }
}
