package org.rakvag.hylla.daos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.rakvag.hylla.domain.Artist;
import org.rakvag.hylla.domain.Sjanger;
import org.springframework.stereotype.Repository;

@Repository
public class ArtistDAOImpl extends SpotifyEntitetDAOImpl<Artist> implements ArtistDAO {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Sjanger> hentArtistersDefaultSjanger() {
		Map<String, Sjanger> sjangre = new HashMap<String, Sjanger>();
		Query query = this.em.createQuery("SELECT a.spotifyURI, a.defaultSjanger FROM Artist a WHERE a.defaultSjanger <> 'IKKE_SATT'");
		List<Object> resultat = query.getResultList();
		for (Object artist : resultat) {
			String uri = (String) ((Object[]) artist)[0];
			Sjanger sjanger = (Sjanger) ((Object[]) artist)[1];
			sjangre.put(uri, sjanger);
		}
		return sjangre;
	}
}
