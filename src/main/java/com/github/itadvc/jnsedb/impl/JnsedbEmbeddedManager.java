package com.github.itadvc.jnsedb.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.itadvc.jnsedb.JnsedbConfiguration;
import com.github.itadvc.jnsedb.JnsedbManager;
import com.github.itadvc.jnsedb.annotations.Id;
import com.github.itadvc.jnsedb.cache.CacheManager;
import com.github.itadvc.jnsedb.cache.NoCacheManager;
import com.github.itadvc.jnsedb.entity.EntityCounter;
import com.github.itadvc.jnsedb.entity.EntityKeys;
import com.github.itadvc.jnsedb.entity.EntityTypes;

public class JnsedbEmbeddedManager implements JnsedbManager {

	static final Logger logger = Logger.getLogger(JnsedbEmbeddedManager.class.getName());
	static final String INDEX_ENTITY_TYPES = "index.et";

	private final JnsedbConfiguration config;
	private final File directory;
	private final KeysStore keysStore;
	private CacheManager cacheManager = new NoCacheManager();

	public JnsedbEmbeddedManager(JnsedbConfiguration config, String directory, String dbName, KeysStore keysStore) {
		this.config = config;
		this.directory = new File(directory, dbName);
		this.keysStore = keysStore;
	}

	@Override
	public <E> Collection<E> loadAll(Class<E> clazz) throws IOException {
		KeysList keys = keysStore.getKeys(clazz.getName());
		return keys.getCollection(this);
	}

	@Override
	public <E> E load(Class<E> clazz, Object id) throws IOException {
		KeysList keys = keysStore.getKeys(clazz.getName());
		E entity = cacheManager.getFromCache(clazz, id.toString(), () -> {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(fileToEntity(keys, id), clazz);
		});
		return entity;
	}

	@Override
	public <E> E store(E entity) throws IOException {
		Object idValue = EntityUtils.getEntityId(entity);
		if (idValue == null || idValue.equals("") || idValue.equals(Long.valueOf(0)) || idValue.equals(Integer.valueOf(0))) {
			idValue = EntityUtils.setEntityId(entity, generateNewId(entity));
		}

		KeysList keys = keysStore.getKeys(entity.getClass().getName());
		keys.ensureKey(this, idValue);

		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(fileToEntity(keys, idValue), entity);
		cacheManager.refreshEntity(entity, idValue.toString());

		if (config.logStoresEnabled()) {
			logger.log(Level.INFO, "Storing entity: " + mapper.writeValueAsString(entity));
		}
		return entity;
	}

	@Override
	public <E> E delete(Class<E> clazz, Object id) throws IOException {
		E entity = load(clazz, id);
		KeysList keys = keysStore.getKeys(clazz.getName());
		keys.removeKey(this, id);
		cacheManager.removeFromCache(clazz, id.toString());
		fileToEntity(keys, id).delete();
		return entity;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	Map<String, String> loadEntityTypes() throws IOException {
		Map<String, String> loadedMap;
		File types = fileToIndexTypes();
		if (types.exists()) {
			ObjectMapper mapper = new ObjectMapper();
			loadedMap = mapper.readValue(types, EntityTypes.class).getTypes();
		} else {
			loadedMap = new HashMap<>();
		}
		return loadedMap;
	}

	void storeEntityTypes(Map<String, String> typesToStore) throws IOException {
		File types = fileToIndexTypes();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(types, EntityTypes.fromMap(typesToStore));
	}

	Collection<String> loadEntityKeys(String id) throws IOException {
		Collection<String> loaded;
		File keys = fileToEntityKeys(id);
		if (keys.exists()) {
			ObjectMapper mapper = new ObjectMapper();
			loaded = mapper.readValue(keys, EntityKeys.class).getKeys();
		} else {
			loaded = new ArrayList<>();
		}
		return loaded;

	}

	void storeEntityKeys(String id, Collection<String> keysToStore) throws IOException {
		new File(directory, id).mkdir();
		File keys = fileToEntityKeys(id);
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(keys, EntityKeys.fromCollection(keysToStore));
	}

	String generateNewId() {
		return UUID.randomUUID().toString();
	}

	Object generateNewId(Object entity) throws IOException {
		Field field = EntityUtils.findIdField(entity);
		if (field.getType().equals(String.class)) {
			return UUID.randomUUID().toString();
		} else if (field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
			return incrementCounter(entity, field).intValue();
		} else if (field.getType().equals(Long.class) || field.getType().equals(long.class)) {
			return incrementCounter(entity, field);
		} else {
			throw new IllegalArgumentException("Entity " + entity.getClass().getSimpleName() + " needs to have String/Integer/Long @id");
		}
	}

	long loadCounter(String id) throws IOException {
		long result = 0;
		File counter = fileToLocalCounter(id);
		if (counter.exists()) {
			ObjectMapper mapper = new ObjectMapper();
			result = mapper.readValue(counter, EntityCounter.class).getCounter();
		}
		return result;
	}

	void storeCounter(String id, long counterToSave) throws IOException {
		File counter = fileToLocalCounter(id);
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(counter, EntityCounter.fromValue(counterToSave));
	}

	long loadGlobalCounter() throws IOException {
		long result = 0;
		File counter = fileToGlobalCounter();
		if (counter.exists()) {
			ObjectMapper mapper = new ObjectMapper();
			result = mapper.readValue(counter, EntityCounter.class).getCounter();
		}
		return result;
	}

	void storeGlobalCounter(long counterToSave) throws IOException {
		File counter = fileToGlobalCounter();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(counter, EntityCounter.fromValue(counterToSave));
	}

	private Long incrementCounter(Object entity, Field field) throws IOException {
		if (field.getAnnotation(Id.class).globalAutoincrement()) {
			return keysStore.incrementGlobalCounter();
		} else {
			KeysList keys = keysStore.getKeys(entity.getClass().getName());
			return keys.incrementCounter(this);
		}
	}

	private File fileToIndexTypes() {
		return new File(directory, INDEX_ENTITY_TYPES);
	}

	private File fileToEntityKeys(String entityTypeId) {
		return new File(directory, "keys." + entityTypeId);
	}

	private File fileToEntity(KeysList keys, Object entityId) {
		return new File(directory, keys.getId() + File.separatorChar + entityId.toString());
	}

	private File fileToGlobalCounter() {
		return new File(directory, "cnt");
	}

	private File fileToLocalCounter(String id) {
		return new File(directory, "cnt." + id);
	}
}