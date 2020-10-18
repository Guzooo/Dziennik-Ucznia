package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.core.view.ActionProvider;

public class ChangeTitle extends ActionProvider {

    public ChangeTitle(Context context) {
        super(context);
    }

    @Override
    public View onCreateActionView() {
        return null;
    }

    @Override
    public View onCreateActionView(MenuItem forItem) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View providerView = layoutInflater.inflate(R.layout.action_bar_edit_text, null);
        return providerView;
    }
}
