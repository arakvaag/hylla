package org.rakvag.hylla.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.rakvag.hylla.domain.Album;
import org.rakvag.hylla.domain.Artist;
import org.rakvag.hylla.services.ArtistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Scope("request")
public class ArtistController {

	private static Logger logger = LoggerFactory.getLogger(ArtistController.class.getName());

	@Inject
	private ArtistService artistService;
	@Inject
	private Sesjonsdata sesjonsdata;

	@RequestMapping(value = "/artist/", method = RequestMethod.GET)
	public ModelAndView aapneDetaljer(@ModelAttribute("artistId") String artistId) {
		logger.info("Starter aapneDetaljer med artistId " + artistId);
		Artist artist = artistService.hentArtist(Long.parseLong(artistId));
		ModelAndView mv = new ModelAndView("artist", "artist", artist);
		List<Album> albumene = new ArrayList<Album>();
		
		logger.debug("Starter filtrering og sortering av artistens " + artist.getAlbum().size() + " album");
		for (Album album : artist.getAlbum()) {
			if ((!album.erEtKortAlbum()) && album.isTilgjengeligINorge()) {
				album.setErPaaHylle(sesjonsdata.getHylleId());
				albumene.add(album);
			}
		}
		Collections.sort(albumene);
		if (albumene.size() >= 60)
			albumene = albumene.subList(0, 59);
		logger.debug("Ferdig med filtrering og sortering av artistens " + artist.getAlbum().size() + " album");

		mv.addObject("albumene", albumene);
		logger.info("Ferdig kjørt aapneDetaljer med artistId " + artistId);
		return mv;
	}
	
}
