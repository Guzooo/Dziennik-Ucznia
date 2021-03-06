package pl.Guzooo.DziennikUcznia;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class NotificationOnline extends AsyncTask<Void, Void, Boolean>{

    //TODO: zmiany

    private Context context;
    private String informationPage = "https://docs.google.com/document/d/1kKQm-7FRS2Wgqi-ypYa40p2riliaiSuYKzMNWyykYmg/edit?usp=sharing";
    private AlertDialog.Builder alert;

    NotificationOnline(Context context){
        setContext(context);
    }

    private void setContext (Context context){
        this.context = context;
    }

    public static void checkAutomatically(Context context){
        if(getWifiConnecting(context)){
            NotificationOnline notificationOnline = new NotificationOnline(context);
            notificationOnline.execute();
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            String informationString;
            boolean read = true;
            BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(informationPage).openStream()));

            while (((informationString = reader.readLine()) != null && read)){
                if(informationString.contains(context.getPackageName())) {
                    int currentVersion = BuildConfig.VERSION_CODE;
                    String[] strings =  informationString.split(context.getPackageName());
                    String onlineVersion = strings[1];
                    String onlineDescription = strings[2];

                    strings = onlineVersion.split("-");
                    if(strings[0].contains("."))
                        onlineVersion = 0+"";
                    else
                        onlineVersion = strings[0];
                    String finalMark = strings[1];

                    if(finalMark.contains("?")){
                        return false;
                    } else if (finalMark.contains("~")){
                        messageFromTheCreator(onlineDescription);
                        if (finalMark.contains("F")){
                            messageFromTheCreatorWitchFacebookButton();
                        } else if (finalMark.contains("M")){
                            messageFromTheCreatorWitchMessengerButton();
                        }
                        return true;
                    } else if (currentVersion < Integer.valueOf(onlineVersion)){
                        update();
                        if (finalMark.equals("^")){
                            availableUpdate();
                        } else if (finalMark.equals("!")){
                            recommendedUpdate();
                        } else {
                            return false;
                        }
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if(aBoolean && !((Activity) context).isFinishing()) {
            alert.show();
        }
    }

    private void messageFromTheCreator(String description){
        alert = new AlertDialog.Builder(context)
                .setTitle(R.string.information_window_message_from_creator)
                .setMessage(description);
    }

    private void messageFromTheCreatorWitchFacebookButton(){
        alert.setPositiveButton(R.string.setting_facebook, new AlertDialog.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(getIntentPage("https://www.facebook.com/GuzoooApps"));
            }
        });
    }

    private void messageFromTheCreatorWitchMessengerButton(){
        alert.setPositiveButton(R.string.setting_messenger, new androidx.appcompat.app.AlertDialog.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(getIntentPage("https://www.messenger.com/t/GuzoooApps"));
                    }
                });
    }

    private void update(){
        alert = new AlertDialog.Builder(context)
                .setPositiveButton(R.string.information_window_update, new androidx.appcompat.app.AlertDialog.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(getIntentPage("https://play.google.com/store/apps/details?id=pl.Guzooo.DziennikUcznia"));
                    }
                });
    }

    private void availableUpdate(){
        alert.setTitle(R.string.information_window_available_update);
    }

    private void recommendedUpdate(){
        alert.setTitle(R.string.information_window_recommended_update);
    }

    public static boolean getWifiConnecting(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isConnectedOrConnecting();
    }

    private Intent getIntentPage(String url){
        Uri uri = Uri.parse(url);
        return new Intent(Intent.ACTION_VIEW, uri);
    }
}
