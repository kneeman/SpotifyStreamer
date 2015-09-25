package com.knee.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.Toast;

import com.knee.spotifystreamer.bus.BusProvider;
import com.knee.spotifystreamer.bus.DialogMessage;
import com.knee.spotifystreamer.model.TopTracksState;
import com.knee.spotifystreamer.utils.Utils;

/**
 * Created by c_cknee on 8/7/2015.
 */
public class ParentActivity extends ActionBarActivity{

    private static final String KEY_CURRENTLY_PLAYING = "keyCurrentlyPlaying";
    private static final String TAG = ParentActivity.class.getSimpleName();
    protected boolean mTwoPane;
    public static final String KEY_SHARED_PREFS = "keySharedPrefs";
    private static final String TAG_FRAGMENT_PLAYER = "fragment_tag_player";
    public static final String KEY_COUNTRY_MAP = "country";
    private Spinner countrySpinner;
    protected PlayerDialogFragment newFragment;
    private MenuItem shareMenuItem, nowListeningMenuItem;
    private ShareActionProvider mShareActionProvider;
    private String currentlyPlayingUrl;
    private SpotifyApplication spotifyApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            currentlyPlayingUrl = savedInstanceState.getString(KEY_CURRENTLY_PLAYING, "");
        }
        spotifyApplication = (SpotifyApplication) getApplicationContext();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        shareMenuItem = menu.findItem(R.id.menu_item_share);
        nowListeningMenuItem = menu.findItem(R.id.action_now_listening);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareMenuItem);
        handleCurrentlyPlayingToolbarFunctionality();
        Log.i(TAG, "onCreateOptionsMenu entered");
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
            case R.id.action_now_listening:
                retrievePlayerFragment();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume entered");
        this.invalidateOptionsMenu();
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
            newFragment = PlayerDialogFragment.newInstance(pTrack);
            if (mTwoPane) {
                newFragment.show(fragmentManager, TAG_FRAGMENT_PLAYER);
            } else {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                //Transition animation
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                // To make it fullscreen, use the 'content' root view as the container
                // for the fragment, which is always the root view for the activity
                transaction.replace(android.R.id.content, newFragment, TAG_FRAGMENT_PLAYER)
                .addToBackStack(null)
                .commit();
            }
        }else{
            Toast.makeText(this, getString(R.string.network_unavailable), Toast.LENGTH_LONG).show();
        }
    }

    protected void handleDialogMessageSuper(DialogMessage pDialogMessage){
        if(pDialogMessage.getDialogAction().equals(DialogMessage.DialogAction.DISMISS)){
            removePlayerDialog();
        }
    }

    protected void setTopTracksState(TopTracksState pTopTracksState){
        spotifyApplication.setTopTracksState(pTopTracksState);
        handleCurrentlyPlayingToolbarFunctionality();
    }


    private void handleCurrentlyPlayingToolbarFunctionality(){
        FragmentManager fm = getSupportFragmentManager();
        PlayerDialogFragment playerDialogFragment = (PlayerDialogFragment) fm.findFragmentByTag(TAG_FRAGMENT_PLAYER);
        if(playerDialogFragment != null){
            return;
        }
        String localCurrentlyPlayingURL = spotifyApplication.serviceCurrentlyPlaying() ?
            spotifyApplication.getTopTracksState().getTracks().get(spotifyApplication.getTopTracksState().getSelectedTrack()).preview_url:
                null;
        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(createShareCurrentlyPlayingIntent(localCurrentlyPlayingURL));
        }
        boolean currentlyPlaying = !(localCurrentlyPlayingURL == null || localCurrentlyPlayingURL.isEmpty());
        shareMenuItem.setVisible(currentlyPlaying);
        nowListeningMenuItem.setVisible(currentlyPlaying);
    }

    protected void removePlayerDialog(){
        if(mTwoPane){
            if (newFragment != null) {
                newFragment.dismiss();
            }
        }else{
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_PLAYER);
            if(fragment != null)
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private Intent createShareCurrentlyPlayingIntent(String urlListening){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, urlListening);
        return shareIntent;
    }

    private void retrievePlayerFragment() {
        PlayerDialogFragment playerDialogFragment = (PlayerDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_PLAYER);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (mTwoPane) {
            playerDialogFragment.show(fragmentManager, TAG_FRAGMENT_PLAYER);
        } else {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(android.R.id.content, playerDialogFragment, TAG_FRAGMENT_PLAYER)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(currentlyPlayingUrl != null && !currentlyPlayingUrl.isEmpty()){
            outState.putString(KEY_CURRENTLY_PLAYING, currentlyPlayingUrl);
        }
    }
}
