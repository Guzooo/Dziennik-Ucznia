package pl.Guzooo.DziennikUcznia;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends GActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigation;

    private MainSettingsFragment settingsFragment = new MainSettingsFragment();
    private MainHomeFragment homeFragment = new MainHomeFragment();
    private MainStatisticsFragment statisticsFragment = new MainStatisticsFragment();
    private MainFragments currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialization();
        setFragment();
        setBottomNavigation();
        setActionBarSubtitle();
        setNotepad();
        NotificationOnline.checkAutomatically(this);
        NotificationsChannels.CreateNotificationsChannels(this);//TODO: czy to musi się wykonywać za każdym uruchomieniem aplikacji
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setActionBarSubtitle();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.settings:
                replaceFragment(settingsFragment);
                return true;
            case R.id.home:
                replaceFragment(homeFragment);
                return true;
            case R.id.statistics:
                replaceFragment(statisticsFragment);
                return true;
            case R.id.lesson_plan:
                return true;
        }
        return false;
    }

    private void initialization(){
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setFragment() {
        MainFragments fragment = (MainFragments) getSupportFragmentManager().findFragmentById(R.id.content);
        if(fragment != null)
            replaceFragment(fragment);
    }

    private void setBottomNavigation(){
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        if(currentFragment == null) {
            MenuItem home = bottomNavigation.getMenu().getItem(1);
            home.setChecked(true);
            onNavigationItemSelected(home);
        }
    }

    private void setActionBarSubtitle(){
        String semester = getSemester();
        String separator = getString(R.string.separator);
        String average = getFinalAverage();
        getSupportActionBar().setSubtitle(semester + separator + average);
    }

    private void setNotepad(){
        //TODO: notatnik w głównym Activity
    }

    private String getSemester(){
        int semester = StatisticsActivity.getSemester(this);
        return getString(R.string.semester_with_colon, semester);
    }

    private String getFinalAverage(){
        float average = UtilsAverage.getFinalAverage(this);
        if(isBelt(average))
            return getString(R.string.final_average, average) + getString(R.string.separator) + getString(R.string.belt);
        return getString(R.string.final_average, average);
    }

    private boolean isBelt(float average){
        if(average >= getMinimumToBelt())
            return true;
        return false;
    }

    private float getMinimumToBelt(){
        //TODO: usunąć i dodać taką medote do ustawień;
        SharedPreferences sharedPreferences = getSharedPreferences(SettingActivity.PREFERENCE_NAME, MODE_PRIVATE);
        return sharedPreferences.getFloat(SettingActivity.PREFERENCE_AVERAGE_TO_BELT, SettingActivity.DEFAULT_AVERAGE_TO_BELT);
    }

    private void replaceFragment(MainFragments fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN); //TODO inne animacje
        transaction.commit();
        currentFragment = fragment;
    }
}
