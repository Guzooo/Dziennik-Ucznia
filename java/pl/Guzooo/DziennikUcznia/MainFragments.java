package pl.Guzooo.DziennikUcznia;

import androidx.fragment.app.Fragment;

public abstract class MainFragments extends Fragment {

    public boolean isHome(){
        return false;
    }

    public boolean isVisibleAddFAB(){
        return true;
    }

    public int getIconActionFAB(){
        return 0;
    }

    public void clickIconActionFAB(){

    }
}
