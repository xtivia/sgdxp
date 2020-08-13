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

import java.lang.invoke.MethodHandles;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.Validator;
import com.xtivia.sgdxp.annotation.ResourceAuthorized;
import com.xtivia.sgdxp.core.ISgDxpApplication;
import com.xtivia.sgdxp.exception.SgDxpRestException;

public class ResourceAuthorizedFilter extends AbstractSecurityFilter {
	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public ResourceAuthorizedFilter(ISgDxpApplication sgDxpApplication) {
		super(sgDxpApplication);
	}

	@Override
	public void filter(ContainerRequestContext requestContext) {
		final ResourceAuthorized resourceAuthorized = getAnnotation(ResourceAuthorized.class);

		if (Validator.isNotNull(resourceAuthorized) && !hasAccess(resourceAuthorized)) {
			LOG.warn("ResourceAuthorized filter blocked access for class={}, method={}",
					getResourceInfo().getResourceClass().getName(), getResourceInfo().getResourceMethod().getName());
			throw new SgDxpRestException("User does not have correct permissions", Status.FORBIDDEN);
		}
	}

	private boolean hasAccess(ResourceAuthorized annotation) {
		boolean result = false;
		final String name = annotation.name();
		final String actionId = annotation.actionId();

		if (name != null && actionId != null) {
			final PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(getUser());
			PermissionThreadLocal.setPermissionChecker(permissionChecker);

			try {
				result = permissionChecker.hasPermission(0L, name, 0L, actionId);
			} catch (final Exception e) {
				LOG.error(e.getMessage(), e);
			}
		} else {
			LOG.warn(
					"ResourceAuthorized filter configured with null resource name or action ID for class={}, method={}",
					getResourceInfo().getResourceClass().getName(), getResourceInfo().getResourceMethod().getName());
		}

		return result;
	}

}
