package com.knee.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by c_cknee on 7/14/2015.
 */
public class ParceableTrack implements Parcelable{
    private String name;
    private String artistName;
    private String imageUrl;


    public  ParceableTrack(Track pTrack){
        this.name = pTrack.name;
        this.artistName = pTrack.artists.get(0).name;
        this.imageUrl = pTrack.album.images.get(0).url;
    }

    public  ParceableTrack(Parcel in){
        this.name = in.readString();
        this.artistName = in.readString();
        this.imageUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.artistName);
        dest.writeString(imageUrl);

    }

    public String getName() {
        return name;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<ParceableTrack> CREATOR = new Parcelable.Creator<ParceableTrack>(){

        @Override
        public ParceableTrack createFromParcel(Parcel source) {
            return new ParceableTrack(source);
        }

        @Override
        public ParceableTrack[] newArray(int size) {
            return new ParceableTrack[size];
        }
    };
}
