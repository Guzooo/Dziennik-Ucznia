package pl.Guzooo.DziennikUcznia;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsCompat;

public abstract class GActivity extends AppCompatActivity {

    private final String DARK_THEME = "darktheme";
    private final String HARD_DARK_THEME = "harddarktheme";

    private String currentDarkTheme;

    private WindowInsetsCompat insets;

    public WindowInsetsCompat getInsets() {
        return insets;
    }

    public void setInsets(WindowInsetsCompat insets) {
        this.insets = insets;
    }

    public int getBottomPadding(){
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onRestart() {//TODO:jakby się nie zmieniał theme to trzeba resume
        refreshTheme();
        super.onRestart();
    }

    private void setTheme(){
        int currentNightMode = this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if(currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            if (DataManager.isHardDarkTheme(this)) {
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
            if (DataManager.isHardDarkTheme(this) && !currentDarkTheme.equals(HARD_DARK_THEME)) {
                this.recreate();
            } else if(!DataManager.isHardDarkTheme(this) && !currentDarkTheme.equals(DARK_THEME)) {
                this.recreate();
            }
        }
    }
}
