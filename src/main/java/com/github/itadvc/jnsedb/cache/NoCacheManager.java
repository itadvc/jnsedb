package com.github.itadvc.jnsedb.cache;

import java.io.IOException;

public class NoCacheManager implements CacheManager {

	public static final NoCacheManager INSTANCE = new NoCacheManager();

	@Override
	public <T> T getFromCache(Class<T> clazz, String id, LoadLambda<T> loadLambda) throws IOException {
		return loadLambda.load();
	}

	@Override
	public void refreshEntity(Object entity, String id) {
	}

	@Override
	public <T> void removeFromCache(Class<T> clazz, String id) {
	}

}