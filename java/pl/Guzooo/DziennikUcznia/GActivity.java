package pl.Guzooo.DziennikUcznia;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class GActivity extends AppCompatActivity {

    private final String DARK_THEME = "darktheme";
    private final String HARD_DARK_THEME = "harddarktheme";

    private String currentDarkTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetTheme();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshTheme();
    }

    private void SetTheme(){
        int currentNightMode = this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if(currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            /*if (SettingsActivity.getHardDarkTheme(this)) {
                setTheme(R.style.AppTheme_HardDarkMode);
                currentDarkTheme = HARD_DARK_THEME;
            } else {
                currentDarkTheme = DARK_THEME;
            }*/
        }
    }

    private void refreshTheme(){
        int currentNightMode = this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if(currentNightMode == Configuration.UI_MODE_NIGHT_YES ) {
           /* if (SettingsActivity.getHardDarkTheme(this) && !currentDarkTheme.equals(HARD_DARK_THEME)) {
                this.recreate();
            } else if(!SettingsActivity.getHardDarkTheme(this) && !currentDarkTheme.equals(DARK_THEME)) {
                this.recreate();
            }*/
        }
    }
}
