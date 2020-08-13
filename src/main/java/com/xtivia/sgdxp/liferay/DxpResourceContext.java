/**
 * Copyright (c) 2016 Xtivia, Inc. All rights reserved.
 * <p/>
 * This file is part of the Xtivia Services Framework (XSF) library.
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.xtivia.sgdxp.liferay;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ResourceInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.xtivia.sgdxp.core.ICommandKeys;
import com.xtivia.sgdxp.core.ResourceContext;

public class DxpResourceContext extends ResourceContext {

	private static Logger _logger = LoggerFactory.getLogger(DxpResourceContext.class);

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	public DxpResourceContext(HttpServletRequest request, Map pathParameters, ResourceInfo resourceInfo) {

		// let the super class do the basic construction
		super(request, pathParameters, resourceInfo);

		try {
			// extract the user from the request
			final User user = getUser(request);

			if (user != null) {
				super.put(DxpCommandKeys.LIFERAY_USER, user);
			}

			// extract the company id from the request
			final Long companyId = getCompanyId(request);
			if (companyId != 0) {
				super.put(DxpCommandKeys.LIFERAY_COMPANY_ID, companyId);
			}

		} catch (final Exception e) {
			_logger.error("Error accessing DXP user", e);
		}
	}

	// the following methods are defined as protected
	// methods that can be overridden in derived classes
	// also enables mocking these methods in a unit
	// test environment

	@SuppressWarnings("unchecked")
	@Override
	public <T> T find(String key) {
		// let the super class find the object.
		final T val = super.find(key);

		// If the key is for the permission checker but it was not found in the
		// context
		if (val == null && key.equals(DxpCommandKeys.LIFERAY_PERMISSION_CHECKER)) {

			// extract the request object from the context.
			final HttpServletRequest request = super.find(ICommandKeys.HTTP_REQUEST);

			// if not found there isn't much we can do as a result.
			if (request == null) {
				return null;
			}

			// try to get the user from the incoming request
			User user = null;

			try {
				user = getUser(request);
			} catch (PortalException | SystemException e) {
				_logger.error("Error accessing DXP user", e);
			}

			// if the user was found
			if (user != null && !user.isDefaultUser()) {

				try {
					// create a new permission checker for the user.
					final PermissionChecker permissionChecker = getPermissionChecker(user);

					// set it in the context so we don't create a new one later
					// on
					super.put(DxpCommandKeys.LIFERAY_PERMISSION_CHECKER, permissionChecker);

					// return the permission checker
					return (T) permissionChecker;
				} catch (final Exception e) {
					_logger.error("Error accessing DXP permission checker", e);
				}
			}

			// if we get here the we did not find a permission checker or could
			// not create one from the user, so
			// little we can do but allow the null object to be returned.
		}

		return val;
	}

	private long getCompanyId(HttpServletRequest request) {
		return PortalUtil.getCompanyId(request);
	}

	private PermissionChecker getPermissionChecker(User user) {
		final PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(user);
		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		return permissionChecker;
	}

	private User getUser(HttpServletRequest request) throws PortalException {
		return PortalUtil.getUser(request);
	}
}