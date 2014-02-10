package org.rakvag.hylla.daos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.rakvag.hylla.domain.Artist;
import org.rakvag.hylla.domain.Sjanger;
import org.springframework.stereotype.Repository;

@Repository
public class ArtistDAOImpl extends SpotifyEntitetDAOImpl<Artist> implements ArtistDAO {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Sjanger> hentArtistersDefaultSjanger(Set<String> artistURIer) {
		Map<String, Sjanger> sjangre = new HashMap<String, Sjanger>();
		if (artistURIer.isEmpty())
			return sjangre;
		
		Query query = this.em.createQuery("SELECT a.spotifyURI, a.defaultSjanger FROM Artist a " +
				"WHERE a.defaultSjanger <> 'IKKE_SATT' and a.spotifyURI in (:artistURIer)");
		query.setParameter("artistURIer", artistURIer);
		List<Object> resultat = query.getResultList();
		for (Object artist : resultat) {
			String uri = (String) ((Object[]) artist)[0];
			Sjanger sjanger = (Sjanger) ((Object[]) artist)[1];
			sjangre.put(uri, sjanger);
		}
		return sjangre;
	}
	
	@Override
	public List<Artist> hentArtister(List<Long> artistIder) {
		TypedQuery<Artist> query = this.em.createQuery("select a from Artist a where id in (:artistIder)", Artist.class);
		query.setParameter("artistIder", artistIder);
		List<Artist> artistene = query.getResultList();
		return artistene;
	}

}
