package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class HoldEditText extends FrameLayout implements View.OnLongClickListener{
    //TODO: popracować nad przycinaniem dzieci
    private String prefix = "";
    private String text = "";
    private String hint = "";
    private String info = "";
    private String separator = getResources().getString(R.string.separator);
    private String helpEdit = getResources().getString(R.string.hold_to_edit);

    private View mainView;
    private ViewGroup editMode;
    private EditText editText;
    private ViewGroup normalMode;
    private View goToEdit;
    private TextView textView;

    private ArrayList<EditText> otherEditsInEditMode = new ArrayList<>();
    private OnHoldEditTextChangeVisibilityListener listener;

    public interface OnHoldEditTextChangeVisibilityListener {
        void onShowEditMode();
        void onHideEditMode();
    }

    public static HoldEditText getCustomView (FrameLayout frameLayout,
                                              ViewGroup editMode,
                                              EditText editText,
                                              ViewGroup normalMode,
                                              View goToEdit,
                                              TextView textView,
                                              Context context){
        HoldEditText customView = new HoldEditText(context);
        customView.mainView = frameLayout;
        customView.editMode = editMode;
        customView.editText = editText;
        customView.normalMode = normalMode;
        customView.goToEdit = goToEdit;
        customView.textView = textView;
        customView.setViews();
        return customView;
    }

    public HoldEditText(Context context){
        super(context);
    }

    public HoldEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialization(attrs);
        setViews();
    }

    @Override
    public boolean onLongClick(View v) {
        showEditMode();
        return true;
    }

    public void showEditMode(){
        if(!isOpenEditMode()) {
            refreshEditMode();
            UtilsKeyboard.showKeyboard(editText, getContext());
            UtilsAnimation.showBackgroundView(normalMode, editMode);
            //TODO: animacja zmiany wysokości
            if(listener != null)
                listener.onShowEditMode();
        }
    }

    public void hideEditMode(){
        if(isOpenEditMode()) {
            refreshNormalMode();
            UtilsAnimation.hideBackgroundView(normalMode, editMode);
            //TODO: animacja zmiany wysokości
            if(listener != null)
                listener.onHideEditMode();
        }
    }

    public void setPrefix(String string){
        prefix = string;
        setTextView();
    }

    public void setText(String string){
        text = string;
        setTextView();
    }

    public void setInfo(String string){
        info = string;
        setTextView();
    }

    public void addOtherEditors(EditText newEdit){
        newEdit.setOnEditorActionListener(getEndEditActionListener());
        newEdit.setOnFocusChangeListener(getEndEditFocusChangeListener());
        otherEditsInEditMode.add(newEdit);
    }

    public EditText getEditText(){
         return editText;
    }

    public ArrayList<EditText> getOtherEditsInEditMode(){
        return otherEditsInEditMode;
    }

    public void setOnHoldEditTextChangeVisibilityListener(OnHoldEditTextChangeVisibilityListener listener){
        this.listener = listener;
    }

    private void initialization(AttributeSet attrs){
        LayoutInflater inflater = (LayoutInflater) getContext(). getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.hold_edit_text, this, true);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HoldEditText, 0, 0);
        prefix = getStringOfTyped(a, R.styleable.HoldEditText_prefix);
        text = getStringOfTyped(a, R.styleable.HoldEditText_text);
        hint = getStringOfTyped(a, R.styleable.HoldEditText_hint);
        info = getStringOfTyped(a, R.styleable.HoldEditText_info);
        a.recycle();

        mainView = this;
        editMode = (ViewGroup) getChildAt(0);
        normalMode = (ViewGroup) getChildAt(1);
        editText = (EditText) editMode.getChildAt(0);
        goToEdit = normalMode.getChildAt(0);
        textView = (TextView) normalMode.getChildAt(1);
    }

    private boolean isOpenEditMode(){
        if(editMode.getVisibility() == VISIBLE)
            return true;
        return false;
    }

    private void setViews(){
        setMainView();
        setGoToEdit();
        setTextView();
        setEditText();
    }

    private void setMainView(){
        mainView.setOnLongClickListener(this);
    }

    private void setGoToEdit(){
        goToEdit.setOnClickListener(getShowOnClickListener());
    }

    private void setTextView(){
        setTextInTextView();
        setTextColorInTextView();
    }

    private void setEditText(){
        editText.setHint(hint);
        editText.setOnEditorActionListener(getEndEditActionListener());
        editText.setOnFocusChangeListener(getEndEditFocusChangeListener());
    }

    private void refreshEditMode(){
        UtilsEditText.setText(editText, text);
    }

    private void refreshNormalMode(){
        setTextFromEditText();
        setTextInTextView();
        setTextColorInTextView();
    }

    private String getStringOfTyped(TypedArray a, int styleable){
        String string =  a.getString(styleable);
        if(string == null)
            return "";
        return string;
    }

    private OnClickListener getShowOnClickListener() {
        return new OnClickListener(){
            @Override
            public void onClick(View v) {
                showEditMode();
            }
        };
    }

    private void setTextInTextView(){
        String string = "";
        if(!text.isEmpty()){
            string = prefix + text;
        } else {
            if(!info.isEmpty())
                string = info + separator;
            string += helpEdit;
        }
        textView.setText(string);
    }

    private void setTextColorInTextView(){
        int color;
        if(text.isEmpty())
            color = getColorFromAttrs(R.attr.colorSecondaryDarkG);
        else
            color = getColorFromAttrs(R.attr.colorSecondaryG);
        textView.setTextColor(color);
    }

    private int getColorFromAttrs(int styleable){
        Resources.Theme theme = getContext().getTheme();
        TypedArray a = theme.obtainStyledAttributes(new int[]{styleable});
        return a.getColor(0, 0);
    }

    private TextView.OnEditorActionListener getEndEditActionListener(){
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                hideEditMode();
                Log.d("G HoldEditText", "EditorActionListener");
                return false;
            }
        };
    }

    private OnFocusChangeListener getEndEditFocusChangeListener(){
        return new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    return;
                if(!hasFocusEditsInEditMode())
                    hideEditMode();
            }
        };
    }

    private boolean hasFocusEditsInEditMode(){
        if(editText.isFocused())
            return true;
        for(EditText edit : otherEditsInEditMode)
            if(edit.isFocused())
                return true;
        return false;
    }

    private void setTextFromEditText(){
        text = UtilsEditText.getString(editText);
    }
}
