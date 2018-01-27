package com.xtivia.sgdxp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.NameBinding;

@NameBinding
@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceAuthorized {
	String name() default "";
	String actionId() default "";
}
