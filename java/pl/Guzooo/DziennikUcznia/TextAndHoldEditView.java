package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class TextAndHoldEditView extends LinearLayout implements View.OnLongClickListener{

    private String prefix;
    private String text;
    private String info;
    private String hint;
    private String separator;
    private String helpEdit;

    private final String DEFAULT_SEPARATOR = getResources().getString(R.string.separation);
    private final String DEFAULT_HELP_EDIT = getResources().getString(R.string.hold_to_edit);

    private TextView textView;
    private EditText editText;

    private ArrayList<EditText> editTexts = new ArrayList<>();

    private EditText.OnFocusChangeListener onFocusChangeListener;
    private EditText.OnEditorActionListener onEditorActionListener;

    private onChangeViewListener onChangeViewListener;

    public TextAndHoldEditView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextAndHoldEditView, 0, 0);

        prefix = a.getString(R.styleable.TextAndHoldEditView_prefix);
        text = a.getString(R.styleable.TextAndHoldEditView_text);
        info = a.getString(R.styleable.TextAndHoldEditView_info);
        hint = a.getString(R.styleable.TextAndHoldEditView_hint);
        separator = a.getString(R.styleable.TextAndHoldEditView_separator);
        helpEdit = a.getString(R.styleable.TextAndHoldEditView_help_edit);

        a.recycle();

        prefix = nullStringToThis(prefix, "");
        text = nullStringToThis(text, "");
        info = nullStringToThis(info, "");
        hint = nullStringToThis(hint, "");
        separator = nullStringToThis(separator, DEFAULT_SEPARATOR);
        helpEdit = nullStringToThis(helpEdit, DEFAULT_HELP_EDIT);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.text_and_hold_edit_view, this, true);
        setOnLongClickListener(this);

        textView = (TextView) getChildAt(0);
        editText = (EditText) getChildAt(1);

        onEditorActionListener = (new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                onEndEdit();
                Log.d("TextAndHoldEditView", "Przycisnieciee ok na klawce");
                return false;
            }
        });
        onFocusChangeListener = (new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b && !onFocusOtherEditText())
                    onEndEdit();
            }
        });

        editText.setOnEditorActionListener(onEditorActionListener);
        editText.setOnFocusChangeListener(onFocusChangeListener);

        editText.setHint(hint);
        setText();
    }

    @Override
    public boolean onLongClick(View view) {
        textView.setVisibility(GONE);
        editText.setVisibility(VISIBLE);
        editText.setText(text);
        callChangeView();

        if(editText.requestFocus()){
            InputMethodManager manager = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
            manager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
        return true;
    }

    private void onEndEdit(){
        setText(editText.getText().toString());
        editText.setVisibility(GONE);
        textView.setVisibility(VISIBLE);
        callChangeView();
    }

    private boolean onFocusOtherEditText(){
        if(editText.isFocused())
            return true;

        for(int i = 0; i < editTexts.size(); i++){
            if(editTexts.get(i).isFocused()){
                return true;
            }
        }
        return false;
    }

    public TextView getTextView(){
        return textView;
    }

    public EditText getEditText(){
        return editText;
    }

    public void AddEditText(EditText editText){
        editText.setOnEditorActionListener(onEditorActionListener);
        editText.setOnFocusChangeListener(onFocusChangeListener);
        editTexts.add(editText);
    }

    public interface onChangeViewListener{
        void onChangeView(boolean isVisibleText, boolean isEmptyText);
    }

    public void setOnChangeViewListener(onChangeViewListener onChangeViewListener){
        this.onChangeViewListener = onChangeViewListener;
    }

    public void callChangeView(){
        if(onChangeViewListener != null)
            onChangeViewListener.onChangeView(isVisibleText(), isEmptyText());
    }

    public void setText(String string){
        text = string.trim();
        setText();
    }

    public String getText(){
        return text;
    }

    private String nullStringToThis(String string, String defaultText){
        if(string == null)
            return defaultText;
        return string;
    }

    private void setText(){
        String string = "";
        if(isEmptyText()){
            if (!info.equals("")){
                string = info + separator;
            }
            string += helpEdit;
            textView.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            string = prefix + text;
            textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        textView.setText(string);
    }

    private boolean isVisibleText(){
        if(textView.getVisibility() == VISIBLE)
            return true;
        return false;
    }

    private boolean isEmptyText(){
        if(text.equals(""))
            return true;
        return false;
    }

    public void EndEdition(){
        if(editText.getVisibility() != GONE){
            onEndEdit();
        }
    }
}
