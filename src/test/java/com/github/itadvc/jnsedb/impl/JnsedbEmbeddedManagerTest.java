package com.github.itadvc.jnsedb.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.github.itadvc.jnsedb.JnsedbConfiguration;
import com.github.itadvc.jnsedb.TestEntity;
import com.github.itadvc.jnsedb.impl.JnsedbEmbeddedManager;
import com.github.itadvc.jnsedb.impl.KeysList;
import com.github.itadvc.jnsedb.impl.KeysStore;

public class JnsedbEmbeddedManagerTest {

	private static final String ENTITY_ID = "key1";

	private static final String ENTITY_TYPE_ID = "entityTypeId";

	private static final String DB_NAME = "test";

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	private JnsedbEmbeddedManager manager;
	private File directory;

	@Before
	public void init() throws IOException {
		directory = tempFolder.newFolder();
		KeysStore keysStore = mock(KeysStore.class);
		KeysList keysList = mock(KeysList.class);
		when(keysStore.getKeys(anyString())).thenReturn(keysList);
		when(keysList.incrementCounter(any(JnsedbEmbeddedManager.class))).thenReturn(1L);
		when(keysList.getId()).thenReturn(ENTITY_TYPE_ID);
		manager = new JnsedbEmbeddedManager(new JnsedbConfiguration(), directory.getAbsolutePath(), DB_NAME, keysStore);
		new File(directory, DB_NAME + File.separatorChar + ENTITY_TYPE_ID).mkdirs();
	}

	@Test
	public void itShouldCreateEntityTypesIndex() throws IOException {
		// given
		Map<String, String> types = new HashMap<>();
		types.put(ENTITY_TYPE_ID, TestEntity.class.getName());
		File indexFile = new File(directory, DB_NAME + File.separatorChar + JnsedbEmbeddedManager.INDEX_ENTITY_TYPES);

		// when
		manager.storeEntityTypes(types);

		// then
		assertTrue(readFileContent(indexFile).contains(ENTITY_TYPE_ID));
	}

	@Test
	public void itShouldLoadCreatedEntityTypesIndex() throws IOException {
		// given
		Map<String, String> types = new HashMap<>();
		types.put(ENTITY_TYPE_ID, TestEntity.class.getName());
		manager.storeEntityTypes(types);

		// when
		Map<String, String> loadedTypes = manager.loadEntityTypes();

		// then
		assertEquals(loadedTypes.get(ENTITY_TYPE_ID), TestEntity.class.getName());
	}

	@Test
	public void itShouldCreateEntityKeys() throws IOException {
		// given
		Collection<String> keysToStore = Arrays.asList(ENTITY_ID);
		File indexFile = new File(directory, DB_NAME + File.separatorChar + "keys." + ENTITY_TYPE_ID);

		// when
		manager.storeEntityKeys(ENTITY_TYPE_ID, keysToStore);

		// then
		assertTrue(readFileContent(indexFile).contains(ENTITY_ID));
	}

	@Test
	public void itShouldLoadCreatedEntityKeys() throws IOException {
		// given
		Collection<String> keysToStore = new ArrayList<>();
		keysToStore.add(ENTITY_ID);
		manager.storeEntityKeys(ENTITY_TYPE_ID, keysToStore);

		// when
		Collection<String> loadedKeys = manager.loadEntityKeys(ENTITY_TYPE_ID);

		// then
		assertTrue(loadedKeys.contains(ENTITY_ID));
	}

	@Test
	public void itShouldCreateNewUniueIdForStoredEntity() throws IOException {
		// given
		TestEntity entity = new TestEntity();

		// when
		entity = manager.store(entity);

		// then
		assertNotNull(entity.getId());
	}

	@Test
	public void itShouldCreateAutoincrementedIdForStoredEntity() throws IOException {
		// given
		TestLongEntity entity = new TestLongEntity();

		// when
		entity = manager.store(entity);

		// then
		assertTrue(entity.getId() > 0);
	}

	@Test
	public void itShouldLoadStoredEntity() throws IOException {
		// given
		TestEntity entity = manager.store(new TestEntity());

		// when
		TestEntity loadedEntity = manager.load(entity.getClass(), entity.getId());

		// then
		assertEquals(entity.getValue(), loadedEntity.getValue());
	}

	private String readFileContent(File file) throws IOException, FileNotFoundException {
		char[] buffer = new char[1000];
		try (FileReader fileReader = new FileReader(file)) {
			fileReader.read(buffer);
		}
		return new String(buffer);
	}
}
