package org.rakvag.hylla.services;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.rakvag.hylla.daos.AlbumDAO;
import org.rakvag.hylla.daos.ArtistDAO;
import org.rakvag.hylla.domain.Artist;
import org.rakvag.hylla.domain.Sjanger;
import org.rakvag.spotifyapi.SpotifyAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArtistServiceImpl implements ArtistService {

	private final static Logger logger = LoggerFactory.getLogger(ArtistServiceImpl.class.getName());
	
	@Inject
	private SpotifyAPI spotifyAPI;
	
	@Inject
	private ArtistDAO artistDAO;
	
	@Inject
	private AlbumDAO albumDAO;

	@Inject
	private AlbumService albumService;

	@Override
	@Transactional
	public Artist hentArtist(long artistID) {
		logger.info("Starter tjenesten hentArtist med artistID " + artistID);
		Artist artist = artistDAO.hent(artistID);
		
		if (!Artist.URI_VARIOUS_ARTISTS_ARTIST.equals(artist.getSpotifyURI())) {
			artist = albumService.hentManglendeAlbumFraSpotify(artist);
			if (artist.getBildelink() == null) {
				artist.setBildelink(spotifyAPI.hentBildelink(artist.getSpotifyURI()));
			}
			lagreArtist(artist);
			
			
			boolean artistEndret = false;
			if (!artist.isErAlleAlbumLastet()) {
				artist = albumService.lastAlleAlbum(artist);
				artistEndret = artist.isErAlleAlbumLastet();
			}
	
			if (artist.getBildelink() == null) {
				artist.setBildelink(spotifyAPI.hentBildelink(artist.getSpotifyURI()));
				artistEndret = true;
			}
			if (artistEndret) 
				artist = artistDAO.lagre(artist);
		}

		logger.info("Fullført tjenesten hentArtist med artistID " + artistID);
		return artist;
	}

	@Override
	@Transactional
	public Artist lagreArtist(Artist artist) {
		logger.info("Kjører tjenesten lagreArtist med artist " + artist.getNavn());
		return artistDAO.lagre(artist);
	}

	@Override
	public Map<String, Sjanger> hentArtistersDefaultSjanger(Set<String> artistURIer) {
		logger.info("Kjører tjenesten hentArtistersDefaultSjanger med " + artistURIer.size() + " artistURIer");
		return artistDAO.hentArtistersDefaultSjanger(artistURIer);
	}

}
