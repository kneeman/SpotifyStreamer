package com.knee.spotifystreamer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.knee.spotifystreamer.adapters.ArtistAdapter;
import com.knee.spotifystreamer.model.ParceableArtist;
import com.knee.spotifystreamer.utils.DividerItemDecoration;
import com.knee.spotifystreamer.utils.SpotifyServiceSingleton;
import com.knee.spotifystreamer.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ArtistAdapter mAdapter;
    private EditText mEditTextArtist;
    private static final String TAG = MainActivity.class.getSimpleName();
    private SpotifyCallTask spotifyCallTask;
    private ProgressDialog progressDialog;
    private List<ParceableArtist> mArtists;
    private static final String KEY_ARTISTS = "keyArtists";


    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            mArtists = savedInstanceState.getParcelableArrayList(KEY_ARTISTS);
        }else
            mArtists = new ArrayList<ParceableArtist>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setRetainInstance(true);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_search_results_artist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //mRecyclerView.setAdapter(mAdapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(),
                LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);
        mAdapter = new ArtistAdapter(getActivity(),mArtists);
        mRecyclerView.setAdapter(mAdapter);
        mEditTextArtist = (EditText) rootView.findViewById(R.id.edit_text_artist_search);
//        RxTextView.editorActionEvents(mEditTextArtist)
//                .filter(new Func1<TextViewEditorActionEvent, Boolean>() {
//                    @Override
//                    public Boolean call(TextViewEditorActionEvent textViewEditorActionEvent) {
//                        return textViewEditorActionEvent.actionId() == EditorInfo.IME_ACTION_SEND;
//                    }
//                }).filter(new Func1<TextViewEditorActionEvent, Boolean>() {
//            @Override
//            public Boolean call(TextViewEditorActionEvent textViewEditorActionEvent) {
//                return mEditTextArtist.getText().length() >= 2;
//            }
//        })
//            .switchOnNext(Observable.just("foo")
//                    .map(new Func1<String, Tracks>() {
//                        @Override
//                        public Tracks call(String pArtistId) {
//                            Map<String, Object> map = new HashMap<>();
//                            map.put(COUNTRY_MAP_KEY, Locale.getDefault().getCountry());
//                            Tracks results = SpotifyServiceSingleton.getInstance().getArtistTopTrack(pArtistId, map);
//                            return results;
//                        }
//                    })
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Observer<Tracks>() {
//                        @Override
//                        public void onCompleted() {
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                        }
//
//                        @Override
//                        public void onNext(Tracks pTracks) {
//                            progressDialog.dismiss();
//                            List<Track> tracks = pTracks.tracks;
//                            if (tracks.size() == 0) {
//                                Toast.makeText(getActivity(), getActivity().getString(R.string.no_tracks_found), Toast.LENGTH_LONG).show();
//                            } else {
//                                if (mTracks != null && mTracks.size() > 0) {
//                                    mTracks.clear();
//                                }
//                                for (Track thisTrack : tracks) {
//                                    ParceableTrack pa = new ParceableTrack(thisTrack);
//                                    mTracks.add(pa);
//                                }
//                                if (mAdapter != null) {
//                                    mAdapter.swapList(mTracks);
//                                }
//                            }
//                        }
//                    });


        mEditTextArtist.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    Context currentContext = (MainActivityFragment.this.getActivity());
                    if(Utils.isNetworkConnected(currentContext)) {
                        progressDialog = ProgressDialog.show(getActivity(),
                                getString(R.string.please_wait_title),
                                getString(R.string.please_wait_artist_message));
                        SpotifyCallTask spotifyCallTask = new SpotifyCallTask();
                        spotifyCallTask.execute(v.getText().toString().trim());
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        handled = true;
                    }else{
                        Toast.makeText(currentContext, currentContext.getString(R.string.network_unavailable), Toast.LENGTH_LONG).show();
                    }
                }
                return handled;
            }
        });

        //Decided to not use this method, but interesting enough to keep an example for later use
//        mEditTextArtist.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String currentText = mEditTextArtist.getText().toString().trim();
//                if(currentText.length() > 2){
//                    if(spotifyCallTask != null){
//                        spotifyCallTask.cancel(true);
//                        spotifyCallTask = null;
//                    }
//                    SpotifyCallTask spotifyCallTask = new SpotifyCallTask();
//                    spotifyCallTask.execute(currentText);
//                }
//            }
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });



        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_ARTISTS, (ArrayList<? extends Parcelable>) mArtists);

    }

    public class SpotifyCallTask extends AsyncTask<String, Void, ArtistsPager>{

        @Override
        protected ArtistsPager doInBackground(String... params) {
            ArtistsPager results = SpotifyServiceSingleton.getInstance().searchArtists(params[0]);
            return results;
        }

        @Override
        protected void onPostExecute(ArtistsPager artistsPager) {
            List<Artist> artists = artistsPager.artists.items;
            if(mArtists != null && mArtists.size() > 0) {
                mArtists.clear();
            }
            for(Artist thisArtist: artists){
                ParceableArtist pa = new ParceableArtist(thisArtist);
                mArtists.add(pa);
            }
            progressDialog.dismiss();
            if(artists.size() == 0 && !this.isCancelled()){
                Toast.makeText(getActivity(), getActivity().getString(R.string.no_artists_found), Toast.LENGTH_LONG).show();
            }else{
                if(mAdapter != null) {
                    mAdapter.swapList(mArtists);
                }
            }
            //Used in conjunction with the optional TextChangedListener.  Uncomment out if using.
            //spotifyCallTask = null;
        }
    }
}
