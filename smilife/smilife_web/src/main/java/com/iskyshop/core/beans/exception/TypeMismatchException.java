/*
 * Copyright 2002-2006 the original author or authors.
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

import com.iskyshop.core.beans.ClassUtils;

/**
 * Exception thrown on a type mismatch when trying to set a bean property.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public class TypeMismatchException extends PropertyAccessException {

	/**
	 * Error code that a type mismatch error will be registered with.
	 */
	public static final String ERROR_CODE = "typeMismatch";


	private Object value;

	private Class requiredType;


	/**
	 * Create a new TypeMismatchException.
	 * @param propertyChangeEvent the PropertyChangeEvent that resulted in the problem
	 * @param requiredType the required target type
	 */
	public TypeMismatchException(PropertyChangeEvent propertyChangeEvent, Class requiredType) {
		this(propertyChangeEvent, requiredType, null);
	}

	/**
	 * Create a new TypeMismatchException.
	 * @param propertyChangeEvent the PropertyChangeEvent that resulted in the problem
	 * @param requiredType the required target type (or <code>null</code> if not known)
	 * @param ex the root cause (may be <code>null</code>)
	 */
	public TypeMismatchException(PropertyChangeEvent propertyChangeEvent, Class requiredType, Throwable ex) {
		super(propertyChangeEvent,
				"Failed to convert property value of type [" +
				(propertyChangeEvent.getNewValue() != null ?
				 ClassUtils.getQualifiedName(propertyChangeEvent.getNewValue().getClass()) : null) + "]" +
				(requiredType != null ?
				 " to required type [" + ClassUtils.getQualifiedName(requiredType) + "]" : "") +
				(propertyChangeEvent.getPropertyName() != null ?
				 " for property '" + propertyChangeEvent.getPropertyName() + "'" : ""),
				ex);
		this.value = propertyChangeEvent.getNewValue();
		this.requiredType = requiredType;
	}

	/**
	 * Create a new TypeMismatchException without PropertyChangeEvent.
	 * @param value the offending value that couldn't be converted (may be <code>null</code>)
	 * @param requiredType the required target type (or <code>null</code> if not known)
	 */
	public TypeMismatchException(Object value, Class requiredType) {
		this(value, requiredType, null);
	}

	/**
	 * Create a new TypeMismatchException without PropertyChangeEvent.
	 * @param value the offending value that couldn't be converted (may be <code>null</code>)
	 * @param requiredType the required target type (or <code>null</code> if not known)
	 * @param ex the root cause (may be <code>null</code>)
	 */
	public TypeMismatchException(Object value, Class requiredType, Throwable ex) {
		super("Failed to convert value of type [" +
				(value != null ?
				 ClassUtils.getQualifiedName(value.getClass()) : null) + "]" +
				(requiredType != null ?
				 " to required type [" + ClassUtils.getQualifiedName(requiredType) + "]" : ""),
				ex);
		this.value = value;
		this.requiredType = requiredType;
	}


	/**
	 * Return the offending value (may be <code>null</code>)
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Return the required target type, if any.
	 */
	public Class getRequiredType() {
		return requiredType;
	}

	public String getErrorCode() {
		return ERROR_CODE;
	}

}
