package org.rakvag.hylla.services;

import java.util.Map;

import org.rakvag.hylla.domain.Artist;
import org.rakvag.hylla.domain.Sjanger;

public interface ArtistService extends SpotifyService {
	Artist hentArtist(long artistID);

	Artist lagreArtist(Artist artist);

	Map<String, Sjanger> hentArtistersDefaultSjanger();

}
