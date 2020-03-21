package pl.Guzooo.DziennikUcznia;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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

    public static void showBackgroundView(final View foreground, final View background){
        ObjectAnimator foregroundScaleX = ObjectAnimator.ofFloat(foreground, "scaleX", 1, 1.5f);
        ObjectAnimator foregroundScaleY = ObjectAnimator.ofFloat(foreground, "scaleY", 1, 1.5f);
        ObjectAnimator foregroundAlpha = ObjectAnimator.ofFloat(foreground, "alpha", 1, 0);
        ObjectAnimator backgroundScaleX = ObjectAnimator.ofFloat(background, "scaleX", 0.5f, 1);
        ObjectAnimator backgroundScaleY = ObjectAnimator.ofFloat(background, "scaleY", 0.5f, 1);
        ObjectAnimator backgroundAlpha = ObjectAnimator.ofFloat(background, "alpha", 0, 1);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(foregroundScaleX, foregroundScaleY, foregroundAlpha,
                backgroundScaleX, backgroundScaleY, backgroundAlpha);
        animatorSet.setDuration(200);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                background.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                foreground.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    public static void hideBackgroundView(final View foreground, final View background){
        ObjectAnimator foregroundScaleX = ObjectAnimator.ofFloat(foreground, "scaleX", 1.5f, 1);
        ObjectAnimator foregroundScaleY = ObjectAnimator.ofFloat(foreground, "scaleY", 1.5f, 1);
        ObjectAnimator foregroundAlpha = ObjectAnimator.ofFloat(foreground, "alpha", 0, 1);
        ObjectAnimator backgroundScaleX = ObjectAnimator.ofFloat(background, "scaleX", 1, 0.5f);
        ObjectAnimator backgroundScaleY = ObjectAnimator.ofFloat(background, "scaleY", 1, 0.5f);
        ObjectAnimator backgroundAlpha = ObjectAnimator.ofFloat(background, "alpha", 1, 0);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(foregroundScaleX, foregroundScaleY, foregroundAlpha,
                backgroundScaleX, backgroundScaleY, backgroundAlpha);
        animatorSet.setDuration(200);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                foreground.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                background.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
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
