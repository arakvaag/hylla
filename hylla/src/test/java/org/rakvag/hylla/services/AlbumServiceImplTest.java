package org.rakvag.hylla.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rakvag.hylla.daos.AlbumDAO;
import org.rakvag.hylla.daos.ArtistDAO;
import org.rakvag.hylla.domain.Album;
import org.rakvag.hylla.domain.Artist;
import org.rakvag.hylla.domain.Sjanger;
import org.rakvag.hylla.domain.Tidsperiode;
import org.rakvag.spotifyapi.SearchResult;
import org.rakvag.spotifyapi.SpotifyAPI;
import org.rakvag.spotifyapi.entity.Availability;
import org.rakvag.spotifyapi.entity.SpotifyAlbum;
import org.rakvag.spotifyapi.entity.SpotifyArtist;

public class AlbumServiceImplTest {

	@Mock
	private SpotifyAPI spotifyAPI;
	@Mock
	private HylleService hylleService;
	@Mock
	private ArtistService artistService;
	@Mock
	private AlbumDAO albumDAO;
	@Mock
	private ArtistDAO artistDAO;
	@Mock
	private Oversetter oversetter;

	private AlbumService service;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		AlbumServiceImpl impl = new AlbumServiceImpl();
		impl.setSpotifyAPI(spotifyAPI);
		impl.setHylleService(hylleService);
		impl.setArtistService(artistService);
		impl.setAlbumDAO(albumDAO);
		impl.setArtistDAO(artistDAO);
		impl.setOversetter(oversetter);
		service = impl;
	}

	@Test
	public void testFinnAlbum_HylleIdSjangerOgTidsperiodeOppgitt_ReturnererFiltrertSett() {
		List<Album> albumene = new ArrayList<Album>();
		when(albumDAO.finnAlbum(anyLong(), any(Sjanger.class), any(Tidsperiode.class))).thenReturn(albumene);
		long hylleId = 1L;
		Sjanger sjanger = Sjanger.POP;
		Tidsperiode tidsperiode = Tidsperiode.ETTER_00;

		List<Album> returnerteAlbum = service.finnAlbum(hylleId, sjanger, tidsperiode);

		verify(albumDAO).finnAlbum(hylleId, sjanger, tidsperiode);
		assertTrue(albumene == returnerteAlbum);
	}

	@Test
	public void testHentManglendeAlbumFraSpotify_NaarArtistsErAlleAlbumLastetErTrue_SkalInputReturneresDirekte() {
		// Arrange
		Artist input = new Artist();
		input.setErAlleAlbumLastet(true);

		// Act
		Artist returArtist = service.hentManglendeAlbumFraSpotify(input);

		// Assert
		assertTrue(returArtist == input);
		verifyZeroInteractions(spotifyAPI, hylleService, artistService, albumDAO, artistDAO, oversetter);
	}

	@Test
	public void testHentManglendeAlbumFraSpotify_NaarArtistsErAlleAlbumLastetErFalse_SkalHenteArtistFraSpotify() {
		// Arrange
		String artistNavn = "artistNavn";
		String artistHref = "spotify:artist:6pmxr66tMAePxzOLfjGNcX";

		SpotifyArtist spotifyArtist = new SpotifyArtist(artistNavn, artistHref);
		spotifyArtist.setAlbums(new ArrayList<SearchResult>());
		when(spotifyAPI.hentArtistPaaSpotifyURI(anyString(), anyInt())).thenReturn(spotifyArtist);

		Artist inputArtist = new Artist();
		inputArtist.setAlbum(new HashSet<Album>());
		inputArtist.setSpotifyURI(artistHref);
		inputArtist.setErAlleAlbumLastet(false);

		// Act
		service.hentManglendeAlbumFraSpotify(inputArtist);

		// Assert
		verify(spotifyAPI).hentArtistPaaSpotifyURI(artistHref, 10);
	}

	@Test
	public void testHentManglendeAlbumFraSpotify_NaarMinstEttAlbumIkkeAlleredeErLastet_SkalHenteAlbumeneFraSpotify() {
		String albumHref1 = "albumHref1";
		String albumHref2 = "albumHref2";

		// Arrange
		SpotifyArtist spotifyArtist = new SpotifyArtist("artistNavn", "artistHref");
		spotifyArtist.setAlbums(new ArrayList<SearchResult>());
		spotifyArtist.getAlbums().add(new SearchResult(new SpotifyAlbum("albumNavn", albumHref1, "artistNavn", "artistHref", "NO")));
		spotifyArtist.getAlbums().add(new SearchResult(new SpotifyAlbum("albumNavn", albumHref2, "artistNavn", "artistHref", "NO")));
		when(spotifyAPI.hentArtistPaaSpotifyURI(anyString(), anyInt())).thenReturn(spotifyArtist);

		Artist inputArtist = new Artist();
		inputArtist.setAlbum(new HashSet<Album>());

		// Act
		service.hentManglendeAlbumFraSpotify(inputArtist);

		// Assert
		Collection<String> albumURIer = new HashSet<String>();
		albumURIer.add(albumHref1);
		albumURIer.add(albumHref2);
		verify(spotifyAPI).hentAlbumPaaSpotifyURIer(albumURIer, 5);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testHentManglendeAlbumFraSpotify_NaarMinstEttAlbumIkkeAlleredeErLastet_SkalFiltrereBortAlbumSomIkkeErTilgjengeligINorge() {
		String albumHref1 = "albumHref1";
		String albumHref2 = "albumHref2";

		// Arrange
		SpotifyArtist spotifyArtist = new SpotifyArtist("artistNavn", "artistHref");
		spotifyArtist.setAlbums(new ArrayList<SearchResult>());
		SpotifyAlbum spotifyAlbum1 = new SpotifyAlbum("albumNavn", albumHref1, "artistNavn", "artistHref", "NO");
		spotifyAlbum1.setAvailability(new Availability("US"));
		spotifyArtist.getAlbums().add(new SearchResult(spotifyAlbum1));
		SpotifyAlbum spotifyAlbum2 = new SpotifyAlbum("albumNavn", albumHref2, "artistNavn", "artistHref", "NO");
		spotifyAlbum2.setAvailability(new Availability("NO"));
		spotifyArtist.getAlbums().add(new SearchResult(spotifyAlbum2));
		when(spotifyAPI.hentArtistPaaSpotifyURI(anyString(), anyInt())).thenReturn(spotifyArtist);
		
		Collection<SpotifyAlbum> spotifyAlbumene = new HashSet<SpotifyAlbum>();
		spotifyAlbumene.add(spotifyAlbum1);
		spotifyAlbumene.add(spotifyAlbum2);
		when(spotifyAPI.hentAlbumPaaSpotifyURIer(anyCollectionOf(String.class), anyInt())).thenReturn(spotifyAlbumene);

		Artist inputArtist = new Artist();
		inputArtist.setAlbum(new HashSet<Album>());

		// Act
		service.hentManglendeAlbumFraSpotify(inputArtist);

		// Assert
		ArgumentCaptor<Collection> spotifyAlbumCaptor = ArgumentCaptor.forClass(Collection.class);
		verify(oversetter).oversettSpotifyAlbum(spotifyAlbumCaptor.capture());
		assertEquals(1, spotifyAlbumCaptor.getValue().size());
		assertEquals(albumHref2, ((SpotifyAlbum) spotifyAlbumCaptor.getValue().iterator().next()).getHref());
	}

	@Test
	public void testHentManglendeAlbumFraSpotify_NaarMinstEttAlbumIkkeAlleredeErLastet_SkalSetteHentetAlbumsSjangerLikArtistsDefaultSjanger() {
		// Arrange
		SpotifyArtist spotifyArtist = new SpotifyArtist("artistNavn", "artistHref");
		spotifyArtist.setAlbums(new ArrayList<SearchResult>());
		spotifyArtist.getAlbums().add(new SearchResult(new SpotifyAlbum("albumNavn", "albumHref", "artistNavn", "artistHref", "NO")));
		when(spotifyAPI.hentArtistPaaSpotifyURI(anyString(), anyInt())).thenReturn(spotifyArtist);
	
		Collection<Album> domeneAlbum = new ArrayList<Album>();
		Album album = new Album();
		album.setNavn("albumNavn");
		album.setSpotifyURI("albumHref");
		domeneAlbum.add(album);
		when(oversetter.oversettSpotifyAlbum(anyCollectionOf(SpotifyAlbum.class))).thenReturn(domeneAlbum);
		
		Artist inputArtist = new Artist();
		inputArtist.setAlbum(new HashSet<Album>());
		inputArtist.setDefaultSjanger(Sjanger.ELECTRONICA);
	
		// Act
		Artist returArtist = service.hentManglendeAlbumFraSpotify(inputArtist);
		
		// Assert
		assertEquals(Sjanger.ELECTRONICA, returArtist.getAlbum().iterator().next().getSjanger());
	}

	@Test
	public void testHentManglendeAlbumFraSpotify_NaarMinstEttAlbumIkkeAlleredeErLastet_SkalHenteOgSetteCoverartLinkPaaHentetAlbum() {
		// Arrange
		SpotifyArtist spotifyArtist = new SpotifyArtist("artistNavn", "artistHref");
		spotifyArtist.setAlbums(new ArrayList<SearchResult>());
		spotifyArtist.getAlbums().add(new SearchResult(new SpotifyAlbum("albumNavn", "albumHref", "artistNavn", "artistHref", "NO")));
		when(spotifyAPI.hentArtistPaaSpotifyURI(anyString(), anyInt())).thenReturn(spotifyArtist);
	
		Collection<Album> domeneAlbum = new ArrayList<Album>();
		Album album = new Album();
		album.setNavn("albumNavn");
		album.setSpotifyURI("albumHref");
		domeneAlbum.add(album);
		when(oversetter.oversettSpotifyAlbum(anyCollectionOf(SpotifyAlbum.class))).thenReturn(domeneAlbum);
		
		Artist inputArtist = new Artist();
		inputArtist.setAlbum(new HashSet<Album>());
		inputArtist.setDefaultSjanger(Sjanger.ELECTRONICA);
	
		// Act
		Artist returArtist = service.hentManglendeAlbumFraSpotify(inputArtist);
		
		// Assert
		assertEquals(Sjanger.ELECTRONICA, returArtist.getAlbum().iterator().next().getSjanger());
	}

	@Test
	public void testHentManglendeAlbumFraSpotify_NaarMinstEttAlbumIkkeAlleredeErLastet_SkalReturnereOversatteAlbum() {
		String albumNavn = "albumNavn";
		String albumHref = "albumHref";
		String artistNavn = "artistNavn";
		String artistHref = "artistHref";

		SpotifyAlbum spotifyAlbum = new SpotifyAlbum(albumNavn, albumHref, artistNavn, artistHref, "NO");
		Collection<SpotifyAlbum> spotifyAlbumene = new ArrayList<SpotifyAlbum>();
		spotifyAlbumene.add(spotifyAlbum);

		// Arrange
		SpotifyArtist spotifyArtist = new SpotifyArtist(artistNavn, artistHref);
		spotifyArtist.setAlbums(new ArrayList<SearchResult>());
		spotifyArtist.getAlbums().add(new SearchResult(spotifyAlbum));
		when(spotifyAPI.hentArtistPaaSpotifyURI(anyString(), anyInt())).thenReturn(spotifyArtist);
		when(spotifyAPI.hentAlbumPaaSpotifyURIer(anyCollectionOf(String.class), anyInt())).thenReturn(spotifyAlbumene);

		Collection<Album> oversatteDomeneAlbum = new ArrayList<Album>();
		Album domeneAlbum = new Album();
		domeneAlbum.setNavn(albumNavn);
		domeneAlbum.setSpotifyURI(albumHref);
		domeneAlbum.setArtist(new Artist());
		domeneAlbum.getArtist().setNavn(artistNavn);
		domeneAlbum.getArtist().setSpotifyURI(artistHref);
		oversatteDomeneAlbum.add(domeneAlbum);
		when(oversetter.oversettSpotifyAlbum(anyCollectionOf(SpotifyAlbum.class))).thenReturn(oversatteDomeneAlbum);

		Artist inputArtist = new Artist();
		inputArtist.setNavn(artistNavn);
		inputArtist.setSpotifyURI(artistHref);
		inputArtist.setAlbum(new HashSet<Album>());

		// Act
		Artist returArtist = service.hentManglendeAlbumFraSpotify(inputArtist);

		// Assert
		verify(oversetter).oversettSpotifyAlbum(spotifyAlbumene);
		assertEquals(artistNavn, returArtist.getNavn());
		assertEquals(artistHref, returArtist.getSpotifyURI());
		Album returAlbum = returArtist.getAlbum().iterator().next();
		assertEquals(albumNavn, returAlbum.getNavn());
		assertEquals(albumHref, returAlbum.getSpotifyURI());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testHentManglendeAlbumFraSpotify_NaarMerEnn50AlbumIkkeAlleredeErLastet_SkalHenteKun50AlbumFraSpotify() {
		// Arrange
		String artistNavn = "artistNavn";
		String artistHref = "artistHref";
		SpotifyArtist spotifyArtist = new SpotifyArtist(artistNavn, artistHref);
		spotifyArtist.setAlbums(new ArrayList<SearchResult>());
		for (int i = 1; i <= 55; i++)
			spotifyArtist.getAlbums().add(new SearchResult(new SpotifyAlbum("albumNavn" + i, "albumHref" + i, artistNavn, artistHref, "NO")));
		when(spotifyAPI.hentArtistPaaSpotifyURI(anyString(), anyInt())).thenReturn(spotifyArtist);

		Artist inputArtist = new Artist();
		inputArtist.setErAlleAlbumLastet(false);
		inputArtist.setAlbum(new HashSet<Album>());

		// Act
		service.hentManglendeAlbumFraSpotify(inputArtist);

		// Assert
		ArgumentCaptor<Collection> spotifyURIerCaptor = ArgumentCaptor.forClass(Collection.class);
		verify(spotifyAPI).hentAlbumPaaSpotifyURIer(spotifyURIerCaptor.capture(), eq(5));
		assertEquals(50, spotifyURIerCaptor.getValue().size());
	}

	@Test
	public void testHentManglendeAlbumFraSpotify_NaarNoenAvAlbumeneAlleredeErLastet_SkalKunDeSomIkkeAlleredeErLastetBliHentet() {
		ArrayList<String> albumURIer = new ArrayList<String>();
		albumURIer.add("albumURI1");
		albumURIer.add("albumURI2");
		albumURIer.add("albumURI3");
		albumURIer.add("albumURI4");

		// Arrange
		String artistNavn = "artistNavn";
		String artistHref = "artistHref";
		SpotifyArtist spotifyArtist = new SpotifyArtist(artistNavn, artistHref);
		spotifyArtist.setAlbums(new ArrayList<SearchResult>());
		for (int i = 0; i <= 3; i++)
			spotifyArtist.getAlbums().add(new SearchResult(new SpotifyAlbum("albumNavn", albumURIer.get(i), artistNavn, artistHref, "NO")));
		when(spotifyAPI.hentArtistPaaSpotifyURI(anyString(), anyInt())).thenReturn(spotifyArtist);

		Artist inputArtist = new Artist();
		inputArtist.setErAlleAlbumLastet(false);
		inputArtist.setAlbum(new HashSet<Album>());
		Album album = new Album();
		album.setNavn("albumNavn");
		album.setSpotifyURI(albumURIer.get(0));
		inputArtist.getAlbum().add(album);
		album = new Album();
		album.setNavn("albumNavn");
		album.setSpotifyURI(albumURIer.get(1));
		inputArtist.getAlbum().add(album);

		// Act
		service.hentManglendeAlbumFraSpotify(inputArtist);

		// Assert
		verify(spotifyAPI).hentAlbumPaaSpotifyURIer(new HashSet<String>(albumURIer.subList(2, 4)), 5);
	}

	@Test
	public void testHentManglendeAlbumFraSpotify_NaarArtistenHarOver50AlbumOgMindreEnn50IkkeErLastet_SkalKunDeSomIkkeAlleredeErLastetBliHentet() {
		ArrayList<String> albumURIer = new ArrayList<String>();
		albumURIer.add("albumURI1");
		albumURIer.add("albumURI2");
		albumURIer.add("albumURI3");
		albumURIer.add("albumURI4");

		// Arrange
		String artistNavn = "artistNavn";
		String artistHref = "artistHref";
		SpotifyArtist spotifyArtist = new SpotifyArtist(artistNavn, artistHref);
		spotifyArtist.setAlbums(new ArrayList<SearchResult>());
		for (int i = 0; i <= 3; i++)
			spotifyArtist.getAlbums().add(new SearchResult(new SpotifyAlbum("albumNavn", albumURIer.get(i), artistNavn, artistHref, "NO")));
		when(spotifyAPI.hentArtistPaaSpotifyURI(anyString(), anyInt())).thenReturn(spotifyArtist);

		Artist inputArtist = new Artist();
		inputArtist.setErAlleAlbumLastet(false);
		inputArtist.setAlbum(new HashSet<Album>());
		Album album = new Album();
		album.setNavn("albumNavn");
		album.setSpotifyURI(albumURIer.get(0));
		inputArtist.getAlbum().add(album);
		album = new Album();
		album.setNavn("albumNavn");
		album.setSpotifyURI(albumURIer.get(1));
		inputArtist.getAlbum().add(album);

		// Act
		service.hentManglendeAlbumFraSpotify(inputArtist);

		// Assert
		verify(spotifyAPI).hentAlbumPaaSpotifyURIer(new HashSet<String>(albumURIer.subList(2, 4)), 5);
	}

	@Test
	public void testSoekEtterAlbumISpotify_NaarDetErIngenTreffISpotify_SkalTjenestenAvslutteOgReturnereTomListe() {
		//Arrange
		when(spotifyAPI.soekEtterAlbum(anyString(), anyString(), anyInt())).thenReturn(new ArrayList<SpotifyAlbum>());
		
		//Act
		List<Album> returAlbum = service.soekEtterAlbumISpotify("", "", false);
		
		//Assert
		assertEquals(0, returAlbum.size());
		verifyZeroInteractions(oversetter, albumDAO, artistService);
	}
	
}
