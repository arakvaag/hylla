package org.rakvag.hylla.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.rakvag.hylla.daos.SporDAO;
import org.rakvag.hylla.domain.Album;
import org.rakvag.hylla.domain.Sjanger;
import org.rakvag.hylla.domain.Spor;
import org.rakvag.spotifyapi.SpotifyAPI;
import org.rakvag.spotifyapi.entity.SpotifyAlbum;
import org.rakvag.spotifyapi.entity.SpotifyTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AlbumServiceImpl extends SpotifyServiceImpl implements AlbumService {

	private final static int MAX_ANTALL_ALBUM_FRA_SOEK = 28;
	private static Logger logger = LoggerFactory.getLogger(AlbumServiceImpl.class.getName());

	@Inject
	private SporDAO sporDAO;
	@Inject
	private SpotifyAPI spotifyAPI;
	@Inject
	private HylleService hylleService;
	@Inject
	private ArtistService artistService;

	@Override
	public List<Album> soekEtterAlbumISpotify(String artistnavn, String albumnavn, boolean taMedKorteAlbum) {
		List<SpotifyAlbum> albumFraSoek = null;
		try {
			albumFraSoek = spotifyAPI.soekEtterAlbum(artistnavn, albumnavn, 20);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
		if (albumFraSoek == null || albumFraSoek.size() == 0)
			return new ArrayList<Album>();

		List<String> skalVisesPaaSiden = finnHvilkeAlbumSomSkalMedISoeketreffene(albumFraSoek);
		Collection<String> maaHentes = new HashSet<String>(skalVisesPaaSiden);
		maaHentes.removeAll(finnHvilkeAlbumSomFinnesIDB(skalVisesPaaSiden));
		Map<String, Sjanger> artistersSjanger = artistService.hentArtistersDefaultSjanger();
		Collection<Album> albumHentetFraSpotify = Oversetter.oversettSpotifyAlbum(
				spotifyAPI.hentAlbumPaaSpotifyURIer(maaHentes, 10), artistersSjanger);
		albumHentetFraSpotify = synkroniserAlbumInklArtistMedDBEtterSpotifyURI(albumHentetFraSpotify);

		Map<String, String> coverartLinker = spotifyAPI.hentBildelinker(maaHentes);
		for (Album hentetAlbum : albumHentetFraSpotify) {
			if (coverartLinker.containsKey(hentetAlbum.getSpotifyURI()))
				hentetAlbum.setCoverartlink(coverartLinker.get(hentetAlbum.getSpotifyURI()));
			lagreAlbum(hentetAlbum);
		}

		List<Album> soeketreffene = new ArrayList<Album>();
		for (String albumURI : skalVisesPaaSiden) {
			Album album = albumDAO.hentPaaSpotifyURI(albumURI);
			if (taMedKorteAlbum || !album.erEtKortAlbum())
				soeketreffene.add(album);
		}

		return soeketreffene;
	}

	@Override
	public Album hentAlbum(long albumID) {
		Album album = albumDAO.hent(albumID);
		if (album.getSjanger() == Sjanger.IKKE_SATT && album.getArtist().getDefaultSjanger() != Sjanger.IKKE_SATT) {
			album.setSjanger(album.getArtist().getDefaultSjanger());
			album = albumDAO.lagre(album);
		}
		return album;
	}

	@Override
	public Set<Spor> hentSporenetilAlbumFraSpotify(String albumsSpotifyURI) {
		Set<Spor> sporene = new HashSet<Spor>();

		ArrayList<String> spotifyURIer = new ArrayList<String>();
		spotifyURIer.add(albumsSpotifyURI);
		Collection<SpotifyAlbum> spotifyAlbums = spotifyAPI.hentAlbumPaaSpotifyURIer(spotifyURIer, 10);

		if (!spotifyAlbums.isEmpty()) {
			List<SpotifyTrack> tracks = spotifyAlbums.iterator().next().getTracks();
			for (SpotifyTrack spotifyTrack : tracks)
				sporene.add(Oversetter.oversettSpotifyTrack(spotifyTrack));
		}

		return sporene;
	}

	@Override
	public Album lagreAlbum(Album album) {
		album.setArtist(artistService.lagreArtist(album.getArtist()));
		return albumDAO.lagre(album);
	}

	@Override
	public void kjoerMasseInnlesning(List<String> linker, long hylleId) {
		linker = new ArrayList<String>(linker);
		Collections.shuffle(linker);

		Set<String> sporURIerIkkeSlaattOpp = new HashSet<String>();
		Set<String> albumURIerIkkeSlaattOpp = new HashSet<String>();
		Map<String, Sjanger> artistersSjanger = artistService.hentArtistersDefaultSjanger();

		for (String link : linker) {
			String uri = lagSpotifyURIFraTrackLink(link);
			if (!sporDAO.finnesDenneIDB(uri))
				sporURIerIkkeSlaattOpp.add(uri);

			if (sporURIerIkkeSlaattOpp.size() >= 25) {
				Set<String> albumURIene = slaaOppAlbumURIerForSporene(sporURIerIkkeSlaattOpp);
				for (String albumURI : albumURIene) {
					if (!albumDAO.finnesDenneIDB(albumURI))
						albumURIerIkkeSlaattOpp.add(albumURI);
				}
				sporURIerIkkeSlaattOpp = new HashSet<String>();
			}

			if (albumURIerIkkeSlaattOpp.size() >= 5) {
				Collection<SpotifyAlbum> spotifyAlbumene = spotifyAPI.hentAlbumPaaSpotifyURIer(albumURIerIkkeSlaattOpp,
						10);
				for (SpotifyAlbum spotifyAlbum : spotifyAlbumene) {
					Sjanger sjanger = Sjanger.IKKE_SATT;
					if (artistersSjanger.containsKey(spotifyAlbum.getArtistid()))
						sjanger = artistersSjanger.get(spotifyAlbum.getArtistid());
					Album album = Oversetter.oversettSpotifyAlbum(spotifyAlbum, sjanger);
					albumDAO.lagre(album);
					hylleService.leggTilAlbumPaaHylle(album.getId(), hylleId);
				}
				albumURIerIkkeSlaattOpp = new HashSet<String>();
			}
		}
		// Få med siste rest
		Collection<SpotifyAlbum> spotifyAlbumene = spotifyAPI.hentAlbumPaaSpotifyURIer(albumURIerIkkeSlaattOpp, 10);
		for (SpotifyAlbum spotifyAlbum : spotifyAlbumene) {
			Sjanger sjanger = Sjanger.IKKE_SATT;
			if (artistersSjanger.containsKey(spotifyAlbum.getArtistid()))
				sjanger = artistersSjanger.get(spotifyAlbum.getArtistid());
			Album album = Oversetter.oversettSpotifyAlbum(spotifyAlbum, sjanger);
			albumDAO.lagre(album);
			hylleService.leggTilAlbumPaaHylle(album.getId(), hylleId);
		}
	}

	private List<String> finnHvilkeAlbumSomSkalMedISoeketreffene(List<SpotifyAlbum> albumFraSoek) {
		List<String> URIerPaaAlbumSomSkalHentesOpp = new ArrayList<String>();
		for (SpotifyAlbum albumet : albumFraSoek) {
			if (!albumet.erTilgjengeligINorge())
				continue;
			URIerPaaAlbumSomSkalHentesOpp.add(albumet.getHref());
			if (URIerPaaAlbumSomSkalHentesOpp.size() >= MAX_ANTALL_ALBUM_FRA_SOEK)
				break;
		}
		return URIerPaaAlbumSomSkalHentesOpp;
	}

	private Collection<String> finnHvilkeAlbumSomFinnesIDB(List<String> albumURIer) {
		Collection<String> finnesIDB = new HashSet<String>();
		for (String uri : albumURIer) {
			if (albumDAO.finnesDenneIDB(uri))
				finnesIDB.add(uri);
		}
		return finnesIDB;
	}

	private String lagSpotifyURIFraTrackLink(String link) {
		final String TRACK_PREFIKS = "http://open.spotify.com/track/";
		if (!link.startsWith(TRACK_PREFIKS))
			return null;
		return "spotify:track:" + link.substring(TRACK_PREFIKS.length());
	}

	private Set<String> slaaOppAlbumURIerForSporene(Set<String> sporURIer) {
		Set<String> albumURIene = new HashSet<String>();

		Collection<SpotifyTrack> sporene = spotifyAPI.hentTracksPaaSpotifyURIer(sporURIer, 10).values();
		for (SpotifyTrack spotifyTrack : sporene)
			albumURIene.add(spotifyTrack.getAlbum().getHref());

		return albumURIene;
	}

}
