package com.github.itadvc.jnsedb.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.itadvc.jnsedb.annotations.Id;

@JsonInclude
class TestLongEntity {

	@Id
	private long id;

	@JsonProperty
	private String value = "exampleValue";

	long getId() {
		return id;
	}

	void setId(long id) {
		this.id = id;
	}
}