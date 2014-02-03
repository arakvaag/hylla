package org.rakvag.hylla.daos;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.rakvag.hylla.domain.SpotifyEntitet;
import org.springframework.transaction.annotation.Transactional;

public interface SpotifyEntitetDAO<T extends SpotifyEntitet> extends EntitetDAO<T> {
	T hentPaaSpotifyURI(String spotifyURI);

	Map<String, T> hentPaaSpotifyURIer(Set<String> spotifyURIene);

	boolean finnesDenneIDB(String spotifyURI);

	Set<String> hvilkeAvDisseFinnesIDB(Set<String> hrefs);

	@Override
	@Transactional
	T lagre(final T entitet);
	
	@Transactional
	Map<String, T> lagre(Collection<T> entiter);

}
