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

import com.iskyshop.core.beans.ClassUtils;
import com.iskyshop.core.tools.StringUtils;

public class ClassArrayEditor extends PropertyEditorSupport {

	private final ClassLoader classLoader;

	/**
	 * Create a default <code>ClassEditor</code>, using the thread context ClassLoader.
	 */
	public ClassArrayEditor() {
		this(null);
	}

	/**
	 * Create a default <code>ClassArrayEditor</code>, using the given ClassLoader.
	 * 
	 * @param classLoader
	 *            the ClassLoader to use (or <code>null</code> for the thread context ClassLoader)
	 */
	public ClassArrayEditor(ClassLoader classLoader) {
		this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
	}

	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.hasText(text)) {
			String[] classNames = StringUtils.commaDelimitedListToStringArray(text);
			Class[] classes = new Class[classNames.length];
			for (int i = 0; i < classNames.length; i++) {
				String className = classNames[i];
				try {
					classes[i] = ClassUtils.forName(className.trim(), this.classLoader);
				} catch (ClassNotFoundException ex) {
					throw new IllegalArgumentException("Class not found: " + ex.getMessage());
				}
			}
			setValue(classes);
		} else {
			setValue(null);
		}
	}

	public String getAsText() {
		Class[] classes = (Class[]) getValue();
		if (classes == null) {
			return "";
		}
		return StringUtils.arrayToCommaDelimitedString(classes);
	}
}
