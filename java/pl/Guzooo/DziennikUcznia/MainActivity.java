package pl.Guzooo.DziennikUcznia;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends GActivity implements BottomNavigationView.OnNavigationItemSelectedListener, PillMenu.OnPillMenuItemSelectedListener, MainFragment.MainFragmentListener {

    private MainFragment currentFragment;

    private TextView noData;
    private FloatingActionButton addFAB;
    private FloatingActionButton actionFAB;
    private PillMenu pillMenu;
    private BottomNavigationView bottomNavigation;

    @Override
    public int getBottomPadding() {
        int bottom = bottomNavigation.getHeight();
        if(currentFragment.isVisibleAddFAB()) {
            bottom += addFAB.getHeight();
            bottom += getResources().getDimensionPixelOffset(R.dimen.margin_biggest) * 2;
        }
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
        setActionBar();
        setBottomNavigation();
        setPillMenu();
        setAddFAB();
        setActionFAB();
        NotificationOnline.checkAutomatically(this);
        NotificationsChannels.CreateNotificationsChannels(this);//TODO: czy to musi się wykonywać za każdym uruchomieniem aplikacji
    }

    @Override
    protected void onRestart() {//TODO: Nwm czy nie resume
        super.onRestart();
        currentFragment.onRestart();
        setActionBarSubtitle();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        MainFragment newFragment = null;
        switch (menuItem.getItemId()){
            case R.id.settings:
                newFragment = new MainSettingsFragment();
                break;
            case R.id.home:
                newFragment = new MainHomeFragment();
                break;
            case R.id.statistics:
                newFragment = new MainStatisticsFragment();
                break;
            case R.id.lesson_plan:
                newFragment = new MainLessonPlanFragment();
                break;
        }
        if(newFragment == null || (currentFragment != null && currentFragment.getClass() == newFragment.getClass()))
            return false;
        setCurrentFragment(newFragment);
        refreshActivityByNewFragment();
        return true;
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
    public void setNoDataVisibility(){
        boolean visible = currentFragment.isNoDateVisible();
        if(visible)
            noData.setVisibility(View.VISIBLE);
        else
            noData.setVisibility(View.GONE);
    }

    @Override
    public void setAgainActionFAB(){
        setActionFabByFragment();
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
        noData = findViewById(R.id.no_data);
        addFAB = findViewById(R.id.fab_add);
        actionFAB = findViewById(R.id.fab_action);
        pillMenu = findViewById(R.id.pill_menu);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setFullScreen(){
        UtilsFullScreen.setUIVisibility(bottomNavigation);
        UtilsFullScreen.setApplyWindowInsets(bottomNavigation, getWindowsInsetsListener());
    }

    private void setFragment() {
        MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.content);
        if(fragment != null)
            setCurrentFragment(fragment);
    }

    private void setActionBar(){
        setActionBarSubtitle();
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

    private void addAssessment(){
        Toast.makeText(this, "ocena", Toast.LENGTH_SHORT).show();
    }

    private void addNote(){
        Toast.makeText(this, "notatka", Toast.LENGTH_SHORT).show();
    }

    private void addLessonPlan(){
        new AddElementOfPlanFragment().show(new ElementOfPlan2020(), getInsertListener(), getSupportFragmentManager());
    }

    private void addSubject(){
        new AddSubjectFragment().show(getInsertListener(), getSupportFragmentManager());
    }

    private MainMenuInsertListener getInsertListener(){
        return new MainMenuInsertListener() {
            @Override
            public void beforeInsert() {
                currentFragment.onRestart();
            }
        };
    }

    private void setCurrentFragment(MainFragment fragment){
        replaceFragment(fragment);
        currentFragment.setMainFragmentListener(this);
        setNoDataByFragment();
        setAddFabByFragment();
        setActionFabByFragment();
    }

    private void replaceFragment(MainFragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
        currentFragment = fragment;
    }

    private void setNoDataByFragment(){
        int string = currentFragment.getNoDataText();
        noData.setText(string);
        noData.setVisibility(View.GONE);
    }

    private void setAddFabByFragment() {
        if (currentFragment.isVisibleAddFAB())
            addFAB.show();
        else
            addFAB.hide();
    }

    private void setActionFabByFragment(){
        if(actionFAB.isShown())
            actionFAB.hide(getOnHideActionFabListener());
        else if(isFragmentHasActionFAB())
            setActionFabDrawable();
    }

    private FloatingActionButton.OnVisibilityChangedListener getOnHideActionFabListener(){
        return new FloatingActionButton.OnVisibilityChangedListener() {
            @Override
            public void onHidden(FloatingActionButton fab) {
                super.onHidden(fab);
                if(isFragmentHasActionFAB())
                    setActionFabDrawable();
            }
        };
    }

    private boolean isFragmentHasActionFAB(){
        if(currentFragment.getIconActionFAB() == 0)
            return false;
        return true;
    }

    private void setActionFabDrawable(){
        int id = currentFragment.getIconActionFAB();
        Drawable icon = getResources().getDrawable(id);
        actionFAB.setImageDrawable(icon);
        actionFAB.show();
    }

    private void refreshActivityByNewFragment(){
        setActionBarSubtitle();
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

    private void setActionBarSubtitle(){
        getSupportActionBar().setSubtitle(getActionBarSubtitle());
        //String currentSubtitle = getCurrentActionBarSubtitle();
        //String newSubtitle = getActionBarSubtitle();
        //UtilsAnimation.OnChangeTextListener listener = getOnChangeActionBarSubtitle();
        //UtilsAnimation.changeText(currentSubtitle, newSubtitle, listener);
        setShortcutSubtitle();
    }

    public String getActionBarSubtitle(){
        if(currentFragment == null || !currentFragment.isActionBarSubtitleIsVisibility())
            return getString(R.string.app_G);
        String semester = getSemester();
        String separator = getString(R.string.separator);
        String average = getFinalAverage();
        return semester + separator + average;
    }

    private String getSemester(){
        int semester = DataManager.getSemester(this);
        return getString(R.string.semester_with_colon, semester);
    }

    private String getFinalAverage(){
        float average = UtilsAverage.getFinalAverage(this);
        if(UtilsAverage.isBelt(average, this))
            return getString(R.string.final_average, average) + getString(R.string.separator) + getString(R.string.belt);
        return getString(R.string.final_average, average);
    }

    private void setShortcutSubtitle(){
        if(true)
        return;
        String subtitle = getCurrentActionBarSubtitle();
        if(subtitle.equals(""))
            return;
        String[] strings = subtitle.split(" ");
        ArrayList<String> oldStrings = getOldSubtitleStrings(strings);
        ArrayList<String> newStrings = getNewSubtitleStrings(strings);
        UtilsAnimation.OnChangeTextListener listener = getOnChangeActionBarSubtitle();
        UtilsAnimation.changeTextMultiString(subtitle, oldStrings, newStrings,
                                                listener, 5);
    }

    private ArrayList<String> getOldSubtitleStrings(String[] subtitleStrings){
        ArrayList<String> strings = new ArrayList<>();
        strings.add(subtitleStrings[0].substring(0, subtitleStrings[0].length()-1));
        strings.add(subtitleStrings[3]);
        strings.add(subtitleStrings[4].substring(0, subtitleStrings[0].length()-1));
        strings.add(subtitleStrings[4].substring(0, 1) + " ");
        return strings;
    }

    private ArrayList<String> getNewSubtitleStrings(String[] subtitleStrings){
        ArrayList<String> strings = new ArrayList<>();
        strings.add(subtitleStrings[0].substring(0, 1));
        strings.add(subtitleStrings[3].substring(0, 1));
        strings.add(subtitleStrings[4].substring(0, 1));
        strings.add(subtitleStrings[4].substring(0, 1));
        return strings;
    }

    private UtilsAnimation.OnChangeTextListener getOnChangeActionBarSubtitle(){
        return new UtilsAnimation.OnChangeTextListener() {
            @Override
            public void setText(String text) {
                getSupportActionBar().setSubtitle(text);
            }
        };
    }

    public String getCurrentActionBarSubtitle(){
        try{
            return getSupportActionBar().getSubtitle().toString();
        } catch (NullPointerException e){
            return "";
        }
    }

    private void openHomeFragment(){
        MenuItem home = bottomNavigation.getMenu().getItem(1);
        home.setChecked(true);
        onNavigationItemSelected(home);
    }
}
