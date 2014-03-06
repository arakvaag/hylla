package org.rakvag.hylla.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.rakvag.hylla.domain.Album;
import org.rakvag.hylla.domain.Artist;
import org.rakvag.hylla.domain.Spor;
import org.rakvag.spotifyapi.SearchResult;
import org.rakvag.spotifyapi.entity.SpotifyAlbum;
import org.rakvag.spotifyapi.entity.SpotifyArtist;
import org.rakvag.spotifyapi.entity.SpotifyTrack;
import org.springframework.stereotype.Component;

@Component
public class Oversetter {
	
	public Collection<Album> oversettSpotifyAlbum(Collection<SpotifyAlbum> spotifyAlbumene) {
		Collection<Album> domAlbumene = new HashSet<Album>();
		for (SpotifyAlbum spotifyAlbum : spotifyAlbumene) {
			domAlbumene.add(oversettSpotifyAlbum(spotifyAlbum));
		}
		return domAlbumene;
	}

	public Album oversettSpotifyAlbum(SpotifyAlbum spotifyAlbum) {
		Album album = new Album();

		album.setAar(spotifyAlbum.getReleased());
		album.setTilgjengeligINorge(spotifyAlbum.erTilgjengeligINorge());
		
		SpotifyArtist spotifyArtist = new SpotifyArtist();
		spotifyArtist.setName(spotifyAlbum.getArtist());
		spotifyArtist.setHref(spotifyAlbum.getArtistid());
		album.setArtist(oversettSpotifyArtist(spotifyArtist));

		album.setSpotifyURI(spotifyAlbum.getHref());
		album.setNavn(spotifyAlbum.getName());
		album.setSpor(new ArrayList<Spor>());
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

	public Spor oversettSpotifyTrack(SpotifyTrack track) {
		Spor spor = new Spor();

		spor.setDisknummer(track.getDiscnumber());
		spor.setLengde(track.getLength());
		spor.setNavn(track.getName());
		spor.setPopularitet(track.getPopularity());
		spor.setSpornummer(track.getTracknumber());
		spor.setSpotifyURI(track.getHref());

		return spor;
	}

	public Artist oversettSpotifyArtist(SpotifyArtist spotifyArtist) {
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
				album.add(oversettSpotifyAlbum(albumWrapper.getAlbum()));
			}
		}
		artist.setAlbum(album);
		return artist;
	}

	public Set<String> hentArtistURIene(Collection<SpotifyAlbum> albumene) {
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

}
