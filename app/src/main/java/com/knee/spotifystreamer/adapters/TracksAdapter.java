package com.knee.spotifystreamer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.knee.spotifystreamer.R;
import com.knee.spotifystreamer.bus.BusProvider;
import com.knee.spotifystreamer.model.TopTracksState;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by c_cknee on 6/12/2015.
 */
public class TracksAdapter extends RecyclerView.Adapter<TracksAdapter.TracksViewHolder>{
    //private List<ParceableTrack> tracks;
    private List<Track> tracks;
    private Context context;

    public TracksAdapter(Context pContext,List<Track> pTracks){
            this.tracks= pTracks;
            this.context=pContext;
    }

    @Override
    public TracksViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.individual_top_track,
                viewGroup, false);
        TracksViewHolder tracksViewHolder = new TracksViewHolder(view);
        return tracksViewHolder;
    }

    @Override
    public void onBindViewHolder(TracksViewHolder tracksViewHolder, final int i){
        final Track thisTrack = tracks.get(i);
        tracksViewHolder.tvTrackArtist.setText(thisTrack.artists.get(0).name);
        tracksViewHolder.tvTrackName.setText(thisTrack.name);
            String urlString = thisTrack.album.images.get(thisTrack.album.images.size() - 1).url;
            if(urlString != null && URLUtil.isValidUrl(urlString)){
                Picasso.with(context)
                .load(urlString)
                .placeholder(R.drawable.image_loading)
                .resize(200, 200)
                .into(tracksViewHolder.ivTrack);
            }else{
                Picasso.with(context)
                .load(R.drawable.no_image_available)
                .resize(200, 200)
                .into(tracksViewHolder.ivTrack);
            }
        tracksViewHolder.vgContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusProvider.getInstance().post(new TopTracksState(i, tracks));
                }
        });

    }

//    public void swapList(List<ParceableTrack> pTracks) {
//        this.tracks = pTracks;
//        notifyDataSetChanged();
//    }

    public void swapList(List<Track> pTracks) {
        this.tracks = pTracks;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount(){
            return tracks.size();
    }

    public class TracksViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTrackName;
        private TextView tvTrackArtist;
        private ImageView ivTrack;
        private ViewGroup vgContainer;


        public TracksViewHolder(View itemView) {
            super(itemView);
            vgContainer = (ViewGroup) itemView.findViewById(R.id.relative_layout_top_track);
            ivTrack = (ImageView) itemView.findViewById(R.id.image_individual_top_track);
            tvTrackName = (TextView) itemView.findViewById(R.id.textview_top_track_name);
            tvTrackArtist = (TextView) itemView.findViewById(R.id.textview_top_track_artist_name);
        }
    }
}

