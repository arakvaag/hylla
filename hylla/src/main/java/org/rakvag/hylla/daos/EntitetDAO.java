package org.rakvag.hylla.daos;

import org.rakvag.hylla.domain.DBEntitet;
import org.springframework.transaction.annotation.Transactional;

public interface EntitetDAO<T extends DBEntitet> {
	T hent(final long id);

	@Transactional
	void slett(final long id);
	
	@Transactional
	T lagre(final T entitet);
}
