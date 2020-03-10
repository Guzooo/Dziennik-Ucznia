package pl.Guzooo.DziennikUcznia;

import android.view.View;

import androidx.fragment.app.Fragment;

public abstract class MainFragment extends Fragment {

    public boolean isHome(){
        return false;
    }

    public boolean isVisibleAddFAB(){
        return true;
    }

    public View getFullScreenView(){
        return null;
    }

    public int getIconActionFAB(){
        return 0;
    }

    public void clickIconActionFAB(){

    }

    public boolean onBackPressed(){
        return false;
    }
}
