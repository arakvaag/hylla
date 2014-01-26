package org.rakvag.hylla.services;

import java.util.List;
import java.util.Set;

import org.rakvag.hylla.domain.Album;
import org.rakvag.hylla.domain.Sjanger;
import org.rakvag.hylla.domain.Spor;
import org.rakvag.hylla.domain.Tidsperiode;

public interface AlbumService extends SpotifyService {

	List<Album> soekEtterAlbumISpotify(String artist, String album, boolean taMedKorteAlbum);

	Album hentAlbum(long albumID);
	
	Set<Spor> hentSporenetilAlbumFraSpotify(String albumsSpotifyURI);

	Album lagreAlbum(Album album);

	List<Album> finnAlbum(Long hylleId, Sjanger sjanger, Tidsperiode tidsperiode);
}
