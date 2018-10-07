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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.xtivia.sgdxp.annotation.OrgRole;
import com.xtivia.sgdxp.core.ISgDxpApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Response.Status;
import java.util.List;

@OrgRole
public class OrgRoleFilter extends AbstractSecurityFilter {

	public OrgRoleFilter(ISgDxpApplication xsfApplication) {
		super(xsfApplication);
	}
		
    @Override
    public void filter(ContainerRequestContext requestContext) {

        if (_logger.isDebugEnabled()) {
            ResourceInfo resourceInfo = super.getResourceInfo();
        	_logger.debug(String.format("Org role filter executes for class=%s, method=%s",
                                        resourceInfo.getResourceClass().getName(),
                                        resourceInfo.getResourceMethod().getName()));
		}

		if (!checkUserInOrgRole()) {
		    throw new WebApplicationException(Status.UNAUTHORIZED);
		}

        if (_logger.isDebugEnabled()) {
            ResourceInfo resourceInfo = super.getResourceInfo();
            _logger.debug(String.format("Org role filter succeeds for class=%s, method=%s",
                    resourceInfo.getResourceClass().getName(),
                    resourceInfo.getResourceMethod().getName()));
        }
    }
    
	private boolean checkUserInOrgRole() {
		boolean result = true;
		OrgRole annotation = getAnnotation(OrgRole.class);
		if (annotation != null) {
			User user = getUser();
			if (user != null && !user.isDefaultUser()) {
				Organization foundOrg = null;
				try {
					String orgname = annotation.org();
					String rolename = annotation.role();
					List<Organization> organizations = user.getOrganizations();
					for (Organization organization : organizations){
						if (organization.getName().equals(orgname)){
							foundOrg = organization;
							break;
						}
					}

					if (foundOrg != null) {
						RoleLocalService roleLocalService = getSgDxpApplication().getRoleLocalService();
						List<Role> roles = roleLocalService.getUserGroupRoles(user.getUserId(),
								foundOrg.getGroupId());
						result = false;
						for (Role role: roles) {
							if (role.getName().equals(rolename)) {
								result = true;
							}
						}
					} else {
						result = false;
					}
				} catch (PortalException | SystemException e) {
					_logger.error("Error accessing DXP role service",e);
					result = false;
				}
			} else {
				result = false; // if not logged in fails too
			}
		}
		return result;
	}

    private static Logger _logger = LoggerFactory.getLogger(OrgRoleFilter.class);
}
