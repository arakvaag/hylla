package org.rakvag.hylla.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
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
		//TODO Skal ikke ha ansvaret for å sette sjanger. Må refaktoreres
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
		
		SpotifyArtist spotifyArtist = new SpotifyArtist();
		spotifyArtist.setName(spotifyAlbum.getArtist());
		spotifyArtist.setHref(spotifyAlbum.getArtistid());
		Map<String, Sjanger> artistSjanger = new HashMap<String, Sjanger>();
		artistSjanger.put(Artist.URI_VARIOUS_ARTISTS_ARTIST, Sjanger.IKKE_SATT);
		album.setArtist(oversettSpotifyArtist(spotifyArtist, artistSjanger));

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
		if ("Various Artists".equalsIgnoreCase(spotifyArtist.getName())) {
			artist.setSpotifyURI(Artist.URI_VARIOUS_ARTISTS_ARTIST); //Dummy-artist for Various Artists, kreves at spotifyURI settes i DB
		} else {
			artist.setSpotifyURI(spotifyArtist.getHref());
		}
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

	public static Set<String> hentArtistURIene(Collection<SpotifyAlbum> albumene) {
		Set<String> artistURIer = new HashSet<String>();
		for (SpotifyAlbum album : albumene) {
			if (StringUtils.isNotBlank(album.getArtistid())) {
				artistURIer.add(album.getArtistid());
				continue;
			} else if (album.getArtists() != null && !album.getArtists().isEmpty()) {
				artistURIer.add(album.getArtists().iterator().next().getHref());
			}
		}
		
		return artistURIer;
	}

	public static Set<String> hentAlbumURIene(List<SpotifyAlbum> albumene) {
		Set<String> albumURIene = new HashSet<String>();
		for (SpotifyAlbum album : albumene) {
			albumURIene.add(album.getHref());
		}
		return albumURIene;
	}

}
