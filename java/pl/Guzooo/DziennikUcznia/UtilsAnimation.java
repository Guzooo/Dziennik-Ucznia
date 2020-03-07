package pl.Guzooo.DziennikUcznia;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;

public class UtilsAnimation {

    public static void showCircleCenter(View viewForShow, View viewInitial){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[] positions = getAbsoluteCenter(viewInitial);
            float radius = getRadius(viewForShow);
            ViewAnimationUtils.createCircularReveal(viewForShow, positions[0], positions[1], 0, radius).start();
        }
        viewForShow.setVisibility(View.VISIBLE);
    }

    public static void hideCircleCenter(final View viewForHide, View viewInitial){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[] positions = getAbsoluteCenter(viewInitial);
            float radius = getRadius(viewForHide);
            Animator anim = ViewAnimationUtils.createCircularReveal(viewForHide, positions[0], positions[1], radius, 0);
            anim.addListener(getAnimationListenerInvisibleEnd(viewForHide));
            anim.start();
        } else
            viewForHide.setVisibility(View.INVISIBLE);
    }

    private static int[] getAbsoluteCenter(View v){
        int[] positions = new int[2];
        v.getLocationInWindow(positions);
        positions[0] += v.getWidth()/2;
        positions[1] -= v.getHeight()/2;
        return positions;
    }

    private static float getRadius(View v){
        int x = v.getWidth();
        int y = v.getHeight();
        return (float) Math.hypot(x,y);
    }

    private static AnimatorListenerAdapter getAnimationListenerInvisibleEnd(final View v){
        return new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                v.setVisibility(View.INVISIBLE);
            }
        };
    }
}
