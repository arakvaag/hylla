package org.rakvag.hylla.daos;

import java.util.Map;

import org.rakvag.hylla.domain.Artist;
import org.rakvag.hylla.domain.Sjanger;

public interface ArtistDAO extends SpotifyEntitetDAO<Artist> {

	Map<String, Sjanger> hentArtistersDefaultSjanger();
}
