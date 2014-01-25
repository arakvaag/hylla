package org.rakvag.spotifyapi;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.rakvag.spotifyapi.entity.SpotifyAlbum;
import org.rakvag.spotifyapi.entity.SpotifyArtist;

public class SpotifyAPIImpl_SoekEtterAlbum_Test {
	private SpotifyAPI api;

	@Mock
	private HTTPBufferedReader httpReader;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		SpotifyAPIImpl impl = new SpotifyAPIImpl();
		impl.setHttpReader(httpReader);
		api = impl;
	}

	@Test
	public void test_BaadeArtistOgAlbumOppgitt_KallOk_ValideringAvAlleFelterIResultat() {
		when(httpReader.lesLinje())
				.thenReturn(
						"{\"info\": {\"num_results\": 1, \"limit\": 100, \"offset\": 0, \"query\": \"artist:radka toneff album:some time ago\", \"type\": \"album\", \"page\": 1}, \"albums\": [{\"name\": \"Some Time Ago (A Collection Of Her Finest Moments)\", \"popularity\": \"0.36620\", \"external-ids\": [{\"type\": \"upc\", \"id\": \"00044006665023\"}], \"href\": \"spotify:album:2EHAqPVIc3KQ5PNMflpeO6\", \"artists\": [{\"href\": \"spotify:artist:4zUpW8OD0YnE6hRmdVv1Go\", \"name\": \"Radka Toneff\"}], \"availability\": {\"territories\": \"AD AT AU BE CH CN CZ DE DK EE ES FI FR GB HK HU IE IL IN IS IT LI LT LU LV MC MY NL NO NZ PT RU SE SG SK TH TR TW UA ZA\"}}]}")
				.thenReturn(null);

		List<SpotifyAlbum> resultat = api.soekEtterAlbum("radka toneff", "some time ago", 1);

		verify(httpReader).aapne("http://ws.spotify.com/search/1/album.json?q=artist:radka+toneff+album:some+time+ago");
		assertEquals(1, resultat.size());
		SpotifyAlbum album = resultat.iterator().next();
		assertNull(album.getArtist());
		assertNull(album.getArtistid());
		assertEquals(1, album.getArtists().size());
		SpotifyArtist artist = album.getArtists().iterator().next();
		assertEquals("Radka Toneff", artist.getName());
		assertEquals("spotify:artist:4zUpW8OD0YnE6hRmdVv1Go", artist.getHref());
		assertEquals(
				"AD AT AU BE CH CN CZ DE DK EE ES FI FR GB HK HU IE IL IN IS IT LI LT LU LV MC MY NL NO NZ PT RU SE SG SK TH TR TW UA ZA",
				album.getAvailability().getTerritories());
		assertEquals("spotify:album:2EHAqPVIc3KQ5PNMflpeO6", album.getHref());
		assertEquals("Some Time Ago (A Collection Of Her Finest Moments)", album.getName());
	}

	@Test
	public void test_BaadeArtistOgAlbumOppgitt_ToSoeketreff_ToAlbumSkalReturneres() {
		when(httpReader.lesLinje())
				.thenReturn(
						"{\"info\": {\"num_results\": 2, \"limit\": 100, \"offset\": 0, \"query\": \"artist:radka toneff\", \"type\": \"album\", \"page\": 1}, \"albums\": [{\"name\": \"Butterfly\", \"popularity\": \"0.40724\", \"external-ids\": [{\"type\": \"upc\", \"id\": \"7042880081086\"}], \"href\": \"spotify:album:5sNUqq4t59InV57ZVgwSsx\", \"artists\": [{\"href\": \"spotify:artist:4zUpW8OD0YnE6hRmdVv1Go\", \"name\": \"Radka Toneff\"}], \"availability\": {\"territories\": \"AD AE ZZ\"}}, {\"name\": \"Some Time Ago (A Collection Of Her Finest Moments)\", \"popularity\": \"0.36620\", \"external-ids\": [{\"type\": \"upc\", \"id\": \"00044006665023\"}], \"href\": \"spotify:album:2EHAqPVIc3KQ5PNMflpeO6\", \"artists\": [{\"href\": \"spotify:artist:4zUpW8OD0YnE6hRmdVv1Go\", \"name\": \"Radka Toneff\"}], \"availability\": {\"territories\": \"AD AT UA ZA\"}}]}")
				.thenReturn(null);

		List<SpotifyAlbum> resultat = api.soekEtterAlbum("radka toneff", "some time ago", 1);

		verify(httpReader).aapne("http://ws.spotify.com/search/1/album.json?q=artist:radka+toneff+album:some+time+ago");
		assertEquals(2, resultat.size());
		Iterator<SpotifyAlbum> iter = resultat.iterator();
		SpotifyAlbum album = iter.next();
		assertEquals("spotify:album:5sNUqq4t59InV57ZVgwSsx", album.getHref());
		album = iter.next();
		assertEquals("spotify:album:2EHAqPVIc3KQ5PNMflpeO6", album.getHref());
	}

	@Test
	public void test_KunAlbumOppgitt_SkalHaKunAlbumISoekeURL() {
		when(httpReader.lesLinje())
				.thenReturn(
						"{\"info\": {\"num_results\": 1, \"limit\": 100, \"offset\": 0, \"query\": \"album:prima norsk\", \"type\": \"album\", \"page\": 1}, \"albums\": [{\"name\": \"Prima Norsk\", \"popularity\": \"0.29314\", \"external-ids\": [{\"type\": \"upc\", \"id\": \"7035538881855\"}], \"href\": \"spotify:album:0utmFVxYrFdFaRzGJOT3Tr\", \"artists\": [{\"name\": \"Various Artists\"}], \"availability\": {\"territories\": \"NL NO NP\"}}]}")
				.thenReturn(null);

		api.soekEtterAlbum(null, "prima norsk", 1);

		verify(httpReader).aapne("http://ws.spotify.com/search/1/album.json?q=album:prima+norsk");
	}

	@Test
	public void test_KunArtistOppgitt_SkalHaKunArtistISoekeURL() {
		when(httpReader.lesLinje())
				.thenReturn(
						"{\"info\": {\"num_results\": 1, \"limit\": 100, \"offset\": 0, \"query\": \"artist:radka toneff\", \"type\": \"album\", \"page\": 1}, \"albums\": [{\"name\": \"Butterfly\", \"popularity\": \"0.40724\", \"external-ids\": [{\"type\": \"upc\", \"id\": \"7042880081086\"}], \"href\": \"spotify:album:5sNUqq4t59InV57ZVgwSsx\", \"artists\": [{\"href\": \"spotify:artist:4zUpW8OD0YnE6hRmdVv1Go\", \"name\": \"Radka Toneff\"}], \"availability\": {\"territories\": \"IE IL IN\"}}]}")
				.thenReturn(null);

		api.soekEtterAlbum("radka toneff", null, 1);

		verify(httpReader).aapne("http://ws.spotify.com/search/1/album.json?q=artist:radka+toneff");
	}

	@Test
	public void test_BaadeArtistOgAlbumErNull_SkalReturnereTomList() {
		SpotifyAPIImpl api = new SpotifyAPIImpl();

		List<SpotifyAlbum> resultat = api.soekEtterAlbum(null, null, 1);

		assertTrue(resultat.isEmpty());
	}

	@Test
	public void test_BaadeAlbumOgArtistHarSpaceISeg_SpaceSkalErstattesMedPlussISoekeURL() {
		
		when(httpReader.lesLinje())
				.thenReturn(
						"{\"info\": {\"num_results\": 1, \"limit\": 100, \"offset\": 0, \"query\": \"artist:radka+toneff\", \"type\": \"album\", \"page\": 1}, \"albums\": [{\"name\": \"Butterfly\", \"popularity\": \"0.40724\", \"external-ids\": [{\"type\": \"upc\", \"id\": \"7042880081086\"}], \"href\": \"spotify:album:5sNUqq4t59InV57ZVgwSsx\", \"artists\": [{\"href\": \"spotify:artist:4zUpW8OD0YnE6hRmdVv1Go\", \"name\": \"Radka Toneff\"}], \"availability\": {\"territories\": \"IE IL IN\"}}]}")
				.thenReturn(null);

		api.soekEtterAlbum("radka toneff", "no album", 1);

		verify(httpReader).aapne("http://ws.spotify.com/search/1/album.json?q=artist:radka+toneff+album:no+album");
	}

	@Test
	public void test_OppgittAlbumErSpace_SkalHaKunArtistISoekeURL() {
		when(httpReader.lesLinje())
				.thenReturn(
						"{\"info\": {\"num_results\": 1, \"limit\": 100, \"offset\": 0, \"query\": \"artist:radka toneff\", \"type\": \"album\", \"page\": 1}, \"albums\": [{\"name\": \"Butterfly\", \"popularity\": \"0.40724\", \"external-ids\": [{\"type\": \"upc\", \"id\": \"7042880081086\"}], \"href\": \"spotify:album:5sNUqq4t59InV57ZVgwSsx\", \"artists\": [{\"href\": \"spotify:artist:4zUpW8OD0YnE6hRmdVv1Go\", \"name\": \"Radka Toneff\"}], \"availability\": {\"territories\": \"IE IL IN\"}}]}")
				.thenReturn(null);

		api.soekEtterAlbum("radka toneff", "  ", 1);

		verify(httpReader).aapne("http://ws.spotify.com/search/1/album.json?q=artist:radka+toneff");
	}

	@Test
	public void test_OppgittArtistErSpace_SkalHaKunAlbumISoekeURL() {
		when(httpReader.lesLinje())
				.thenReturn(
						"{\"info\": {\"num_results\": 1, \"limit\": 100, \"offset\": 0, \"query\": \"album:prima norsk\", \"type\": \"album\", \"page\": 1}, \"albums\": [{\"name\": \"Prima Norsk\", \"popularity\": \"0.29314\", \"external-ids\": [{\"type\": \"upc\", \"id\": \"7035538881855\"}], \"href\": \"spotify:album:0utmFVxYrFdFaRzGJOT3Tr\", \"artists\": [{\"name\": \"Various Artists\"}], \"availability\": {\"territories\": \"NL NO NP\"}}]}")
				.thenReturn(null);

		api.soekEtterAlbum("  ", "prima norsk", 1);

		verify(httpReader).aapne("http://ws.spotify.com/search/1/album.json?q=album:prima+norsk");
	}

	@Test
	public void test_SoekeParamatreneHarSpaceRundt_ArtistOgAlbumIURLSkalVaereTrimmet() {
		when(httpReader.lesLinje())
				.thenReturn(
						"{\"info\": {\"num_results\": 1, \"limit\": 100, \"offset\": 0, \"query\": \"artist:radka toneff album:some time ago\", \"type\": \"album\", \"page\": 1}, \"albums\": [{\"name\": \"Some Time Ago (A Collection Of Her Finest Moments)\", \"popularity\": \"0.36620\", \"external-ids\": [{\"type\": \"upc\", \"id\": \"00044006665023\"}], \"href\": \"spotify:album:2EHAqPVIc3KQ5PNMflpeO6\", \"artists\": [{\"href\": \"spotify:artist:4zUpW8OD0YnE6hRmdVv1Go\", \"name\": \"Radka Toneff\"}], \"availability\": {\"territories\": \"AD AT AU BE CH CN CZ DE DK EE ES FI FR GB HK HU IE IL IN IS IT LI LT LU LV MC MY NL NO NZ PT RU SE SG SK TH TR TW UA ZA\"}}]}")
				.thenReturn(null);

		api.soekEtterAlbum(" radka toneff ", " some time ago ", 1);

		verify(httpReader).aapne("http://ws.spotify.com/search/1/album.json?q=artist:radka+toneff+album:some+time+ago");
	}

	@Test
	public void test_VariousArtistRespons_GirIkkeTekniskFeil() {
		when(httpReader.lesLinje())
				.thenReturn(
						"{\"info\": {\"num_results\": 1, \"limit\": 100, \"offset\": 0, \"query\": \"artist:various artists album:prima norsk\", \"type\": \"album\", \"page\": 1}, \"albums\": [{\"name\": \"Prima Norsk\", \"popularity\": \"0.29314\", \"external-ids\": [{\"type\": \"upc\", \"id\": \"7035538881855\"}], \"href\": \"spotify:album:0utmFVxYrFdFaRzGJOT3Tr\", \"artists\": [{\"name\": \"Various Artists\"}], \"availability\": {\"territories\": \"NL NO NP\"}}]}")
				.thenReturn(null);

		List<SpotifyAlbum> resultat = api.soekEtterAlbum("various artists", "prima norsk", 1);

		SpotifyAlbum album = resultat.iterator().next();
		SpotifyArtist artist = album.getArtists().iterator().next();
		assertEquals("Various Artists", artist.getName());
		assertNull(artist.getHref()); // Det er kun ved Various Artists at Href mangler
	}

	@Test
	public void test_BadGatewayErrorFraServerVedFoersteKall_SkalProeveIgjen() {
		doThrow(new BadGatewayException("test")).doNothing().when(httpReader).aapne(anyString());
		when(httpReader.lesLinje())
				.thenReturn(
						"{\"info\": {\"num_results\": 1, \"limit\": 100, \"offset\": 0, \"query\": \"artist:radka toneff album:some time ago\", \"type\": \"album\", \"page\": 1}, \"albums\": [{\"name\": \"Some Time Ago (A Collection Of Her Finest Moments)\", \"popularity\": \"0.36620\", \"external-ids\": [{\"type\": \"upc\", \"id\": \"00044006665023\"}], \"href\": \"spotify:album:2EHAqPVIc3KQ5PNMflpeO6\", \"artists\": [{\"href\": \"spotify:artist:4zUpW8OD0YnE6hRmdVv1Go\", \"name\": \"Radka Toneff\"}], \"availability\": {\"territories\": \"AD AT AU BE CH CN CZ DE DK EE ES FI FR GB HK HU IE IL IN IS IT LI LT LU LV MC MY NL NO NZ PT RU SE SG SK TH TR TW UA ZA\"}}]}")
				.thenReturn(null);

		List<SpotifyAlbum> resultat = api.soekEtterAlbum("radka toneff", "some time ago", 2);

		SpotifyAlbum album = resultat.iterator().next();
		assertEquals("Some Time Ago (A Collection Of Her Finest Moments)", album.getName());
	}

	@Test(expected = BadGatewayException.class)
	public void test_BadGatewayErrorFraServerVedFoersteKall_KunEttForsoekTillatt_SkalKasteBadGatewayException() {
		doThrow(new BadGatewayException("test")).when(httpReader).aapne(anyString());

		api.soekEtterAlbum("radka toneff", "some time ago", 1);
	}

	@Test
	public void test_IngenTreffPaaSoek_SkalReturnereTomList() {
		when(httpReader.lesLinje())
				.thenReturn(
						"{\"info\": {\"num_results\": 0, \"limit\": 100, \"offset\": 0, \"query\": \"artist:radka tonedeaf\", \"type\": \"album\", \"page\": 1}, \"albums\": []}")
				.thenReturn(null);

		List<SpotifyAlbum> resultat = api.soekEtterAlbum("radka tonedeaf", null, 1);

		assertTrue(resultat.isEmpty());
	}

}
