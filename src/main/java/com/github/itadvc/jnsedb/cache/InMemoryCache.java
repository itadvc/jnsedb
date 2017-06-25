package com.github.itadvc.jnsedb.cache;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.itadvc.jnsedb.annotations.Cache;

class InMemoryCache implements CacheContainer {

	private static final int STATIC_ENTRY_SIZE = 100;

	private final Class<?> clazz;
	private boolean enabled;
	private long maxSize;
	private long cacheSize = 0;
	private final Map<Object, CacheData> cache;
	private final Comparator<CacheData> policy;

	InMemoryCache(Class<?> clazz, Comparator<CacheData> replacementPolicy) {
		this.clazz = clazz;
		this.policy = replacementPolicy;
		this.cache = new ConcurrentHashMap<>(100);
	}

	void analyze() {
		Cache cacheAnnotation = clazz.getAnnotation(Cache.class);
		enabled = cacheAnnotation != null;
		if (enabled) {
			maxSize = cacheAnnotation.maxSizeMB() << 20;
		}
	}

	@Override
	public Optional<CacheData> get(String id) {
		return Optional.ofNullable(cache.get(id));
	}

	@Override
	public void put(String id, Object entity) throws IOException {
		CacheData existing = cache.get(id);
		long existingSize = 0;
		CacheData newData;
		if (existing != null) {
			existingSize = existing.getSize();
			newData = existing.changeValue(entity);
		} else {
			newData = new CacheData(id, entity);
			cache.put(id, newData);
		}
		long newSize = calcSize(entity);
		newData.setSize(newSize);
		cacheSize += (newSize - existingSize);
		checkCacheSize();
	}

	@Override
	public void remove(String id) {
		cache.remove(id);
	}

	void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
	}

	private long calcSize(Object value) throws IOException {
		return new ObjectMapper().writeValueAsBytes(value).length + STATIC_ENTRY_SIZE;
	}

	private void checkCacheSize() {
		while (cacheSize > maxSize) {
			Optional<CacheData> first = cache.values().stream().sorted(policy).findFirst();
			if (first.isPresent()) {
				cache.remove(first.get().getId());
				cacheSize -= first.get().getSize();
			}
		}
	}

}