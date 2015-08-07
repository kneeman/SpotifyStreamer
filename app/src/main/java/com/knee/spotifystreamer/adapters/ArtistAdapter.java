package com.knee.spotifystreamer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.knee.spotifystreamer.R;
import com.knee.spotifystreamer.bus.BusProvider;
import com.knee.spotifystreamer.model.ParceableArtist;
import com.knee.spotifystreamer.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by c_cknee on 6/8/2015.
 */
public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>{
    private List<ParceableArtist> artists;
    private Context context;
    private int selectedItem;

    public ArtistAdapter(Context  pContext, List<ParceableArtist> pArtists){
        this.artists = pArtists;
        this.context = pContext;
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int i){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_search_result, parent, false);
        ArtistViewHolder artistViewHolder = new ArtistViewHolder(v);
        return artistViewHolder;
    }

    @Override
    public void onBindViewHolder(ArtistViewHolder artistViewHolder, int i) {
        final ParceableArtist thisArtist = artists.get(i);
        final int location = i;
        artistViewHolder.tvArtistNameName.setText(thisArtist.getArtistName());
        if(thisArtist.getArtistImageUrls().size() > 0) {
            Picasso.with(context)
                    .load(thisArtist.getArtistImageUrls().get(0))
                    .placeholder(R.drawable.image_loading)
                    .resize(200, 200)
                    .into(artistViewHolder.ivArtist);
        }else{
            Picasso.with(context)
                    .load(R.drawable.no_image_available)
                    .resize(200, 200)
                    .into(artistViewHolder.ivArtist);
        }
        artistViewHolder.vgContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isNetworkConnected(context)) {
                    BusProvider.getInstance().post(thisArtist);
                }else{
                    Toast.makeText(context, context.getString(R.string.network_unavailable), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
        notifyDataSetChanged();
    }

    public void swapList(List<ParceableArtist> pArtists) {
        artists = pArtists;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder{
        private TextView tvArtistNameName;
        private ImageView ivArtist;
        private ViewGroup vgContainer;


        public ArtistViewHolder(View itemView) {
            super(itemView);
            vgContainer = (ViewGroup) itemView.findViewById(R.id.linearlayout_individual_artist);
            ivArtist = (ImageView) itemView.findViewById(R.id.image_individual_artist_thumbnail);
            tvArtistNameName = (TextView) itemView.findViewById(R.id.textview_artist_name);
        }


    }
}
