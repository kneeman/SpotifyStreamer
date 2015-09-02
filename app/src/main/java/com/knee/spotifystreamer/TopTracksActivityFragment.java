package com.knee.spotifystreamer;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.knee.spotifystreamer.adapters.TracksAdapter;
import com.knee.spotifystreamer.model.ParceableTrack;
import com.knee.spotifystreamer.utils.DividerItemDecoration;
import com.knee.spotifystreamer.utils.SpotifyServiceSingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment {

    private String artistId;
    private RecyclerView mRecyclerView;
    private TracksAdapter mAdapter;
    private final String COUNTRY_MAP_KEY = "country", KEY_TRACKS = "keyTracks";
    private ProgressDialog progressDialog;
    private List<ParceableTrack> mTracks;


    public TopTracksActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        artistId = getActivity().getIntent().getExtras().getString(TopTracksActivity.ARTIST_ID_KEY);
        if(savedInstanceState != null){
            mTracks = savedInstanceState.getParcelableArrayList(KEY_TRACKS);
        }else
            mTracks = new ArrayList<ParceableTrack>();
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
//            TopTracksTask topTracksTask = new TopTracksTask();
//            topTracksTask.execute(artistId);

            Observable.just(artistId)
                    .map(new Func1<String,Tracks>() {
                        @Override
                        public Tracks call(String pArtistId) {
                            Map<String, Object> map = new HashMap<>();
                            map.put(COUNTRY_MAP_KEY, Locale.getDefault().getCountry());
                            Tracks results = SpotifyServiceSingleton.getInstance().getArtistTopTrack(pArtistId, map);
                            return results;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Tracks>() {
                        @Override
                        public void onCompleted() {}
                        @Override
                        public void onError(Throwable e) {}
                        @Override
                        public void onNext(Tracks pTracks) {
                            progressDialog.dismiss();
                            List<Track> tracks = pTracks.tracks;
                            if(tracks.size() == 0){
                                Toast.makeText(getActivity(), getActivity().getString(R.string.no_tracks_found), Toast.LENGTH_LONG).show();
                            }else{
                                if(mTracks != null && mTracks.size() > 0) {
                                    mTracks.clear();
                                }
                                for(Track thisTrack: tracks){
                                    ParceableTrack pa = new ParceableTrack(thisTrack);
                                    mTracks.add(pa);
                                }
                                if(mAdapter != null) {
                                    mAdapter.swapList(mTracks);
                                }
                            }
                        }
                    });
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_TRACKS, (ArrayList<? extends Parcelable>) mTracks);

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
                if(mTracks != null && mTracks.size() > 0) {
                    mTracks.clear();
                }
                for(Track thisTrack: tracks){
                    ParceableTrack pa = new ParceableTrack(thisTrack);
                    mTracks.add(pa);
                }
                if(mAdapter != null) {
                    mAdapter.swapList(mTracks);
                }
            }
        }
    }
}
