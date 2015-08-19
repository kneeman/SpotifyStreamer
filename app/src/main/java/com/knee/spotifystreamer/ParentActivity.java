package com.knee.spotifystreamer;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.Toast;

import com.knee.spotifystreamer.bus.BusProvider;
import com.knee.spotifystreamer.model.TopTracksState;
import com.knee.spotifystreamer.utils.Utils;

/**
 * Created by c_cknee on 8/7/2015.
 */
public class ParentActivity extends ActionBarActivity {

    protected boolean mTwoPane;
    public static final String KEY_SHARED_PREFS = "keySharedPrefs";
    public static final String KEY_COUNTRY_MAP = "country";
    private Spinner countrySpinner;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_country_codes:
                FragmentManager fragmentManager = getSupportFragmentManager();
                DialogCountryCodesFragment newFragment = DialogCountryCodesFragment.newInstance();
                newFragment.show(fragmentManager, "fragment_country_code");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    protected void showDialog(TopTracksState pTrack) {
        if(Utils.isNetworkConnected(this)) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            PlayerDialogFragment newFragment = PlayerDialogFragment.newInstance(pTrack);
            if (mTwoPane) {
                newFragment.show(fragmentManager, "dialog");
            } else {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                //Transition animation
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                // To make it fullscreen, use the 'content' root view as the container
                // for the fragment, which is always the root view for the activity
                transaction.replace(android.R.id.content, newFragment, "fragmentPlayerDialogFragment")
                .addToBackStack(null)
                .commit();
            }
        }else{
            Toast.makeText(this, getString(R.string.network_unavailable), Toast.LENGTH_LONG).show();
        }
    }
}
