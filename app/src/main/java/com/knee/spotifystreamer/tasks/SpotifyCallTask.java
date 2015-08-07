package com.knee.spotifystreamer.tasks;

import android.os.AsyncTask;

import com.knee.spotifystreamer.bus.BusProvider;
import com.knee.spotifystreamer.utils.SpotifyServiceSingleton;

import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Created by c_cknee on 7/29/2015.
 */
public class SpotifyCallTask extends AsyncTask<String, Void, ArtistsPager> {

    @Override
    protected ArtistsPager doInBackground(String... params) {
        ArtistsPager results = SpotifyServiceSingleton.getInstance().searchArtists(params[0]);
        return results;
    }

    @Override
    protected void onPostExecute(ArtistsPager artistsPager) {
        BusProvider.getInstance().post(artistsPager);
    }
}
