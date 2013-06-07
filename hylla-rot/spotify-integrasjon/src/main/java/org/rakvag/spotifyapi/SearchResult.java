package org.rakvag.spotifyapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rakvag.spotifyapi.entity.SpotifyAlbum;
import org.rakvag.spotifyapi.entity.SpotifyArtist;
import org.rakvag.spotifyapi.entity.SpotifyTrack;

import com.google.gson.Gson;

public class SearchResult {

	private List<SpotifyAlbum> albums;
	private SpotifyAlbum album;
	private SpotifyTrack track;
	private SpotifyArtist artist;

	public static SearchResult parseJsonSearchResult(String jsonResponse) {
		// Har ikke funnet hvordan GSON kan deserialisere når feltnavnet har - i seg
		jsonResponse = jsonResponse.replace("-id", "id");
		jsonResponse = jsonResponse.replace("-number", "number");
		SearchResult resultat = new Gson().fromJson(jsonResponse, SearchResult.class);
		return sikreEnObjektinstansPrEntitet(resultat);
	}

	private static SearchResult sikreEnObjektinstansPrEntitet(SearchResult resultat) {
		Map<String, SpotifyAlbum> albumMap = new HashMap<String, SpotifyAlbum>();
		Map<String, SpotifyTrack> trackMap = new HashMap<String, SpotifyTrack>();
		Map<String, SpotifyArtist> artistMap = new HashMap<String, SpotifyArtist>();

		SearchResult vasketResultat = new SearchResult();

		if (resultat.albums != null) {
			vasketResultat.albums = new ArrayList<SpotifyAlbum>();
			for (SpotifyAlbum album : resultat.getAlbums()) {
				vasketResultat.albums.add(sikreEnObjektinstansPrEntitet(album, albumMap, trackMap, artistMap));
			}
		}
		if (resultat.album != null)
			vasketResultat.album = sikreEnObjektinstansPrEntitet(resultat.album, albumMap, trackMap, artistMap);
		if (resultat.artist != null)
			vasketResultat.artist = sikreEnObjektinstansPrEntitet(resultat.artist, albumMap, trackMap, artistMap);
		if (resultat.track != null)
			vasketResultat.track = sikreEnObjektinstansPrEntitet(resultat.track, albumMap, trackMap, artistMap);

		return vasketResultat;
	}

	private static SpotifyAlbum sikreEnObjektinstansPrEntitet(SpotifyAlbum album,
			final Map<String, SpotifyAlbum> albumMap, final Map<String, SpotifyTrack> trackMap,
			final Map<String, SpotifyArtist> artistMap) {

		if (albumMap.containsKey(album.getHref()))
			return albumMap.get(album.getHref());

		albumMap.put(album.getHref(), album);
		if (album.getTracks() != null) {
			List<SpotifyTrack> nyeTracks = new ArrayList<SpotifyTrack>();
			for (SpotifyTrack track : album.getTracks()) {
				nyeTracks.add(sikreEnObjektinstansPrEntitet(track, albumMap, trackMap, artistMap));
			}
			album.setTracks(nyeTracks);
		}

		return album;
	}

	private static SpotifyTrack sikreEnObjektinstansPrEntitet(SpotifyTrack track,
			final Map<String, SpotifyAlbum> albumMap, final Map<String, SpotifyTrack> trackMap,
			final Map<String, SpotifyArtist> artistMap) {

		if (trackMap.containsKey(track.getHref()))
			return trackMap.get(track.getHref());

		trackMap.put(track.getHref(), track);
		if (track.getAlbum() != null) {
			if (albumMap.containsKey(track.getAlbum().getHref()))
				track.setAlbum(albumMap.get(track.getAlbum().getHref()));
			else
				track.setAlbum(sikreEnObjektinstansPrEntitet(track.getAlbum(), albumMap, trackMap, artistMap));
		}
		if (track.getArtists() != null) {
			List<SpotifyArtist> nyeArtists = new ArrayList<SpotifyArtist>();
			for (SpotifyArtist artist : track.getArtists()) {
				nyeArtists.add(sikreEnObjektinstansPrEntitet(artist, albumMap, trackMap, artistMap));
			}
			track.setArtists(nyeArtists);
		}

		return track;
	}

	private static SpotifyArtist sikreEnObjektinstansPrEntitet(SpotifyArtist artist,
			final Map<String, SpotifyAlbum> albumMap, final Map<String, SpotifyTrack> trackMap,
			final Map<String, SpotifyArtist> artistMap) {

		if (artistMap.containsKey(artist.getHref()))
			return artistMap.get(artist.getHref());

		artistMap.put(artist.getHref(), artist);

		return artist;
	}

	public List<SpotifyAlbum> getAlbums() {
		return albums;
	}

	public SpotifyAlbum getAlbum() {
		return album;
	}

	public SpotifyTrack getTrack() {
		return track;
	}

	public void setTrack(SpotifyTrack track) {
		this.track = track;
	}

	public SpotifyArtist getArtist() {
		return artist;
	}

}
