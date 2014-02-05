package org.rakvag.hylla.daos;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rakvag.hylla.domain.Artist;
import org.rakvag.hylla.domain.Sjanger;

public interface ArtistDAO extends SpotifyEntitetDAO<Artist> {

	Map<String, Sjanger> hentArtistersDefaultSjanger(Set<String> artistURIer);
	
	List<Artist> hentArtister(List<Long> artistIder);

}
