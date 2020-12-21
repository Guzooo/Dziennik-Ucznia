package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntDef;

import java.util.ArrayList;

public class RecyclerManager extends FrameLayout {

    @IntDef(value =  {NONE, UNSELECTED, DELETE, SHARE})
    private @interface SelectedMode{}

    private static final int NONE = 0;
    private static final int UNSELECTED = 1;
    private static final int DELETE = 2;
    private static final int SHARE = 3;

    private View undo;
    private View cancel;
    private View delete;
    private View share;
    private View add;
    private View undoSpace;
    private View cancelSpace;
    private View deleteSpace;
    private View shareSpace;
    private View selectMode;
    private CheckBox selectAll;
    private TextView selectInfo;

    private boolean selectedMode;
    private int reasonOfSelectedMode = NONE;
    private ArrayList<DatabaseObject> selectObjects = new ArrayList<>();
    private ArrayList<DatabaseObject> deleteObjects = new ArrayList<>();

    private OnRecyclerManagerRequestListener listener;

    interface OnRecyclerManagerRequestListener {
        void onClickAdd();
        void refreshData();
        int getObjectsCount();
        String getSubjectName();
        void clickSelectAll(boolean checked);
        ArrayList<DatabaseObject> getAllObjects();
    }

    public RecyclerManager(Context context, AttributeSet attrs){
        super(context, attrs);
        initialization(attrs);
        setButtons();
        setSelectMode();
    }

    public void resetView(){
        setSelectedMode(NONE);
        setVisibilityOfElements();
    }

    public boolean isSelectedMode(){
        return selectedMode;
    }

    public boolean select(DatabaseObject object){
        if(!selectedMode)
            return false;

        boolean returned;
        if (selectObjects.contains(object)) {
            selectObjects.remove(object);
            returned = false;
        } else {
            selectObjects.add(object);
            returned = true;
        }
        refreshSelectedMode();
        return returned;
    }

    public void setListener(OnRecyclerManagerRequestListener listener){
        this.listener = listener;
    }

    public void setUnselectedSelectedMode(){
        selectObjects.clear();
        setSelectedMode(UNSELECTED);
        setVisibilityOfElements();
    }

    public void setSelectedModeNone(){
        selectObjects.clear();
        setSelectedMode(NONE);
        setVisibilityOfElements();
    }

    private void initialization(AttributeSet a){
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.recycler_manager, this, true);

        undo = findViewById(R.id.undo);
        cancel = findViewById(R.id.cancel);
        delete = findViewById(R.id.delete);
        share = findViewById(R.id.share);
        add = findViewById(R.id.add);
        undoSpace = findViewById(R.id.space2);
        cancelSpace = findViewById(R.id.space3);
        deleteSpace = findViewById(R.id.space4);
        shareSpace = findViewById(R.id.space5);
        selectMode = findViewById(R.id.select_mode);
        selectAll = findViewById(R.id.select_all);
        selectInfo = findViewById(R.id.select_info);
    }

    private void setButtons(){
        undo.setOnClickListener(getOnClickManagementButtonsListener());
        cancel.setOnClickListener(getOnClickManagementButtonsListener());
        delete.setOnClickListener(getOnClickManagementButtonsListener());
        share.setOnClickListener(getOnClickManagementButtonsListener());
        add.setOnClickListener(getOnClickManagementButtonsListener());
    }

    private void setSelectMode(){
        selectAll.setOnClickListener(getOnClickSelectAllListener());
    }

    private void setVisibilityOfElements(){
        setVisibilityOfElementSelectMode();
        setVisibilityOfElementDelete();
        setVisibilityOfElementSelectOption();
    }

    private void refreshSelectedMode(){
        setSelectAll();
        setSelectInfo();
    }

    private OnClickListener getOnClickManagementButtonsListener(){
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){

                    case R.id.undo:
                        clickUndo();
                        return;

                    case R.id.cancel:
                        clickCancel();
                        return;

                    case R.id.delete:
                        clickDelete();
                        return;

                    case R.id.share:
                        clickShare();
                        return;

                    case R.id.add:
                        clickAdd();
                        return;
                }
            }
        };
    }

    private void clickUndo(){
        for(DatabaseObject object : deleteObjects)
            object.insert(getContext());
        deleteObjects.clear();
        listener.refreshData();//wiem ile dodano
        setVisibilityOfElements();
    }

    private void clickCancel(){
        setSelectedMode(NONE);
        selectObjects.clear();
        setVisibilityOfElements();
    }

    private void clickDelete(){
        if(!selectedMode) {
            setSelectedMode(DELETE);
            refreshSelectedMode();
        } else if(selectObjects.size() == 0){
            unselectedToast();
        } else {
            setSelectedMode(NONE);
            doDelete();
            selectObjects.clear();
            listener.refreshData();//wiem które usunąłem
            Toast.makeText(getContext(), getDeleteToast(), Toast.LENGTH_SHORT).show();
        }
        setVisibilityOfElements();
    }

    private void clickShare(){
        if(!selectedMode) {
            setSelectedMode(SHARE);
            refreshSelectedMode();
        } else if(selectObjects.size() == 0){
            unselectedToast();
        } else {
            //nie powinno miec miejsca gdy nie są notatkami;
            setSelectedMode(NONE);
            startShare();
            selectObjects.clear();
        }
        setVisibilityOfElements();
    }

    private void clickAdd() {
        listener.onClickAdd();

        //TODO:powinno wywoływać setVisibilityOfElements()
        //  przyda się kolejne sprzątanie kodu
    }

    private void unselectedToast(){
        Toast.makeText(getContext(), R.string.no_object_was_selected, Toast.LENGTH_SHORT).show();
    }

    private void setSelectedMode(@SelectedMode int reason){
        reasonOfSelectedMode = reason;
        if(reason == NONE) {
            selectedMode = false;
            listener.clickSelectAll(false);
        }else
            selectedMode = true;
    }

    private void doDelete(){
        for(DatabaseObject object : selectObjects)
            object.delete(getContext());
        deleteObjects.addAll(selectObjects);
    }

    private String getDeleteToast(){
        int i = deleteObjects.size();
        return getResources().getQuantityString(R.plurals.done_delete_notes, i, i);
    }

    private void startShare(){
        String text = getShareText();
        String chooseTitle = getResources().getString(R.string.share_title);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        Intent intentChoose = Intent.createChooser(intent, chooseTitle);
        getContext().startActivity(intentChoose);
    }

    private String getShareText(){
        String text = getResources().getString(R.string.share_notes_subject, listener.getSubjectName());
        for(DatabaseObject object : selectObjects){
            Note2020 note = (Note2020) object;
            text += note.getShareText(getContext());
        }
        text += getResources().getString(R.string.share_notes_info);
        return text;
    }

    private OnClickListener getOnClickSelectAllListener(){
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSelectAll();
                refreshSelectedMode();
            }
        };
    }

    private void clickSelectAll(){
        boolean checked = selectAll.isChecked();
        listener.clickSelectAll(checked);
        if(checked) {
            for (DatabaseObject object : listener.getAllObjects())
                if (!selectObjects.contains(object))
                    selectObjects.add(object);
        } else
            selectObjects.clear();
    }

    private void setVisibilityOfElementSelectMode(){
        if(selectedMode){
            selectMode.setVisibility(VISIBLE);
            setVisibilityCancel(true);
            setVisibilityAdd(false);
        } else {
            selectMode.setVisibility(GONE);
            setVisibilityCancel(false);
            setVisibilityAdd(true);
        }
    }

    private void setVisibilityOfElementDelete(){
        if(deleteObjects.size() > 0 && !selectedMode)
            setVisibilityUndo(true);
        else
            setVisibilityUndo(false);
    }

    private void setVisibilityOfElementSelectOption(){
        if(reasonOfSelectedMode == SHARE){
            setVisibilityDelete(false);
            setVisibilityShare(true);
        }else if(reasonOfSelectedMode == DELETE){
            setVisibilityDelete(true);
            setVisibilityShare(false);
        }else if(reasonOfSelectedMode == UNSELECTED || listener.getObjectsCount() > 0){
            setVisibilityDelete(true);
            setVisibilityShare(true);
        }else{
            setVisibilityDelete(false);
            setVisibilityShare(false);
        }
    }

    private void setVisibilityCancel(boolean visible){
        if(visible){
            cancel.setVisibility(VISIBLE);
            cancelSpace.setVisibility(VISIBLE);
        } else {
            cancel.setVisibility(GONE);
            cancelSpace.setVisibility(GONE);
        }
    }

    private void setVisibilityUndo(boolean visible){
        if(visible){
            undo.setVisibility(VISIBLE);
            undoSpace.setVisibility(VISIBLE);
        } else {
            undo.setVisibility(GONE);
            undoSpace.setVisibility(GONE);
        }
    }

    private void setVisibilityDelete(boolean visible){
        if(visible){
            delete.setVisibility(VISIBLE);
            if(reasonOfSelectedMode == DELETE)
                deleteSpace.setVisibility(GONE);
            else
                deleteSpace.setVisibility(VISIBLE);
        } else {
            delete.setVisibility(GONE);
            deleteSpace.setVisibility(GONE);
        }
    }

    private void setVisibilityShare(boolean visible){
        if(visible){
            share.setVisibility(VISIBLE);
            if(selectedMode)
                shareSpace.setVisibility(GONE);
            else
                shareSpace.setVisibility(VISIBLE);
        } else {
            share.setVisibility(GONE);
            shareSpace.setVisibility(GONE);
        }
    }

    private void setVisibilityAdd(boolean visible){
        if(visible){
            add.setVisibility(VISIBLE);
        } else {
            add.setVisibility(GONE);
        }
    }

    private void setSelectAll(){
        if(listener.getObjectsCount() == selectObjects.size())
            selectAll.setChecked(true);
        else
            selectAll.setChecked(false);
    }

    private void setSelectInfo(){
        String text = getResources().getString(R.string.selected_item, selectObjects.size());
        selectInfo.setText(text);
    }
}