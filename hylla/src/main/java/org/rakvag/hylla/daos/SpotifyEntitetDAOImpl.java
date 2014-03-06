package org.rakvag.hylla.daos;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.TypedQuery;

import org.rakvag.hylla.domain.SpotifyEntitet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public abstract class SpotifyEntitetDAOImpl<T extends SpotifyEntitet> extends EntitetDAOImpl<T> implements
		SpotifyEntitetDAO<T> {

	private final static Logger logger = LoggerFactory.getLogger(SpotifyEntitetDAOImpl.class.getName());
	
	@Override
	public T hentPaaSpotifyURI(String spotifyURI) {
		logger.info("Starter hentPaaSpotifyURI med spotifyURI:" + spotifyURI);
		final StringBuffer queryString = new StringBuffer("SELECT o FROM ");
		queryString.append(type.getSimpleName());
		queryString.append(" o WHERE o.spotifyURI = :spotifyURI");
		TypedQuery<T> query = this.em.createQuery(queryString.toString(), this.type);
		query.setParameter("spotifyURI", spotifyURI);
		return query.getSingleResult();
	}

	@Override
	public Map<String, T> hentPaaSpotifyURIer(Set<String> spotifyURIene) {
		logger.info("Starter hentPaaSpotifyURIer på " + spotifyURIene.size() + " spotifyURIer");
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
		logger.info("Starter finnesDenneIDB med spotifyURI:" + spotifyURI);
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
	public Set<String> hvilkeAvDisseFinnesIDB(Set<String> spotifyURIer) {
		logger.info("Starter hvilkeAvDisseFinnesIDB på " + spotifyURIer.size() + " spotifyURIer");
		final StringBuffer queryString = new StringBuffer("SELECT o.spotifyURI FROM ");
		queryString.append(type.getSimpleName());
		queryString.append(" o WHERE o.spotifyURI in (:spotifyURIer)");
		TypedQuery<String> query = this.em.createQuery(queryString.toString(), String.class);
		query.setParameter("spotifyURIer", spotifyURIer);
		List<String> uriFunnet = query.getResultList();

		logger.info("Avslutter hvilkeAvDisseFinnesIDB på " + spotifyURIer.size() + " spotifyURIer");
		return new HashSet<String>(uriFunnet);
	}

	@Override
	@Transactional
	public T lagre(final T entitet) {
		logger.info("Starter lagring av spotifyentitet med URI: " + entitet.getSpotifyURI());
		if (entitet.getId() != null) {
			return this.em.merge(entitet);
		} else {
			this.em.persist(entitet);
			return entitet;
		}
	}
	
	@Override
	@Transactional
	public Map<String, T> opprett(Collection<T> entiter) {
		logger.info("Starter opprett med " + entiter.size() + " spotifyentiteter");
		
		if (entiter.isEmpty())
			return new HashMap<String, T>();
		
		Map<String, T> entitMap = new HashMap<String, T>();
		for (T entitet : entiter) {
			entitMap.put(entitet.getSpotifyURI(), entitet);
		}
		Map<String, T> entiterIDB = new HashMap<String, T>();
		
		for (String spotifyURI : entitMap.keySet()) {
			T entitet = entitMap.get(spotifyURI);
			if (entitet.getId() != null)
				throw new RuntimeException("Denne Spotify-entiteten er hentet fra databasen, kan ikke opprettes som ny");
			
			this.em.persist(entitet);
			entiterIDB.put(spotifyURI, entitet);
		}
		
		logger.info("Avslutter opprett");
		return entiterIDB;
	}


}
