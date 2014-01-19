package org.rakvag.spotifyapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Klasse som forenkler lesing av innhold over HTTP, og også mocking av HTTP-kall der denne klassen brukes.
 * 
 * @author André Rakvåg (andre@rakvag.org)
 */
public class HTTPBufferedReader {

	private static final Logger logger = LoggerFactory.getLogger(HTTPBufferedReader.class.getName());

	private BufferedReader bufferedReader;

	public void aapne(String url) {
		try {
			logger.debug("URL: " + url);
			URL url2 = new URL(url);
			Charset cs = Charset.forName("UTF-8");
			InputStreamReader inputStreamReader = new InputStreamReader(url2.openStream(), cs);
			bufferedReader = new BufferedReader(inputStreamReader);
		} catch (MalformedURLException e) {
			logger.error("Ugyldig URL oppgitt");
			logger.error(e.getMessage());
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			if (e.getMessage().contains("Server returned HTTP response code: 502"))
				throw new BadGatewayException(e.getMessage(), e);
			logger.error("IO-feil");
			logger.error(e.getMessage());
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public String lesLinje() {
		try {
			if (bufferedReader == null)
				throw new IllegalStateException("Kobling er ikke åpen");
			return bufferedReader.readLine();
		} catch (IOException e) {
			if (e.getMessage().contains("Server returned HTTP response code: 502"))
				throw new BadGatewayException(e.getMessage(), e);
			logger.error("IO-feil");
			logger.error(e.getMessage());
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public void lukkKobling() {
		try {
			if (bufferedReader == null)
				throw new IllegalStateException("Kobling er ikke åpen");
			bufferedReader.close();
		} catch (IOException e) {
			logger.error("IO-feil");
			logger.error(e.getMessage());
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
