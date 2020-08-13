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

import com.xtivia.sgdxp.filter.AuthenticatedFilter;
import com.xtivia.sgdxp.filter.AuthorizedFilter;
import com.xtivia.sgdxp.filter.OmniadminFilter;
import com.xtivia.sgdxp.filter.OrgMemberFilter;
import com.xtivia.sgdxp.filter.OrgRoleFilter;
import com.xtivia.sgdxp.filter.RegularRoleFilter;
import com.xtivia.sgdxp.filter.ResourceAuthorizedFilter;

public abstract class BaseSgDxpApplication extends Application implements ISgDxpApplication {
	@Override
	public IAuthorizer getAuthorizer(IContext iContext) {
		return null;
	}

	@Override
	public Set<Class<?>> getClasses() {
		final Set<Class<?>> resources = new HashSet<>();

		resources.add(ServiceContextContextProvider.class);
		resources.add(UserContextProvider.class);

		return resources;
	}

	@Override
	public Set<Object> getSingletons() {
		final Set<Object> singletons = new HashSet<>();

		singletons.add(new AuthenticatedFilter(this));
		singletons.add(new AuthorizedFilter(this));
		singletons.add(new OmniadminFilter(this));
		singletons.add(new OrgMemberFilter(this));
		singletons.add(new OrgRoleFilter(this));
		singletons.add(new RegularRoleFilter(this));
		singletons.add(new ResourceAuthorizedFilter(this));

		return singletons;
	}
}
