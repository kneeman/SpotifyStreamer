package com.knee.spotifystreamer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.knee.spotifystreamer.PlayerDialogFragment;
import com.knee.spotifystreamer.R;
import com.knee.spotifystreamer.bus.AudioControlsDisplayMessage;
import com.knee.spotifystreamer.bus.BusProvider;
import com.knee.spotifystreamer.bus.DialogMessage;
import com.knee.spotifystreamer.model.TopTracksState;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.ref.WeakReference;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by me for a work project in March 2015.  This will either stream or play a downloaded file.
 */
public class AudioService extends Service implements MediaPlayer.OnPreparedListener {

    @SuppressWarnings("unused")
    private final String TAG = "AudioService";

    public static final String ACTION_SETUP = "actions_setup";
    public static final String ACTION_PLAY_PAUSE = "action_play_pause";
    public static final String ACTION_NOTIFICATION_PLAY_PAUSE = "action_notification_play_pause";
    public static final String ACTION_NOTIFICATION_FAST_FORWARD = "action_notification_fast_forward";
    public static final String ACTION_NOTIFICATION_REWIND = "action_notification_rewind";
    public static final String ACTION_NOTIFICATION_STOP = "action_notification_stop";
    public static final String ACTION_NOTIFICATION_NEXT = "action_notification_next";
    public static final String ACTION_NOTIFICATION_PREVIOUS = "action_notification_previous";
    public static final String AUDIO_FILE_LOCATION = "audio_file_location";
    public static final String NOTIFICATION_SEARCH_VALUE = "_notification_"; //Used in handleIntent to determine if intent was triggered by notification
    private boolean mIsPlaying = false;
    private static final int NOTIFICATION_ID = 1;
    private MediaPlayer mPlayer;
    private int mStartID;
    private int seekForwardTime = 10000;
    private int seekBackwardTime = 10000;
    private NotificationManager manager;
    private Intent intentHolder;
    private DialogMessage dialogMessage;
    private WifiManager.WifiLock wifiLock;
    private final IBinder mBinder = new LocalBinder();
    private AudioStatusListener mListener;
    private TopTracksState topTracksState;
    private Track thisTrack;
    private Notification notification;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        intentHolder = intent;
        if(intent.getAction().equalsIgnoreCase(ACTION_SETUP)){
            String passedTopTrackStateString = intent.getExtras().getString(PlayerDialogFragment.KEY_TOP_TRACKS_STATE);
            topTracksState = new Gson().fromJson(passedTopTrackStateString, TopTracksState.class);
            setTrack(topTracksState.getSelectedTrack());
            createPlayer();
            prepareData();
        }else {
            handleIntent(intent);
        }
        mStartID = startId;
        // Don't automatically restart this Service if it is killed
        return START_NOT_STICKY;
    }

    private void prepareData() {
        try {
            mPlayer.setDataSource(thisTrack.preview_url);
            mPlayer.setOnPreparedListener(this);
            mPlayer.prepareAsync();
            dialogMessage = new DialogMessage(getString(R.string.preparing_audio_message),
                    getString(R.string.preparing_audio_title), DialogMessage.DialogAction.START);
            BusProvider.getInstance().post(dialogMessage);
        } catch (IllegalArgumentException | SecurityException
                | IllegalStateException | IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void createPlayer() {
        if(mPlayer != null){
            mPlayer.stop();
        }
        mPlayer = new MediaPlayer();
        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "spotifyStreamerLock");
        wifiLock.acquire();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setLooping(false);
        // Stop Service when music has finished playing
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // stop Service if it was started with this ID
                // Otherwise let other start commands proceed
                stopSelf(); //mStartID
            }
        });
    }

    @Override
    public void onDestroy() {
        haltPlayback();
        manager.cancelAll();
        mListener = null;
        stopForeground(true);
    }

    private void haltPlayback(){
        if (null != mPlayer){
            if (mPlayer.isPlaying()){
                mPlayer.stop();
            }
            mPlayer.setOnPreparedListener(null);
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mPlayer = mp;
        handleIntent(intentHolder);
        //intentHolder = null;
        if (dialogMessage != null){
            dialogMessage.setDialogAction(DialogMessage.DialogAction.STOP);
            BusProvider.getInstance().post(dialogMessage);
            dialogMessage = null;
        }
        BusProvider.getInstance().post
                (new AudioControlsDisplayMessage(AudioControlsDisplayMessage.AudioControlsAction.PREPARED, "Temp Title"));

    }

    private void handleIntent( Intent intent ) {
        boolean fromNotification = intent.getAction().contains(NOTIFICATION_SEARCH_VALUE);
        switch(intent.getAction()){
            case ACTION_SETUP:
            case ACTION_PLAY_PAUSE:
            case ACTION_NOTIFICATION_PLAY_PAUSE:
                playPause();
                break;
            case ACTION_NOTIFICATION_FAST_FORWARD:
                skipForward();
                break;
            case ACTION_NOTIFICATION_REWIND:
                skipBack();
                break;
            case ACTION_NOTIFICATION_STOP:
                stop();
                break;
            case ACTION_NOTIFICATION_NEXT:
                playNext();
                break;
            case ACTION_NOTIFICATION_PREVIOUS:
                playPrevious();
                break;

        }
//        if( intent != null && intent.getAction() != null ) {
//            String actionPassed = intent.getAction();
//            if( actionPassed.equalsIgnoreCase( ACTION_NOTIFICATION_PLAY_PAUSE)
//                    || actionPassed.equalsIgnoreCase(ACTION_PLAY_PAUSE)) {
//                playPause();
//            } else if( actionPassed.equalsIgnoreCase( ACTION_NOTIFICATION_FAST_FORWARD ) ) {
//                skipForward();
//            } else if( actionPassed.equalsIgnoreCase( ACTION_NOTIFICATION_REWIND ) ) {
//                skipBack();
//            } else if (actionPassed.equalsIgnoreCase( ACTION_NOTIFICATION_STOP )){
//                stop();
//            }
//            if(actionPassed.contains(NOTIFICATION_SEARCH_VALUE)){
//                if(mListener != null){
//                    if(actionPassed.equals(ACTION_NOTIFICATION_PLAY_PAUSE)){
//                        if(mIsPlaying){
//                            mListener.sendStatusUpdate(AudioStatus.PLAYING);
//                        }else{
//                            mListener.sendStatusUpdate(AudioStatus.PAUSED);
//                        }
//                    }else{
//                        mListener.sendStatusUpdate(AudioStatus.CHANGED);
//                    }
//                }
//            }
//        }
    }



    public void playPause(){
        if(!mPlayer.isPlaying()){
            mPlayer.start();
            mListener.sendStatusUpdate(AudioStatus.PLAYING);
        }else{
            mPlayer.pause();
            mListener.sendStatusUpdate(AudioStatus.PAUSED);
        }
        showNotification(mPlayer.isPlaying());
    }
    public void skipBack() {
        int currentPosition = mPlayer.getCurrentPosition();
        if (currentPosition - seekBackwardTime >= 0) {
            mPlayer.seekTo(currentPosition - seekBackwardTime);
        } else {
            mPlayer.seekTo(0);
        }
    }


    public void skipForward() {
        int currentPosition = mPlayer.getCurrentPosition();
        if (currentPosition + seekForwardTime <= mPlayer.getDuration()) {
            mPlayer.seekTo(currentPosition + seekForwardTime);
        } else {
            stop();
        }
    }
    public void stop(){
//		if(wifiLock != null){
//			wifiLock.release();
//		}
        stopSelf();
    }

    public void playPrevious(){
        int currentPosition = topTracksState.getSelectedTrack();
        if(currentPosition == 0){
            mPlayer.seekTo(0);
        }else {
            moveTracks(currentPosition - 1);
            //TODO Have to handle next step as intentHolder will be null.
        }
    }

    public void playNext(){
        int currentPosition = topTracksState.getSelectedTrack();
        if(currentPosition >= (topTracksState.getTracks().size() - 1)) {
            mPlayer.seekTo(mPlayer.getDuration());
        }else{
            moveTracks(currentPosition + 1);
        }
    }

    public int getPosition(){
        return mPlayer.getCurrentPosition();
    }

    public int getDuration(){
        return mPlayer.getDuration();
    }

    public void seek(int position){
        mPlayer.seekTo(position);
    }

    public boolean isPlaying(){
        return mPlayer.isPlaying();
    }

    public String getTrackName(){
        if(thisTrack != null){
            return thisTrack.name;
        }else
            return null;
    }

    private void showNotification( boolean isPlaying ) {
        notification = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.spot_stream)
                .setAutoCancel(true)
                .setContentTitle( getString( R.string.app_name ) )
                .setPriority(Notification.PRIORITY_MAX)
                .build();
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ){
            notification.bigContentView = getExpandedView( isPlaying );
        }else{
            //TODO implement ICS handling with relaunch of activity if currently playing.  Otherwise
            //maybe launch generic audio player.  Not sure.
        }
        // Put this Service in a foreground state, so it won't be killed
        startForeground(NOTIFICATION_ID, notification);
        manager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        manager.notify( NOTIFICATION_ID, notification );
    }

    private void setTrack(int i){
        topTracksState.setSelectedTrack(i);
        thisTrack = topTracksState.getTracks().get(i);
    }

    public int getCurrentTrack(){
        return topTracksState.getSelectedTrack();
    }

    public static Intent getBaseIntent(Context context){
        return new Intent(context, AudioService.class);
    }

    private void moveTracks(int toTrack){
        mListener.trackChanging();
        topTracksState.setCurrentlyPlaying(true);
        setTrack(toTrack);
        haltPlayback();
        createPlayer();
        prepareData();
        mListener.trackChanged(toTrack);
    }

    private RemoteViews getExpandedView( boolean isPlaying ) {
        RemoteViews customView = new RemoteViews(this.getPackageName(), R.layout.notification);
        String thumbNailUri = thisTrack.album.images.get(thisTrack.album.images.size() - 1).url;
        Picasso.with(AudioService.this)
                .load(thumbNailUri)
                .into(customView, R.id.notification_icon, NOTIFICATION_ID, notification);
        customView.setTextViewText(R.id.notification_textview, getTrackName());
        if( isPlaying )
            customView.setImageViewResource( R.id.notification_play_pause, R.drawable.ic_pause );
        else
            customView.setImageViewResource( R.id.notification_play_pause, R.drawable.ic_play );

        Intent intent = new Intent( getApplicationContext(), AudioService.class );

        intent.setAction( ACTION_NOTIFICATION_PLAY_PAUSE );
        PendingIntent pendingIntent = PendingIntent.getService( getApplicationContext(), 1, intent, 0 );
        customView.setOnClickPendingIntent( R.id.notification_play_pause, pendingIntent );

        intent.setAction( ACTION_NOTIFICATION_STOP );
        pendingIntent = PendingIntent.getService( getApplicationContext(), 1, intent, 0 );
        customView.setOnClickPendingIntent( R.id.notification_stop, pendingIntent );

        intent.setAction( ACTION_NOTIFICATION_FAST_FORWARD );
        pendingIntent = PendingIntent.getService( getApplicationContext(), 1, intent, 0 );
        customView.setOnClickPendingIntent( R.id.notification_fast_forward, pendingIntent );

        intent.setAction( ACTION_NOTIFICATION_REWIND );
        pendingIntent = PendingIntent.getService( getApplicationContext(), 1, intent, 0 );
        customView.setOnClickPendingIntent( R.id.notification_rewind, pendingIntent );

        intent.setAction( ACTION_NOTIFICATION_NEXT);
        pendingIntent = PendingIntent.getService( getApplicationContext(), 1, intent, 0 );
        customView.setOnClickPendingIntent( R.id.notification_next, pendingIntent );

        intent.setAction( ACTION_NOTIFICATION_PREVIOUS);
        pendingIntent = PendingIntent.getService( getApplicationContext(), 1, intent, 0 );
        customView.setOnClickPendingIntent( R.id.notification_previous, pendingIntent );

        return customView;
    }

    @Override
    public IBinder onBind(Intent intent) {return mBinder;}

    public class LocalBinder extends Binder {
        public WeakReference<AudioService> getService() {
            // Return this instance of LocalService so clients can call public methods
            return new WeakReference<AudioService>(AudioService.this);
        }

        public void setListener(AudioStatusListener listener){
            mListener = listener;
        }
    }

    public enum AudioStatus{PLAYING, PAUSED, CHANGED}

    public interface AudioStatusListener{
        public void sendStatusUpdate(AudioStatus playing);
        public void trackChanged(int newTrack);
        public void trackChanging();
    }

    public interface OnServiceConnectedListener{
        public void onAudioServiceConnected(Intent musicServiceIntent);
    }
}
