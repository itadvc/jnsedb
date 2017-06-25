package com.github.itadvc.jnsedb.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude
public class EntityKeys {

	@JsonProperty
	private List<String> keys = new ArrayList<>();

	public List<String> getKeys() {
		return keys;
	}

	void setKeys(List<String> keys) {
		this.keys = keys;
	}

	public static EntityKeys fromCollection(Collection<String> keys) {
		EntityKeys entityKeys = new EntityKeys();
		entityKeys.setKeys(new ArrayList<>(keys));
		return entityKeys;
	}
}
