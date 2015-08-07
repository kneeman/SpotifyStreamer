package com.knee.spotifystreamer;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Created by c_cknee on 8/5/2015.
 */
public class PlayerDialogFragment extends DialogFragment{

    private boolean mIsLargeLayout;
    private String songUrl;
    private static final String KEY_LARGE_LAYOUT = "keyLargeLayout", KEY_TRACK_URL = "keyTrackUrl";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        mIsLargeLayout = arguments.getBoolean(KEY_LARGE_LAYOUT, false);
        songUrl = arguments.getString(KEY_TRACK_URL, "");
        return inflater.inflate(R.layout.fragment_track_player, container, false);
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
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
}
