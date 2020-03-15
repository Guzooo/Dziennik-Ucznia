package pl.Guzooo.DziennikUcznia;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Insets;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class PillMenu extends ConstraintLayout {

    private Menu menu;
    private ImageView background;
    private LinearLayout linearLayout;
    private FloatingActionButton layoutFAB;
    private int initialFABid;

    private OnPillMenuItemSelectedListener listener;

    public interface OnPillMenuItemSelectedListener {
        void onPillMenuItemSelected (int id);
    }

    public PillMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialization(attrs);
        setMainView();
        setFAB();
        createButtons();
        setVisibility(INVISIBLE);
    }

    public void setFullScreen(Activity activity){
        ((ViewGroup)getParent()).removeView(this);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ViewGroup vg = (ViewGroup)(activity.getWindow().getDecorView().getRootView());
        vg.addView(this, params);
    }

    public void setOnPillMenuItemSelectedListener(OnPillMenuItemSelectedListener listener){
        this.listener = listener;
    }

    public void show(){
        setPositionFAB();
        setVisibility(VISIBLE);
        ObjectAnimator anim = ObjectAnimator.ofFloat(background, "alpha", 0, 1)
                .setDuration(200);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(layoutFAB, "rotation", 0, 135)
                .setDuration(200);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(anim, anim2);
        ArrayList<Animator> animB = new ArrayList<>();
        animB.add(set);
        for(int i = linearLayout.getChildCount() -1; i >= 0; i--){
            int dlugoscPlus = linearLayout.getChildAt(i).getWidth();
            dlugoscPlus += getRootWindowInsets().getSystemWindowInsetRight();
            dlugoscPlus += getResources().getDimensionPixelOffset(R.dimen.margin_biggest) * 2;
            ObjectAnimator animbi = ObjectAnimator.ofFloat(linearLayout.getChildAt(i), "translationX", dlugoscPlus, 0)
                    .setDuration(200);
            animB.add(animbi);
        }
        AnimatorSet del = new AnimatorSet();
        del.playSequentially(animB);
        del.setDuration(200);
        del.start();
    }

    public void hide(){
        ObjectAnimator anim = ObjectAnimator.ofFloat(background, "alpha", 1, 0)
                .setDuration(200);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(layoutFAB, "rotation", 135, 0)
                .setDuration(200);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(anim, anim2);
        ArrayList<Animator> animB = new ArrayList<>();
        for(int i = 0; i < linearLayout.getChildCount(); i++){
            int dlugoscPlus = linearLayout.getChildAt(i).getWidth();
            dlugoscPlus += getRootWindowInsets().getSystemWindowInsetRight();
            dlugoscPlus += getResources().getDimensionPixelOffset(R.dimen.margin_biggest) * 2;
            ObjectAnimator animbi = ObjectAnimator.ofFloat(linearLayout.getChildAt(i), "translationX", 0, dlugoscPlus)
                    .setDuration(200);
            animB.add(animbi);
        }
        animB.add(set);
        AnimatorSet del = new AnimatorSet();
        del.playSequentially(animB);
        del.setDuration(200);
        final View v = this;
        del.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
               v.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        del.start();
    }

    public boolean isVisible(){
        return (getVisibility() == VISIBLE);
    }

    private void initialization(AttributeSet attrs){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.pill_menu, this, true);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PillMenu, 0, 0);
        initialMenu(a);
        initialFABid(a);
        a.recycle();

        background = (ImageView) getChildAt(0);
        layoutFAB = (FloatingActionButton) getChildAt(1);
        linearLayout = (LinearLayout) getChildAt(2);
    }

    private void setMainView(){
        setOnClickListener(getHideOnClickListener());
    }

    private void setFAB(){
        setOnClickListener(getHideOnClickListener());
    }

    private void createButtons(){
        for(int i = 0; i < menu.size(); i++){
            MenuItem item = menu.getItem(i);
            createButton(item);
        }
    }

    private void initialMenu(TypedArray a){
        int resourceId = a.getResourceId(R.styleable.PillMenu_menu, 0);
        menu = new MenuBuilder(getContext());
        new MenuInflater(getContext()).inflate(resourceId, menu);
    }

    private void initialFABid(TypedArray a){
        int resourceId = a.getResourceId(R.styleable.PillMenu_initialFAB, 0);
        initialFABid = resourceId;
    }

    private void createButton(MenuItem item){
        Button button = (Button) LayoutInflater.from(getContext()).inflate(R.layout.pill_option, null);
        button.setBackgroundResource(R.drawable.pill);
        button.setText(item.getTitle());
        button.setEnabled(item.isEnabled());
        setParams(button);
        setListener(button, item.getItemId());
        addToPillMenu(button);
    }

    private void setPositionFAB(){
        View parent = (View) getParent();
        FloatingActionButton initialFAB = parent.findViewById(initialFABid);
        int correctX = (int) initialFAB.getX();
        int correctY = (int) initialFAB.getY();
        if(!isFABinCorrectPosition(correctX, correctY)){
            LayoutParams params = (ConstraintLayout.LayoutParams) layoutFAB.getLayoutParams();
            params.leftMargin = correctX;
            params.topMargin = correctY;
            layoutFAB.requestLayout();
        }
    }

    private boolean isFABinCorrectPosition(int x, int y){
        if(layoutFAB.getX() != x)
            return false;
        if(layoutFAB.getY() != y)
            return false;
        return true;
    }

    private void setParams(Button button){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.margin_biggest);
        button.setLayoutParams(params);
    }

    private void setListener(Button button, int id){
        OnClickListener clickListener = getMenuOptionClickListener(id);
        button.setOnClickListener(clickListener);
    }

    private OnClickListener getMenuOptionClickListener(final int id){
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onPillMenuItemSelected(id);
                hide();
            }
        };
    }

    private void addToPillMenu(Button button){
        linearLayout.addView(button);
    }

    private OnClickListener getHideOnClickListener(){
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        };
    }
}