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
	@Inject
	private Oversetter oversetter;

	@Override
	@Transactional
	public List<Album> soekEtterAlbumISpotify(String artistnavn, String albumnavn, boolean taMedKorteAlbum) {
		logger.info("Starter tjenesten soekEtterAlbumISpotify med artistnavn " + artistnavn + " og albumnavn " + albumnavn);
		
		List<SpotifyAlbum> albumFraSoek = spotifyAPI.soekEtterAlbum(artistnavn, albumnavn, 20);
		if (albumFraSoek.size() == 0)
			return new ArrayList<Album>();

		albumFraSoek = fjernAlbumSomIkkeErTilgjengeligINorge(albumFraSoek);

		int maxAntallAlbumFraSoek = 28;
		if (albumFraSoek.size() > maxAntallAlbumFraSoek)
			albumFraSoek.subList(0, maxAntallAlbumFraSoek - 1);
		
		Set<String> albumSomSkalLagresIDB = hentAlbumURIene(albumFraSoek);
		Map<String, Album> albumLagretIDB = albumDAO.hentPaaSpotifyURIer(albumSomSkalLagresIDB);
		albumSomSkalLagresIDB.removeAll(albumLagretIDB.keySet());
		Collection<SpotifyAlbum> spotifyAlbumHentet = spotifyAPI.hentAlbumPaaSpotifyURIer(albumSomSkalLagresIDB, 3);
		
		Map<String, Sjanger> artistersSjanger = artistService.hentArtistersDefaultSjanger(oversetter.hentArtistURIene(spotifyAlbumHentet));
		Collection<Album> albumHentetFraSpotify = oversetter.oversettSpotifyAlbum(spotifyAlbumHentet);
		for(Album album : albumHentetFraSpotify) {
			String artistSpotifyURI = album.getArtist().getSpotifyURI();
			if (artistersSjanger.containsKey(artistSpotifyURI))
				album.setSjanger(artistersSjanger.get(artistSpotifyURI));
			else 
				album.setSjanger(Sjanger.IKKE_SATT);
		}
			
		albumHentetFraSpotify = synkroniserAlbumInklArtistMedDB(albumHentetFraSpotify);
		Map<String, String> coverartLinker = spotifyAPI.hentBildelinker(albumSomSkalLagresIDB);
		for (Album hentetAlbum : albumHentetFraSpotify)
			hentetAlbum.setCoverartlink(coverartLinker.get(hentetAlbum.getSpotifyURI()));
		
		for (Album album : albumHentetFraSpotify)
			albumLagretIDB.put(album.getSpotifyURI(), lagreAlbum(album));

		List<Album> soeketreffeneIRiktigRekkefolge = new ArrayList<Album>();
		for (String albumURI : hentAlbumURIene(albumFraSoek)) {
			if(albumLagretIDB.containsKey(albumURI))
				soeketreffeneIRiktigRekkefolge.add(albumLagretIDB.get(albumURI));
		}
		
		logger.info("Avslutter tjenesten soekEtterAlbumISpotify med artistnavn " + artistnavn + " og albumnavn " + albumnavn);
		return soeketreffeneIRiktigRekkefolge;
	}

	private Set<String> hentAlbumURIene(Collection<SpotifyAlbum> albumene) {
		Set<String> albumURIene = new HashSet<String>();
		for (SpotifyAlbum album : albumene)
			albumURIene.add(album.getHref());
		return albumURIene;
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
	public List<Spor> hentSporenetilAlbumFraSpotify(String albumsSpotifyURI) {
		logger.info("Starter tjenesten hentSporenetilAlbumFraSpotify med albumsSpotifyURI " + albumsSpotifyURI);
		List<Spor> sporene = new ArrayList<Spor>();

		ArrayList<String> spotifyURIer = new ArrayList<String>();
		spotifyURIer.add(albumsSpotifyURI);
		Collection<SpotifyAlbum> spotifyAlbums = spotifyAPI.hentAlbumPaaSpotifyURIer(spotifyURIer, 10);

		if (!spotifyAlbums.isEmpty()) {
			List<SpotifyTrack> tracks = spotifyAlbums.iterator().next().getTracks();
			for (SpotifyTrack spotifyTrack : tracks)
				sporene.add(oversetter.oversettSpotifyTrack(spotifyTrack));
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
		
		artist = hentManglendeAlbumFraSpotify(artist);
		for(Album album : artist.getAlbum())
			albumDAO.lagre(album);
		artistDAO.lagre(artist);

		logger.info("Fullført lastAlleAlbum med artistId: " + artist.getId());
		return artist;
	}

	@Override
	public Artist hentManglendeAlbumFraSpotify(Artist artist) {
		logger.info("Starter hentManglendeAlbumFraSpotify med artistId: " + artist.getId());
		if (artist.isErAlleAlbumLastet())
			return artist;
		
		SpotifyArtist spotifyArtist = spotifyAPI.hentArtistPaaSpotifyURI(artist.getSpotifyURI(), 10);
		spotifyArtist.fjernAlbumeneSomIkkeHarArtistenSomHovedartist();
		Set<String> urierPaaAlbumSomSkalHentes = spotifyArtist.hentAlbumURIene();
		urierPaaAlbumSomSkalHentes = fjernAlbumSomFinnesPaaArtistenAllerede(artist, urierPaaAlbumSomSkalHentes);
		artist.setErAlleAlbumLastet(urierPaaAlbumSomSkalHentes.size() <= 50); 
		urierPaaAlbumSomSkalHentes = lagSubsettPaaMaks50stk(urierPaaAlbumSomSkalHentes);
		Collection<SpotifyAlbum> spotifyAlbum = spotifyAPI.hentAlbumPaaSpotifyURIer(urierPaaAlbumSomSkalHentes, 5);
		spotifyAlbum = fjernAlbumSomIkkeErTilgjengeligINorge(new ArrayList<SpotifyAlbum>(spotifyAlbum));
		Collection<Album> hentedeAlbum = oversetter.oversettSpotifyAlbum(spotifyAlbum);
		Map<String, String> coverartlinker = spotifyAPI.hentBildelinker(urierPaaAlbumSomSkalHentes);
		for(Album album : hentedeAlbum) {
			album.setArtist(artist);
			album.setSjanger(artist.getDefaultSjanger());
			album.setCoverartlink(coverartlinker.get(album.getSpotifyURI()));
		}
		artist.getAlbum().addAll(hentedeAlbum);

		logger.info("Fullført hentManglendeAlbumFraSpotify med artistId: " + artist.getId());
		return artist;
	}

	private List<SpotifyAlbum> fjernAlbumSomIkkeErTilgjengeligINorge(List<SpotifyAlbum> spotifyAlbum) {
		Iterator<SpotifyAlbum> iterator = spotifyAlbum.iterator();
		while (iterator.hasNext()){
			SpotifyAlbum album =  iterator.next();
			if (!album.erTilgjengeligINorge())
				iterator.remove();
		}
		
		return spotifyAlbum;
	}

	private Set<String> fjernAlbumSomFinnesPaaArtistenAllerede(Artist artist, Set<String> urierPaaAlbumSomSkalHentes) {
		Set<String> filtrerteAlbumURIer = new HashSet<String>();
		Set<String> albumURIerIDomeneObjekt = new HashSet<String>();
		for(Album album : artist.getAlbum()) {
			albumURIerIDomeneObjekt.add(album.getSpotifyURI());
		}
		for(String albumURI : urierPaaAlbumSomSkalHentes) {
			if (!albumURIerIDomeneObjekt.contains(albumURI))
				filtrerteAlbumURIer.add(albumURI);
		}
		urierPaaAlbumSomSkalHentes = filtrerteAlbumURIer;
		return urierPaaAlbumSomSkalHentes;
	}

	private Set<String> lagSubsettPaaMaks50stk(Set<String> strenger) {
		if (strenger.size() > 50) {
			Set<String> maks50Strenger = new HashSet<String>();
			for(String streng : strenger) {
				if (maks50Strenger.size() >= 50)
					break;
				maks50Strenger.add(streng);
			}
			strenger = maks50Strenger;
		}
		return strenger;
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
	
	//For å støtte enhetstesting
	void setAlbumDAO(AlbumDAO albumDAO) { 
		this.albumDAO = albumDAO;
	}
	void setOversetter(Oversetter oversetter) {
		this.oversetter = oversetter;
	}
	void setSpotifyAPI(SpotifyAPI spotifyAPI) {
		this.spotifyAPI = spotifyAPI;
	}
	void setHylleService(HylleService hylleService) {
		this.hylleService = hylleService;
	}
	void setArtistService(ArtistService artistService) {
		this.artistService = artistService;
	}
	void setArtistDAO(ArtistDAO artistDAO) {
		this.artistDAO = artistDAO;
	}

}
