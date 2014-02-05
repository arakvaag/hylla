package org.rakvag.hylla.web;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.rakvag.hylla.domain.Album;
import org.rakvag.hylla.domain.Hylle;
import org.rakvag.hylla.domain.Sjanger;
import org.rakvag.hylla.domain.Tidsperiode;
import org.rakvag.hylla.services.AlbumService;
import org.rakvag.hylla.services.HylleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Scope("request")
@SessionAttributes
public class HjemController {

	private final static Logger logger = LoggerFactory.getLogger(HjemController.class);
	
	private final static String KODE_ALLE_SJANGRE = "ALLE";
	private final static String KODE_ALLE_TIDSPERIODER = "ALLE";

	@Inject
	private AlbumService albumService;

	@Inject
	private HylleService hyllaService;

	@Inject
	private Sesjonsdata sesjonsdata;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView hjem(@RequestHeader("User-Agent") String userAgent) {
		logger.info("Starter hjem-metoden");
		Hylle hylle = hyllaService.hentHylle(sesjonsdata.getHylleId());

		Map<String, String> sjangre = Sjanger.lagSjangerMap();
		sjangre.put(KODE_ALLE_SJANGRE, "Alle sjangre");
		Map<String, String> tidsperioder = Tidsperiode.lagTidsperiodeMap();
		tidsperioder.put(KODE_ALLE_TIDSPERIODER, "Alle år");
		HjemFilterForm filterForm = new HjemFilterForm();
		filterForm.setValgtSjanger(hylle.getValgtSjanger() != null ? hylle.getValgtSjanger().name() : KODE_ALLE_SJANGRE);
		filterForm.setValgtTidsperiode(hylle.getValgtTidsperiode() != null ? hylle.getValgtTidsperiode().name()
				: KODE_ALLE_TIDSPERIODER);

		ModelAndView mv = new ModelAndView("hjem");
		mv.addObject("albumene", lagAlbumlisteForView(hylle));
		mv.addObject("visAarOgLengdePaaAlbum", false);
		mv.addObject("command", filterForm);
		mv.addObject("spotifyURI", hylle.getSpotifyURIAapentAlbum());
		mv.addObject("sjangre", sjangre);
		mv.addObject("tidsperioder", tidsperioder);

		logger.info("Avslutter hjem-metoden");
		return mv;
	}

	@RequestMapping(value = "/endreFilter", method = RequestMethod.POST)
	public ModelAndView filtrer(@RequestHeader("User-Agent") String userAgent, 
								@ModelAttribute("sjanger") String sjanger, 
								@ModelAttribute("tidsperiode") String tidsperiode) {
		
		logger.info("Starter filtrer-metoden med sjanger " + sjanger + " og tidsperiode " + tidsperiode);
		Hylle hylle = hyllaService.hentHylle(sesjonsdata.getHylleId());
		hylle = oppdaterHylle(hylle, sjanger, tidsperiode, null);
		
		ModelAndView mv = new ModelAndView("_hylle");
		mv.addObject("albumene", lagAlbumlisteForView(hylle));

		logger.info("Avslutter filtrer-metoden med sjanger " + sjanger + " og tidsperiode " + tidsperiode);
		return mv;
	}

	@RequestMapping(value = "/aapne", method = RequestMethod.GET)
	public String aapneMusikk(@ModelAttribute("spotifyURI") String spotifyURI, RedirectAttributes redirAttr) {
		logger.info("Starter metode aapneMusikk med spotifyURI " + spotifyURI);
		oppdaterHylle(hyllaService.hentHylle(sesjonsdata.getHylleId()), null, null, spotifyURI);
		logger.info("Avslutter metode aapneMusikk (redirigerer til hjem-metoden)");
		return "redirect:/";
	}

	@RequestMapping(value = "/lagreAapentAlbum", method = RequestMethod.POST)
	public @ResponseBody String lagreAapentAlbum(@ModelAttribute("spotifyURI") String spotifyURI) {
		logger.info("Starter metode lagreAapentAlbum med spotifyURI " + spotifyURI);
		Hylle hylle = hyllaService.hentHylle(sesjonsdata.getHylleId());
		hylle.setSpotifyURIAapentAlbum(spotifyURI);
		hyllaService.lagreHylle(hylle);
		logger.info("Avslutter metode lagreAapentAlbum med spotifyURI " + spotifyURI);
		return "";
	}

	private Hylle oppdaterHylle(Hylle hylle, String valgtSjanger, String valgtTidsperiode, String spotifyURIAapentAlbum) {
		if (valgtSjanger != null) {
			if (KODE_ALLE_SJANGRE.equals(valgtSjanger))
				hylle.setValgtSjanger(null);
			else
				hylle.setValgtSjanger(Sjanger.valueOf(valgtSjanger));
		}

		if (valgtTidsperiode != null) {
			if (valgtTidsperiode == null || KODE_ALLE_TIDSPERIODER.equals(valgtTidsperiode))
				hylle.setValgtTidsperiode(null);
			else
				hylle.setValgtTidsperiode(Tidsperiode.valueOf(valgtTidsperiode));
		}

		hylle.setSpotifyURIAapentAlbum(!StringUtils.isBlank(spotifyURIAapentAlbum) ? spotifyURIAapentAlbum : hylle
				.getSpotifyURIAapentAlbum());

		hyllaService.lagreHylle(hylle);

		return hylle;
	}

	private List<Album> lagAlbumlisteForView(Hylle hylle) {
		List<Album> albumene = albumService.finnAlbum(hylle.getId(), hylle.getValgtSjanger(), hylle.getValgtTidsperiode());
		
		Collections.sort(albumene, new Comparator<Album>() {
			@Override
			public int compare(Album a1, Album a2) {
				if (a1 == null && a2 == null)
					return 0;
				if (a1 == null)
					return -1;
				if (a2 == null)
					return 1;

				int navnOrder = a1.getArtist().getNavn().toUpperCase()
						.compareTo(a2.getArtist().getNavn().toUpperCase());
				if (navnOrder != 0)
					return navnOrder;
				else
					return a1.getNavn().toUpperCase().compareTo(a2.getNavn().toUpperCase());
			}
		});

		for (Album album : albumene)
			album.setErPaaHylle(true);

		return albumene;
	}

}