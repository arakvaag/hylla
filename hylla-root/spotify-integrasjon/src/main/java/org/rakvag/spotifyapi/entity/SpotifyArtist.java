package org.rakvag.spotifyapi.entity;

import java.util.List;

import org.rakvag.spotifyapi.SearchResult;

public class SpotifyArtist extends SpotifyEntitet {
	private List<SearchResult> albums;

	public SpotifyArtist() {
		super();
	}
	
	public List<SearchResult> getAlbums() {
		return albums;
	}

	public static String lagSpotifylink(String artistURI) {
		return "http://open.spotify.com/artist/" + artistURI.split(":")[2];
	}

}
