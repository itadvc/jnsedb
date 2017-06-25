package com.github.itadvc.jnsedb.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Id {

	/**
	 * Changes policy of generating autoincremented primary key to use global autoincrement counter.
	 * It only applies for id fields typed as Integer/int or Long/long.
	 * Default value is "false".
	 */
	boolean globalAutoincrement() default false;

}