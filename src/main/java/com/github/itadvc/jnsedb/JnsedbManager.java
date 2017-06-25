package com.github.itadvc.jnsedb;

import java.io.IOException;
import java.util.Collection;

public interface JnsedbManager {

	<E> Collection<E> loadAll(Class<E> clazz) throws IOException;

	<E> E load(Class<E> clazz, Object id) throws IOException;

	<E> E store(E entity) throws IOException;

	<E> E delete(Class<E> clazz, Object id) throws IOException;

}