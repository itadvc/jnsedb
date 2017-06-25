package com.github.itadvc.jnsedb.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

class KeysList {

	private String id;
	private long counter = -1;
	private String entityType;
	private boolean initialized;
	private Set<String> keys;

	KeysList(String id, String entityType) {
		this.id = id;
		this.entityType = entityType;
		this.initialized = false;
	}

	String getId() {
		return id;
	}

	String getEntityType() {
		return entityType;
	}

	@SuppressWarnings("unchecked")
	<E> Iterator<E> getIterator(JnsedbEmbeddedManager manager) throws IOException {
		initialize(manager);

		EntityIterator<E> iterator;
		try {
			iterator = new EntityIterator<E>(manager, (Class<E>) Class.forName(entityType));
		} catch (ClassNotFoundException e) {
			throw new IOException("Could not load class " + entityType, e);
		}
		iterator.addIds(keys);
		return iterator.finish();
	}

	@SuppressWarnings("unchecked")
	<E> Collection<E> getCollection(JnsedbEmbeddedManager manager) throws IOException {
		initialize(manager);

		EntityCollection<E> collection;
		try {
			collection = new EntityCollection<E>(manager, (Class<E>) Class.forName(entityType));
		} catch (ClassNotFoundException e) {
			throw new IOException("Could not load class " + entityType, e);
		}
		collection.addIds(keys);
		return collection;
	}

	void ensureKey(JnsedbEmbeddedManager manager, Object key) throws IOException {
		initialize(manager);

		if (!this.keys.contains(key.toString())) {
			this.keys.add(key.toString());
			manager.storeEntityKeys(id, this.keys);
		}
	}

	void removeKey(JnsedbEmbeddedManager manager, Object key) throws IOException {
		initialize(manager);

		if (this.keys.contains(key.toString())) {
			this.keys.remove(key.toString());
			manager.storeEntityKeys(id, this.keys);
		}
	}

	synchronized long incrementCounter(JnsedbEmbeddedManager manager) throws IOException {
		if (counter < 0) {
			counter = manager.loadCounter(id);
		}
		++counter;
		manager.storeCounter(id, counter);
		return counter;
	}

	private void initialize(JnsedbEmbeddedManager manager) throws IOException {
		if (!initialized) {
			this.keys = new CopyOnWriteArraySet<>(manager.loadEntityKeys(id));
			initialized = true;
		}
	}
}
