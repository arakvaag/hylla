package org.rakvag.spotifyapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

import org.rakvag.spotifyapi.entity.SpotifyAlbum;
import org.rakvag.spotifyapi.entity.SpotifyArtist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BildelinkLookup implements Callable<BildelinkInfo> {

	private static final Logger logger = LoggerFactory.getLogger(BildelinkLookup.class.getName());

	private String spotifyURI;

	public BildelinkLookup(String spotifyURI) {
		this.spotifyURI = spotifyURI;
	}

	@Override
	public BildelinkInfo call() throws Exception {
		StringBuffer html = new StringBuffer();
		logger.info("Henter link til bilde tilhørende URI: " + this.spotifyURI);
		InputStream is = null;
		try {
			String spotifylink = null;
			if (this.spotifyURI.contains("album"))
				spotifylink = SpotifyAlbum.lagSpotifylink(this.spotifyURI);
			else if (this.spotifyURI.contains("artist"))
				spotifylink = SpotifyArtist.lagSpotifylink(this.spotifyURI);
			else
				throw new RuntimeException("Typen Spotify-entitet er ukjent");

			URL url = new URL(spotifylink);
			is = url.openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = reader.readLine()) != null) {
				html.append(line);
			}
			reader.close();
		} catch (MalformedURLException e) {
			logger.warn("Feil ved kall mot Spotify");
			logger.warn(e.getMessage());
			return null;
		} catch (IOException e) {
			logger.warn("Feil ved kall mot Spotify");
			logger.warn(e.getMessage());
			return null;
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				logger.warn("Feil ved kall mot Spotify");
				logger.warn(e.getMessage());
				return null;
			}
		}

		int indeksStart = html.indexOf("http://o.scdn.co/300/");
		if (indeksStart < 1)
			return null;
		int indeksSlutt = html.indexOf("\" border=\"0\"", indeksStart);
		if (indeksStart < 1)
			return null;

		logger.info("Ferdig å hente link til covertart på album med URI: " + this.spotifyURI);
		return new BildelinkInfo(this.spotifyURI, html.substring(indeksStart, indeksSlutt));
	}

}
