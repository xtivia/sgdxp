package com.xtivia.sgdxp.core;

import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.Set;

public interface ISgDxpApplication {
    public Set<Object> getSingletons();
    public UserLocalService getUserLocalService();
    public OrganizationLocalService getOrganizationLocalService();
    public RoleLocalService getRoleLocalService();
    public IAuthorizer getAuthorizer(IContext ctx);
}
