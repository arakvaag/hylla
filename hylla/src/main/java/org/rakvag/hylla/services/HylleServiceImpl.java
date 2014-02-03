package org.rakvag.hylla.services;

import javax.inject.Inject;

import org.rakvag.hylla.daos.AlbumDAO;
import org.rakvag.hylla.daos.HylleDAO;
import org.rakvag.hylla.domain.Album;
import org.rakvag.hylla.domain.Hylle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HylleServiceImpl implements HylleService {

	@Inject
	private HylleDAO hylleDAO;
	@Inject
	private AlbumDAO albumDAO;
	
	void setHylleDAO(HylleDAO hylleDAO) {
		this.hylleDAO = hylleDAO;
	}
	
	void setAlbumDAO(AlbumDAO albumDAO) {
		this.albumDAO = albumDAO;
	}
	
	@Override
	@Transactional
	public Hylle lagreHylle(Hylle hylle) {
		return hylleDAO.lagre(hylle);
	}

	@Override
	public Hylle hentHylle(Long hylleId) {
		return hylleDAO.hent(hylleId);
	}

	@Override
	@Transactional
	public Hylle leggTilAlbumPaaHylle(Long albumId, Long hylleId) {
		Hylle hylle = hylleDAO.hent(hylleId);
		Album album = albumDAO.hent(albumId);
		hylle.getAlbumene().add(album);
		return hylleDAO.lagre(hylle);
	}

	@Override
	@Transactional
	public Hylle fjernAlbumFraHylle(Long albumId, Long hylleId) {
		Hylle hylle = hylleDAO.hent(hylleId);
		Album album = albumDAO.hent(albumId);
		if(hylle.getAlbumene().contains(album))
			hylle.getAlbumene().remove(album);
		else
			throw new RuntimeException("Albumet fantes ikke på hylla");
		
		return hylleDAO.lagre(hylle);
	}

}
