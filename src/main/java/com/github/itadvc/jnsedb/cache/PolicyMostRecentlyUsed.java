package com.github.itadvc.jnsedb.cache;

class PolicyMostRecentlyUsed implements Policy {

	@Override
	public int compare(CacheData o1, CacheData o2) {
		return (int)(o2.getLastReadTimestamp() - o1.getLastReadTimestamp());
	}

}