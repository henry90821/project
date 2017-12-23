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

package com.iskyshop.core.beans.propertyeditors;

import java.beans.PropertyEditorSupport;

import com.iskyshop.core.tools.StringUtils;

public class StringTrimmerEditor extends PropertyEditorSupport {

	private final String charsToDelete;

	private final boolean emptyAsNull;


	/**
	 * Create a new StringTrimmerEditor instance.
	 * @param emptyAsNull <code>true</code> if an empty string is to be transformed into <code>null</code>
	 */
	public StringTrimmerEditor(boolean emptyAsNull) {
		this.charsToDelete = null;
		this.emptyAsNull = emptyAsNull;
	}

	/**
	 * Create a new StringTrimmerEditor instance.
	 * @param charsToDelete a set of characters to delete, in addition to
	 * trimming an input String. Useful for deleting unwanted line breaks.
	 * E.g. "\r\n\f" will delete all new lines and line feeds in a String.
	 * @param emptyAsNull <code>true</code> if an empty string is to be transformed into <code>null</code> 
	 */
	public StringTrimmerEditor(String charsToDelete, boolean emptyAsNull) {
		this.charsToDelete = charsToDelete;
		this.emptyAsNull = emptyAsNull;
	}


	public void setAsText(String text) {
		if (text == null) {
			setValue(null);
		}
		else {
			String value = text.trim();
			if (this.charsToDelete != null) {
				value = StringUtils.deleteAny(value, this.charsToDelete);
			}
			if (this.emptyAsNull && "".equals(value)) {
				setValue(null);
			}
			else {
				setValue(value);
			}
		}
	}

	public String getAsText() {
		Object value = getValue();
		return (value != null ? value.toString() : "");
	}

}
