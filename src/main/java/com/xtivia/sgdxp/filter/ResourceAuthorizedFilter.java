package com.xtivia.sgdxp.filter;

import java.lang.invoke.MethodHandles;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.Validator;
import com.xtivia.sgdxp.annotation.ResourceAuthorized;
import com.xtivia.sgdxp.core.SgDxpApplication;

public class ResourceAuthorizedFilter extends AbstractSecurityFilter {
	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Context
	private HttpServletRequest httpRequest;
	@Context
	private ResourceInfo resourceInfo;
	@Context
	private UriInfo uriInfo;

	public ResourceAuthorizedFilter(SgDxpApplication sgDxpApplication) {
		super(sgDxpApplication);
	}

	@Override
	public void filter(ContainerRequestContext requestContext) {
		ResourceAuthorized resourceAuthorized = getAnnotation(ResourceAuthorized.class);
		if (Validator.isNotNull(resourceAuthorized)) {
			if (!hasAccess(resourceAuthorized)) {
				LOG.warn("ResourceAuthorized filter blocked access for class={}, method={}", resourceInfo.getResourceClass().getName(), resourceInfo.getResourceMethod().getName());
				throw new WebApplicationException(Status.FORBIDDEN);
			}
		}
	}

	private boolean hasAccess(ResourceAuthorized annotation) {
		boolean result = false;
		String name = annotation.name();
		String actionId = annotation.actionId();
		
		if ((name != null) && (actionId != null)) {
			PermissionChecker permissionChecker = PermissionThreadLocal.getPermissionChecker();
			result = permissionChecker.hasPermission(0L, name, 0L, actionId);
		} else {
			LOG.warn("ResourceAuthorized filter configured with null resource name or action ID for class={}, method={}", 
					resourceInfo.getResourceClass().getName(), resourceInfo.getResourceMethod().getName());
		}
		return result;
	}

}
