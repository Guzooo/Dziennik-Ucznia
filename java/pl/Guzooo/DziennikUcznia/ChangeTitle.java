package pl.Guzooo.DziennikUcznia;


import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;

public class ChangeTitle extends LinearLayout {

    private EditText editText;
    private View negativeButton;
    private View positiveButton;

    private ActionBar actionBar;

    public ChangeTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialization();
        setButton();
        setActionBar(context);
        setVisibility(GONE);
    }

    public void moveViewToActionBar(GActivity activity){
        ((ViewGroup)getParent()).removeView(this);
        getActionBarContainer(activity).addView(this, this.getLayoutParams());
    }

    public void show(){
        if(!isVisible()) {
            setEditText();
            UtilsAnimation.showCircle(this, UtilsAnimation.RIGHT);
        }
    }

    public void hide(){
        if(isVisible())
            UtilsAnimation.hideCircle(this, UtilsAnimation.RIGHT);
    }

    public boolean isVisible(){
        if(getVisibility() == VISIBLE)
            return true;
        return false;
    }

    public void setDefaultValue(String hint){
        editText.setHint(hint);
    }

    private void initialization(){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.change_title, this, true);

        editText = (EditText) getChildAt(0);
        negativeButton = getChildAt(1);
        positiveButton = getChildAt(2);
    }

    private void setButton(){
        negativeButton.setOnClickListener(getOnClickNegativeListener());
        positiveButton.setOnClickListener(getOnClickPositiveListener());
    }

    private void setActionBar(Context context){
        GActivity activity = (GActivity) context;
        actionBar = activity.getSupportActionBar();
    }

    private ViewGroup getActionBarContainer(Activity activity){
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup mainLinear = (ViewGroup) decorView.getChildAt(0);
        ViewGroup mainFrame = (ViewGroup) mainLinear.getChildAt(1);
        ViewGroup decorContentParent = (ViewGroup) mainFrame.getChildAt(0);
        return (ViewGroup) decorContentParent.getChildAt(1);
    }

    private void setEditText() {
        CharSequence title = actionBar.getTitle();
        editText.setText(title);
    }

    private OnClickListener getOnClickNegativeListener(){
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        };
    }

    private OnClickListener getOnClickPositiveListener(){
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActionBarTitle();
                hide();
            }
        };
    }

    private void changeActionBarTitle(){
        String startTitle = actionBar.getTitle().toString();
        String newTitle = getNewText();
        UtilsAnimation.OnChangeTextListener listener = getOnChangeActionBarTitleListener();
        UtilsAnimation.changeText(startTitle, newTitle, listener);
    }

    private String getNewText(){
        String edit = UtilsEditText.getString(editText);
        if(edit.isEmpty())
            return editText.getHint().toString();
        return edit;
    }

    private UtilsAnimation.OnChangeTextListener getOnChangeActionBarTitleListener(){
        return new UtilsAnimation.OnChangeTextListener() {
            @Override
            public void setText(String text) {
                actionBar.setTitle(text);
            }
        };
    }
}