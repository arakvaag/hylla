package org.rakvag.spotifyapi.entity;

import java.util.List;

public class SpotifyTrack extends SpotifyEntitet {

	private int tracknumber;
	private int discnumber;
	private float popularity;
	private float length;
	private SpotifyAlbum album;
	private List<SpotifyArtist> artists;

	public SpotifyTrack() {
		super();
	}
	
	public int getTracknumber() {
		return tracknumber;
	}

	public int getDiscnumber() {
		return discnumber;
	}

	public float getPopularity() {
		return popularity;
	}

	public float getLength() {
		return length;
	}

	public SpotifyAlbum getAlbum() {
		return album;
	}

	public void setAlbum(SpotifyAlbum album) {
		this.album = album;
	}

	public List<SpotifyArtist> getArtists() {
		return artists;
	}

	public void setArtists(List<SpotifyArtist> artists) {
		this.artists = artists;
	}

}
