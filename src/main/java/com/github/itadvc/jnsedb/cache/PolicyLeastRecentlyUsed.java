package com.github.itadvc.jnsedb.cache;

class PolicyLeastRecentlyUsed implements Policy {

	@Override
	public int compare(CacheData o1, CacheData o2) {
		return (int)(o1.getLastReadTimestamp() - o2.getLastReadTimestamp());
	}

}