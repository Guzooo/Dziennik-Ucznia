package pl.Guzooo.DziennikUcznia;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public abstract class GActivity extends AppCompatActivity {

    private final String DARK_THEME = "darktheme";
    private final String HARD_DARK_THEME = "harddarktheme";

    private String currentDarkTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
    }

    @Override
    protected void onRestart() {//TODO:jakby się nie zmieniał theme to trzeba resume
        super.onRestart();
        refreshTheme();
    }

    private void setTheme(){
        int currentNightMode = this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if(currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            if (MainSettingsFragment.getHardDarkTheme(this)) {
                setTheme(R.style.AppTheme_HardDarkMode);
                currentDarkTheme = HARD_DARK_THEME;
            } else {
                currentDarkTheme = DARK_THEME;
            }
        }
    }

    private void refreshTheme(){
        int currentNightMode = this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if(currentNightMode == Configuration.UI_MODE_NIGHT_YES ) {
            if (MainSettingsFragment.getHardDarkTheme(this) && !currentDarkTheme.equals(HARD_DARK_THEME)) {
                this.recreate();
            } else if(!MainSettingsFragment.getHardDarkTheme(this) && !currentDarkTheme.equals(DARK_THEME)) {
                this.recreate();
            }
        }
    }

    public int getBottomPadding(){
        return 0;
    }
}
