package pl.Guzooo.DziennikUcznia;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class AddNoteFragment extends DialogFragment {
    private final String TAG = "ADD_NOTE";
    private final int NOTIFICATION_ID = 1000;

    private Note2020 noteObj;

    private View shareIcon;
    private ImageView pinIcon;
    private View deleteIcon;
    private HoldEditText title;
    private HoldEditText note;

    private OnNoteChangeDataListener listener;
    private boolean pinned;

    public interface OnNoteChangeDataListener {
        void mustRefreshData();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View layout = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_note, null);
        initialization(layout);
        setShareIcon();
        setPinIcon();
        setDeleteIcon();
        setTitle();
        setNote();
        return getAlertDialog(layout);
    }

    public void show(Note2020 note, OnNoteChangeDataListener listener, FragmentManager manager){
        super.show(manager, TAG);
        noteObj = note;
        this.listener = listener;
    }

    private void initialization(View v){
        shareIcon = v.findViewById(R.id.share);
        pinIcon = v.findViewById(R.id.pin);
        deleteIcon = v.findViewById(R.id.delete);
        title = v.findViewById(R.id.title);
        note = v.findViewById(R.id.note);
    }

    private void setShareIcon(){
        shareIcon.setOnClickListener(getOnClickShareListener());
    }

    private void setPinIcon(){
        pinIcon.setOnClickListener(getOnClickPinListener());

        if(isNotificationIsExists())
            setPinned(true);
    }

    private void setDeleteIcon(){
        if(isNewNote())
            deleteIcon.setVisibility(View.GONE);
        else
            deleteIcon.setOnClickListener(getOnClickDeleteListener());
    }

    private void setTitle(){
        title.getEditText().setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        title.getTextView().setLines(2);
        title.getTextView().setEllipsize(TextUtils.TruncateAt.END);
        String text = noteObj.getTitle();
        title.setText(text);
        if(!text.isEmpty())
            title.setEmptyValue(text);
        if(isNewNote())
            title.showEditMode();
    }

    private void setNote(){
        note.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        String text = noteObj.getNote();
        note.setText(text);
    }

    private AlertDialog getAlertDialog(View layout){
        return new AlertDialog.Builder(getContext())
                .setTitle(getDialogTitle())
                .setView(layout)
                .setPositiveButton(android.R.string.ok, getPositiveDialogListener())
                .setNegativeButton(android.R.string.cancel, null)
                .setNeutralButton(getNeutralButton(), getNeutralDialogListener())
                .create();
    }

    private View.OnClickListener getOnClickShareListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAllHoldEditText();
                if(!title.getText().isEmpty())
                    startShare();
                else
                    Toast.makeText(getContext(), R.string.can_not_share_without_title, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void startShare(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getShareText());
        Intent intentChoose = Intent.createChooser(intent, getString(R.string.share_title));
        startActivity(intentChoose);
    }

    private String getShareText(){
        Subject2020 subject = getSubject();
        String text = getString(R.string.share_notes_subject, subject.getName());
        String titleStr = title.getText();
        String noteStr = note.getText();
        text += titleStr;
        if(!noteStr.isEmpty())
            text += noteStr;
        return text;
    }

    private View.OnClickListener getOnClickPinListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAllHoldEditText();
                if(pinned) {
                    unpinNote();
                    Toast.makeText(getContext(), R.string.unpin_note, Toast.LENGTH_SHORT).show();
                } else {
                    pinNote();
                    Toast.makeText(getContext(), R.string.pin_note, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void unpinNote(){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.cancel(NOTIFICATION_ID + noteObj.getId());
        setPinned(false);
    }

    private void pinNote(){
        NotificationsChannels.CheckChannelNoteIsActive(getContext());
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        Notification notification = getNotification();
        notificationManager.notify(NOTIFICATION_ID + noteObj.getId(), notification);
        setPinned(true);
    }

    private void setPinned(boolean b){
        pinned = b;
        if(pinned)
            pinIcon.setImageResource(R.drawable.pinned_pin);
        else
            pinIcon.setImageResource(R.drawable.pin);
    }

    private Notification getNotification(){
        return new NotificationCompat.Builder(getContext(), NotificationsChannels.CHANNEL_NOTE_ID)
                .setSmallIcon(R.drawable.pinned_pin)
                .setContentTitle(getNotificationTitle())
                .setContentText(getNotificationText())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getNotificationBigText()))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setColor(UtilsColor.getColorFromAttrs(R.attr.colorAccentG, getContext()))
                /*.setGroup(noteObj.getIdSubject() + "")TODO: jak tego użyje, to w magiczny sposob juz nic sie nie grupuje*/
                /*.setWhen(System.currentTimeMillis() + 1000 * 60 * 60 * 24)TODO: wykorzystać do odliczania do lekcji*/
                //TODO: otworz ten fragment po klinieciu .setContentIntent()
                .build();
    }

    private String getNotificationTitle(){
        Subject2020 subject = getSubject();
        return subject.getName();
    }

    private Subject2020 getSubject(){
        Subject2020 subject = new Subject2020();
        subject.setVariablesOfId(noteObj.getIdSubject(), getContext());
        return subject;
    }

    private String getNotificationText(){
        String text = title.getText();
        String noteStr = note.getText();
        if(!noteStr.isEmpty())
             return text
                     + getString(R.string.separator)
                     + noteStr;
        return text;
    }

    private String getNotificationBigText(){
        String text = title.getText();
        String noteStr = note.getText();
        if(!noteStr.isEmpty())
            return text
                    + "\n"
                    + noteStr;
        return text;
    }

    private boolean isNotificationIsExists(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            StatusBarNotification[] notifications = notificationManager.getActiveNotifications();
            for(StatusBarNotification notification : notifications)
                if(notification.getId() == NOTIFICATION_ID + noteObj.getId())
                    return true;
        }
        return false;
    }

    private View.OnClickListener getOnClickDeleteListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.want_delete_this_note)
                        .setPositiveButton(android.R.string.yes, getDeleteListener())
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        };
    }

    private DialogInterface.OnClickListener getDeleteListener(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                noteObj.delete(getContext());
                listener.mustRefreshData();
                dismiss();
                unpinNote();
                Toast.makeText(getContext(), R.string.done_delete_this_note, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private int getDialogTitle(){
        if(isNewNote())
            return R.string.title_add_note;
        return R.string.title_edit_note;
    }

    private DialogInterface.OnClickListener getPositiveDialogListener(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveNote();
            }
        };
    }

    private int getNeutralButton(){
        if(isNewNote())
            return R.string.next;
        return R.string.next_new;
    }

    private DialogInterface.OnClickListener getNeutralDialogListener(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveNote();
                dismiss();
                show(getNewNote(), listener, getFragmentManager());
            }
        };
    }

    private Note2020 getNewNote(){
        Note2020 note = new Note2020();
        note.setIdSubject(noteObj.getIdSubject());
        return note;
    }

    private void saveNote(){
        closeAllHoldEditText();
        if(!canSave())
            return;
        noteObj.setTitle(title.getText());
        noteObj.setNote(note.getText());
        if(isNewNote())
            noteObj.insert(getContext());
        else {
            noteObj.update(getContext());
        }
        refreshPin();
        listener.mustRefreshData();
    }

    private void closeAllHoldEditText(){
        title.hideEditMode();
        note.hideEditMode();
    }

    private boolean canSave(){
        if(title.getText().isEmpty()){
            String text = getString(R.string.cant_save)
                    + getString(R.string.separator)
                    + getString(R.string.title_hint);
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isNewNote(){
        if(noteObj.getId() == 0)
            return true;
        return false;
    }

    private void refreshPin(){
        if(pinned)
            pinNote();
    }
}
