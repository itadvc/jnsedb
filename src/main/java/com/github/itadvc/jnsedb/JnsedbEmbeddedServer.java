package com.github.itadvc.jnsedb;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.itadvc.jnsedb.cache.CacheManager;
import com.github.itadvc.jnsedb.cache.InMemoryCacheManager;
import com.github.itadvc.jnsedb.cache.NoCacheManager;
import com.github.itadvc.jnsedb.cache.Policy;
import com.github.itadvc.jnsedb.cache.PolicyFactory;
import com.github.itadvc.jnsedb.impl.JnsedbEmbeddedManager;
import com.github.itadvc.jnsedb.impl.KeysStore;

public class JnsedbEmbeddedServer {

	public static final String DEFAULT_DB_NAME = "default";

	private final String directory;
	private final Map<String, KeysStore> keysStores;
	private final Map<String, CacheManager> cacheManagers;
	private final JnsedbConfiguration configuration;
	private final PolicyFactory policyFactory;

	public JnsedbEmbeddedServer(String directory) {
		this.directory = directory;
		this.keysStores = new ConcurrentHashMap<>();
		this.cacheManagers = new ConcurrentHashMap<>();
		this.configuration = new JnsedbConfiguration();
		this.policyFactory = new PolicyFactory(this.configuration);
	}

	public JnsedbConfiguration getConfiguration() {
		return configuration;
	}

	public JnsedbManager getManager() throws IOException {
		return createManager(DEFAULT_DB_NAME);
	}

	public JnsedbManager getManager(String databaseName) throws IOException {
		return createManager(databaseName);
	}

	public JnsedbEmbeddedServer startup() throws IOException {
		new File(directory).mkdirs();
		return this;
	}

	private KeysStore ensureKeysStore(String databaseName) throws IOException {
		KeysStore keysStore = keysStores.get(databaseName);
		if (keysStore == null) {
			new File(directory, databaseName).mkdirs();

			keysStore = new KeysStore();
			keysStores.put(databaseName, keysStore);
			keysStore.init(createManager(databaseName));
		}
		return keysStore;
	}

	private CacheManager ensureCacheManager(String databaseName) {
		CacheManager cacheManager = cacheManagers.get(databaseName);
		if (cacheManager == null) {
			cacheManager = NoCacheManager.INSTANCE;
			if (configuration.isCacheEnabled()) {
				Policy policy = policyFactory.getPolicy();
				cacheManager = new InMemoryCacheManager(policy);
			}
			cacheManagers.put(databaseName, cacheManager);
		}
		return cacheManager;
	}

	private JnsedbEmbeddedManager createManager(String databaseName) throws IOException {
		KeysStore keysStore = ensureKeysStore(databaseName);
		CacheManager cacheManager = ensureCacheManager(databaseName);
		JnsedbEmbeddedManager manager = new JnsedbEmbeddedManager(configuration, directory, databaseName, keysStore);
		manager.setCacheManager(cacheManager);
		return manager;
	}
}