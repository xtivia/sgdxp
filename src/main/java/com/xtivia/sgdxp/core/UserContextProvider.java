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
package com.xtivia.sgdxp.core;

import javax.servlet.http.HttpServletRequest;

import org.apache.cxf.jaxrs.ext.ContextProvider;
import org.apache.cxf.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.PortalUtil;

public class UserContextProvider implements ContextProvider<User> {
	private static final Logger _log = LoggerFactory.getLogger(UserContextProvider.class);

	private static final String PROPKEY_HTTP_REQUEST = "HTTP.REQUEST";

	@Override
	public User createContext(Message message) {
		User user = null;

		final HttpServletRequest request = (HttpServletRequest) message.getContextualProperty(PROPKEY_HTTP_REQUEST);

		try {
			user = PortalUtil.getUser(request);
		} catch (final PortalException e) {
			_log.warn("Fail to retrieve user: {}", e);
		}

		return user;
	}
}