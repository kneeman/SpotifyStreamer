package com.knee.spotifystreamer.bus;

/**
 * Created by c_cknee on 8/4/2015.
 */
public class AudioControlsDisplayMessage {
    private AudioControlsAction audioControlsAction;
    private String audioTitle;

    public AudioControlsDisplayMessage(AudioControlsAction audioControlsAction,
                                       String audioTitle) {
        super();
        this.audioControlsAction = audioControlsAction;
        this.audioTitle = audioTitle;
    }

    public String getAudioTitle() {
        return audioTitle;
    }

    public void setAudioTitle(String audioTitle) {
        this.audioTitle = audioTitle;
    }

    public AudioControlsAction getAudioControlsAction() {
        return audioControlsAction;
    }

    public void setAudioControlsAction(AudioControlsAction audioControlsAction) {
        this.audioControlsAction = audioControlsAction;
    }

    public enum AudioControlsAction{
        PREPARED;
    }
}
