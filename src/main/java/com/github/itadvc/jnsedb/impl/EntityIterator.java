package com.github.itadvc.jnsedb.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.github.itadvc.jnsedb.JnsedbManager;

class EntityIterator<E> implements Iterator<E> {

	private List<String> ids = new ArrayList<>(100);
	private Iterator<String> iter = null;
	private JnsedbManager manager;
	private Class<E> clazz;

	EntityIterator(JnsedbManager manager, Class<E> clazz) {
		this.manager = manager;
		this.clazz = clazz;
	}

	EntityIterator(JnsedbManager manager, Class<E> clazz, Iterator<String> idIter) {
		this.manager = manager;
		this.clazz = clazz;
		this.iter = idIter;
	}

	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}

	@Override
	public E next() {
		try {
			return manager.load(clazz, iter.next());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	void addIds(Collection<String> collection) {
		ids.addAll(collection);
	}

	Iterator<E> finish() {
		iter = ids.iterator();
		return this;
	}

	int getSize() {
		return ids.size();
	}
}
