package com.github.itadvc.jnsedb.cache;

class CacheData {

	private final String id;
	private Object value;
	private long createTimestamp;
	private long lastWriteTimestamp;
	private long lastReadTimestamp = -1;
	private int readCount = 0;
	private long size;

	CacheData(String id, Object value) {
		this.id = id;
		this.value = value;
		this.lastWriteTimestamp = this.createTimestamp = this.lastReadTimestamp = System.currentTimeMillis();
	}

	String getId() {
		return id;
	}

	Object getValue() {
		lastReadTimestamp = System.currentTimeMillis();
		readCount++;
		return value;
	}

	CacheData changeValue(Object value) {
		this.value = value;
		this.lastWriteTimestamp = System.currentTimeMillis();
		return this;
	}

	long getSize() {
		return size;
	}

	void setSize(long size) {
		this.size = size;
	}

	long getLastReadTimestamp() {
		return lastReadTimestamp;
	}

	int getReadCount() {
		return readCount;
	}

	long getCreateTimestamp() {
		return createTimestamp;
	}

	long getLastWriteTimestamp() {
		return lastWriteTimestamp;
	}
}