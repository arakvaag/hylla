package org.rakvag.hylla.daos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.TypedQuery;

import org.rakvag.hylla.domain.SpotifyEntitet;

public abstract class SpotifyEntitetDAOImpl<T extends SpotifyEntitet> extends EntitetDAOImpl<T> implements
		SpotifyEntitetDAO<T> {

	@Override
	public T hentPaaSpotifyURI(String spotifyURI) {
		final StringBuffer queryString = new StringBuffer("SELECT o FROM ");
		queryString.append(type.getSimpleName());
		queryString.append(" o WHERE o.spotifyURI = :spotifyURI");
		TypedQuery<T> query = this.em.createQuery(queryString.toString(), this.type);
		query.setParameter("spotifyURI", spotifyURI);
		return query.getSingleResult();
	}

	@Override
	public Map<String, T> hentPaaSpotifyURIer(Set<String> spotifyURIene) {
		final StringBuffer queryString = new StringBuffer("SELECT o FROM ");
		queryString.append(type.getSimpleName());
		queryString.append(" o WHERE o.spotifyURI in (:spotifyURIListe)");
		TypedQuery<T> query = this.em.createQuery(queryString.toString(), this.type);
		query.setParameter("spotifyURIListe", spotifyURIene);
		List<T> entiteter = query.getResultList();
		Map<String, T> entiteterMap = new HashMap<String, T>();
		for(T entitet : entiteter)
			entiteterMap.put(entitet.getSpotifyURI(), entitet);
		return entiteterMap;
	}

	@Override
	public boolean finnesDenneIDB(String spotifyURI) {
		final StringBuffer queryString = new StringBuffer("SELECT o.spotifyURI FROM ");
		queryString.append(type.getSimpleName());
		queryString.append(" o WHERE o.spotifyURI = :spotifyURI");
		TypedQuery<String> query = this.em.createQuery(queryString.toString(), String.class);
		query.setParameter("spotifyURI", spotifyURI);
		List<String> uriFunnet = query.getResultList();
		if (uriFunnet != null && uriFunnet.size() > 0)
			return true;
		else
			return false;
	}

	@Override
	public T lagre(final T entitet) {
		if (entitet.getId() != null)
			return this.em.merge(entitet);
		else if (finnesDenneIDB(entitet.getSpotifyURI())) {
			throw new RuntimeException("Spotify-entiteten finnes allerede i databasen, kan ikke lagre");
		} else {
			this.em.persist(entitet);
			return entitet;
		}
	}

	@Override
	public T erstattMedEksisterendeSpotifyentitetEllerLagreNy(T entitet) {
		if (finnesDenneIDB(entitet.getSpotifyURI()))
			return hentPaaSpotifyURI(entitet.getSpotifyURI());
		else
			return lagre(entitet);
	}

}
