package org.rakvag.spotifyapi.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rakvag.spotifyapi.SearchResult;

public class SpotifyArtist extends SpotifyEntitet {
	private List<SearchResult> albums;

	public SpotifyArtist() {
		super();
	}
	
	public SpotifyArtist(String name, String href) {
		super();
		this.name = name;
		this.href = href;
	}
	
	public List<SearchResult> getAlbums() {
		return albums;
	}

	public void setAlbums(List<SearchResult> albums) {
		this.albums = albums;
	}
	
	public static String lagSpotifylink(String artistURI) {
		return "http://open.spotify.com/artist/" + artistURI.split(":")[2];
	}

	public Set<String> hentAlbumURIene() {
		Set<String> albumURIene = new HashSet<String>();
		for (SearchResult searchResult : albums) {
			if (searchResult.getAlbum() != null)
				albumURIene.add(searchResult.getAlbum().getHref());
		}
		return albumURIene;
	}

}
