package org.rakvag.hylla.daos;

import org.rakvag.hylla.domain.Album;
import org.springframework.stereotype.Repository;

@Repository
public class AlbumDAOImpl extends SpotifyEntitetDAOImpl<Album> implements AlbumDAO {
}
