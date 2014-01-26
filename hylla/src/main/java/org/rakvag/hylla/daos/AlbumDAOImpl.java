package org.rakvag.hylla.daos;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.rakvag.hylla.domain.Album;
import org.rakvag.hylla.domain.Sjanger;
import org.rakvag.hylla.domain.Tidsperiode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class AlbumDAOImpl extends SpotifyEntitetDAOImpl<Album> implements AlbumDAO {

	private static Logger logger = LoggerFactory.getLogger(AlbumDAOImpl.class.getName());
	
	@Inject
	private ArtistDAO artistDAO;
	
	@Override
	public List<Album> finnAlbum(Long hylleId, Sjanger sjanger, Tidsperiode tidsperiode) {
		logger.debug("Starter finnAlbum");
		
		StringBuilder queryString = new StringBuilder("select albumene_id from hyllealbum where hyller_id = :hylleId ");
		if (sjanger != null || tidsperiode != null) {
			queryString.append("and albumene_id in (select id from album where ");
			
			if (sjanger != null)
				queryString.append("sjanger = :sjanger ");
			
			if (sjanger != null && tidsperiode != null)
				queryString.append("and ");
			
			if (tidsperiode != null) {
				switch (tidsperiode) {
				case FOER_80:
					queryString.append("aar < 1980 ");
					break;
				case I_80:
					queryString.append("aar between 1980 and 1989 ");
					break;
				case I_90:
					queryString.append("aar between 1990 and 1999 ");
					break;
				case I_00:
					queryString.append("aar between 2000 and 2009 ");
					break;
				case ETTER_00:
					queryString.append("aar > 2009 ");
					break;
				default:
					throw new RuntimeException("Uventet feil, switch på Tidsperiode gikk til default");
				}
			}
			queryString.append(")");
		}

		Query nativeQ = em.createNativeQuery(queryString.toString());
		nativeQ.setParameter("hylleId", hylleId);
		if (sjanger != null)
			nativeQ.setParameter("sjanger", sjanger.toString());
		@SuppressWarnings("unchecked")
		List<BigInteger> albumIderBigInt = nativeQ.getResultList();
		List<Long> albumIder = new ArrayList<Long>();
		for (BigInteger id : albumIderBigInt)
			albumIder.add(id.longValue());
		
		nativeQ = em.createNativeQuery("select distinct(artistid) from album where id in (:albumIder)");
		nativeQ.setParameter("albumIder", albumIder);
		@SuppressWarnings("unchecked")
		List<BigInteger> artistIderBigInt = nativeQ.getResultList();
		List<Long> artistIder = new ArrayList<Long>();
		for (BigInteger id : artistIderBigInt)
			artistIder.add(id.longValue());
		
		//Kjøres for å unngå lazyloading av artister som skjer på en svært ineffektiv måte
		artistDAO.hentArtister(artistIder);
		
		List<Album> albumene = hentAlbum(albumIder);
		
		logger.debug("Avslutter finnAlbum");
		return albumene;
	}

	@Override
	public List<Album> hentAlbum(List<Long> albumIder) {
		TypedQuery<Album> query = this.em.createQuery("select a from Album a where id in (:albumIder)", Album.class);
		query.setParameter("albumIder", albumIder);
		List<Album> albumene = query.getResultList();
		return albumene;
	}
}
