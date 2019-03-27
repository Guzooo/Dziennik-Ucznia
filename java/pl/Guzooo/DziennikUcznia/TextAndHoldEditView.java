package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TextAndHoldEditView extends LinearLayout implements View.OnLongClickListener {

    private String text;
    private String hint;
    private String separator;
    private String helpEdit;

    private final String DEFAULT_SEPARATOR = getResources().getString(R.string.separation);
    private final String DEFAULT_HELP_EDIT = "hold to edit"; //TODO: na resource

    private TextView textView;
    private EditText editText;

    public TextAndHoldEditView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextAndHoldEditView, 0, 0);

        text = a.getString(R.styleable.TextAndHoldEditView_text);
        hint = a.getString(R.styleable.TextAndHoldEditView_hint);
        separator = a.getString(R.styleable.TextAndHoldEditView_separator);
        helpEdit = a.getString(R.styleable.TextAndHoldEditView_help_edit);

        a.recycle();

        if (separator == null || separator.equals(""))
            separator = DEFAULT_SEPARATOR;
        if (helpEdit == null || helpEdit.equals(""))
            helpEdit = DEFAULT_HELP_EDIT;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.text_and_hold_edit_view, this, true);
        setOnLongClickListener(this);

        textView = (TextView) getChildAt(0);
        editText = (EditText) getChildAt(1);

        setText();
    }

    @Override
    public boolean onLongClick(View view) {
        textView.setVisibility(GONE);
        editText.setVisibility(VISIBLE);
        if(editText.requestFocus()){
            InputMethodManager manager = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
            manager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
        return true;
    }

    public void setText(String string){
        text = string.trim();
        setText();
    }

    public String getText(){
        if(text == null)
            return "";
        return text;
    }

    private void setText(){
        String string = "";
        if(text == null || text.equals("")){
            if (hint != null && !hint.equals("")){
                setHint();
                string = hint + separator;
            }
            string += helpEdit;
            textView.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            string = text;
            textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        textView.setText(string);
    }

    private void setHint(){
        editText.setHint(hint);
    }
}
