package com.github.itadvc.jnsedb.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude
public class EntityCounter {

	@JsonProperty
	private long counter = 1;

	public long getCounter() {
		return counter;
	}

	void setCounter(long counter) {
		this.counter = counter;
	}

	public static EntityCounter fromValue(long value) {
		EntityCounter counter = new EntityCounter();
		counter.setCounter(value);
		return counter;
	}

}
