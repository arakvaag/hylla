package org.rakvag.hylla.services;

import java.util.Map;
import java.util.Set;

import org.rakvag.hylla.domain.Artist;
import org.rakvag.hylla.domain.Sjanger;

public interface ArtistService {
	
	Artist hentArtist(long artistID);

	Artist lagreArtist(Artist artist);

	Map<String, Sjanger> hentArtistersDefaultSjanger(Set<String> artistURIer);

}
