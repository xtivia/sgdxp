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

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.model.User;
import com.xtivia.sgdxp.annotation.Authorized;
import com.xtivia.sgdxp.core.IAuthorizer;
import com.xtivia.sgdxp.core.IContext;
import com.xtivia.sgdxp.core.ISgDxpApplication;
import com.xtivia.sgdxp.exception.SgDxpRestException;
import com.xtivia.sgdxp.liferay.DxpResourceContext;

public class AuthorizedFilter extends AbstractSecurityFilter {

	private static Logger _logger = LoggerFactory.getLogger(AuthorizedFilter.class);

	public AuthorizedFilter(ISgDxpApplication xsfApplication) {
		super(xsfApplication);
	}

	@Override
	public void filter(ContainerRequestContext requestContext) {
		if (_logger.isDebugEnabled()) {
			final ResourceInfo resourceInfo = super.getResourceInfo();
			_logger.debug(String.format("Authorized filter executes for class=%s, method=%s",
					resourceInfo.getResourceClass().getName(), resourceInfo.getResourceMethod().getName()));
		}

		if (!checkIsAuthorized()) {
			throw new SgDxpRestException("User not authorized", Status.UNAUTHORIZED);
		}

		if (_logger.isDebugEnabled()) {
			final ResourceInfo resourceInfo = super.getResourceInfo();
			_logger.debug(String.format("Authorized filter succeeds for class=%s, method=%s",
					resourceInfo.getResourceClass().getName(), resourceInfo.getResourceMethod().getName()));
		}
	}

	private boolean checkIsAuthorized() {
		boolean result = true;
		final Authorized annotation = getAnnotation(Authorized.class);

		if (annotation != null) {
			final User user = getUser();

			if (user != null && !user.isDefaultUser()) {
				final IContext ctx = new DxpResourceContext(super.getRequest(), super.getUriInfo().getPathParameters(),
						super.getResourceInfo());
				final IAuthorizer authorizer = super.getSgDxpApplication().getAuthorizer(ctx);
				if (authorizer != null) {
					result = authorizer.authorize(ctx);
				}
			} else {
				result = false; // if not logged in fails too
			}
		}

		return result;
	}
}
