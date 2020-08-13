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
package com.xtivia.sgdxp.samples.rest;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.xtivia.sgdxp.annotation.Authenticated;
import com.xtivia.sgdxp.core.IAuthorizer;
import com.xtivia.sgdxp.core.IContext;
import com.xtivia.sgdxp.core.SgDxpApplication;

/**
 * @author Xtivia
 */
@Component(
	property = {
		JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE + "=/users",
		JaxrsWhiteboardConstants.JAX_RS_NAME + "=Users",
		"auth.verifier.guest.allowed=true",
		"oauth2.scopechecker.type=none",
		"liferay.access.control.disable=true",
		"auth.verifier.auth.verifier.PortalSessionAuthVerifier.check.csrf.token=false"
	},
	service = Application.class)
public class UsersRestService extends SgDxpApplication implements IAuthorizer {

	private static final Log _log = LogFactoryUtil.getLog(UsersRestService.class);

	@Reference
	private UserLocalService _userLocalService;

	@Activate
	public void activate() {
		if (_log.isInfoEnabled()) {
			_log.info("User Rest service activated");
		}
	}

	/*
	 * A somewhat nonsensical method that simply authorizes based on whether or not the current minute is even A real
	 * world example would do something more meaningful using the values available in the suppplied context
	 */
	@Override
	public boolean authorize(IContext context) {
		final Calendar now = Calendar.getInstance();

		if (now.get(Calendar.MINUTE) % 2 == 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public IAuthorizer getAuthorizer(IContext ctx) {
		return this;
	}

	@Override
	public Set<Object> getSingletons() {
		final Set<Object> singletons = new HashSet<>();
		singletons.addAll(super.getSingletons());
		singletons.add(this);
		return singletons;
	}

	@GET
	@Path("/list")
	@Produces("text/plain")
	@Authenticated
	// @Authorized
	// @Omniadmin
	// @OrgMember("IBM")
	// @OrgRole(
	// org = "UJA",
	// role = "Bender")
	// @RegularRole({ "BOB", "Manager" })
	public String getUsers() {
		_log.debug("list users");

		final StringBuilder result = new StringBuilder();

		for (final User user : _userLocalService.getUsers(-1, -1)) {
			result.append(user.isDefaultUser() ? "Guest" : user.getFullName());
			result.append("\n");
		}

		return result.toString();
	}

}