package pl.Guzooo.DziennikUcznia;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
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
import android.widget.TextView;

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
    private AnimatorSet animShow;
    private AnimatorSet animHide;

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
        setAnimations();
        animShow.start();
    }

    public void hide(){
        if(!animHide.isRunning())
            animHide.start();
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

    private OnClickListener getHideOnClickListener(){
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        };
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
        if(!item.isEnabled())
            return;
        View v = LayoutInflater.from(getContext()).inflate(R.layout.pill_option, null);
        TextView text = v.findViewById(R.id.text);
        text.setText(item.getTitle().toString().toUpperCase());
        setParams(v);
        setListener(v, item.getItemId());
        addToPillMenu(v);
    }

    private void setParams(View v){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.margin_biggest);
        v.setLayoutParams(params);
    }

    private void setListener(View v, int id){
        OnClickListener clickListener = getMenuOptionClickListener(id);
        v.setOnClickListener(clickListener);
    }

    private OnClickListener getMenuOptionClickListener(final int id){
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && !animHide.isRunning())
                    listener.onPillMenuItemSelected(id);
                hide();
            }
        };
    }

    private void addToPillMenu(View v){
        linearLayout.addView(v);
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

    private void setAnimations(){
        if(animShow == null) {
            setButtonPositionBehindScreen();
            setAnimationShow();
        }
        if(animHide == null)
            setAnimationHide();
    }

    private void setButtonPositionBehindScreen(){
        for(int i = 0; i < linearLayout.getChildCount(); i++){
            View currentButton = linearLayout.getChildAt(i);
            int positionBehindScreen = getTranslationPlus(currentButton);
            currentButton.setX(positionBehindScreen);
        }
    }

    private void setAnimationShow(){
        AnimatorSet visible = getVisibility(1, 135);
        ArrayList<Animator> allAnimators = new ArrayList<>();
        allAnimators.add(visible);
        allAnimators.addAll(getButtonsAnimatorShow());
        AnimatorSet fullAnimator = getFullAnimator(allAnimators);
        fullAnimator.addListener(getShowAnimatorListener());
        animShow = fullAnimator;
    }

    private void setAnimationHide(){
        AnimatorSet visible = getVisibility(0 ,0);
        ArrayList<Animator> allAnimators = getButtonsAnimatorHide();
        allAnimators.add(visible);
        AnimatorSet fullAnimator = getFullAnimator(allAnimators);
        fullAnimator.addListener(getHideAnimatorListener());
        animHide = fullAnimator;
    }

    private AnimatorSet getVisibility(int alphaFinal, int rotationFinal){
        AnimatorSet visibility = new AnimatorSet();
        ObjectAnimator alphaBackground = getAlphaBackground(alphaFinal);
        ObjectAnimator rotationFAB = getRotationFAB(rotationFinal);
        visibility.playTogether(alphaBackground, rotationFAB);
        return visibility;
    }

    private ObjectAnimator getAlphaBackground(int alphaFinal){
        return ObjectAnimator.ofFloat(background, "alpha", alphaFinal);
    }

    private ObjectAnimator getRotationFAB(int rotationFinal){
        return ObjectAnimator.ofFloat(layoutFAB, "rotation", rotationFinal);
    }

    private ArrayList<Animator> getButtonsAnimatorHide(){
        ArrayList<Animator> animators = new ArrayList<>();
        for(int i = 0; i < linearLayout.getChildCount(); i++){
            View currentButton = linearLayout.getChildAt(i);
            int translationPlus = getTranslationPlus(currentButton);
            ObjectAnimator currentAnim = getTranslationButton(currentButton, translationPlus);
            animators.add(currentAnim);
        }
        return animators;
    }

    private ArrayList<Animator> getButtonsAnimatorShow(){
        ArrayList<Animator> animators = new ArrayList<>();
        for(int i = linearLayout.getChildCount() -1; i >= 0; i--){
            View currentButton = linearLayout.getChildAt(i);
            ObjectAnimator currentAnim = getTranslationButton(currentButton, 0);
            animators.add(currentAnim);
        }
        return animators;
    }

    private int getTranslationPlus(View v){
        int translationPlus = v.getWidth();
        translationPlus += getResources().getDimensionPixelOffset(R.dimen.margin_biggest) * 2;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            translationPlus += getRootWindowInsets().getSystemWindowInsetRight();
        return translationPlus * 2;//TODO: "* 2" Kill gdy wszystkei będzą enable
    }

    private ObjectAnimator getTranslationButton(View v, int finalTranslationX){
        return ObjectAnimator.ofFloat(v, "translationX", finalTranslationX);
    }

    private AnimatorSet getFullAnimator(ArrayList<Animator> animators){
        AnimatorSet fullAnimator = new AnimatorSet();
        fullAnimator.playSequentially(animators);
        fullAnimator.setDuration(200);
        return fullAnimator;
    }

    private Animator.AnimatorListener getShowAnimatorListener(){
        return new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
    }

    private Animator.AnimatorListener getHideAnimatorListener(){
        return new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                animShow.cancel();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
    }
}