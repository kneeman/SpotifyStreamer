package com.knee.spotifystreamer;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by c_cknee on 8/5/2015.
 */
public class PlayerDialogFragment extends DialogFragment{

    private boolean mIsLargeLayout;
    private String songUrl;
    private static final String KEY_LARGE_LAYOUT = "keyLargeLayout", KEY_TRACK_URL = "keyTrackUrl";
    private TextView mArtistName, mAlbumName, mTrackName, mLeftTrackTime, mRightTrackTime;
    private ImageView mAlbumImage;
    private ImageButton mPreviousTrackButton, mNextTrackButton, mPlayPauseButton;
    private SeekBar mSeekBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_track_player, container, false);
        bindControls(view);
        Bundle arguments = getArguments();
        mIsLargeLayout = arguments.getBoolean(KEY_LARGE_LAYOUT, false);
        songUrl = arguments.getString(KEY_TRACK_URL, "");
        return view;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public static PlayerDialogFragment newInstance(boolean largeScreen, String pSongUrl) {
        PlayerDialogFragment frag = new PlayerDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(KEY_LARGE_LAYOUT, largeScreen);
        args.putString(KEY_TRACK_URL, pSongUrl);
        frag.setArguments(args);
        return frag;
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
        mSeekBar = (SeekBar) view.findViewById(R.id.track_seek_bar);
    }
}
