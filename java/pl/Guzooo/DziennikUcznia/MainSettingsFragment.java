package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import java.util.ArrayList;

public class MainSettingsFragment extends MainFragment {

    private final String FACEBOOK = "https://www.facebook.com/GuzoooApps";
    private final String MESSENGER = "https://www.messenger.com/t/GuzoooApps";

    private static final String DEFAULT_THEME(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            return "3";
        return "-1";
    }

    private View logoG;
    private View infoG;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_main_settings, container, false);
        initialization(layout);
        setSettingPreferenceFragment();
        setG();
        setVersionInfo();
        return layout;
    }

    @Override
    public boolean isVisibleAddFAB() {
        return false;
    }

    @Override
    public boolean onBackPressed() {
        if(isInfoOpen()) {
            hideInfoG();
            return true;
        }
        return false;
    }

    private void initialization(View v){
        logoG = v.findViewById(R.id.logo_g);
        infoG = v.findViewById(R.id.info_g);
    }

    private void setSettingPreferenceFragment(){
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.content, new PreferenceSettingsFragment());
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN); //TODO inne animacje
        transaction.commit();
    }

    private void setG(){
        logoG.setOnClickListener(getClickListenerOpenInfo());
        logoG.setOnLongClickListener(TestAndErrorsFix.assessmentToBeltLongListener(getContext()));
        infoG.findViewById(R.id.close).setOnClickListener(getClickListenerCloseInfo());
        infoG.findViewById(R.id.facebook).setOnClickListener(getClickListenerOpenPage(FACEBOOK));
        infoG.findViewById(R.id.messenger).setOnClickListener(getClickListenerOpenPage(MESSENGER));
    }

    private void setVersionInfo(){
        TextView version = infoG.findViewById(R.id.version);
        try {
            String packageName = getActivity().getPackageName();
            PackageInfo info = getActivity().getPackageManager().getPackageInfo(packageName, 0);
            version.setText("v" + info.versionName);
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
    }

    private boolean isInfoOpen(){
        if(infoG.getVisibility() == View.VISIBLE && ViewCompat.isAttachedToWindow(infoG))
            return true;
        return false;
    }

    private View.OnClickListener getClickListenerOpenInfo(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoG();
            }
        };
    }

    private View.OnClickListener getClickListenerCloseInfo(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInfoG();
            }
        };
    }

    private View.OnClickListener getClickListenerOpenPage(final String url){
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openPage(url);
            }
        };
    }

    private void showInfoG(){
        UtilsAnimation.showCircleCenter(infoG, logoG);
    }

    private void hideInfoG(){
        UtilsAnimation.hideCircleCenter(infoG, logoG);
    }

    private void openPage(String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {/*kill*/
        super.onActivityCreated(savedInstanceState);

        final View logo = getActivity().findViewById(R.id.logo_g);
        final View info = getActivity().findViewById(R.id.info_g);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilsAnimation.showCircleCenter(info, logo);
            }
        });
    }

    public static int getTheme(Context context){
        String id = context.getString(R.string.ID_THEME);
        String theme = getPref(context).getString(id, DEFAULT_THEME());
        return Integer.valueOf(theme);
    }

    public static boolean getHardDarkTheme(Context context){
        String id = context.getString(R.string.ID_HARD_DARK);
        boolean def = context.getResources().getBoolean(R.bool.DEFAULT_HARD_DARK);
        return getPref(context).getBoolean(id, def);
    }

    public static boolean getAverageWeight(Context context){
        String id = context.getString(R.string.ID_AVERAGE_WEIGHT);
        boolean def = context.getResources().getBoolean(R.bool.DEFAULT_AVERAGE_WEIGHT);
        return getPref(context).getBoolean(id, def);
    }

    public static boolean getAverageToAssessment(Context context){
        String id = context.getString(R.string.ID_AVERAGE_TO_ASSESSMENT);
        boolean def = context.getResources().getBoolean(R.bool.DEFAULT_AVERAGE_TO_ASSESSMENT);
        return getPref(context).getBoolean(id, def);
    }
//TODO: jesli to są edit teksty to def musi być stringiem, potem z pref bierzemy string, zmieniamy na float i return
    public static float getAverageToSix(Context context){
        String id = context.getString(R.string.ID_AVERAGE_TO_SIX);
        float def = context.getResources().getFloat(R.string.DEFAULT_AVERAGE_TO_SIX);
        return getPref(context).getFloat(id, def);
    }

    public static float getAverageToFive(Context context){
        String id = context.getString(R.string.ID_AVERAGE_TO_FIVE);
        float def = context.getResources().getFloat(R.string.DEFAULT_AVERAGE_TO_FIVE);
        return getPref(context).getFloat(id, def);
    }

    public static float getAverageToFour(Context context){
        String id = context.getString(R.string.ID_AVERAGE_TO_FOUR);
        float def = context.getResources().getFloat(R.string.DEFAULT_AVERAGE_TO_FOUR);
        return getPref(context).getFloat(id, def);
    }

    public static float getAverageToThree(Context context){
        String id = context.getString(R.string.ID_AVERAGE_TO_THREE);
        float def = context.getResources().getFloat(R.string.DEFAULT_AVERAGE_TO_THREE);
        return getPref(context).getFloat(id, def);
    }

    public static float getAverageToTwo(Context context){
        String id = context.getString(R.string.ID_AVERAGE_TO_TWO);
        float def = context.getResources().getFloat(R.string.DEFAULT_AVERAGE_TO_TWO);
        return getPref(context).getFloat(id, def);
    }

    public static float getAverageToBelt(Context context){
        String id = context.getString(R.string.ID_AVERAGE_TO_BELT);
        String def = context.getResources().getString(R.string.DEFAULT_AVERAGE_TO_BELT);
        return Float.valueOf(getPref(context).getString(id, def));
    }

    public static float getSemester(Context context){
        String id = context.getString(R.string.ID_SEMESTER);
        int def = context.getResources().getInteger(R.integer.DEFAULT_SEMESTER);
        return getPref(context).getInt(id, def);
    }

    private static SharedPreferences getPref(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}