package org.rakvag.hylla.services;

import org.rakvag.hylla.domain.Hylle;
import org.springframework.transaction.annotation.Transactional;

public interface HylleService {

	@Transactional
	Hylle lagreHylle(Hylle hylle);

	Hylle hentHylle(Long hylleId);
	
	@Transactional
	Hylle leggTilAlbumPaaHylle(Long albumId, Long hylleId);

	@Transactional
	Hylle fjernAlbumFraHylle(Long albumId, Long hylleId);
}
