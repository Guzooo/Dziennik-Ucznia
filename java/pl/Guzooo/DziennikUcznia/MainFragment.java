package pl.Guzooo.DziennikUcznia;

import androidx.fragment.app.Fragment;

public abstract class MainFragment extends Fragment {

    public MainFragmentListener mainFragmentListener;

    public interface MainFragmentListener{
        void setNoDataVisibility();
        void setAgainActionFAB();
    }

    public void setMainFragmentListener(MainFragmentListener listener){
        mainFragmentListener = listener;
    }

    public boolean isHome(){
        return false;
    }

    public boolean isActionBarSubtitleIsVisibility(){
        return true;
    }

    public boolean isVisibleAddFAB(){
        return true;
    }

    public int getNoDataText(){
        return R.string.no_subject;
    }

    public boolean isNoDateVisible(){
        return false;
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
}