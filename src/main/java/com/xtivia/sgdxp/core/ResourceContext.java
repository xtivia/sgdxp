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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ResourceInfo;

@SuppressWarnings({ "rawtypes" })
public class ResourceContext extends BaseContext implements IContext {

	private static final long serialVersionUID = 1L;

	private final HttpServletRequest request;

	private final HashSet updates = new HashSet();

	@SuppressWarnings("unchecked")
	public ResourceContext(HttpServletRequest request, Map pathParameters, ResourceInfo resourceInfo) {
		this.request = request;
		super.put(ICommandKeys.HTTP_REQUEST, request);
		super.put(ICommandKeys.HTTP_SESSION, request.getSession());
		super.put(ICommandKeys.SERVLET_CONTEXT, request.getSession().getServletContext());
		super.put(ICommandKeys.PATH_PARAMETERS, pathParameters);
		super.put(ICommandKeys.RESOURCE_CLASS, resourceInfo.getClass());
		super.put(ICommandKeys.RESOURCE_METHOD, resourceInfo.getResourceMethod());
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object get(Object key) {
		// simple null check to avoid NPEs.
		if (key == null) {
			return null;
		}

		// try the super class' get method and return it if found.
		Object o = super.get(key);
		if (o != null) {
			return o;
		}

		// not in the super context, try the path parameters.
		final Map pathParameters = (Map) super.get(ICommandKeys.PATH_PARAMETERS);
		if (pathParameters != null) {
			// check path parameters and return if found.
			o = pathParameters.get(key);
			if (o != null) {
				// we only want to return a single value if it is not really an
				// array.
				final List<String> list = (List<String>) o;
				if (list.size() > 1) {
					return list;
				} else {
					return list.get(0);
				}
			}
		}

		// not in path params, check the request attributes and return if found.
		o = request.getAttribute(key.toString());
		if (o != null) {
			return o;
		}

		// not in request attribs, check request parameters and return if found.
		o = request.getParameterValues(key.toString());
		if (o != null) {
			// we only want to return a single string if it is not really an
			// array.
			final String[] arr = (String[]) o;
			if (arr.length > 1) {
				return arr;
			} else {
				return arr[0];
			}
		}

		// not in the request parameters, check the session and return if found.
		o = request.getSession().getAttribute(key.toString());
		if (o != null) {
			return o;
		}

		// Not in the session, last chance is if it is in the servlet context
		// attributes.
		o = request.getSession().getServletContext().getAttribute(key.toString());

		// will return either the found object or <code>null</code> if it was
		// not in the servlet context attribs.
		return o;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public Set getUpdates() {
		return updates;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object put(Object key, Object value) {
		// add the key to the update list.
		updates.add(key);

		// let the super class do the add.
		return super.put(key, value);
	}

	public void unload() {
		// get the updates
		final Set updates = getUpdates();

		// for each updated key
		for (final Iterator iter = updates.iterator(); iter.hasNext();) {
			// extract the key/value objects
			final String key = (String) iter.next();
			final Object value = super.get(key);

			// null objects should not be handled.
			if (value == null) {
				continue;
			}

			// save as a request attribute.
			request.setAttribute(key, value);
		}
	}
}