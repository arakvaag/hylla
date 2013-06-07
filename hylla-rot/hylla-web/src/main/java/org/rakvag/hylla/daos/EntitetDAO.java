package org.rakvag.hylla.daos;

import org.rakvag.hylla.domain.Entitet;
import org.springframework.transaction.annotation.Transactional;

public interface EntitetDAO<T extends Entitet> {
	T hent(final long id);

	@Transactional
	void slett(final long id);
	
	@Transactional
	T lagre(final T entitet);
}
