/*
 * Copyright 2002-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iskyshop.core.beans.exception;

import java.beans.PropertyChangeEvent;


/**
 * Superclass for exceptions related to a property access,
 * such as type mismatch or invocation target exception.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public abstract class PropertyAccessException extends PropertyException {

	private PropertyChangeEvent propertyChangeEvent;
	

	/**
	 * Create a new PropertyAccessException.
	 * @param propertyChangeEvent the PropertyChangeEvent that resulted in the problem
	 * @param msg the detail message
	 * @param ex the root cause
	 */
	public PropertyAccessException(PropertyChangeEvent propertyChangeEvent, String msg, Throwable ex) {
		super(msg, ex);
		this.propertyChangeEvent = propertyChangeEvent;
	}

	/**
	 * Create a new PropertyAccessException without PropertyChangeEvent.
	 * @param msg the detail message
	 * @param ex the root cause
	 */
	public PropertyAccessException(String msg, Throwable ex) {
		super(msg, ex);
	}


	/**
	 * Return the PropertyChangeEvent that resulted in the problem.
	 * Only available if an actual bean property was affected.
	 */
	public PropertyChangeEvent getPropertyChangeEvent() {
		return propertyChangeEvent;
	}

}
