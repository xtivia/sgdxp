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

/**
 * class IAuthorizer: Interface defining a component which manages authorization on JAX-RS resource methods.
 */
public interface IAuthorizer {

	/**
	 * authorize: Checks whether the current request is authorized or not.
	 * @param context Context for the authorization.
	 * @return boolean <code>true</code> if the user can access the resource method, otherwise they cannot.
	 */
    boolean authorize(IContext context);
  
}
