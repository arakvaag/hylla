package org.rakvag.hylla.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.rakvag.hylla.daos.AlbumDAO;
import org.rakvag.hylla.daos.ArtistDAO;
import org.rakvag.hylla.domain.Album;
import org.rakvag.hylla.domain.Artist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public abstract class SpotifyServiceImpl implements SpotifyService {

	private final static Logger logger = LoggerFactory.getLogger(SpotifyServiceImpl.class.getName());

	@Inject
	protected AlbumDAO albumDAO;
	@Inject
	protected ArtistDAO artistDAO;

	@Override
	@Transactional
	public Set<Album> synkroniserAlbumInklArtistMedDBEtterSpotifyURI(Collection<Album> albumene) {
		logger.info("Starter synkroniserAlbumInklArtistMedDBEtterSpotifyURI på " + albumene.size() + " album");
		
		if (albumene.size() == 0)
			return new HashSet<Album>();
		
		Map<String, Album> vaskedeAlbum = new HashMap<String, Album>();
		Set<String> albumURISomSkalLagres = new HashSet<String>();
		for (Album album : albumene) {
			if (StringUtils.isNotEmpty(album.getSpotifyURI()) 
					&& album.getArtist() != null 
					&& StringUtils.isNotEmpty(album.getArtist().getSpotifyURI())) {
				vaskedeAlbum.put(album.getSpotifyURI(), album);
				albumURISomSkalLagres.add(album.getSpotifyURI());				
			}
		}
		
		Map<String, Artist> artisterIDB = synkroniserAlleArtisteneMedDB(vaskedeAlbum.values());
		Map<String, Album> albumIDB = albumDAO.hentPaaSpotifyURIer(albumURISomSkalLagres);
		albumURISomSkalLagres.removeAll(albumIDB.keySet());
		
		Set<Album> albumSomSkalLagres = new HashSet<Album>();
		for (Album album : vaskedeAlbum.values()) {
			Artist artist = artisterIDB.get(album.getArtist().getSpotifyURI());
			album.setArtist(artist);
			if (artist.getAlbum() == null)
				artist.setAlbum(new HashSet<Album>());
			artist.getAlbum().add(album);
			albumSomSkalLagres.add(album);
		}
		albumIDB.putAll(albumDAO.lagre(albumSomSkalLagres));
		
		logger.info("Fullført synkroniserAlbumInklArtistMedDBEtterSpotifyURI på " + albumene.size() + " album");
		return new HashSet<Album>(albumIDB.values());
	}

	private Map<String, Artist> synkroniserAlleArtisteneMedDB(Collection<Album> albumene) {
		Map<String, Artist> artister = new HashMap<String, Artist>();
		for (Album album : albumene) {
			if (album.getArtist() != null)
				artister.put(album.getArtist().getSpotifyURI(), album.getArtist());
		}
		Map<String, Artist> artisterIDB = artistDAO.hentPaaSpotifyURIer(artister.keySet());
		Map<String, Artist> nyeArtister = new HashMap<String, Artist>();
		for (Artist artist : artister.values()) {
			if (!artisterIDB.containsKey(artist.getSpotifyURI()))
				nyeArtister.put(artist.getSpotifyURI(), artist);
		}
		artisterIDB.putAll(artistDAO.lagre(nyeArtister.values()));
		
		return artisterIDB;
	}

}
