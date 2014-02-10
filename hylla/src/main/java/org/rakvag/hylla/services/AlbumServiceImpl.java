package org.rakvag.hylla.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.rakvag.hylla.daos.AlbumDAO;
import org.rakvag.hylla.daos.ArtistDAO;
import org.rakvag.hylla.domain.Album;
import org.rakvag.hylla.domain.Artist;
import org.rakvag.hylla.domain.Sjanger;
import org.rakvag.hylla.domain.Spor;
import org.rakvag.hylla.domain.Tidsperiode;
import org.rakvag.spotifyapi.SearchResult;
import org.rakvag.spotifyapi.SpotifyAPI;
import org.rakvag.spotifyapi.entity.SpotifyAlbum;
import org.rakvag.spotifyapi.entity.SpotifyArtist;
import org.rakvag.spotifyapi.entity.SpotifyTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlbumServiceImpl implements AlbumService {

	private final static int MAX_ANTALL_ALBUM_FRA_SOEK = 28;
	private final static int MAX_ANTALL_ALBUM_SOM_HENTES_SAMTIDIG = 50;

	private static Logger logger = LoggerFactory.getLogger(AlbumServiceImpl.class.getName());

	@Inject
	private SpotifyAPI spotifyAPI;
	@Inject
	private HylleService hylleService;
	@Inject
	private ArtistService artistService;
	@Inject
	private AlbumDAO albumDAO;
	@Inject 
	private ArtistDAO artistDAO;

	void setAlbumDAO(AlbumDAO albumDAO) { //For å støtte enhetstesting
		this.albumDAO = albumDAO;
	}
	
	@Override
	@Transactional
	public List<Album> soekEtterAlbumISpotify(String artistnavn, String albumnavn, boolean taMedKorteAlbum) {
		logger.info("Starter tjenesten soekEtterAlbumISpotify med artistnavn " + artistnavn + " og albumnavn " + albumnavn);
		List<SpotifyAlbum> albumFraSoek = null;
		try {
			albumFraSoek = spotifyAPI.soekEtterAlbum(artistnavn, albumnavn, 20);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		if (albumFraSoek == null || albumFraSoek.size() == 0)
			return new ArrayList<Album>();

		Iterator<SpotifyAlbum> iterator = albumFraSoek.iterator();
		while (iterator.hasNext()) {
			if (!iterator.next().erTilgjengeligINorge())
				iterator.remove();
		}
		if (albumFraSoek.size() > MAX_ANTALL_ALBUM_FRA_SOEK)
			albumFraSoek.subList(0, MAX_ANTALL_ALBUM_FRA_SOEK - 1);
		
		Set<String> albumSomSkalLagresIDB = Oversetter.hentAlbumURIene(albumFraSoek);
		Map<String, Album> albumLagretIDB = albumDAO.hentPaaSpotifyURIer(albumSomSkalLagresIDB);
		albumSomSkalLagresIDB.removeAll(albumLagretIDB.keySet());
		Collection<SpotifyAlbum> spotifyAlbumHentet = spotifyAPI.hentAlbumPaaSpotifyURIer(albumSomSkalLagresIDB, 3);
		
		Map<String, Sjanger> artistersSjanger = artistService.hentArtistersDefaultSjanger(Oversetter.hentArtistURIene(spotifyAlbumHentet));
		Collection<Album> albumHentetFraSpotify = Oversetter.oversettSpotifyAlbum(spotifyAlbumHentet, artistersSjanger);
		albumHentetFraSpotify = synkroniserAlbumInklArtistMedDB(albumHentetFraSpotify);
		Map<String, String> coverartLinker = spotifyAPI.hentBildelinker(albumSomSkalLagresIDB);
		for (Album hentetAlbum : albumHentetFraSpotify)
			hentetAlbum.setCoverartlink(coverartLinker.get(hentetAlbum.getSpotifyURI()));
		
		for (Album album : albumHentetFraSpotify)
			albumLagretIDB.put(album.getSpotifyURI(), lagreAlbum(album));

		List<Album> soeketreffeneIRiktigRekkefolge = new ArrayList<Album>();
		for (String albumURI : Oversetter.hentAlbumURIene(albumFraSoek)) {
			if(albumLagretIDB.containsKey(albumURI))
				soeketreffeneIRiktigRekkefolge.add(albumLagretIDB.get(albumURI));
		}
		
		logger.info("Avslutter tjenesten soekEtterAlbumISpotify med artistnavn " + artistnavn + " og albumnavn " + albumnavn);
		return soeketreffeneIRiktigRekkefolge;
	}

	@Override
	@Transactional
	public Album hentAlbum(long albumID) {
		logger.info("Starter tjenesten hentAlbum med albumID " + albumID);
		Album album = albumDAO.hent(albumID);
		if (album.getSjanger() == Sjanger.IKKE_SATT && album.getArtist().getDefaultSjanger() != Sjanger.IKKE_SATT) {
			album.setSjanger(album.getArtist().getDefaultSjanger());
			album = albumDAO.lagre(album);
		}
		logger.info("Avslutter tjenesten hentAlbum med albumID " + albumID);
		return album;
	}

	@Override
	public Set<Spor> hentSporenetilAlbumFraSpotify(String albumsSpotifyURI) {
		logger.info("Starter tjenesten hentSporenetilAlbumFraSpotify med albumsSpotifyURI " + albumsSpotifyURI);
		Set<Spor> sporene = new HashSet<Spor>();

		ArrayList<String> spotifyURIer = new ArrayList<String>();
		spotifyURIer.add(albumsSpotifyURI);
		Collection<SpotifyAlbum> spotifyAlbums = spotifyAPI.hentAlbumPaaSpotifyURIer(spotifyURIer, 10);

		if (!spotifyAlbums.isEmpty()) {
			List<SpotifyTrack> tracks = spotifyAlbums.iterator().next().getTracks();
			for (SpotifyTrack spotifyTrack : tracks)
				sporene.add(Oversetter.oversettSpotifyTrack(spotifyTrack));
		}

		logger.info("Avslutter tjenesten hentSporenetilAlbumFraSpotify med albumsSpotifyURI " + albumsSpotifyURI);
		return sporene;
	}

	@Override
	@Transactional
	public Album lagreAlbum(Album album) {
		logger.info("Starter tjenesten lagreAlbum med album " + album.getNavn());
		album.setArtist(artistService.lagreArtist(album.getArtist()));
		Artist artist = album.getArtist();
		if (Sjanger.IKKE_SATT.equals(artist.getDefaultSjanger()) 
				&& !Artist.URI_VARIOUS_ARTISTS_ARTIST.equals(artist.getSpotifyURI())) {
			artist.setDefaultSjanger(album.getSjanger());
		}

		logger.info("Avslutter tjenesten lagreAlbum med album " + album.getNavn());
		return albumDAO.lagre(album);
	}

	@Override
	public List<Album> finnAlbum(Long hylleId, Sjanger sjanger, Tidsperiode tidsperiode) {
		logger.info("Kjører tjenesten finnAlbum");
		return albumDAO.finnAlbum(hylleId, sjanger, tidsperiode);
	}
	
	@Override
	@Transactional
	public Artist lastAlleAlbum(Artist artist) {
		logger.info("Starter lastAlleAlbum med artistId: " + artist.getId());
		SpotifyArtist spotifyArtist = spotifyAPI.hentArtistPaaSpotifyURI(artist.getSpotifyURI(), 10);
		
		boolean lasterAlleAlbum = true;
		Set<String> albumSomSkalLastes = new HashSet<String>();
		for (SearchResult albumWrapper : spotifyArtist.getAlbums()) {
			SpotifyAlbum album = albumWrapper.getAlbum();
			if (artist.getSpotifyURI().equals(album.getArtistid())) {
				albumSomSkalLastes.add(album.getHref());
			}
		}

		Set<String> urierPaaAlbumSomSkalHentes = new HashSet<String>();
		Set<String> urierPaaAlbumSomFinnesIDB = albumDAO.hvilkeAvDisseFinnesIDB(albumSomSkalLastes);
		for (String albumHref : albumSomSkalLastes) {
			if (!urierPaaAlbumSomFinnesIDB.contains(albumHref))
				urierPaaAlbumSomSkalHentes.add(albumHref);
			if (urierPaaAlbumSomSkalHentes.size() >= MAX_ANTALL_ALBUM_SOM_HENTES_SAMTIDIG) {
				lasterAlleAlbum = false;
				break;
			}
		}
		
		Collection<SpotifyAlbum> spotifyAlbumene = spotifyAPI.hentAlbumPaaSpotifyURIer(urierPaaAlbumSomSkalHentes, 5);
		Set<String> artistURIer = Oversetter.hentArtistURIene(spotifyAlbumene);
		Collection<Album> albumene = Oversetter.oversettSpotifyAlbum(spotifyAlbumene, 
				artistService.hentArtistersDefaultSjanger(artistURIer));
		Map<String, String> bildelinker = spotifyAPI.hentBildelinker(urierPaaAlbumSomSkalHentes);
		for (Album album : albumene)
			album.setCoverartlink(bildelinker.get(album.getSpotifyURI()));
		albumene.addAll(albumDAO.hentPaaSpotifyURIer(urierPaaAlbumSomFinnesIDB).values());
		Set<Album> synkedeAlbum = synkroniserAlbumInklArtistMedDB(albumene);
		artist.setAlbum(synkedeAlbum);
		artist.setErAlleAlbumLastet(lasterAlleAlbum);

		logger.info("Fullført lastAlleAlbum med artistId: " + artist.getId());
		return artist;
	}

	private Set<Album> synkroniserAlbumInklArtistMedDB(Collection<Album> albumene) {
		//TODO Denne er trolig alt for avansert. Se om det kan gjøres til en mye enklere lagre-metode.
			// Dette forutsetter at input kun innholder album som ikke allerede finnes i DB
		logger.info("Starter synkroniserAlbumInklArtistMedDB på " + albumene.size() + " album");
		
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
		for (String albumURI : albumURISomSkalLagres) {
			Album album = vaskedeAlbum.get(albumURI);
			Artist artist = artisterIDB.get(album.getArtist().getSpotifyURI());
			album.setArtist(artist);
			if (artist.getAlbum() == null)
				artist.setAlbum(new HashSet<Album>());
			artist.getAlbum().add(album);
			albumSomSkalLagres.add(album);
		}
		albumIDB.putAll(albumDAO.opprett(albumSomSkalLagres));
		
		logger.info("Fullført synkroniserAlbumInklArtistMedDB på " + albumene.size() + " album");
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
		if (!nyeArtister.isEmpty())
			artisterIDB.putAll(artistDAO.opprett(nyeArtister.values()));
		
		return artisterIDB;
	}

}
