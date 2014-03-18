package com.java.query.anotation;

import static com.java.query.anotation.TableType.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Create {
	String value ();
	TableType type = NONE;
}