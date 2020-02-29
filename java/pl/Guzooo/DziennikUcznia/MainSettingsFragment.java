package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

public class MainSettingsFragment extends MainFragments {

    private static final String DEFAULT_THEME(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            return "3";
        return "-1";
    }
    private static final boolean DEFAULT_HARD_DARK_THEME = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_settings, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.content, new SettingsFragment());
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN); //TODO inne animacje
        transaction.commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private <T extends Preference> T findPref(int id) {
            String stringId = getString(id);
            return findPreference(stringId);
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings_preference, rootKey);

            setTheme();
            setHardDarkTheme();
        }

        private void setTheme() {
            ListPreference theme = findPref(R.string.THEME_ID);
            if (theme.getValue() == null)
                theme.setValueIndex(2);
            theme.setOnPreferenceChangeListener(getThemeListener());
        }

        private void setHardDarkTheme() {
            SwitchPreference hardDarkTheme = findPref(R.string.HARD_DARK_ID);
            hardDarkTheme.setOnPreferenceChangeListener(getHardDarkThemeListener());
        }

        private Preference.OnPreferenceChangeListener getThemeListener() {
            return new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String value = newValue.toString();
                    int nightMode = Integer.valueOf(value);
                    AppCompatDelegate.setDefaultNightMode(nightMode);
                    return true;
                }
            };
        }

        private Preference.OnPreferenceChangeListener getHardDarkThemeListener(){
            return new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int currentNightMode = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                    if(currentNightMode == Configuration.UI_MODE_NIGHT_YES)
                        getActivity().recreate();
                    return true;
                }
            };
        }
    }

    public static int getTheme(Context context){
        String id = context.getString(R.string.THEME_ID);
        String theme = getPref(context).getString(id, DEFAULT_THEME());
        return Integer.valueOf(theme);
    }

    public static boolean getHardDarkTheme(Context context){
        String id = context.getString(R.string.HARD_DARK_ID);
        return getPref(context).getBoolean(id, DEFAULT_HARD_DARK_THEME);
    }

    private static SharedPreferences getPref(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
