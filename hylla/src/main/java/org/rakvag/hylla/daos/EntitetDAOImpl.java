package org.rakvag.hylla.daos;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rakvag.hylla.domain.DBEntitet;

public abstract class EntitetDAOImpl<T extends DBEntitet> implements EntitetDAO<T> {

	@PersistenceContext
	protected EntityManager em;

	protected Class<T> type;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public EntitetDAOImpl() {
		Type t = getClass().getGenericSuperclass();
		ParameterizedType pt = (ParameterizedType) t;
		type = (Class) pt.getActualTypeArguments()[0];
	}

	@Override
	public void slett(final long id) {
		em.remove(this.em.getReference(type, id));
	}

	@Override
	public T hent(final long id) {
		return (T) this.em.find(type, id);
	}

	@Override
	public T lagre(final T entitet) {
		if (entitet.getId() != null)
			return this.em.merge(entitet);

		this.em.persist(entitet);
		return entitet;
	}

}
