package pl.Guzooo.DziennikUcznia;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends GActivity implements BottomNavigationView.OnNavigationItemSelectedListener, PillMenu.OnPillMenuItemSelectedListener {

    private BottomNavigationView bottomNavigation;
    private MainFragment currentFragment;

    private FloatingActionButton addFAB;
    private FloatingActionButton actionFAB;
    private PillMenu pillMenu;

    @Override
    public int getBottomPadding() {
        int bottom = bottomNavigation.getHeight();
        bottom += addFAB.getHeight();
        bottom += getResources().getDimensionPixelOffset(R.dimen.margin_biggest) * 2;
        return bottom;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilsTheme.setTheme(this);
        setContentView(R.layout.activity_main);

        BugFix.startFixingBugs(this);

        initialization();
        setFullScreen();
        setFragment();
        setBottomNavigation();
        setPillMenu();
        setAddFAB();
        setActionFAB();
        setNotepad();
        NotificationOnline.checkAutomatically(this);
        NotificationsChannels.CreateNotificationsChannels(this);//TODO: czy to musi się wykonywać za każdym uruchomieniem aplikacji
    }

    @Override
    protected void onStart() {
        super.onStart();
        setActionBarSubtitle();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        currentFragment.onRestart();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.settings:
                replaceFragment(new MainSettingsFragment());
                return true;
            case R.id.home:
                replaceFragment(new MainHomeFragment());
                return true;
            case R.id.statistics:
                replaceFragment(new MainStatisticsFragment());
                return true;
            case R.id.lesson_plan:
                replaceFragment(new MainLessonPlanFragment());
                return true;
        }
        return false;
    }

    @Override
    public void onPillMenuItemSelected(int id) {
        switch (id){
            case R.id.add_assessment:
                addAssessment();
                break;
            case R.id.add_note:
                addNote();
                break;
            case R.id.add_lesson_plan:
                addLessonPlan();
                break;
            case R.id.add_subject:
                addSubject();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(pillMenu.isVisible())
            pillMenu.hide();
        else if(currentFragment.onBackPressed())
            ;
        else if(!currentFragment.isHome())
            openHomeFragment();
        else
            super.onBackPressed();
    }

    private void initialization(){
        bottomNavigation = findViewById(R.id.bottom_navigation);
        addFAB = findViewById(R.id.fab_add);
        actionFAB = findViewById(R.id.fab_action);
        pillMenu = findViewById(R.id.pill_menu);
    }

    private void setFullScreen(){
        UtilsFullScreen.setUIVisibility(bottomNavigation);
        UtilsFullScreen.setApplyWindowInsets(bottomNavigation, getWindowsInsetsListener());
    }

    private void setFragment() {
        MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.content);
        if(fragment != null)
            replaceFragment(fragment);
    }

    private void setBottomNavigation(){
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        if(currentFragment == null)
            openHomeFragment();
    }

    private void setPillMenu(){
        pillMenu.setFullScreen(this);
        pillMenu.setOnPillMenuItemSelectedListener(this);
    }

    private void setAddFAB(){
       addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pillMenu.show();
            }
        });
    }

    private void setActionFAB(){
        actionFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentFragment.clickIconActionFAB();
            }
        });
    }

    private void setActionBarSubtitle(){
        String subtitle = currentFragment.getActionBarSubtitle();
        getSupportActionBar().setSubtitle(subtitle);
    }

    private void setNotepad(){
        //TODO: notatnik w głównym Activity
    }

    private void addAssessment(){
        Toast.makeText(this, "ocena", Toast.LENGTH_SHORT).show();
    }

    private void addNote(){
        Toast.makeText(this, "notatka", Toast.LENGTH_SHORT).show();
    }

    private void addLessonPlan(){
        Toast.makeText(this, "plan lekcji", Toast.LENGTH_SHORT).show();
    }

    private void addSubject(){
        Toast.makeText(this, "przedmiot", Toast.LENGTH_SHORT).show();
    }

    private void replaceFragment(MainFragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN); //TODO inne animacje
        transaction.commit();
        currentFragment = fragment;
    }

    private OnApplyWindowInsetsListener getWindowsInsetsListener(){
        return new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                setInsets(insets);
                setBottomNavigationSpacing(insets);
                return insets;
            }
        };
    }

    private void setBottomNavigationSpacing(WindowInsetsCompat insets){
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) bottomNavigation.getLayoutParams();
        params.bottomMargin = insets.getSystemWindowInsetBottom();
        params.rightMargin = insets.getSystemWindowInsetRight();
        params.leftMargin = insets.getSystemWindowInsetLeft();
    }

    private void openHomeFragment(){
        MenuItem home = bottomNavigation.getMenu().getItem(1);
        home.setChecked(true);
        onNavigationItemSelected(home);
    }
}
