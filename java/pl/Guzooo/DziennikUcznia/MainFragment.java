package pl.Guzooo.DziennikUcznia;

import androidx.fragment.app.Fragment;

public abstract class MainFragment extends Fragment {

    public boolean isHome(){
        return false;
    }

    public String getActionBarSubtitle(){
        String semester = getSemester();
        String separator = getString(R.string.separator);
        String average = getFinalAverage();
        return semester + separator + average;
    }

    public boolean isVisibleAddFAB(){
        return true;
    }

    public int getIconActionFAB(){
        return 0;
    }

    public void clickIconActionFAB(){

    }

    public boolean onBackPressed(){
        return false;
    }

    public void onRestart(){

    }

    private String getSemester(){
        int semester = DataManager.getSemester(getContext());
        return getString(R.string.semester_with_colon, semester);
    }

    private String getFinalAverage(){
        float average = UtilsAverage.getFinalAverage(getContext());
        if(UtilsAverage.isBelt(average, getContext()))
            return getString(R.string.final_average, average) + getString(R.string.separator) + getString(R.string.belt);
        return getString(R.string.final_average, average);
    }
}
