package org.rakvag.hylla.services;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.rakvag.hylla.domain.Album;
import org.rakvag.hylla.domain.Artist;
import org.rakvag.hylla.domain.Sjanger;
import org.rakvag.hylla.domain.Spor;
import org.rakvag.spotifyapi.SearchResult;
import org.rakvag.spotifyapi.entity.SpotifyAlbum;
import org.rakvag.spotifyapi.entity.SpotifyArtist;
import org.rakvag.spotifyapi.entity.SpotifyTrack;

public class Oversetter {

	public static Collection<Album> oversettSpotifyAlbum(Collection<SpotifyAlbum> spotifyAlbumene, Map<String, Sjanger> artistersSjanger) {
		Collection<Album> domAlbumene = new HashSet<Album>();
		for (SpotifyAlbum spotifyAlbum : spotifyAlbumene) {
			Sjanger artistsSjanger = Sjanger.IKKE_SATT;
			if (artistersSjanger.containsKey(spotifyAlbum.getArtistid()))
				artistsSjanger = artistersSjanger.get(spotifyAlbum.getArtistid());
			domAlbumene.add(oversettSpotifyAlbum(spotifyAlbum, artistsSjanger));
		}
		return domAlbumene;
	}

	public static Album oversettSpotifyAlbum(SpotifyAlbum spotifyAlbum, Sjanger sjanger) {
		Album album = new Album();

		album.setAar(spotifyAlbum.getReleased());
		album.setTilgjengeligINorge(spotifyAlbum.erTilgjengeligINorge());
		
		Artist artist = new Artist();
		artist.setNavn(spotifyAlbum.getArtist());
		artist.setSpotifyURI(spotifyAlbum.getArtistid());
		album.setArtist(artist);

		album.setSpotifyURI(spotifyAlbum.getHref());
		album.setNavn(spotifyAlbum.getName());
		album.setSjanger(sjanger);
		album.setSpor(new HashSet<Spor>());
		Double albumLengde = 0D;
		Float popularitet = 0F;
		for (SpotifyTrack track : spotifyAlbum.getTracks()) {
			Spor spor = oversettSpotifyTrack(track);
			albumLengde += spor.getLengde();
			popularitet += spor.getPopularitet();
			album.getSpor().add(spor);
		}
		album.setLengde(albumLengde);
		album.setPopularitet(popularitet / album.getSpor().size());
		
		return album;
	}

	public static Spor oversettSpotifyTrack(SpotifyTrack track) {
		Spor spor = new Spor();

		spor.setDisknummer(track.getDiscnumber());
		spor.setLengde(track.getLength());
		spor.setNavn(track.getName());
		spor.setPopularitet(track.getPopularity());
		spor.setSpornummer(track.getTracknumber());
		spor.setSpotifyURI(track.getHref());

		return spor;
	}

	public static Artist oversettSpotifyArtist(SpotifyArtist spotifyArtist, Map<String, Sjanger> artistersSjanger) {
		Artist artist = new Artist();
		artist.setNavn(spotifyArtist.getName());
		artist.setSpotifyURI(spotifyArtist.getHref());
		Set<Album> album = new HashSet<Album>();
		if (spotifyArtist.getAlbums() != null) {
			for (SearchResult albumWrapper : spotifyArtist.getAlbums()) {
				Sjanger artistsSjanger = Sjanger.IKKE_SATT;
				if (artistersSjanger.containsKey(spotifyArtist.getHref()))
					artistsSjanger = artistersSjanger.get(spotifyArtist.getHref());
				album.add(oversettSpotifyAlbum(albumWrapper.getAlbum(), artistsSjanger));
			}
		}
		artist.setAlbum(album);
		return artist;
	}

}