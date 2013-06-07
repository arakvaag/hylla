package org.rakvag.hylla.services;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.rakvag.hylla.daos.AlbumDAO;
import org.rakvag.hylla.daos.ArtistDAO;
import org.rakvag.hylla.domain.Album;
import org.rakvag.hylla.domain.Artist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SpotifyServiceImpl implements SpotifyService {

	private final static Logger logger = LoggerFactory.getLogger(SpotifyServiceImpl.class.getName());

	@Inject
	protected AlbumDAO albumDAO;
	@Inject
	protected ArtistDAO artistDAO;

	@Override
	public Set<Album> synkroniserAlbumInklArtistMedDBEtterSpotifyURI(Collection<Album> albumene) {
		logger.info("Starter synkroniserAlbumInklArtistMedDBEtterSpotifyURI på " + albumene.size() + " album");
		
		if (albumene.size() == 0)
			return new HashSet<Album>();
					
		Set<String> albumURIene = new HashSet<String>();
		for (Album album : albumene)
			albumURIene.add(album.getSpotifyURI());
		Map<String, Album> persisterteAlbum = albumDAO.hentPaaSpotifyURIer(albumURIene);
		albumURIene.removeAll(persisterteAlbum.keySet());
		for (Album album : albumene) {
			if (album.getArtist() != null && albumURIene.contains(album.getSpotifyURI())) {
				Artist artist = synkroniserArtistMedDBEtterSpotifyURI(album.getArtist());
				if (artist.getAlbum() == null)
					artist.setAlbum(new HashSet<Album>());
				artist.getAlbum().add(album);
				album.setArtist(artist);
				persisterteAlbum.put(album.getSpotifyURI(), albumDAO.lagre(album));
			}
		}
		logger.info("Fullført synkroniserAlbumInklArtistMedDBEtterSpotifyURI på " + albumene.size() + " album");
		return new HashSet<Album>(persisterteAlbum.values());
	}

	@Override
	public Set<Artist> synkroniserArtisterInklAlbumMedDBEtterSpotifyURI(Collection<Artist> artistene) {
		Set<Artist> persisterteArtister = new HashSet<Artist>();
		for (Artist artist : artistene) {
			Artist dbArtist = null;
			if (artistDAO.finnesDenneIDB(artist.getSpotifyURI()))
				dbArtist = artistDAO.hentPaaSpotifyURI(artist.getSpotifyURI());
			else
				dbArtist = artist;

			Set<Album> dbAlbumene = new HashSet<Album>();
			for (Album album : artist.getAlbum()) {
				dbAlbumene.add(synkroniserAlbumMedDBEtterSpotifyURI(album));
			}
			dbArtist.setAlbum(dbAlbumene);
			persisterteArtister.add(artistDAO.lagre(dbArtist));
		}
		return persisterteArtister;
	}

	private Artist synkroniserArtistMedDBEtterSpotifyURI(Artist artist) {
		if (artistDAO.finnesDenneIDB(artist.getSpotifyURI()))
			return artistDAO.hentPaaSpotifyURI(artist.getSpotifyURI());
		else {
			return artistDAO.lagre(artist);
		}
	}

	private Album synkroniserAlbumMedDBEtterSpotifyURI(Album album) {
		if (albumDAO.finnesDenneIDB(album.getSpotifyURI()))
			return albumDAO.hentPaaSpotifyURI(album.getSpotifyURI());
		else {
			return albumDAO.lagre(album);
		}
	}
}
