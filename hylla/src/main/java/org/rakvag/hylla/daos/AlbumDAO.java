package org.rakvag.hylla.daos;

import java.util.List;

import org.rakvag.hylla.domain.Album;
import org.rakvag.hylla.domain.Sjanger;
import org.rakvag.hylla.domain.Tidsperiode;

public interface AlbumDAO extends SpotifyEntitetDAO<Album> {

	List<Album> finnAlbum(Long hylleId, Sjanger sjanger, Tidsperiode tidsperiode);
	
	List<Album> hentAlbum(List<Long> albumIder);

}
