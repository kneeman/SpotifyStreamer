package com.knee.spotifystreamer;

import android.app.Application;

import com.knee.spotifystreamer.model.TopTracksState;

/**
 * Created by c_cknee on 9/22/2015.
 */
public class SpotifyApplication extends Application {
    private TopTracksState topTracksState;

    public TopTracksState getTopTracksState() {
        return topTracksState;
    }

    public void setTopTracksState(TopTracksState topTracksState) {
        this.topTracksState = topTracksState;
    }

    public boolean serviceCurrentlyPlaying(){
        return (topTracksState != null && topTracksState.isCurrentlyPlaying());
    }
}
