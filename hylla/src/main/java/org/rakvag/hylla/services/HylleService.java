package org.rakvag.hylla.services;

import java.util.List;

import org.rakvag.hylla.domain.Album;
import org.rakvag.hylla.domain.Hylle;
import org.rakvag.hylla.domain.Sjanger;
import org.rakvag.hylla.domain.Tidsperiode;
import org.springframework.transaction.annotation.Transactional;

public interface HylleService {

	@Transactional
	Hylle lagreHylle(Hylle hylle);

	Hylle hentHylle(Long hylleId);
	
	@Transactional
	Hylle leggTilAlbumPaaHylle(Long albumId, Long hylleId);

	@Transactional
	Hylle fjernAlbumFraHylle(Long albumId, Long hylleId);
	
	List<Album> hentFiltrertListeAvHyllesAlbum(Long hylleId, Sjanger sjanger, Tidsperiode tidsperiode);
}
