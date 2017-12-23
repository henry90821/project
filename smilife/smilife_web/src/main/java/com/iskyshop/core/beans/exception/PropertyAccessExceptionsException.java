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

import java.io.PrintStream;
import java.io.PrintWriter;

import com.iskyshop.core.beans.ObjectUtils;

/**
 * Combined exception, composed of individual PropertyAccessException instances.
 * An object of this class is created at the beginning of the binding
 * process, and errors added to it as necessary.
 *
 * <p>The binding process continues when it encounters application-level
 * propertyAccessExceptions, applying those changes that can be applied and storing
 * rejected changes in an object of this class.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 18 April 2001
 */
public class PropertyAccessExceptionsException extends PropertyException {

	/** List of PropertyAccessException objects */
	private PropertyAccessException[] propertyAccessExceptions;


	/**
	 * Create a new PropertyAccessExceptionsException.
	 * @param propertyAccessExceptions the List of PropertyAccessExceptions
	 */
	public PropertyAccessExceptionsException(PropertyAccessException[] propertyAccessExceptions) {
		super("");
		if (ObjectUtils.isEmpty(propertyAccessExceptions)) {
			throw new IllegalArgumentException("At least 1 PropertyAccessException required");
		}
		this.propertyAccessExceptions = propertyAccessExceptions;
	}


	/**
	 * If this returns 0, no errors were encountered during binding.
	 */
	public int getExceptionCount() {
		return this.propertyAccessExceptions.length;
	}

	/**
	 * Return an array of the propertyAccessExceptions stored in this object.
	 * Will return the empty array (not null) if there were no errors.
	 */
	public PropertyAccessException[] getPropertyAccessExceptions() {
		return this.propertyAccessExceptions;
	}

	/**
	 * Return the exception for this field, or <code>null</code> if there isn't one.
	 */
	public PropertyAccessException getPropertyAccessException(String propertyName) {
		for (int i = 0; i < this.propertyAccessExceptions.length; i++) {
			PropertyAccessException pae = this.propertyAccessExceptions[i];
			if (propertyName.equals(pae.getPropertyChangeEvent().getPropertyName())) {
				return pae;
			}
		}
		return null;
	}


	public String getMessage() {
		return "" + getExceptionCount() + " errors";
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getClass().getName()).append(" (").append(getExceptionCount());
		sb.append(" errors); nested PropertyAccessExceptions are:");
		for (int i = 0; i < this.propertyAccessExceptions.length; i++) {
			sb.append('\n').append("PropertyAccessException ").append(i + 1).append(": ");
			sb.append(this.propertyAccessExceptions[i]);
		}
		return sb.toString();
	}

	public void printStackTrace(PrintStream ps) {
		ps.println(getClass().getName() + " (" + getExceptionCount() +
				" errors); nested PropertyAccessException details are:");
		for (int i = 0; i < this.propertyAccessExceptions.length; i++) {
			ps.println("PropertyAccessException " + (i + 1) + ":");
			this.propertyAccessExceptions[i].printStackTrace(ps);
		}
	}

	public void printStackTrace(PrintWriter pw) {
		pw.println(getClass().getName() + " (" + getExceptionCount() +
				" errors); nested PropertyAccessException details are:");
		for (int i = 0; i < this.propertyAccessExceptions.length; i++) {
			pw.println("PropertyAccessException " + (i + 1) + ":");
			this.propertyAccessExceptions[i].printStackTrace(pw);
		}
	}

	public boolean contains(Class exClass) {
		if (exClass == null) {
			return false;
		}
		if (exClass.isInstance(this)) {
			return true;
		}
		for (int i = 0; i < this.propertyAccessExceptions.length; i++) {
			PropertyAccessException pae = this.propertyAccessExceptions[i];
			if (pae.contains(exClass)) {
				return true;
			}
		}
		return false;
	}

}
