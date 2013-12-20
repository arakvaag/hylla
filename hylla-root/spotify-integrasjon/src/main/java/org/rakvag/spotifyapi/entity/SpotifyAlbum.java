package org.rakvag.spotifyapi.entity;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpotifyAlbum extends SpotifyEntitet {

	private static final Logger logger = LoggerFactory.getLogger(SpotifyEntitet.class.getName());

	private String artistid;
	private String artist;
	private int released;
	private List<SpotifyTrack> tracks;
	private List<SpotifyArtist> artists;
	private Availability availability;

	public SpotifyAlbum() {
		super();
	}

	public static String lagSpotifylink(String spotifyURI) {
		return "http://open.spotify.com/album/" + spotifyURI.split(":")[2];
	}

	public boolean erTilgjengeligINorge() {
		if (availability == null) {
			logger.warn("På SpotifyAlbum med href " + href + " er availability = null");
			return false;
		}

		String territories = availability.getTerritories();
		return (territories.isEmpty() || territories.contains("NO") || territories.contains("worldwide"));
	}

	public String getArtistid() {
		return artistid;
	}

	public int getReleased() {
		return released;
	}

	public List<SpotifyTrack> getTracks() {
		return tracks;
	}

	public Availability getAvailability() {
		return availability;
	}

	public void setTracks(List<SpotifyTrack> tracks) {
		this.tracks = tracks;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public List<SpotifyArtist> getArtists() {
		return artists;
	}

	public void setArtists(List<SpotifyArtist> artists) {
		this.artists = artists;
	}

}