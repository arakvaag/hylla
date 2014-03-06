package org.rakvag.spotifyapi;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.rakvag.spotifyapi.entity.SpotifyAlbum;
import org.rakvag.spotifyapi.entity.SpotifyArtist;
import org.rakvag.spotifyapi.entity.SpotifyTrack;

public interface SpotifyAPI {
	List<SpotifyAlbum> soekEtterAlbum(String artist, String album, int maksForsoek);

	Collection<SpotifyAlbum> hentAlbumPaaSpotifyURIer(Collection<String> spotifyURIer, int maksForsoek);

	Map<String, SpotifyArtist> hentArtisterPaaSpotifyURIer(Collection<String> spotifyURIer, int maksForsoek);

	SpotifyArtist hentArtistPaaSpotifyURI(String spotifyURI, int maksForsoek);

	Map<String, SpotifyTrack> hentTracksPaaSpotifyURIer(Collection<String> spotifyURIer, int maksForsoek);

	Map<String, String> hentBildelinker(Collection<String> spotifyURIer);

	String hentBildelink(String artistURI);
}
