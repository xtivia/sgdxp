/**
 * Copyright (c) 2016 Xtivia, Inc. All rights reserved.
 *
 * This file is part of the Xtivia Services Framework (XSF) library.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.xtivia.sgdxp.filter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.cxf.jaxrs.utils.AnnotationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.xtivia.sgdxp.core.ISgDxpApplication;

abstract class AbstractSecurityFilter implements ContainerRequestFilter {

	private static Logger _logger = LoggerFactory.getLogger(AbstractSecurityFilter.class);

	/*
	 * NOTE: the following context elements are injected by the JAX-RS runtime. Its important to note that even though
	 * JAX-RS filters are inherently singleton-scoped, the injected values will be both request-scoped and thread-safe.
	 * This is typically accomplished via by injecting a proxy that retrieves the value from a thread-local variable.
	 *
	 * As per chapter 9.1 of the JAX-RS specification ....
	 *
	 * "Context is specific to a particular request but instances of certain JAX-RS components (providers and resource
	 * classes with a lifecycle other than per-request) may need to support multiple concurrent requests. When injecting
	 * an instance of one of the types listed in Section 9.2, the instance supplied MUST be capable of selecting the
	 * correct context for a particular request. Use of a thread-local proxy is a common way to achieve this."
	 */
	@Context
	private HttpServletRequest httpRequest;

	@Context
	private ResourceInfo resourceInfo;

	private final ISgDxpApplication sgDxpApplication;

	@Context
	private UriInfo uriInfo;

	protected AbstractSecurityFilter(ISgDxpApplication sgDxpApplication) {
		this.sgDxpApplication = sgDxpApplication;
	}

	@Override
	public abstract void filter(ContainerRequestContext requestContext);

	@SuppressWarnings("unchecked")
	protected <T> T getAnnotation(Class<? extends Annotation> annotationClass) {
		Annotation annotation = resourceInfo.getResourceMethod().getAnnotation(annotationClass);

		if (annotation == null) {
			annotation = resourceInfo.getResourceClass().getAnnotation(annotationClass);

			if (annotation == null) {
				final Method interfaceMethod = AnnotationUtils.getAnnotatedMethod(annotationClass,
						resourceInfo.getResourceMethod());
				annotation = interfaceMethod.getAnnotation(annotationClass);
			}
		}

		return (T) annotation;
	}

	protected HttpServletRequest getRequest() {
		return httpRequest;
	}

	protected ResourceInfo getResourceInfo() {
		return resourceInfo;
	}

	protected ISgDxpApplication getSgDxpApplication() {
		return sgDxpApplication;
	}

	protected UriInfo getUriInfo() {
		return uriInfo;
	}

	protected User getUser() {
		User user = null;
		final UserLocalService userLocalService = getSgDxpApplication().getUserLocalService();
		final String userid = httpRequest.getRemoteUser();

		try {
			if (userid != null && userLocalService != null) {
				user = userLocalService.getUser(new Long(userid));

				if (user.isDefaultUser()) {
					user = null;
				}
			}
		} catch (final Exception e) {
			_logger.error("Error accessing DXP user service", e);
		}

		return user;
	}
}
