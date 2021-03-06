package com.knee.spotifystreamer.model;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by c_cknee on 8/11/2015.
 */
public class TopTracksState {
    private int selectedTrack;
    private List<Track> tracks;
    private boolean currentlyPlaying;

    public TopTracksState(int pInt, List<Track> pList){
        this.selectedTrack = pInt;
        this.tracks = pList;
        this.currentlyPlaying = false;
    }

    public int getSelectedTrack() {
        return selectedTrack;
    }

    public void setSelectedTrack(int selectedTrack) {
        this.selectedTrack = selectedTrack;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public boolean isCurrentlyPlaying() {
        return currentlyPlaying;
    }

    public void setCurrentlyPlaying(boolean currentlyPlaying) {
        this.currentlyPlaying = currentlyPlaying;
    }
}
