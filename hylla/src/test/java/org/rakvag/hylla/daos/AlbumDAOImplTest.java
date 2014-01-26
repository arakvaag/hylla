package org.rakvag.hylla.daos;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rakvag.hylla.domain.Album;
import org.rakvag.hylla.domain.Sjanger;
import org.rakvag.hylla.domain.Tidsperiode;

public class AlbumDAOImplTest {

	private AlbumDAO dao;

	@Mock
	private EntityManager em;

	@Mock
	private TypedQuery<Album> query;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		AlbumDAOImpl impl = new AlbumDAOImpl();
		impl.em = em;
		dao = impl;
	}

	@Test
	public void testFinnAlbum_AlleParametreOppgitt_KjoererQueryMedAlleParametre() {
		// Arrange
		when(em.createQuery(anyString(), eq(Album.class))).thenReturn(query);
		List<Album> albumene = new ArrayList<Album>();
		when(query.getResultList()).thenReturn(albumene);

		// Act
		List<Album> returnerteAlbum = dao.finnAlbum(1L, Sjanger.POP, Tidsperiode.I_00);

		// Assert
		verify(em).createQuery("select o from album where :hylleId in (select albumene_id from hyllealbum where hyller_id = :hylleId) " 
				+ " and sjanger = :sjanger  and aar between 2000 and 2009 ", Album.class);
		verify(query, times(1)).getResultList();
		assertTrue(returnerteAlbum == albumene);
	}

}
