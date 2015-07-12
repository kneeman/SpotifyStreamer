package com.knee.spotifystreamer;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.knee.spotifystreamer.adapters.TracksAdapter;
import com.knee.spotifystreamer.utils.DividerItemDecoration;
import com.knee.spotifystreamer.utils.SpotifyServiceSingleton;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment {

    private String artistId;
    private RecyclerView mRecyclerView;
    private final String COUNTRY_MAP_KEY = "country";
    private ProgressDialog progressDialog;

    public TopTracksActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        artistId = getActivity().getIntent().getExtras().getString(TopTracksActivity.ARTIST_ID_KEY);
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
        progressDialog = ProgressDialog.show(getActivity(),
                getString(R.string.please_wait_title),
                getString(R.string.please_wait_tracks));
        TopTracksTask topTracksTask = new TopTracksTask();
        topTracksTask.execute(artistId);
        return rootView;
    }

    public static TopTracksActivityFragment newInstance(String pArtistId) {
        TopTracksActivityFragment topTracksActivityFragment = new TopTracksActivityFragment();
        Bundle args = new Bundle();
        args.putString(TopTracksActivity.ARTIST_ID_KEY, pArtistId);
        topTracksActivityFragment.setArguments(args);
        return topTracksActivityFragment;
    }

    public class TopTracksTask extends AsyncTask<String, Void, Tracks> {

        @Override
        protected Tracks doInBackground(String... params) {
            Map<String, Object> map = new HashMap<>();
            map.put(COUNTRY_MAP_KEY, Locale.getDefault().getCountry());
            Tracks results = SpotifyServiceSingleton.getInstance().getArtistTopTrack(params[0], map);
            return results;
        }

        @Override
        protected void onPostExecute(Tracks pTracks) {
            progressDialog.dismiss();
            List<Track> tracks = pTracks.tracks;
            if(tracks.size() == 0 && !this.isCancelled()){
                Toast.makeText(getActivity(), getActivity().getString(R.string.no_tracks_found), Toast.LENGTH_LONG).show();
            }else{
                TracksAdapter mAdapter = new TracksAdapter(getActivity(), pTracks.tracks);
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }
}
