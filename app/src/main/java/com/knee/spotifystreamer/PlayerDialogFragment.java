package com.knee.spotifystreamer;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.URLUtil;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.knee.spotifystreamer.model.TopTracksState;
import com.knee.spotifystreamer.service.AudioService;
import com.knee.spotifystreamer.service.AudioService.AudioStatus;
import com.knee.spotifystreamer.service.AudioService.LocalBinder;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by c_cknee on 8/5/2015.
 */
public class PlayerDialogFragment extends DialogFragment{

    private TopTracksState topTracksState;
    public static final String KEY_TOP_TRACKS_STATE = "keyTopTracksState";
    private TextView mArtistName, mAlbumName, mTrackName, mLeftTrackTime, mRightTrackTime;
    private ImageView mAlbumImage;
    private ImageButton mPreviousTrackButton, mNextTrackButton, mPlayPauseButton, mFastForwardButton, mRewindButton;
    private SeekBar mSeekBar;
    private Gson gson;
    private WeakReference<AudioService> mService;
    private boolean mBound = false;
    private AudioService.OnServiceConnectedListener mServiceListener;
    private Intent musicServiceIntent;
    private View fullPlayerView;
    private Handler seekbarUpdateHandler = new Handler();
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            updateSeeker();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        if(savedInstanceState != null){
            topTracksState = gson.fromJson(savedInstanceState.getString(KEY_TOP_TRACKS_STATE), TopTracksState.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_track_player, container, false);
        bindControls(view);
        if(savedInstanceState == null) {
            Bundle arguments = getArguments();
            topTracksState = gson.fromJson(arguments.getString(KEY_TOP_TRACKS_STATE), TopTracksState.class);
        }
        populateControls();
        musicServiceIntent = new Intent(getActivity(), AudioService.class);
        musicServiceIntent.setAction(AudioService.ACTION_SETUP);
        musicServiceIntent.putExtra(KEY_TOP_TRACKS_STATE,
                gson.toJson(topTracksState));
        getActivity().bindService(musicServiceIntent, mConnection, Context.BIND_ABOVE_CLIENT);
        getActivity().startService(musicServiceIntent);
        fullPlayerView = view;
        return view;
    }




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public static PlayerDialogFragment newInstance(TopTracksState pTopTracksState) {
        PlayerDialogFragment frag = new PlayerDialogFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString(KEY_TOP_TRACKS_STATE, gson.toJson(pTopTracksState));
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_TOP_TRACKS_STATE, gson.toJson(topTracksState));
    }

    private void updateSeeker(){
        if(mService.get() != null) {
            mSeekBar.setProgress(mService.get().getPosition());
            seekbarUpdateHandler.postDelayed(run, 1000);
        }
    }

    private void bindControls(View view) {
        mArtistName = (TextView) view.findViewById(R.id.track_group_name);
        mAlbumName =  (TextView) view.findViewById(R.id.track_album_name);
        mTrackName = (TextView) view.findViewById(R.id.track_track_name);
        mLeftTrackTime = (TextView) view.findViewById(R.id.track_left_track_time);
        mRightTrackTime = (TextView) view.findViewById(R.id.track_right_track_time);
        mAlbumImage = (ImageView) view.findViewById(R.id.track_album_image);
        mPreviousTrackButton = (ImageButton) view.findViewById(R.id.track_previous_button);
        mNextTrackButton = (ImageButton) view.findViewById(R.id.track_next_button);
        mPlayPauseButton = (ImageButton) view.findViewById(R.id.track_play_pause_button);
        mFastForwardButton = (ImageButton) view.findViewById(R.id.track_fastforward_button);
        mRewindButton = (ImageButton) view.findViewById(R.id.track_rewind_button);
        mSeekBar = (SeekBar) view.findViewById(R.id.track_seek_bar);

    }

    private void attachButtonListeners(){
        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.get().playPause();
            }
        });
        mPreviousTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.get().playPrevious();
            }
        });
        mFastForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.get().skipForward();
            }
        });

        mRewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.get().skipBack();
            }
        });
    }

    private void populateControls() {
        Track thisTrack = topTracksState.getTracks().get(topTracksState.getSelectedTrack());
        //TODO need to replace zero with the actual original artist searched for.  Probably need to add
        //to model
        mArtistName.setText(thisTrack.artists.get(0).name);
        mAlbumName.setText(thisTrack.album.name);
        mTrackName.setText(thisTrack.name);
        //TODO Implement in a better way
        mLeftTrackTime.setText("0:00");
        long duration = thisTrack.duration_ms;
        String formattedDuration = String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        mRightTrackTime.setText(formattedDuration);
        populateImage(thisTrack.album.images.get(0).url);
    }

    private void setSeekbar(){
        if(mService != null && mService.get() != null){
            mSeekBar.setMax(mService.get().getDuration());
            seekbarUpdateHandler.postDelayed(run, 100);
        }
    }

    private void populateImage(String pUrlString){
        if(pUrlString != null && URLUtil.isValidUrl(pUrlString)){
            Picasso.with(getActivity())
                    .load(pUrlString)
                    .placeholder(R.drawable.image_loading)
                    .into(mAlbumImage);
        }else{
            Picasso.with(getActivity())
                    .load(R.drawable.no_image_available)
                    .into(mAlbumImage);
        }
    }

    protected void handleAudioUpdate(AudioStatus passedStatus) {
        if (passedStatus.equals(AudioStatus.CHANGED)){
            updateAudioControls();
        }else if(passedStatus.equals(AudioStatus.PLAYING) || passedStatus.equals(AudioStatus.PAUSED)){
            updateAudioPlayPause(passedStatus);
        }
    }

    private void updateAudioPlayPause(AudioStatus passedStatus) {
        if (passedStatus.equals(AudioStatus.PLAYING)) {
            mPlayPauseButton.setImageResource(android.R.drawable.ic_media_pause);
            setSeekbar();
        } else {
            mPlayPauseButton.setImageResource(android.R.drawable.ic_media_play);
        }

    }
    private void updateAudioControls() {
//        if(mediaController != null && mService != null && mService.get() != null){
//            seekTo(getCurrentPosition());
//        }

    }
//
//    @Override
//    public void onAudioServiceConnected(Intent musicServiceIntent) {
//
//    }

    private void handleTrackChanged(int newTrack) {
        topTracksState.setSelectedTrack(newTrack);
        //fullPlayerView.invalidate();
        populateControls();
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            binder.setListener(new AudioService.AudioStatusListener() {
                @Override
                public void sendStatusUpdate(AudioService.AudioStatus status) {
                    PlayerDialogFragment.this.handleAudioUpdate(status);
                }

                @Override
                public void trackChanged(int newTrack) {
                    PlayerDialogFragment.this.handleTrackChanged(newTrack);
                }

            });
            mBound = true;
            attachButtonListeners();
            //mServiceListener.onAudioServiceConnected(musicServiceIntent);
            musicServiceIntent = null;
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mBound = false;
        }
    };
}
