package org.rakvag.hylla.services;

import java.util.Collection;

import org.rakvag.hylla.domain.Album;
import org.rakvag.hylla.domain.Artist;

public abstract interface SpotifyService {
	/**
	 * Oppdaterer alle Spotify-entitene i input til å være persisterte entiteter, basert på SpotifyURI. Dette gjelder
	 * både albumene, og entitetene som er del av album. For de spotifyURI som allerede finnes i databasen erstattet
	 * objektet i input med objekt hentet fra databasen. For de spotifyURI som ikke allerede finnes i databasen blir
	 * entiteten i input lagret til databasen.
	 * 
	 * @param album
	 * @return persisterte album
	 */
	Collection<Album> synkroniserAlbumInklArtistMedDBEtterSpotifyURI(Collection<Album> albumene);

	/**
	 * Oppdaterer alle Spotify-entitene i input til å være persisterte entiteter, basert på SpotifyURI. Dette gjelder
	 * både artistene, og entitetene som er del av artister. For de spotifyURI som allerede finnes i databasen erstattet
	 * objektet i input med objekt hentet fra databasen. For de spotifyURI som ikke allerede finnes i databasen blir
	 * entiteten i input lagret til databasen.
	 * 
	 * @param artister
	 * @return persisterte artister
	 */
	Collection<Artist> synkroniserArtisterInklAlbumMedDBEtterSpotifyURI(Collection<Artist> artistene);
}
