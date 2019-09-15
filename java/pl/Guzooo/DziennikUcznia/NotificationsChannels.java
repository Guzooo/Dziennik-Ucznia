package pl.Guzooo.DziennikUcznia;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

public class NotificationsChannels {

    public static final String CHANNEL_NOTE_ID = "notechannel";

    public static void CreateNotificationsChannels(Context context){
        CreateNotificationChannelNote(context);
    }

    public static void CheckChannelNoteIsActive(Context context){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = manager.getNotificationChannel(CHANNEL_NOTE_ID);
        if(channel.getImportance() == NotificationManager.IMPORTANCE_NONE){
            Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, CHANNEL_NOTE_ID);
            context.startActivity(intent);
        }
    }

    private static void CreateNotificationChannelNote(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = context.getString(R.string.notification_channel_note);
            String description = context.getString(R.string.notification_channel_note_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_NOTE_ID, name, importance);
            channel.setDescription(description);
            context.getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }
}
