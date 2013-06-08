package org.rakvag.hylla.services;

import java.util.List;

import org.rakvag.hylla.domain.Album;

public interface AlbumService extends SpotifyService {

	List<Album> soekEtterAlbumISpotify(String artist, String album, boolean taMedKorteAlbum);

	Album hentAlbum(long albumID);

	Album lagreAlbum(Album album);

	void kjoerMasseInnlesning(List<String> linker, long hylleId);
}
