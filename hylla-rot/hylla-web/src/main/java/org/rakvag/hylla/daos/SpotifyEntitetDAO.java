package org.rakvag.hylla.daos;

import java.util.Map;
import java.util.Set;

import org.rakvag.hylla.domain.SpotifyEntitet;
import org.springframework.transaction.annotation.Transactional;

public interface SpotifyEntitetDAO<T extends SpotifyEntitet> extends EntitetDAO<T> {
	T hentPaaSpotifyURI(String spotifyURI);

	Map<String, T> hentPaaSpotifyURIer(Set<String> spotifyURIene);

	boolean finnesDenneIDB(String spotifyURI);

	@Transactional
	T erstattMedEksisterendeSpotifyentitetEllerLagreNy(final T entitet);

	@Override
	@Transactional
	T lagre(final T entitet);
}
