package com.github.itadvc.jnsedb.impl;

import java.lang.reflect.Field;

import com.github.itadvc.jnsedb.annotations.Id;

public class EntityUtils {

	public static Object getEntityId(Object entity) {
		Object idValue = null;
		try {
			idValue = findIdField(entity).get(entity);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Change access error occurred");
		}
		return idValue;
	}

	public static Object setEntityId(Object entity, Object id) {
		try {
			findIdField(entity).set(entity, id);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Change access error occurred");
		}
		return id;
	}

	public static Field findIdField(Object object) {
		Field idField = null;
		for (Field field: object.getClass().getDeclaredFields()) {
			Id id = field.getAnnotation(Id.class);
			if (id != null) {
				idField = field;
				break;
			}
		}
		if (idField == null) {
			throw new IllegalArgumentException("Entity class " + object.getClass().getSimpleName() + " does not contains field annotated with @Id");
		}
		idField.setAccessible(true);
		return idField;
	}

}