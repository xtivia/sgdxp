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

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.xtivia.sgdxp.filter.AuthenticatedFilter;
import com.xtivia.sgdxp.filter.AuthorizedFilter;
import com.xtivia.sgdxp.filter.OmniadminFilter;
import com.xtivia.sgdxp.filter.OrgMemberFilter;
import com.xtivia.sgdxp.filter.OrgRoleFilter;
import com.xtivia.sgdxp.filter.RegularRoleFilter;

public class SgDxpApplication extends Application {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Set<Object> getSingletons() {

	    // set up the service trackers for the DXP services
        BundleContext bc = FrameworkUtil.getBundle(SgDxpApplication.class).getBundleContext();
        _userLocalServiceTracker = new ServiceTracker(bc, UserLocalService.class.getName(), null);
        _organizationLocalServiceTracker = new ServiceTracker(bc, OrganizationLocalService.class.getName(), null);
        _roleLocalServiceTracker = new ServiceTracker(bc, RoleLocalService.class.getName(), null);
        
        _userLocalServiceTracker.open();
        _organizationLocalServiceTracker.open();
        _roleLocalServiceTracker.open();
        
	    Set<Object> singletons = new HashSet<Object>();

        //add the XSF security filters
		singletons.add(new AuthenticatedFilter(this));
        singletons.add(new AuthorizedFilter(this));
        singletons.add(new OmniadminFilter(this));
        singletons.add(new OrgMemberFilter(this));
        singletons.add(new OrgRoleFilter(this));
        singletons.add(new RegularRoleFilter(this));

		return singletons;
	}	
	
	/*
	 * Management of the Liferay UserLocalService class provided as an OSGi service. 
	 */
	
	public UserLocalService getUserLocalService() {
		return (UserLocalService) this._userLocalServiceTracker.getService();
	}
	
	@SuppressWarnings("rawtypes")
	private ServiceTracker _userLocalServiceTracker;

	/*
	 * Management of the Liferay OrganizationLocalService class provided as an OSGi service. 
	 */
	
	public OrganizationLocalService getOrganizationLocalService() {
		return (OrganizationLocalService) this._organizationLocalServiceTracker.getService();
	}
	
	@SuppressWarnings("rawtypes")
	private ServiceTracker _organizationLocalServiceTracker;
	
	/*
	 * Management of the Liferay RoleLocalService class provided as an OSGi service. 
	 */
	
	public RoleLocalService getRoleLocalService() {
		return (RoleLocalService) this._roleLocalServiceTracker.getService();
	}
	
	@SuppressWarnings("rawtypes")
	private ServiceTracker _roleLocalServiceTracker;
	
	/*
	 * Method overridden by derived classes when custom authorization is needed
	 */
	public IAuthorizer getAuthorizer(IContext ctx) {
		return null;
	}
}