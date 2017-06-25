package com.github.itadvc.jnsedb.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.github.itadvc.jnsedb.JnsedbManager;

class EntityCollection<E> implements Collection<E> {

	private static final String UNSUPPORTED_ADD_MESSAGE = "Use manager to add new entities";
	private static final String UNSUPPORTED_REMOVE_MESSAGE = "Use manager to delete entities";

	private List<String> ids = new ArrayList<>(100);
	private JnsedbManager manager;
	private Class<E> clazz;

	public EntityCollection(JnsedbManager manager, Class<E> clazz) {
		this.manager = manager;
		this.clazz = clazz;
	}

	void addIds(Collection<String> collection) {
		ids.addAll(collection);
	}

	@Override
	public int size() {
		return ids.size();
	}

	@Override
	public boolean isEmpty() {
		return ids.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return new EntityIterator<>(manager, clazz, ids.iterator());
	}

	@Override
	public boolean contains(Object entity) {
		Object id = EntityUtils.getEntityId(entity);
		return ids.contains(id.toString());
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return c.stream().allMatch(elem -> contains(elem));
	}

	@Override
	public Object[] toArray() {
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return null;
	}

	@Override
	public boolean add(E e) {
		throw new UnsupportedOperationException(UNSUPPORTED_ADD_MESSAGE);
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException(UNSUPPORTED_REMOVE_MESSAGE);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException(UNSUPPORTED_ADD_MESSAGE);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException(UNSUPPORTED_REMOVE_MESSAGE);
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException(UNSUPPORTED_REMOVE_MESSAGE);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException(UNSUPPORTED_REMOVE_MESSAGE);
	}
}
