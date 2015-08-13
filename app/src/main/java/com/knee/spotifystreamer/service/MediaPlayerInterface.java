package com.knee.spotifystreamer.service;

public interface MediaPlayerInterface {
	public boolean play();
	public boolean stream();
	public boolean status();
	public boolean download();
	public void stop();
}
