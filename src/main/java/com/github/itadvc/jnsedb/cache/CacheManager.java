package com.github.itadvc.jnsedb.cache;

import java.io.IOException;

public interface CacheManager {

	@FunctionalInterface
	interface LoadLambda<T> {
		T load() throws IOException;
	}

	<T> T getFromCache(Class<T> clazz, String id, LoadLambda<T> loadLambda) throws IOException;

	<T> void refreshEntity(T entity, String id) throws IOException;

	<T> void removeFromCache(Class<T> clazz, String id);

}