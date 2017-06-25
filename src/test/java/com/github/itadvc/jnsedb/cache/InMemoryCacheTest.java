package com.github.itadvc.jnsedb.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.github.itadvc.jnsedb.TestEntity;
import com.github.itadvc.jnsedb.cache.CacheData;
import com.github.itadvc.jnsedb.cache.InMemoryCache;
import com.github.itadvc.jnsedb.cache.Policy;
import com.github.itadvc.jnsedb.cache.PolicyLeastFrequentlyUsed;
import com.github.itadvc.jnsedb.cache.PolicyLeastRecentlyUsed;
import com.github.itadvc.jnsedb.cache.PolicyMostRecentlyUsed;

public class InMemoryCacheTest {

	private static final String VALUE_0_BYTES = "";
	private static final String VALUE_100_BYTES = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";

	private static final Policy LFU = new PolicyLeastFrequentlyUsed();
	private static final Policy LRU = new PolicyLeastRecentlyUsed();
	private static final Policy MRU = new PolicyMostRecentlyUsed();

	InMemoryCache cacheLFU = new InMemoryCache(TestEntity.class, LFU);
	InMemoryCache cacheLRU = new InMemoryCache(TestEntity.class, LRU);
	InMemoryCache cacheMRU = new InMemoryCache(TestEntity.class, MRU);

	@Before
	public void init() {
		cacheLFU.analyze();
	}

	@Test
	public void itShould_returnOptionalAbsent_when_emptyCache() {
		assertFalse(cacheLFU.get("1").isPresent());
	}

	@Test
	public void itShould_removeId_when_emptyCache() {
		cacheLFU.remove("1");
	}

	@Test
	public void itShould_returnObject_when_addedPreviously() throws IOException {
		// given
		TestEntity entity = new TestEntity();
		cacheLFU.put("1", entity);

		// when
		Optional<CacheData> cachedInstance = cacheLFU.get("1");

		// then
		assertEquals(entity.getValue(), ((TestEntity)cachedInstance.get().getValue()).getValue());
	}

	@Test
	public void itShould_removeLFU_when_cacheToBig() throws IOException {
		// given
		cacheLFU.setMaxSize(600);
		cacheLFU.put("1", new TestEntity(VALUE_100_BYTES));
		cacheLFU.put("2", new TestEntity(VALUE_100_BYTES));
		cacheLFU.put("3", new TestEntity(VALUE_0_BYTES));
		cacheLFU.get("1").get().getValue();
		cacheLFU.get("3").get().getValue();
		cacheLFU.get("1").get().getValue();

		// when
		cacheLFU.put("3", new TestEntity(VALUE_100_BYTES));	// replace with bigger value - over max size
		Optional<CacheData> removed = cacheLFU.get("2");

		// then
		assertFalse(removed.isPresent());
	}

	@Test
	public void itShould_removeLRU_when_cacheToBig() throws IOException, InterruptedException {
		// given
		cacheLRU.setMaxSize(600);
		cacheLRU.put("1", new TestEntity(VALUE_100_BYTES));
		cacheLRU.put("2", new TestEntity(VALUE_100_BYTES));
		cacheLRU.put("3", new TestEntity(VALUE_0_BYTES));
		cacheLRU.get("1").get().getValue();
		Thread.sleep(2);
		cacheLRU.get("3").get().getValue();
		Thread.sleep(2);
		cacheLRU.get("2").get().getValue();

		// when
		cacheLRU.put("3", new TestEntity(VALUE_100_BYTES));	// replace with bigger value - over max size
		Optional<CacheData> removed = cacheLRU.get("1");

		// then
		assertFalse(removed.isPresent());
	}

	@Test
	public void itShould_removeMRU_when_cacheToBig() throws IOException, InterruptedException {
		// given
		cacheMRU.setMaxSize(600);
		cacheMRU.put("1", new TestEntity(VALUE_100_BYTES));
		cacheMRU.put("2", new TestEntity(VALUE_100_BYTES));
		cacheMRU.put("3", new TestEntity(VALUE_0_BYTES));
		cacheMRU.get("1").get().getValue();
		Thread.sleep(2);
		cacheMRU.get("3").get().getValue();
		Thread.sleep(2);
		cacheMRU.get("2").get().getValue();

		// when
		cacheMRU.put("3", new TestEntity(VALUE_100_BYTES));	// replace with bigger value - over max size
		Optional<CacheData> removed = cacheMRU.get("2");

		// then
		assertFalse(removed.isPresent());
	}

	void template() {
		// given

		// when

		// then
	}
}