package org.rakvag.hylla.services;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.rakvag.hylla.domain.Album;
import org.rakvag.hylla.domain.Artist;
import org.rakvag.hylla.domain.Sjanger;
import org.rakvag.spotifyapi.SearchResult;
import org.rakvag.spotifyapi.SpotifyAPI;
import org.rakvag.spotifyapi.entity.SpotifyAlbum;
import org.rakvag.spotifyapi.entity.SpotifyArtist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ArtistServiceImpl extends SpotifyServiceImpl implements ArtistService {

	private final static int MAX_ANTALL_ALBUM_SOM_HENTES_SAMTIDIG = 50;
	private final static Logger logger = LoggerFactory.getLogger(ArtistServiceImpl.class.getName());
	
	@Inject
	private SpotifyAPI spotifyAPI;

	@Override
	public Artist hentArtist(long artistID) {
		logger.info("Starter tjenesten hentArtist med artistID " + artistID);
		Artist artist = artistDAO.hent(artistID);
		
		if (!Artist.URI_VARIOUS_ARTISTS_ARTIST.equals(artist.getSpotifyURI())) {
			if (!artist.isErAlleAlbumLastet())
				artist = lastAlleAlbum(artist);
	
			if (artist.getBildelink() == null)
				artist.setBildelink(spotifyAPI.hentBildelink(artist.getSpotifyURI()));
		}

		logger.info("Fullført tjenesten hentArtist med artistID " + artistID);
		return artistDAO.lagre(artist);
	}

	private Artist lastAlleAlbum(Artist artist) {
		logger.info("Starter lastAlleAlbum med artistId: " + artist.getId());
		SpotifyArtist spotifyArtist = spotifyAPI.hentArtistPaaSpotifyURI(artist.getSpotifyURI(), 10);
		Set<String> urierPaaAlbumSomSkalHentes = new HashSet<String>();
		Set<String> urierPaaAlbumSomFinnesIDB = new HashSet<String>();
		boolean lasterAlleAlbum = true;
		for (SearchResult albumWrapper : spotifyArtist.getAlbums()) {
			SpotifyAlbum album = albumWrapper.getAlbum();
			if (artist.getSpotifyURI().equals(album.getArtistid())) {
				if (albumDAO.finnesDenneIDB(album.getHref()))
					urierPaaAlbumSomFinnesIDB.add(album.getHref());
				else
					urierPaaAlbumSomSkalHentes.add(album.getHref());
			}
			if (urierPaaAlbumSomSkalHentes.size() > MAX_ANTALL_ALBUM_SOM_HENTES_SAMTIDIG) {
				lasterAlleAlbum = false;
				break;
			}
		}
		Collection<SpotifyAlbum> spotifyAlbumene = spotifyAPI.hentAlbumPaaSpotifyURIer(urierPaaAlbumSomSkalHentes, 10);
		Collection<Album> albumene = Oversetter.oversettSpotifyAlbum(spotifyAlbumene, hentArtistersDefaultSjanger());
		Map<String, String> bildelinker = spotifyAPI.hentBildelinker(urierPaaAlbumSomSkalHentes);
		for (Album album : albumene)
			album.setCoverartlink(bildelinker.get(album.getSpotifyURI()));
		for (String albumURI : urierPaaAlbumSomFinnesIDB)
			albumene.add(albumDAO.hentPaaSpotifyURI(albumURI));
		Set<Album> synkedeAlbum = synkroniserAlbumInklArtistMedDBEtterSpotifyURI(albumene);
		artist.setAlbum(synkedeAlbum);
		artist.setErAlleAlbumLastet(lasterAlleAlbum);
		logger.info("Fullført lastAlleAlbum med artistId: " + artist.getId());
		return artist;
	}

	@Override
	public Artist lagreArtist(Artist artist) {
		return artistDAO.lagre(artist);
	}

	@Override
	public Map<String, Sjanger> hentArtistersDefaultSjanger() {
		return artistDAO.hentArtistersDefaultSjanger();
	}

}
