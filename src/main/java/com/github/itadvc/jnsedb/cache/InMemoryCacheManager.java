package com.github.itadvc.jnsedb.cache;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.github.itadvc.jnsedb.annotations.Cache;

@SuppressWarnings("unchecked")
public class InMemoryCacheManager implements CacheManager {

	private Map<Class<?>, CacheContainer> cacheMap = new ConcurrentHashMap<>();
	private final Policy replacementPolicy;

	public InMemoryCacheManager(Policy policy) {
		this.replacementPolicy = policy;
	}

	@Override
	public <T> T getFromCache(Class<T> clazz, String id, LoadLambda<T> loadLambda) throws IOException {
		Optional<CacheData> cacheData = getCache(clazz).get(id);
		T result = null;
		if (cacheData.isPresent()) {
			result = (T) cacheData.get().getValue();
		} else {
			result = loadLambda.load();
			getCache(clazz).put(id, result);
		}
		return result;
	}

	@Override
	public <T> void refreshEntity(T entity, String id) throws IOException {
		getCache(entity.getClass()).put(id, entity);
	}

	@Override
	public <T> void removeFromCache(Class<T> clazz, String id) {
		getCache(clazz).remove(id);
	}

	private <T> CacheContainer getCache(Class<T> clazz) {
		CacheContainer cache = cacheMap.get(clazz);
		if (cache == null) {
			if (clazz.getAnnotation(Cache.class) != null) {
				cache = new InMemoryCache(clazz, replacementPolicy);
				((InMemoryCache)cache).analyze();
			} else {
				cache = new NoCache();
			}
			cacheMap.put(clazz, cache);
		}
		return cache;
	}

}