package com.github.itadvc.jnsedb.cache;

class PolicyLeastFrequentlyUsed implements Policy {

	@Override
	public int compare(CacheData o1, CacheData o2) {
		return o1.getReadCount() - o2.getReadCount();
	}

}