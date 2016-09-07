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

import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.xtivia.sgdxp.annotation.OrgMember;
import com.xtivia.sgdxp.core.SgDxpApplication;

@OrgMember
public class OrgMemberFilter extends AbstractSecurityFilter {

	public OrgMemberFilter(SgDxpApplication xsfApplication) {
		super(xsfApplication);
	}
		
    @Override
    public void filter(ContainerRequestContext requestContext) {

        if (_logger.isDebugEnabled()) {
            ResourceInfo resourceInfo = super.getResourceInfo();
        	_logger.debug(String.format("Org member filter executes for class=%s, method=%s",
                                        resourceInfo.getResourceClass().getName(),
                                        resourceInfo.getResourceMethod().getName()));
		}

		if (!checkUserInOrg()) {
		    throw new WebApplicationException(Status.UNAUTHORIZED);
		}

        if (_logger.isDebugEnabled()) {
            ResourceInfo resourceInfo = super.getResourceInfo();
            _logger.debug(String.format("Org member filter succeeds for class=%s, method=%s",
                    resourceInfo.getResourceClass().getName(),
                    resourceInfo.getResourceMethod().getName()));
        }
    }
    
	private boolean checkUserInOrg() {
		boolean result = true;
		OrgMember annotation = getAnnotation(OrgMember.class);
		if (annotation != null) {
			User user = getUser();
			if (user != null && !user.isDefaultUser()) {
				try {
					String orgname = annotation.value();
					List<Organization> organizations = user.getOrganizations();
					result = false;
					for (Organization organization : organizations){
						if (organization.getName().equals(orgname)){
							result = true;
							break;
						}
					}
				} catch (PortalException | SystemException e) {
					_logger.error("Error accessing user organizations",e);
					result = false;
				}
			} else {
				result = false; // if not logged in fails too
			}
		}
		return result;
	}

    private static Logger _logger = LoggerFactory.getLogger(OrgMemberFilter.class);
}