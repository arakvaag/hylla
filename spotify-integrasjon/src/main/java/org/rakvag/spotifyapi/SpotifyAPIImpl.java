package org.rakvag.spotifyapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.rakvag.spotifyapi.entity.SpotifyAlbum;
import org.rakvag.spotifyapi.entity.SpotifyArtist;
import org.rakvag.spotifyapi.entity.SpotifyTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpotifyAPIImpl implements SpotifyAPI {

	private static final Logger logger = LoggerFactory.getLogger(SpotifyAPIImpl.class.getName());
	private static final int MAX_ANTALL_TRAADER = 50;
	private static final int TIMEOUT_TRAADER = 10;

	private HTTPBufferedReader httpReader;

	@Override
	public List<SpotifyAlbum> soekEtterAlbum(String artist, String album, int maxForsek) {
		artist = trimTilNull(artist);
		album = trimTilNull(album);

		if (artist == null && album == null)
			return new ArrayList<SpotifyAlbum>();

		StringBuilder soekestreng = new StringBuilder(artist == null ? "" : "artist:" + artist);
		if (artist != null && album != null)
			soekestreng.append("+");
		soekestreng.append(album == null ? "" : "album:" + album);
		logger.debug("Søkestreng: " + soekestreng);
		
		logger.info("Kaller Spotify");
		boolean proevEnGangTil = false;
		int antallForsoek = 0;
		StringBuffer soekerespons = new StringBuffer();
		do {
			try {
				antallForsoek++;
				httpReader.aapne("http://ws.spotify.com/search/1/album.json?q=" + soekestreng.toString());
				String line = null;
				while ((line = httpReader.lesLinje()) != null) {
					soekerespons.append(line);
				}
				httpReader.lukkKobling();
				logger.debug("Søkerespons: " + soekerespons.toString());
				proevEnGangTil = false;
			} catch (BadGatewayException bge) {
				if (antallForsoek >= maxForsek)
					throw bge;
				proevEnGangTil = true;
			}

		} while (proevEnGangTil);

		SearchResult soekeResultat = SearchResult.parseJsonSearchResult(soekerespons.toString());

		if (soekeResultat.getAlbums() != null && soekeResultat.getAlbums().size() > 0) {
			return soekeResultat.getAlbums();
		} else
			return new ArrayList<SpotifyAlbum>();
	}

	private String trimTilNull(String streng) {
		if (streng == null)
			return null;

		streng = streng.trim();
		if (streng.isEmpty())
			return null;

		return streng;
	}

	@Override
	public Collection<SpotifyAlbum> hentAlbumPaaSpotifyURIer(Collection<String> spotifyURIer, int maksForsoek) {
		List<SearchResult> resultatene = hentPaaSpotifyURIer(new ArrayList<String>(spotifyURIer), maksForsoek);
		Collection<SpotifyAlbum> albumene = new HashSet<SpotifyAlbum>();
		for (SearchResult resultat : resultatene)
			albumene.add(resultat.getAlbum());
		return albumene;
	}

	@Override
	public Map<String, SpotifyArtist> hentArtisterPaaSpotifyURIer(Collection<String> spotifyURIer, int maksForsoek) {
		List<SearchResult> resultatene = hentPaaSpotifyURIer(new ArrayList<String>(spotifyURIer), maksForsoek);
		Map<String, SpotifyArtist> artistene = new HashMap<String, SpotifyArtist>();
		for (SearchResult resultat : resultatene) {
			artistene.put(resultat.getArtist().getHref(), resultat.getArtist());
		}

		return artistene;
	}

	@Override
	public SpotifyArtist hentArtistPaaSpotifyURI(String spotifyURI, int maksForsoek) {
		SearchResult searchResult = null;
		try {
			searchResult = new SpotifyLookup(spotifyURI, maksForsoek).call();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
		return searchResult != null ? searchResult.getArtist() : null;
	}

	@Override
	public Map<String, SpotifyTrack> hentTracksPaaSpotifyURIer(Collection<String> spotifyURIer, int maksForsoek) {
		List<SearchResult> resultatene = hentPaaSpotifyURIer(new ArrayList<String>(spotifyURIer), maksForsoek);
		Map<String, SpotifyTrack> tracks = new HashMap<String, SpotifyTrack>();
		for (SearchResult resultat : resultatene) {
			SpotifyTrack track = resultat.getTrack();
			tracks.put(track.getHref(), track);
		}
		return tracks;
	}

	@Override
	public Map<String, String> hentBildelinker(Collection<String> spotifyURIer) {
		List<BildelinkLookup> traader = new ArrayList<BildelinkLookup>();
		for (String spotifyURI : spotifyURIer)
			traader.add(new BildelinkLookup(spotifyURI));
		List<Future<BildelinkInfo>> traadTasks = null;
		boolean proevIgjen = false;
		int antallTraader = MAX_ANTALL_TRAADER;
		do {
			ExecutorService executor = Executors.newFixedThreadPool(MAX_ANTALL_TRAADER);
			try {
				traadTasks = executor.invokeAll(traader, TIMEOUT_TRAADER, TimeUnit.SECONDS);
				proevIgjen = false;
				executor.shutdown();
			} catch (InterruptedException e) {
				if (antallTraader > 5) {
					proevIgjen = true;
					antallTraader -= 5;
				}
				throw new RuntimeException("Forsøk på å starte " + antallTraader + " tråder feiler. Feilmelding: "
						+ e.getMessage(), e);
			} catch (OutOfMemoryError e) {
				logger.warn("Fikk OutOfMemoryError ved forsøk på å åpne " + antallTraader + " tråder");
				if (antallTraader > 5) {
					proevIgjen = true;
					antallTraader -= 5;
				}
				throw new RuntimeException("Forsøk på å starte " + antallTraader + " tråder feiler. Feilmelding: "
						+ e.getMessage(), e);
			}
		} while (proevIgjen);

		Set<BildelinkInfo> resultatene = new HashSet<BildelinkInfo>();
		for (Future<BildelinkInfo> task : traadTasks) {
			try {
				resultatene.add(task.get());
			} catch (InterruptedException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (ExecutionException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}

		return BildelinkInfo.lagMap(resultatene);
	}

	@Override
	public String hentBildelink(String artistURI) {
		BildelinkInfo info;
		try {
			info = new BildelinkLookup(artistURI).call();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
		return info != null ? info.bildelink : null;
	}

	private List<SearchResult> hentPaaSpotifyURIer(List<String> spotifyURIer, int maksForsoek) {
		List<SpotifyLookup> oppslag = new ArrayList<SpotifyLookup>();
		for (String spotifyURI : spotifyURIer) {
			if (spotifyURI != null)
				oppslag.add(new SpotifyLookup(spotifyURI, 10));
		}

		List<Future<SearchResult>> traadTasks = null;
		boolean proevIgjen = false;
		int antallTraader = MAX_ANTALL_TRAADER;
		do {
			ExecutorService executor = Executors.newFixedThreadPool(MAX_ANTALL_TRAADER);
			try {
				traadTasks = executor.invokeAll(oppslag, TIMEOUT_TRAADER, TimeUnit.SECONDS);
				executor.shutdown();
				proevIgjen = false;
			} catch (InterruptedException e) {
				if (antallTraader > 5) {
					proevIgjen = true;
					antallTraader -= 5;
				} else {
					throw new RuntimeException("Forsøk på å starte " + antallTraader + " tråder feiler. Feilmelding: "
							+ e.getMessage(), e);
				}
			} catch (OutOfMemoryError e) {
				logger.warn("Fikk OutOfMemoryError ved forsøk på å åpne " + antallTraader + " tråder");
				if (antallTraader > 5) {
					proevIgjen = true;
					antallTraader -= 5;
				} else {
					throw new RuntimeException("Forsøk på å starte " + antallTraader + " tråder feiler. Feilmelding: "
							+ e.getMessage(), e);
				}
			}
		} while (proevIgjen);

		List<SearchResult> resultatene = new ArrayList<SearchResult>();
		for (Future<SearchResult> task : traadTasks) {
			try {
				resultatene.add(task.get());
			} catch (InterruptedException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (ExecutionException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (CancellationException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}

		return resultatene;
	}

	public void setHttpReader(HTTPBufferedReader httpReader) {
		this.httpReader = httpReader;
	}
}
