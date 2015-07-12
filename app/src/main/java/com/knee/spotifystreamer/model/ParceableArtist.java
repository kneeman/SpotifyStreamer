package com.knee.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by c_cknee on 6/25/2015.
 */
public class ParceableArtist implements Parcelable{
    private String artistId;
    private String artistName;
    private List<String> artistImageUrls = new ArrayList<String>();

    public ParceableArtist(Artist pArtist){
        this.artistId = pArtist.id;
        this.artistName = pArtist.name;
        for(Image image : pArtist.images){
            this.artistImageUrls.add(image.url);
        }
    }

    public ParceableArtist(Parcel pParcel){
        this.artistId = pParcel.readString();
        this.artistName = pParcel.readString();
        pParcel.readStringList(this.artistImageUrls);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.artistId);
        dest.writeString(this.artistName);
        dest.writeStringList(this.artistImageUrls);
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public List<String> getArtistImageUrls() {
        return artistImageUrls;
    }

    public void setArtistImageUrls(List<String> artistImageUrls) {
        this.artistImageUrls = artistImageUrls;
    }

    public static final Parcelable.Creator<ParceableArtist> CREATOR = new Parcelable.Creator<ParceableArtist>(){
        public ParceableArtist createFromParcel(Parcel pc){
            return new ParceableArtist(pc);
        }
        @Override
        public ParceableArtist[] newArray(int size) {
            return new ParceableArtist[size];
        }
    };
}
