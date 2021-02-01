package pl.Guzooo.DziennikUcznia;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

public class MainSettingsFragment extends MainFragment {

    private final String GOOGLE_PLAY = "https://play.google.com/store/apps/dev?id=6989903521291385498";
    private final String FACEBOOK = "https://www.facebook.com/GuzoooApps";
    private final String MESSENGER = "https://www.messenger.com/t/GuzoooApps";

    private View nestScroll;
    private View logoG;
    private View infoG;

    @Override
    public boolean isActionBarSubtitleIsVisibility() {
        return false;
    }

    @Override
    public boolean isVisibleAddFAB() {
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_main_settings, container, false);
        initialization(layout);
        setFullScreen();
        setSettingPreferenceFragment();
        setG();
        setVersionInfo();
        return layout;
    }

    private void initialization(View v){
        nestScroll = v.findViewById(R.id.nest_scroll);
        logoG = v.findViewById(R.id.logo_g);
        infoG = v.findViewById(R.id.info_g);
    }

    private void setFullScreen(){
        UtilsFullScreen.setPaddings(nestScroll, this);
    }

    private void setSettingPreferenceFragment(){
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.content, new PreferenceSettingsFragment());
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }

    private void setG(){
        logoG.setOnClickListener(getClickListenerOpenInfo());
        logoG.setOnLongClickListener(TestMethods.assessmentToBeltLongListener(getContext()));
        infoG.findViewById(R.id.close).setOnClickListener(getClickListenerCloseInfo());
        infoG.findViewById(R.id.google_play).setOnClickListener(getClickListenerOpenPage(GOOGLE_PLAY));
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