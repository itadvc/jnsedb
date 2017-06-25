package com.github.itadvc.jnsedb;

public class JnsedbConfiguration {

	private CacheType cacheType = CacheType.IN_MEMORY;

	private CacheReplacementPolicy policy = CacheReplacementPolicy.MostRecentlyUsed;

	private boolean logStoresEnabled = false;

	public CacheType getCacheType() {
		return cacheType;
	}

	public void setCacheType(CacheType cacheType) {
		this.cacheType = cacheType;
	}

	public void enableCache() {
		this.cacheType = CacheType.IN_MEMORY;
	}

	public void disableCache() {
		this.cacheType = CacheType.DISABLED;
	}

	public boolean isCacheEnabled() {
		return this.cacheType != CacheType.DISABLED;
	}

	public CacheReplacementPolicy getCacheReplacementPolicy() {
		return policy;
	}

	public void setCacheReplacementPolicy(CacheReplacementPolicy policy) {
		this.policy = policy;
	}

	public boolean logStoresEnabled() {
		return logStoresEnabled;
	}

	public void setLogStoresEnabled(boolean logStoresEnabled) {
		this.logStoresEnabled = logStoresEnabled;
	}
}