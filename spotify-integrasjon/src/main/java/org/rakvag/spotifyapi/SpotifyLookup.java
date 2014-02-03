package org.rakvag.spotifyapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SpotifyLookup implements Callable<SearchResult> {

	private static final Logger logger = LoggerFactory.getLogger(SpotifyLookup.class.getName());

	private String spotifyURI;
	private int maksForsoek;

	public SpotifyLookup(String spotifyURI, int maksForsoek) {
		this.spotifyURI = spotifyURI;
		this.maksForsoek = maksForsoek;
	}

	@Override
	public SearchResult call() throws Exception {
		boolean proevEnGangTil = false;
		int antallForsoek = 0;
		SearchResult searchResult = null;
		do {
			try {
				antallForsoek++;
				searchResult = utfoerHentPaaSpotifyURI(this.spotifyURI);
				proevEnGangTil = (searchResult == null);
			} catch (BadGatewayException bge) {
				if (antallForsoek >= this.maksForsoek)
					return null;
				proevEnGangTil = true;
			}
		} while (proevEnGangTil && antallForsoek < this.maksForsoek);

		return searchResult;
	}

	private SearchResult utfoerHentPaaSpotifyURI(String spotifyURI) {
		StringBuffer lookuprespons = new StringBuffer();
		logger.debug("Starter lookup av SpotifyURI: " + spotifyURI);
		InputStream is = null;
		try {
			StringBuffer urlStr = new StringBuffer("http://ws.spotify.com/lookup/1/.json?uri=" + spotifyURI);
			if (spotifyURI.contains("album"))
				urlStr.append("&extras=trackdetail");
			else if (spotifyURI.contains("artist"))
				urlStr.append("&extras=albumdetail");
			URL url = new URL(urlStr.toString());
			is = url.openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = reader.readLine()) != null) {
				lookuprespons.append(line);
			}
			reader.close();
		} catch (MalformedURLException e) {
			logger.warn("Feil ved kall mot Spotify");
			logger.warn(e.getMessage());
			return null;
		} catch (IOException e) {
			return haandterIOException(e);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				return haandterIOException(e);
			}
		}

		logger.debug("Ferdig med lookup av SpotifyURI: " + spotifyURI);
		return SearchResult.parseJsonSearchResult(lookuprespons.toString());
	}

	private SearchResult haandterIOException(IOException e) {
		if (e.getMessage().contains("Server returned HTTP response code: 502"))
			throw new BadGatewayException(e.getMessage());

		logger.warn("Feil ved kall mot Spotify");
		logger.warn(e.getMessage());
		return null;
	}

}
