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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.model.User;
import com.xtivia.sgdxp.annotation.Authorized;
import com.xtivia.sgdxp.core.IAuthorizer;
import com.xtivia.sgdxp.core.IContext;
import com.xtivia.sgdxp.core.SgDxpApplication;
import com.xtivia.sgdxp.liferay.DxpResourceContext;

@Authorized
public class AuthorizedFilter extends AbstractSecurityFilter {

	public AuthorizedFilter(SgDxpApplication xsfApplication) {
		super(xsfApplication);
	}
		
    @Override
    public void filter(ContainerRequestContext requestContext) {

        if (_logger.isDebugEnabled()) {
            ResourceInfo resourceInfo = super.getResourceInfo();
        	_logger.debug(String.format("Authorized filter executes for class=%s, method=%s",
                                        resourceInfo.getResourceClass().getName(),
                                        resourceInfo.getResourceMethod().getName()));
		}

		if (!checkIsAuthorized()) {
		    throw new WebApplicationException(Status.UNAUTHORIZED);
		}

        if (_logger.isDebugEnabled()) {
            ResourceInfo resourceInfo = super.getResourceInfo();
            _logger.debug(String.format("Authorized filter succeeds for class=%s, method=%s",
                    resourceInfo.getResourceClass().getName(),
                    resourceInfo.getResourceMethod().getName()));
        }
    }
    
	private boolean checkIsAuthorized() {
		boolean result = true;
		Authorized annotation = getAnnotation(Authorized.class);
		if (annotation != null) {
			User user = getUser();
			if (user != null && !user.isDefaultUser()) {
				IContext ctx = new DxpResourceContext(super.getRequest(),
						super.getUriInfo().getPathParameters(),
						super.getResourceInfo());
				IAuthorizer authorizer = super.getSgDxpApplication().getAuthorizer(ctx);
				if (authorizer != null) {
					result = authorizer.authorize(ctx);
				}
			} else {
				result = false; // if not logged in fails too
			}
		}
		return result;
	}

    private static Logger _logger = LoggerFactory.getLogger(AuthorizedFilter.class);
}
