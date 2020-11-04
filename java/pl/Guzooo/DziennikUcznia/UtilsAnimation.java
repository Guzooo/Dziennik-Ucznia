package pl.Guzooo.DziennikUcznia;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.StringRes;

public class UtilsAnimation {

    @IntDef(value = {LEFT, TOP, RIGHT, BOTTOM})
    private @interface Position{}

    public static final int LEFT = 0;
    public static final int TOP = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 3;

    interface OnChangeTextListener {
        void setText(String text);
    }

    public static void showCircleCenter(View viewForShow, View viewInitial){
        int[] circlePositions = getAbsoluteCenter(viewInitial);
        showCircle(viewForShow, circlePositions);
    }

    public static void showCircle(View viewForShow, @Position int position){
        int[] circlePosition = getAbsoluteCenterPosition(viewForShow, position);
        showCircle(viewForShow, circlePosition);

    }

    public static void showCircle(View viewForShow, int[] circlePositions){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            float radius = getRadius(viewForShow);
            ViewAnimationUtils.createCircularReveal(viewForShow, circlePositions[0], circlePositions[1], 0, radius)
                    .start();
        }
        viewForShow.setVisibility(View.VISIBLE);
    }

    public static void hideCircleCenter(View viewForHide, View viewInitial){
        int[] circlePositions = getAbsoluteCenter(viewInitial);
        hideCircle(viewForHide, circlePositions);
    }

    public static void hideCircle(View viewForHide, @Position int position){
        int[] circlePosition = getAbsoluteCenterPosition(viewForHide, position);
        hideCircle(viewForHide, circlePosition);
    }

    public static void  hideCircle(View viewForHide, int[] circlePositions){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            float radius = getRadius(viewForHide);
            Animator anim = ViewAnimationUtils.createCircularReveal(viewForHide, circlePositions[0], circlePositions[1], radius, 0);
            anim.addListener(getAnimationListenerInvisibleEnd(viewForHide));
            anim.start();
        } else
            viewForHide.setVisibility(View.INVISIBLE);
    }

    public static void changeText(TextView textView, @StringRes int newText){
        Context context = textView.getContext();
        String string = context.getString(newText);
        changeText(textView, string);
    }

    public static void changeText(final TextView textView, String newText){
        String startText = textView.getText().toString();
        OnChangeTextListener listener = getOnChangeTextViewListener(textView);
        changeText(startText, newText, listener);
    }

    public static void changeText(final String startText, final String newText, final OnChangeTextListener listener){
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            String currentText = startText;
            @Override
            public void run() {
                if(!currentText.equals(newText)) {
                    if (newText.startsWith(currentText))
                        currentText = newText.substring(0, currentText.length() + 1);
                    else if(newText.endsWith(currentText))
                        currentText = newText.substring(newText.length() - currentText.length()-1);
                    else if(currentText.endsWith(newText) && !newText.isEmpty())
                        currentText = currentText.substring(1);
                    else
                        currentText = currentText.substring(0, currentText.length() - 1);
                    listener.setText(currentText);

                    handler.postDelayed(this, 10);
                }
            }
        });
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
        positions[0] = (int) v.getX() + v.getWidth()/2;
        positions[1] = (int) v.getY() + v.getHeight()/2;
        return positions;
    }

    private static int[] getAbsoluteCenterPosition(View v, @Position int position){
        switch (position){
            case LEFT:
                return getAbsoluteCenterLeft(v);
            case TOP:
                return getAbsoluteCenterTop(v);
            case RIGHT:
                return getAbsoluteCenterRight(v);
            case BOTTOM:
                return getAbsoluteCenterBottom(v);
            default:
                return null;
        }
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

    private static OnChangeTextListener getOnChangeTextViewListener(final TextView textView){
        return new OnChangeTextListener() {
            @Override
            public void setText(String text) {
                textView.setText(text);
            }
        };
    }

    private static int[] getAbsoluteCenterLeft(View v){
        int[] positions = new int[2];
        positions[0] = (int) v.getX();
        positions[1] = (int) v.getY() + v.getHeight()/2;
        return positions;
    }

    private static int[] getAbsoluteCenterTop(View v){
        int[] positions = new int[2];
        positions[0] = (int) v.getX() + v.getWidth()/2;
        positions[1] = (int) v.getY();
        return positions;
    }

    private static int[] getAbsoluteCenterRight(View v){
        int[] positions = new int[2];
        positions[0] = (int) v.getX() + v.getWidth();
        positions[1] = (int) v.getY() + v.getHeight()/2;
        return positions;
    }

    private static int[] getAbsoluteCenterBottom(View v){
        int[] positions = new int[2];
        positions[0] = (int) v.getX() + v.getWidth()/2;
        positions[1] = (int) v.getY() + v.getHeight();
        return positions;
    }
}
