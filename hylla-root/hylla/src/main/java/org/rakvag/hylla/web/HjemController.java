package org.rakvag.hylla.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.rakvag.hylla.domain.Album;
import org.rakvag.hylla.domain.Hylle;
import org.rakvag.hylla.domain.Nasjonalitet;
import org.rakvag.hylla.domain.Sjanger;
import org.rakvag.hylla.domain.Tidsperiode;
import org.rakvag.hylla.services.AlbumService;
import org.rakvag.hylla.services.HylleService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Scope("request")
@SessionAttributes
public class HjemController {

	private final static String KODE_ALLE_SJANGRE = "ALLE";
	private final static String KODE_ALLE_TIDSPERIODER = "ALLE";
	private final static String KODE_ALLE_NASJONALITETER = "ALLE";

	@Inject
	private AlbumService albumService;

	@Inject
	private HylleService hyllaService;

	@Inject
	private Sesjonsdata sesjonsdata;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView hjem() {
		Hylle hylle = hyllaService.hentHylle(sesjonsdata.getHylleId());

		Set<Album> albumene = hylle.getAlbumene();
		albumene = filtrerAlbumene(albumene, hylle.getValgtSjanger(), hylle.getValgtTidsperiode(), hylle.getValgtNasjonalitet());
		List<Album> sortertListeAvAlbum = lagSortertListMedAlbum(albumene);
		for (Album album : sortertListeAvAlbum)
			album.setErPaaHylle(sesjonsdata.getHylleId());

		Map<String, String> sjangre = Sjanger.lagSjangerMap();
		sjangre.put(KODE_ALLE_SJANGRE, "Alle sjangre");
		Map<String, String> tidsperioder = Tidsperiode.lagTidsperiodeMap();
		tidsperioder.put(KODE_ALLE_TIDSPERIODER, "Alle år");
		Map<String, String> nasjonaliteter = Nasjonalitet.lagNasjonalitetMap();
		nasjonaliteter.put(KODE_ALLE_NASJONALITETER, "Alle nasjonaliteter");
		HjemFilterForm filterForm = new HjemFilterForm();
		filterForm
				.setValgtSjanger(hylle.getValgtSjanger() != null ? hylle.getValgtSjanger().name() : KODE_ALLE_SJANGRE);
		filterForm.setValgtTidsperiode(hylle.getValgtTidsperiode() != null ? hylle.getValgtTidsperiode().name()
				: KODE_ALLE_TIDSPERIODER);
		filterForm.setValgtNasjonalitet(hylle.getValgtNasjonalitet() != null ? hylle.getValgtNasjonalitet().name()
				: KODE_ALLE_NASJONALITETER);

		ModelAndView mv = new ModelAndView("hjem");
		mv.addObject("albumene", sortertListeAvAlbum);
		mv.addObject("command", filterForm);
		mv.addObject("spotifyURI", hylle.getSpotifyURIAapentAlbum());
		mv.addObject("sjangre", sjangre);
		mv.addObject("tidsperioder", tidsperioder);
		mv.addObject("nasjonaliteter", nasjonaliteter);

		return mv;
	}

	@RequestMapping(value = "/endreFilter", method = RequestMethod.POST)
	public String filtrer(@ModelAttribute("command") HjemFilterForm filterForm, RedirectAttributes redirAttr) {
		oppdaterHylle(hyllaService.hentHylle(sesjonsdata.getHylleId()), filterForm, null);
		return "redirect:/";
	}

	@RequestMapping(value = "/aapne", method = RequestMethod.GET)
	public String aapneMusikk(@ModelAttribute("spotifyURI") String spotifyURI, RedirectAttributes redirAttr) {
		oppdaterHylle(hyllaService.hentHylle(sesjonsdata.getHylleId()), null, spotifyURI);
		return "redirect:/";
	}

	private Hylle oppdaterHylle(Hylle hylle, HjemFilterForm filterForm, String spotifyURIAapentAlbum) {
		if (filterForm != null) {
			String valgtSjanger = filterForm.getValgtSjanger();
			if (valgtSjanger != null) {
				if (KODE_ALLE_SJANGRE.equals(valgtSjanger))
					hylle.setValgtSjanger(null);
				else
					hylle.setValgtSjanger(Sjanger.valueOf(valgtSjanger));
			}

			String valgtTidsperiode = filterForm.getValgtTidsperiode();
			if (valgtTidsperiode != null) {
				if (valgtTidsperiode == null || KODE_ALLE_TIDSPERIODER.equals(valgtTidsperiode))
					hylle.setValgtTidsperiode(null);
				else
					hylle.setValgtTidsperiode(Tidsperiode.valueOf(valgtTidsperiode));
			}

			String valgtNasjonalitet = filterForm.getValgtNasjonalitet();
			if (valgtNasjonalitet != null) {
				if (valgtNasjonalitet == null || KODE_ALLE_TIDSPERIODER.equals(valgtNasjonalitet))
					hylle.setValgtNasjonalitet(null);
				else
					hylle.setValgtNasjonalitet(Nasjonalitet.valueOf(valgtNasjonalitet));
			}
		}

		hylle.setSpotifyURIAapentAlbum(!StringUtils.isBlank(spotifyURIAapentAlbum) ? spotifyURIAapentAlbum : hylle
				.getSpotifyURIAapentAlbum());

		hyllaService.lagreHylle(hylle);

		return hylle;
	}

	private Set<Album> filtrerAlbumene(Set<Album> favorittene, Sjanger sjanger, Tidsperiode tidsperiode, Nasjonalitet nasjonalitet) {
		Set<Album> filtrert = new HashSet<Album>();

		for (Album album : favorittene) {
			boolean skalMed = true;

			if (sjanger != null && sjanger != album.getSjanger())
				skalMed = false;

			if (tidsperiode != null && tidsperiode != Tidsperiode.hentTidsperiode(album.getAar()))
				skalMed = false;

			if (nasjonalitet != null && nasjonalitet != album.getArtist().getNasjonalitet())
				skalMed = false;

			if (skalMed)
				filtrert.add(album);
		}

		return filtrert;
	}

	private List<Album> lagSortertListMedAlbum(Set<Album> albumene) {
		List<Album> albumeneList = new ArrayList<Album>(albumene);
		Collections.sort(albumeneList, new Comparator<Album>() {
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
		return albumeneList;
	}

}