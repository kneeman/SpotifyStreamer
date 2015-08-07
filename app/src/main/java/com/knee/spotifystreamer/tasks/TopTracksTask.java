package com.knee.spotifystreamer.tasks;

import android.os.AsyncTask;

import com.knee.spotifystreamer.bus.BusProvider;
import com.knee.spotifystreamer.utils.SpotifyServiceSingleton;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by c_cknee on 7/29/2015.
 */
public class TopTracksTask extends AsyncTask<String, Void, Tracks> {
    private final String COUNTRY_MAP_KEY = "country";
    @Override
    protected Tracks doInBackground(String... params) {
        Map<String, Object> map = new HashMap<>();
        map.put(COUNTRY_MAP_KEY, Locale.getDefault().getCountry());
        Tracks results = SpotifyServiceSingleton.getInstance().getArtistTopTrack(params[0], map);
        return results;
    }

    @Override
    protected void onPostExecute(Tracks pTracks) {
        BusProvider.getInstance().post(pTracks);
    }
}
