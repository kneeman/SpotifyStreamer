package com.knee.spotifystreamer.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.knee.spotifystreamer.ParentActivity;
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
    private Context mContext;
    public TopTracksTask (Context context){
        mContext = context;
    }

    @Override
    protected Tracks doInBackground(String... params) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(ParentActivity.KEY_SHARED_PREFS, Activity.MODE_PRIVATE);
        String countryCode = sharedPreferences.getString(ParentActivity.KEY_COUNTRY_MAP, Locale.getDefault().getCountry());
        Map<String, Object> map = new HashMap<>();
        map.put(ParentActivity.KEY_COUNTRY_MAP, countryCode);
        Tracks results = SpotifyServiceSingleton.getInstance().getArtistTopTrack(params[0], map);
        return results;
    }

    @Override
    protected void onPostExecute(Tracks pTracks) {
        BusProvider.getInstance().post(pTracks);
    }
}
