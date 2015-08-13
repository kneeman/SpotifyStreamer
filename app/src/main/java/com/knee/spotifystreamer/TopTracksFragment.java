package com.knee.spotifystreamer;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.knee.spotifystreamer.adapters.TracksAdapter;
import com.knee.spotifystreamer.bus.BusProvider;
import com.knee.spotifystreamer.tasks.TopTracksTask;
import com.knee.spotifystreamer.utils.DividerItemDecoration;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksFragment extends Fragment {

    public static final String FRAGMENT_TAG = "topTracksActivityFragmentTag";
    private static final String TAG = TopTracksFragment.class.getSimpleName();
    private String artistId;
    private RecyclerView mRecyclerView;
    private TracksAdapter mAdapter;
    private final String KEY_TRACKS = "keyTracks";
    private ProgressDialog progressDialog;
    private List<Track> mTracks;
    private Gson gson;


    public TopTracksFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        if(getActivity().getIntent().hasExtra(TopTracksActivity.ARTIST_ID_KEY)){
            artistId = getActivity().getIntent().getExtras().getString(TopTracksActivity.ARTIST_ID_KEY);
        }else {
            artistId = getArguments().getString(TopTracksActivity.ARTIST_ID_KEY);
        }
        if(savedInstanceState != null){
            String retrievedTracks = savedInstanceState.getString(KEY_TRACKS, null);
            if(retrievedTracks != null){
                mTracks= gson.fromJson(retrievedTracks, new TypeToken<List<Track>>(){}.getType());
            }else{
                mTracks = new ArrayList<Track>();
            }
            //mTracks = savedInstanceState.getParcelableArrayList(KEY_TRACKS);
        }else
            mTracks = new ArrayList<Track>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_top_tracks);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(),
                LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);
        mAdapter = new TracksAdapter(getActivity(), mTracks);
        mRecyclerView.setAdapter(mAdapter);
        if(savedInstanceState == null) {
            progressDialog = ProgressDialog.show(getActivity(),
                    getString(R.string.please_wait_title),
                    getString(R.string.please_wait_tracks));
            TopTracksTask topTracksTask = new TopTracksTask();
            topTracksTask.execute(artistId);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mTracks.size() > 0){
            String json = gson.toJson(mTracks);
            outState.putString(KEY_TRACKS, json);
        }
        //outState.putParcelableArrayList(KEY_TRACKS, (ArrayList<? extends Parcelable>) mTracks);
    }

    public static TopTracksFragment newInstance(String pArtistId) {
        TopTracksFragment topTracksFragment = new TopTracksFragment();
        Bundle args = new Bundle();
        args.putString(TopTracksActivity.ARTIST_ID_KEY, pArtistId);
        topTracksFragment.setArguments(args);
        return topTracksFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void handleSpotifyNetworkResult(Tracks pTracks) {
        progressDialog.dismiss();
        List<Track> tracks = pTracks.tracks;
        if (tracks.size() == 0) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.no_tracks_found), Toast.LENGTH_LONG).show();
        } else {
            if (mTracks != null && mTracks.size() > 0) {
                mTracks.clear();
            }
            mTracks = tracks;
//
//            for (Track thisTrack : tracks) {
//                //ParceableTrack pa = new ParceableTrack(thisTrack);
//                mTracks.add(pa);
//            }
            if (mAdapter != null) {
                mAdapter.swapList(mTracks);
            }
        }
    }
}
