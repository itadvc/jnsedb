package com.github.itadvc.jnsedb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.itadvc.jnsedb.annotations.Cache;
import com.github.itadvc.jnsedb.annotations.Id;

@JsonInclude
@Cache
public class TestEntity {

	@Id
	private String id;

	@JsonProperty
	private String value = "exampleValue";

	public TestEntity() {
	}

	public TestEntity(String value) {
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}
}