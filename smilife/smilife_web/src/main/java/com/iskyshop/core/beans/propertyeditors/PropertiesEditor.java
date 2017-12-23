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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class PropertiesEditor extends PropertyEditorSupport {
	
	/**
	 * Any of these characters, if they're first after whitespace or first
	 * on a line, mean that the line is a comment and should be ignored.
	 */
	private final static String COMMENT_MARKERS = "#!";


	/**
	 * Convert {@link String} into {@link Properties}, considering it as
	 * properties content.
	 * @param text the text to be so converted
	 */
	public void setAsText(String text) throws IllegalArgumentException {
		if (text == null) {
			throw new IllegalArgumentException("Cannot set Properties to null");
		}
		Properties props = new Properties();
		try {
			// must use the ISO-8859-1 encosing because Properties.load(stream) does
			props.load(new ByteArrayInputStream(text.getBytes("ISO-8859-1")));
			dropComments(props);
		}
		catch (IOException ex) {
			// Should never happen.
			throw new IllegalArgumentException(
					"Failed to parse [" + text + "] into Properties: " + ex.getMessage());
		}
		setValue(props);
	}

	/**
	 * Take {@link Properties} as-is; convert {@link Map} into <code>Properties</code>.
	 */
	public void setValue(Object value) {
		if (!(value instanceof Properties) && value instanceof Map) {
			Properties props = new Properties();
			props.putAll((Map) value);
			super.setValue(props);
		}
		else {
			super.setValue(value);
		}
	}

	/**
	 * Remove comment lines, even if they contain whitespace before the
	 * comment marker. This happens automatically on JDK >= 1.4, but we
	 * need to do this manually on JDK 1.3.
	 */
	private void dropComments(Properties props) {
		Iterator keys = props.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			// A comment line starts with one of our comment markers.
			if (key.length() > 0 && COMMENT_MARKERS.indexOf(key.charAt(0)) != -1) {
				keys.remove();
			}
		}
	}

}
