package com.knee.spotifystreamer.utils;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

/**
 * Created by c_cknee on 6/11/2015.
 */
public class SpotifyServiceSingleton {
    private static SpotifyService instance = null;
    protected SpotifyServiceSingleton(){}
    public static SpotifyService getInstance(){
        if(instance == null){
            instance = new SpotifyApi().getService();
        }
        return instance;
    }
}
