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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.xtivia.sgdxp.annotation.RegularRole;
import com.xtivia.sgdxp.core.ISgDxpApplication;
import com.xtivia.sgdxp.exception.SgDxpRestException;

public class RegularRoleFilter extends AbstractSecurityFilter {

	private static Logger _logger = LoggerFactory.getLogger(RegularRoleFilter.class);

	public RegularRoleFilter(ISgDxpApplication xsfApplication) {
		super(xsfApplication);
	}

	@Override
	public void filter(ContainerRequestContext requestContext) {

		if (_logger.isDebugEnabled()) {
			final ResourceInfo resourceInfo = super.getResourceInfo();
			_logger.debug(String.format("Regular role filter executes for class=%s, method=%s",
					resourceInfo.getResourceClass().getName(), resourceInfo.getResourceMethod().getName()));
		}

		if (!checkInRegularRole()) {
			throw new SgDxpRestException("User not in role", Status.BAD_REQUEST);
		}

		if (_logger.isDebugEnabled()) {
			final ResourceInfo resourceInfo = super.getResourceInfo();
			_logger.debug(String.format("Regular role filter succeeds for class=%s, method=%s",
					resourceInfo.getResourceClass().getName(), resourceInfo.getResourceMethod().getName()));
		}
	}

	private boolean checkInRegularRole() {
		boolean result = true;
		final RegularRole annotation = getAnnotation(RegularRole.class);

		if (annotation != null) {
			List<String> roles = Collections.emptyList();

			final String[] values = annotation.value();
			if (values != null) {
				roles = Arrays.asList(values);
			}

			final User user = getUser();

			if (user != null && !user.isDefaultUser()) {
				try {
					final RoleLocalService roleLocalService = getSgDxpApplication().getRoleLocalService();
					result = false;
					for (final String roleName : roles) {
						if (roleLocalService.hasUserRole(user.getUserId(), user.getCompanyId(), roleName, true)) {
							result = true;
						}
					}
				} catch (PortalException | SystemException e) {
					_logger.error("Error accessing DXP role service", e);
				}
			}
		}

		return result;
	}
}
