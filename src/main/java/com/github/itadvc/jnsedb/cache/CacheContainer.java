package com.github.itadvc.jnsedb.cache;

import java.io.IOException;
import java.util.Optional;

interface CacheContainer {

	Optional<CacheData> get(String id);

	void put(String id, Object entity) throws IOException;

	void remove(String id);

}