package com.github.itadvc.jnsedb.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class KeysStore {

	private long counter = -1;
	private JnsedbEmbeddedManager manager;
	private Map<String, KeysList> keysMap = null;

	public void init(JnsedbEmbeddedManager manager) throws IOException {
		Map<String, String> types = manager.loadEntityTypes();
		this.manager = manager;
		this.keysMap = new ConcurrentHashMap<>(types.size());
		for (Entry<String, String> type: types.entrySet()) {
			String id = type.getKey();
			String entityType = type.getValue();
			KeysList keysList = new KeysList(id, entityType);
			keysMap.put(entityType, keysList);
		}
	}

	KeysList getKeys(String entityType) throws IOException {
		if (keysMap.containsKey(entityType)) {
			return keysMap.get(entityType);
		} else {
			KeysList keysList = new KeysList(manager.generateNewId(), entityType);
			keysMap.put(entityType, keysList);
			Map<String, String> types = new HashMap<>(keysMap.size() + 1, 1);
			for (KeysList keyEntry: keysMap.values()) {
				types.put(keyEntry.getId(), keyEntry.getEntityType());
			}
			manager.storeEntityTypes(types);
			return keysList;
		}
	}

	synchronized long incrementGlobalCounter() throws IOException {
		if (counter < 0) {
			counter = manager.loadGlobalCounter();
		}
		++counter;
		manager.storeGlobalCounter(counter);
		return counter;
	}
}