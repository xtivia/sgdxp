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

import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;

public class SgDxpApplication extends BaseSgDxpApplication implements ISgDxpApplication {

	@Override
	public IAuthorizer getAuthorizer(IContext ctx) {
		return null;
	}

	@Override
	public OrganizationLocalService getOrganizationLocalService() {
		return OrganizationLocalServiceUtil.getService();
	}

	@Override
	public RoleLocalService getRoleLocalService() {
		return RoleLocalServiceUtil.getService();
	}

	@Override
	public UserLocalService getUserLocalService() {
		return UserLocalServiceUtil.getService();
	}
}