package com.github.itadvc.jnsedb.cache;

import com.github.itadvc.jnsedb.JnsedbConfiguration;

public class PolicyFactory {

	private JnsedbConfiguration configuration;

	public PolicyFactory(JnsedbConfiguration configuration) {
		this.configuration = configuration;
	}

	public Policy getPolicy() {
		Policy policy;
		switch (configuration.getCacheReplacementPolicy()) {
			case LeastFrequentlyUsed: policy = new PolicyLeastFrequentlyUsed(); break;
			case LeastRecentlyUsed: policy = new PolicyLeastRecentlyUsed(); break;
			case MostRecentlyUsed: policy = new PolicyMostRecentlyUsed(); break;
			default: throw new IllegalStateException("Unknown policyType " + configuration.getCacheReplacementPolicy().name());
		}
		return policy;
	}

}