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
package com.xtivia.sgdxp.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;

public class SgDxpRestException extends WebApplicationException {
	private static final long serialVersionUID = 1L;

	private final Response.Status status;

	public SgDxpRestException() {
		super();
		status = Response.Status.INTERNAL_SERVER_ERROR;
	}

	public SgDxpRestException(String msg) {
		super(msg);
		status = Response.Status.INTERNAL_SERVER_ERROR;
	}

	public SgDxpRestException(String msg, Exception e) {
		super(msg, e);
		status = Response.Status.INTERNAL_SERVER_ERROR;
	}

	public SgDxpRestException(String msg, Exception e, Response.Status status) {
		super(msg, e);
		this.status = status;
	}

	public SgDxpRestException(String msg, Response.Status status) {
		super(msg);
		this.status = status;
	}

	@Override
	public Response getResponse() {
		final JSONObject json = JSONFactoryUtil.createJSONObject();
		json.put("message", super.getMessage());

		return Response.status(getStatus()).entity(json.toString()).build();
	}

	public Response.Status getStatus() {
		return status;
	}

}
