package com.github.itadvc.jnsedb.entity;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude
public class EntityTypes {

	@JsonProperty
	private Map<String, String> types = new HashMap<>();

	public Map<String, String> getTypes() {
		return types;
	}

	void setTypes(Map<String, String> types) {
		this.types = types;
	}

	public static EntityTypes fromMap(Map<String, String> typeMap) {
		EntityTypes entityTypes = new EntityTypes();
		entityTypes.setTypes(typeMap);
		return entityTypes;
	}
}
