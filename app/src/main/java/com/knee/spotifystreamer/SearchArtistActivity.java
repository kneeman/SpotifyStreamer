package com.knee.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.knee.spotifystreamer.model.TopTracksState;
import com.knee.spotifystreamer.model.ParceableArtist;
import com.knee.spotifystreamer.utils.Utils;
import com.squareup.otto.Subscribe;


public class SearchArtistActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_artist);
        if(findViewById(R.id.fragment_top_tracks) != null){
            mTwoPane = true;
        }
    }




    @Subscribe
    public void handleArtistSelected(ParceableArtist pArtist){
        if(Utils.isNetworkConnected(this)) {
            if(mTwoPane){
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction()
                    .replace(R.id.fragment_top_tracks, TopTracksFragment.newInstance(pArtist.getArtistId()), "fragment_top_tracks")
                    .commit();
            }else {
                Intent intent = TopTracksActivity.makeIntent(this, pArtist);
                startActivity(intent);
            }
        }else {
            Toast.makeText(this, getString(R.string.network_unavailable), Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe
    //Below method cannot be moved to super class, limitation of Otto
    public void handleTrackSelected(TopTracksState pTrack){ showDialog(pTrack);}
}
