package pl.Guzooo.DziennikUcznia;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends GActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        setContentView(R.layout.activity_main);

        initialization();
        setFragment();
        setBottomNavigation();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.settings:
            case R.id.home:
            case R.id.statistics:
            case R.id.lesson_plan:
                return true;
        }
        return false;
    }

    private void setTheme(){
        /*int theme = SettingActivity.getTheme(this); TODO:zrobić setting activity
        AppCompatDelegate.setDefaultNightMode(theme);*/
    }

    private void initialization(){
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setFragment() {
        //TODO: nwm czy potrzebny
    }

    private void setBottomNavigation(){
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        //if(currentFragment == null)
        MenuItem home = bottomNavigation.getMenu().getItem(1);
        home.setChecked(true);
        onNavigationItemSelected(home);
    }
}
