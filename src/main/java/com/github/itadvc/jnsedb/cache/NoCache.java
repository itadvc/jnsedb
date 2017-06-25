package com.github.itadvc.jnsedb.cache;

import java.io.IOException;
import java.util.Optional;

class NoCache implements CacheContainer {

	@Override
	public Optional<CacheData> get(String id) {
		return Optional.empty();
	}

	@Override
	public void put(String id, Object entity) throws IOException {
	}

	@Override
	public void remove(String id) {
	}

}
