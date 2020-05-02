package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

public class MainSettingsFragment extends MainFragment {

    private final String FACEBOOK = "https://www.facebook.com/GuzoooApps";
    private final String MESSENGER = "https://www.messenger.com/t/GuzoooApps";

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
        logoG.setOnLongClickListener(TestMethods.assessmentToBeltLongListener(getContext()));
        infoG.findViewById(R.id.close).setOnClickListener(getClickListenerCloseInfo());
        infoG.findViewById(R.id.facebook).setOnClickListener(getClickListenerOpenPage(FACEBOOK));
        infoG.findViewById(R.id.messenger).setOnClickListener(getClickListenerOpenPage(MESSENGER));
    }

    private void setVersionInfo(){
        TextView version = infoG.findViewById(R.id.version);
        try {
            String packageName = getActivity().getPackageName();
            PackageInfo info = getActivity().getPackageManager().getPackageInfo(packageName, 0);
            String appInfo = getString(R.string.app_name);
            appInfo += getString(R.string.separator);
            appInfo += getString(R.string.app_v, info.versionName);
            version.setText(appInfo);
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
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
}