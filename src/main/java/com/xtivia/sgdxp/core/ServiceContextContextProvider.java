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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;

public class ServiceContextContextProvider implements ContextProvider<ServiceContext> {
	private static final Logger _log = LoggerFactory.getLogger(ServiceContextContextProvider.class);

	private static final String PROPKEY_HTTP_REQUEST = "HTTP.REQUEST";

	@Override
	public ServiceContext createContext(Message message) {
		ServiceContext serviceContext = null;

		final HttpServletRequest request = (HttpServletRequest) message.getContextualProperty(PROPKEY_HTTP_REQUEST);

		try {
			serviceContext = ServiceContextFactory.getInstance(request);
		} catch (final PortalException e) {
			_log.warn("Fail to create service context: {}", e.getMessage());
		}

		return serviceContext;
	}
}