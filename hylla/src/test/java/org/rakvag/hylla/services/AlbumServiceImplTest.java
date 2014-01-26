package org.rakvag.hylla.services;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;
import org.mockito.*;
import org.rakvag.hylla.daos.AlbumDAO;
import org.rakvag.hylla.domain.Album;
import org.rakvag.hylla.domain.Sjanger;
import org.rakvag.hylla.domain.Tidsperiode;

public class AlbumServiceImplTest {

	@Mock
	private AlbumDAO albumDAO;
	
	private AlbumService service;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		AlbumServiceImpl impl = new AlbumServiceImpl();
		impl.setAlbumDAO(albumDAO);
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

}
