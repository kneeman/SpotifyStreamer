package com.knee.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.knee.spotifystreamer.model.ParceableArtist;
import com.knee.spotifystreamer.model.ParceableTrack;
import com.squareup.otto.Subscribe;


public class TopTracksActivity extends ParentActivity {
    public static final String ARTIST_ID_KEY = "keyArtistId";
    private static final String ARTIST_NAME_KEY = "keyArtistName" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);

        String artistName = getIntent().getExtras().getString(ARTIST_NAME_KEY);
        if(artistName != null && artistName.length() > 0) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setSubtitle(artistName);
        }
    }

    public static Intent makeIntent(Context pContext, ParceableArtist pArtist){
        Intent intent = new Intent(pContext, TopTracksActivity.class);
        intent.putExtra(ARTIST_ID_KEY, pArtist.getArtistId());
        intent.putExtra(ARTIST_NAME_KEY, pArtist.getArtistName());
        return intent;
    }

    @Subscribe
    //Below method cannot be moved to super class, limitation of Otto
    public void handleTrackSelected(ParceableTrack pTrack){ showDialog(pTrack);}
}
