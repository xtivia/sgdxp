package com.xtivia.sgdxp.core;

import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.xtivia.sgdxp.filter.*;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseSgDxpApplication extends Application implements ISgDxpApplication {
    //@Override
    public Set<Object> getSingletons() {
        //return super.getSingletons();
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
    //@Override
    public abstract UserLocalService getUserLocalService();

    //@Override
    public abstract OrganizationLocalService getOrganizationLocalService();

    //@Override
    public abstract RoleLocalService getRoleLocalService();

    @Override
    public IAuthorizer getAuthorizer(IContext iContext) {
        return null;
    }
}
