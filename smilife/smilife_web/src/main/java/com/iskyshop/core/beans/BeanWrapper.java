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

package com.iskyshop.core.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.iskyshop.core.beans.exception.BeansException;
import com.iskyshop.core.beans.exception.MethodInvocationException;
import com.iskyshop.core.beans.exception.NotReadablePropertyException;
import com.iskyshop.core.beans.exception.NotWritablePropertyException;
import com.iskyshop.core.beans.exception.NullValueInNestedPathException;
import com.iskyshop.core.beans.exception.PropertyException;
import com.iskyshop.core.beans.exception.TypeMismatchException;

public class BeanWrapper extends AbstractPropertyAccessor {

	/**
	 * We'll create a lot of these objects, so we don't want a new logger every time.
	 */
	private static Logger logger = Logger.getLogger(BeanWrapper.class);

	/** The wrapped object */
	private Object object;

	private String nestedPath = "";

	private Object rootObject;

	private TypeConverter typeConverterDelegate;

	/**
	 * Cached introspections results for this object, to prevent encountering the cost of JavaBeans introspection every time.
	 */
	private CachedIntrospectionResults cachedIntrospectionResults;

	/**
	 * Map with cached nested BeanWrappers: nested path -> BeanWrapper instance.
	 */
	private Map nestedBeanWrappers;

	/**
	 * Create new empty BeanWrapperImpl. Wrapped instance needs to be set afterwards. Registers default editors.
	 * 
	 * @see #setWrappedInstance
	 */
	public BeanWrapper() {
		this(true);
	}

	/**
	 * Create new empty BeanWrapperImpl. Wrapped instance needs to be set afterwards.
	 * 
	 * @param registerDefaultEditors
	 *            whether to register default editors (can be suppressed if the BeanWrapper won't need any type conversion)
	 * @see #setWrappedInstance
	 */
	public BeanWrapper(boolean registerDefaultEditors) {
		if (registerDefaultEditors) {
			registerDefaultEditors();
		}
		this.typeConverterDelegate = new TypeConverter(this);
	}

	/**
	 * Create new BeanWrapperImpl for the given object.
	 * 
	 * @param object
	 *            object wrapped by this BeanWrapper
	 */
	public BeanWrapper(Object object) {
		registerDefaultEditors();
		setWrappedInstance(object);
	}

	/**
	 * Create new BeanWrapperImpl, wrapping a new instance of the specified class.
	 * 
	 * @param clazz
	 *            class to instantiate and wrap
	 */
	public BeanWrapper(Class clazz) {
		registerDefaultEditors();
		setWrappedInstance(BeanUtils.instantiateClass(clazz));
	}

	/**
	 * Create new BeanWrapperImpl for the given object, registering a nested path that the object is in.
	 * 
	 * @param object
	 *            object wrapped by this BeanWrapper
	 * @param nestedPath
	 *            the nested path of the object
	 * @param rootObject
	 *            the root object at the top of the path
	 */
	public BeanWrapper(Object object, String nestedPath, Object rootObject) {
		registerDefaultEditors();
		setWrappedInstance(object, nestedPath, rootObject);
	}

	/**
	 * Create new BeanWrapperImpl for the given object, registering a nested path that the object is in.
	 * 
	 * @param object
	 *            object wrapped by this BeanWrapper
	 * @param nestedPath
	 *            the nested path of the object
	 * @param superBw
	 *            the containing BeanWrapper (must not be <code>null</code>)
	 */
	private BeanWrapper(Object object, String nestedPath, BeanWrapper superBw) {
		setWrappedInstance(object, nestedPath, superBw.getWrappedInstance());
		// setExtractOldValueForEditor(superBw.isExtractOldValueForEditor());
	}

	// ---------------------------------------------------------------------
	// Implementation of BeanWrapper interface
	// ---------------------------------------------------------------------

	/**
	 * Switch the target object, replacing the cached introspection results only if the class of the new object is different
	 * to that of the replaced object.
	 * 
	 * @param object
	 *            new target
	 */
	public void setWrappedInstance(Object object) {
		setWrappedInstance(object, "", null);
	}

	/**
	 * Switch the target object, replacing the cached introspection results only if the class of the new object is different
	 * to that of the replaced object.
	 * 
	 * @param object
	 *            new target
	 * @param nestedPath
	 *            the nested path of the object
	 * @param rootObject
	 *            the root object at the top of the path
	 */
	public void setWrappedInstance(Object object, String nestedPath, Object rootObject) {
		Assert.notNull(object, "Bean object must not be null");
		this.object = object;
		this.nestedPath = (nestedPath != null ? nestedPath : "");
		this.rootObject = (!"".equals(this.nestedPath) ? rootObject : object);
		this.nestedBeanWrappers = null;
		this.typeConverterDelegate = new TypeConverter(this, object);
		setIntrospectionClass(object.getClass());
	}

	public Object getWrappedInstance() {
		return this.object;
	}

	public Class getWrappedClass() {
		return this.object.getClass();
	}

	/**
	 * Return the nested path of the object wrapped by this BeanWrapper.
	 */
	public String getNestedPath() {
		return this.nestedPath;
	}

	/**
	 * Return the root object at the top of the path of this BeanWrapper.
	 * 
	 * @see #getNestedPath
	 */
	public Object getRootInstance() {
		return this.rootObject;
	}

	/**
	 * Return the class of the root object at the top of the path of this BeanWrapper.
	 * 
	 * @see #getNestedPath
	 */
	public Class getRootClass() {
		return (this.rootObject != null ? this.rootObject.getClass() : null);
	}

	/**
	 * Set the class to introspect. Needs to be called when the target object changes.
	 * 
	 * @param clazz
	 *            the class to introspect
	 */
	protected void setIntrospectionClass(Class clazz) {
		if (this.cachedIntrospectionResults == null || !this.cachedIntrospectionResults.getBeanClass().equals(clazz)) {
			this.cachedIntrospectionResults = CachedIntrospectionResults.forClass(clazz);
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors() {
		return this.cachedIntrospectionResults.getBeanInfo().getPropertyDescriptors();
	}

	public PropertyDescriptor getPropertyDescriptor(String propertyName) throws BeansException {
		Assert.notNull(propertyName, "Property name must not be null");
		PropertyDescriptor pd = getPropertyDescriptorInternal(propertyName);
		if (pd != null) {
			return pd;
		} else {
			throw new PropertyException(getRootClass(), this.nestedPath + propertyName, "No property '" + propertyName
					+ "' found");
		}
	}

	/**
	 * Internal version of getPropertyDescriptor: Returns <code>null</code> if not found rather than throwing an exception.
	 */
	protected PropertyDescriptor getPropertyDescriptorInternal(String propertyName) throws BeansException {
		Assert.state(this.object != null, "BeanWrapper does not hold a bean instance");
		BeanWrapper nestedBw = getBeanWrapperForPropertyPath(propertyName);
		return nestedBw.cachedIntrospectionResults.getPropertyDescriptor(getFinalPath(nestedBw, propertyName));
	}

	public boolean isReadableProperty(String propertyName) {
		Assert.notNull(propertyName, "Property name must not be null");
		try {
			PropertyDescriptor pd = getPropertyDescriptorInternal(propertyName);
			if (pd != null) {
				if (pd.getReadMethod() != null) {
					return true;
				}
			} else {
				// Maybe an indexed/mapped property...
				getPropertyValue(propertyName);
				return true;
			}
		} catch (PropertyException ex) {
			// Cannot be evaluated, so can't be readable.
		}
		return false;
	}

	public boolean isWritableProperty(String propertyName) {
		Assert.notNull(propertyName, "Property name must not be null");
		try {
			PropertyDescriptor pd = getPropertyDescriptorInternal(propertyName);
			if (pd != null) {
				if (pd.getWriteMethod() != null) {
					return true;
				}
			} else {
				// Maybe an indexed/mapped property...
				getPropertyValue(propertyName);
				return true;
			}
		} catch (PropertyException ex) {
			// Cannot be evaluated, so can't be writable.
		}
		return false;
	}

	public Class getPropertyType(String propertyName) throws BeansException {
		try {
			PropertyDescriptor pd = getPropertyDescriptorInternal(propertyName);
			if (pd != null) {
				return pd.getPropertyType();
			} else {
				// Maybe an indexed/mapped property...
				Object value = getPropertyValue(propertyName);
				if (value != null) {
					return value.getClass();
				}
				// Check to see if there is a custom editor,
				// which might give an indication on the desired target type.
				Class editorType = guessPropertyTypeFromEditors(propertyName);
				if (editorType != null) {
					return editorType;
				}
			}
		} catch (PropertyException ex) {
			// Consider as not determinable.
		}
		return null;
	}

	// ---------------------------------------------------------------------
	// Implementation of TypeConverter interface
	// ---------------------------------------------------------------------

	public Object convertIfNecessary(Object value, Class requiredType) throws TypeMismatchException {
		return convertIfNecessary(value, requiredType, null);
	}

	public Object convertIfNecessary(Object value, Class requiredType, MethodParameter methodParam)
			throws TypeMismatchException {
		try {
			return this.typeConverterDelegate.convertIfNecessary(value, requiredType, methodParam);
		} catch (IllegalArgumentException ex) {
			throw new TypeMismatchException(value, requiredType, ex);
		}
	}

	// ---------------------------------------------------------------------
	// Implementation methods
	// ---------------------------------------------------------------------

	/**
	 * Get the last component of the path. Also works if not nested.
	 * 
	 * @param bw
	 *            BeanWrapper to work on
	 * @param nestedPath
	 *            property path we know is nested
	 * @return last component of the path (the property on the target bean)
	 */
	private String getFinalPath(BeanWrapper bw, String nestedPath) {
		if (bw == this) {
			return nestedPath;
		}
		return nestedPath.substring(PropertyAccessorUtils.getLastNestedPropertySeparatorIndex(nestedPath) + 1);
	}

	/**
	 * Recursively navigate to return a BeanWrapper for the nested property path.
	 * 
	 * @param propertyPath
	 *            property property path, which may be nested
	 * @return a BeanWrapper for the target bean
	 */
	protected BeanWrapper getBeanWrapperForPropertyPath(String propertyPath) throws BeansException {
		int pos = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(propertyPath);
		// handle nested properties recursively
		if (pos > -1) {
			String nestedProperty = propertyPath.substring(0, pos);
			String nestedPath = propertyPath.substring(pos + 1);
			BeanWrapper nestedBw = getNestedBeanWrapper(nestedProperty);
			return nestedBw.getBeanWrapperForPropertyPath(nestedPath);
		} else {
			return this;
		}
	}

	/**
	 * Retrieve a BeanWrapper for the given nested property. Create a new one if not found in the cache.
	 * <p>
	 * Note: Caching nested BeanWrappers is necessary now, to keep registered custom editors for nested properties.
	 * 
	 * @param nestedProperty
	 *            property to create the BeanWrapper for
	 * @return the BeanWrapper instance, either cached or newly created
	 */
	private BeanWrapper getNestedBeanWrapper(String nestedProperty) throws BeansException {
		if (this.nestedBeanWrappers == null) {
			this.nestedBeanWrappers = new HashMap();
		}
		// Get Value of bean property-
		PropertyTokenHolder tokens = getPropertyNameTokens(nestedProperty);
		String canonicalName = tokens.canonicalName;
		Object propertyValue = getPropertyValue(tokens);
		if (propertyValue == null) {
			throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + canonicalName);
		}

		// Lookup cached sub-BeanWrapper, create new one if not found.
		BeanWrapper nestedBw = (BeanWrapper) this.nestedBeanWrappers.get(canonicalName);
		if (nestedBw == null || nestedBw.getWrappedInstance() != propertyValue) {
			if (logger.isDebugEnabled()) {
				logger.debug("Creating new nested BeanWrapper for property '" + canonicalName + "'");
			}
			nestedBw = newNestedBeanWrapper(propertyValue, this.nestedPath + canonicalName + NESTED_PROPERTY_SEPARATOR);
			// Inherit all type-specific PropertyEditors.
			copyDefaultEditorsTo(nestedBw);
			copyCustomEditorsTo(nestedBw, canonicalName);
			this.nestedBeanWrappers.put(canonicalName, nestedBw);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Using cached nested BeanWrapper for property '" + canonicalName + "'");
			}
		}
		return nestedBw;
	}

	/**
	 * Create a new nested BeanWrapper instance.
	 * <p>
	 * Default implementation creates a BeanWrapperImpl instance. Can be overridden in subclasses to create a BeanWrapperImpl
	 * subclass.
	 * 
	 * @param object
	 *            object wrapped by this BeanWrapper
	 * @param nestedPath
	 *            the nested path of the object
	 * @return the nested BeanWrapper instance
	 */
	protected BeanWrapper newNestedBeanWrapper(Object object, String nestedPath) {
		return new BeanWrapper(object, nestedPath, this);
	}

	/**
	 * Parse the given property name into the corresponding property name tokens.
	 * 
	 * @param propertyName
	 *            the property name to parse
	 * @return representation of the parsed property tokens
	 */
	private PropertyTokenHolder getPropertyNameTokens(String propertyName) {
		PropertyTokenHolder tokens = new PropertyTokenHolder();
		String actualName = null;
		List keys = new ArrayList(2);
		int searchIndex = 0;
		while (searchIndex != -1) {
			int keyStart = propertyName.indexOf(PROPERTY_KEY_PREFIX, searchIndex);
			searchIndex = -1;
			if (keyStart != -1) {
				int keyEnd = propertyName.indexOf(PROPERTY_KEY_SUFFIX, keyStart + PROPERTY_KEY_PREFIX.length());
				if (keyEnd != -1) {
					if (actualName == null) {
						actualName = propertyName.substring(0, keyStart);
					}
					String key = propertyName.substring(keyStart + PROPERTY_KEY_PREFIX.length(), keyEnd);
					if ((key.startsWith("'") && key.endsWith("'")) || (key.startsWith("\"") && key.endsWith("\""))) {
						key = key.substring(1, key.length() - 1);
					}
					keys.add(key);
					searchIndex = keyEnd + PROPERTY_KEY_SUFFIX.length();
				}
			}
		}
		tokens.actualName = (actualName != null ? actualName : propertyName);
		tokens.canonicalName = tokens.actualName;
		if (!keys.isEmpty()) {
			tokens.canonicalName += PROPERTY_KEY_PREFIX
					+ StringUtils.collectionToDelimitedString(keys, PROPERTY_KEY_SUFFIX + PROPERTY_KEY_PREFIX)
					+ PROPERTY_KEY_SUFFIX;
			tokens.keys = StringUtils.toStringArray(keys);
		}
		return tokens;
	}

	// ---------------------------------------------------------------------
	// Implementation of PropertyAccessor interface
	// ---------------------------------------------------------------------

	public Object getPropertyValue(String propertyName) throws BeansException {
		BeanWrapper nestedBw = getBeanWrapperForPropertyPath(propertyName);
		PropertyTokenHolder tokens = getPropertyNameTokens(getFinalPath(nestedBw, propertyName));
		return nestedBw.getPropertyValue(tokens);
	}

	private Object getPropertyValue(PropertyTokenHolder tokens) throws BeansException {
		String propertyName = tokens.canonicalName;
		String actualName = tokens.actualName;
		PropertyDescriptor pd = getPropertyDescriptorInternal(tokens.actualName);
		if (pd == null || pd.getReadMethod() == null) {
			throw new NotReadablePropertyException(getRootClass(), this.nestedPath + propertyName);
		}
		Method readMethod = pd.getReadMethod();
		logger.debug("About to invoke read method [" + readMethod + "] on object of class ["
				+ this.object.getClass().getName() + "]");

		try {
			if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
				readMethod.setAccessible(true);
			}
			Object value = readMethod.invoke(this.object, (Object[]) null);

			if (tokens.keys != null) {
				// apply indexes and map keys
				for (int i = 0; i < tokens.keys.length; i++) {
					String key = tokens.keys[i];
					if (value == null) {
						throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + propertyName,
								"Cannot access indexed value of property referenced in indexed " + "property path '"
										+ propertyName + "': returned null");
					} else if (value.getClass().isArray()) {
						value = Array.get(value, Integer.parseInt(key));
					} else if (value instanceof List) {
						List list = (List) value;
						value = list.get(Integer.parseInt(key));
					} else if (value instanceof Set) {
						// Apply index to Iterator in case of a Set.
						Set set = (Set) value;
						int index = Integer.parseInt(key);
						if (index < 0 || index >= set.size()) {
							throw new PropertyException(getRootClass(), this.nestedPath + propertyName,
									"Cannot get element with index " + index + " from Set of size " + set.size()
											+ ", accessed using property path '" + propertyName + "'");
						}
						Iterator it = set.iterator();
						for (int j = 0; it.hasNext(); j++) {
							Object elem = it.next();
							if (j == index) {
								value = elem;
								break;
							}
						}
					} else if (value instanceof Map) {
						Map map = (Map) value;
						Class mapKeyType = null;
						if (JdkVersion.getJavaVersion() >= JdkVersion.JDK_1_5) {
							mapKeyType = GenericCollectionTypeResolver.getMapKeyReturnType(pd.getReadMethod());
						}
						// IMPORTANT: Do not pass full property name in here - property editors
						// must not kick in for map keys but rather only for map values.
						Object convertedMapKey = this.typeConverterDelegate.convertIfNecessary(null, null, key, mapKeyType);
						// Pass full property name and old value in here, since we want full
						// conversion ability for map values.
						value = map.get(convertedMapKey);
					} else {
						throw new PropertyException(getRootClass(), this.nestedPath + propertyName,
								"Property referenced in indexed property path '" + propertyName
										+ "' is neither an array nor a List nor a Set nor a Map; returned value was ["
										+ value + "]");
					}
				}
			}

			return value;
		} catch (InvocationTargetException ex) {
			throw new PropertyException(getRootClass(), this.nestedPath + propertyName, "Getter for property '" + actualName
					+ "' threw exception", ex);
		} catch (IllegalAccessException ex) {
			throw new PropertyException(getRootClass(), this.nestedPath + propertyName, "Illegal attempt to get property '"
					+ actualName + "' threw exception", ex);
		} catch (IndexOutOfBoundsException ex) {
			throw new PropertyException(getRootClass(), this.nestedPath + propertyName,
					"Index of out of bounds in property path '" + propertyName + "'", ex);
		} catch (NumberFormatException ex) {
			throw new PropertyException(getRootClass(), this.nestedPath + propertyName, "Invalid index in property path '"
					+ propertyName + "'", ex);
		}
	}

	public void setPropertyValue(String propertyName, Object value) throws BeansException {
		BeanWrapper nestedBw = null;
		try {
			nestedBw = getBeanWrapperForPropertyPath(propertyName);
		} catch (NotReadablePropertyException ex) {
			throw new NotWritablePropertyException(getRootClass(), this.nestedPath + propertyName,
					"Nested property in path '" + propertyName + "' does not exist", ex);
		}
		PropertyTokenHolder tokens = getPropertyNameTokens(getFinalPath(nestedBw, propertyName));
		nestedBw.setPropertyValue(tokens, value);
	}

	private void setPropertyValue(PropertyTokenHolder tokens, Object newValue) throws BeansException {
		String propertyName = tokens.canonicalName;
		if (tokens.keys != null) {
			// Apply indexes and map keys: fetch value for all keys but the last one.
			PropertyTokenHolder getterTokens = new PropertyTokenHolder();
			getterTokens.canonicalName = tokens.canonicalName;
			getterTokens.actualName = tokens.actualName;
			getterTokens.keys = new String[tokens.keys.length - 1];
			System.arraycopy(tokens.keys, 0, getterTokens.keys, 0, tokens.keys.length - 1);
			Object propValue = null;
			try {
				propValue = getPropertyValue(getterTokens);
			} catch (NotReadablePropertyException ex) {
				throw new NotWritablePropertyException(getRootClass(), this.nestedPath + propertyName,
						"Cannot access indexed value in property referenced " + "in indexed property path '" + propertyName
								+ "'", ex);
			}
			// Set value for last key.
			String key = tokens.keys[tokens.keys.length - 1];
			if (propValue == null) {
				throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + propertyName,
						"Cannot access indexed value in property referenced " + "in indexed property path '" + propertyName
								+ "': returned null");
			} else if (propValue.getClass().isArray()) {
				Class requiredType = propValue.getClass().getComponentType();
				int arrayIndex = Integer.parseInt(key);
				Object oldValue = null;
				try {
					Object convertedValue = this.typeConverterDelegate.convertIfNecessary(propertyName, oldValue, newValue,
							requiredType);
					Array.set(propValue, Integer.parseInt(key), convertedValue);
				} catch (IllegalArgumentException ex) {
					PropertyChangeEvent pce = new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName,
							oldValue, newValue);
					throw new TypeMismatchException(pce, requiredType, ex);
				} catch (IndexOutOfBoundsException ex) {
					throw new PropertyException(getRootClass(), this.nestedPath + propertyName,
							"Invalid array index in property path '" + propertyName + "'", ex);
				}
			} else if (propValue instanceof List) {
				PropertyDescriptor pd = getPropertyDescriptorInternal(tokens.actualName);
				Class requiredType = null;
				if (JdkVersion.getJavaVersion() >= JdkVersion.JDK_1_5) {
					requiredType = GenericCollectionTypeResolver.getCollectionReturnType(pd.getReadMethod());
				}
				List list = (List) propValue;
				int index = Integer.parseInt(key);
				Object oldValue = null;
				try {
					Object convertedValue = this.typeConverterDelegate.convertIfNecessary(propertyName, oldValue, newValue,
							requiredType);
					if (index < list.size()) {
						list.set(index, convertedValue);
					} else if (index >= list.size()) {
						for (int i = list.size(); i < index; i++) {
							try {
								list.add(null);
							} catch (NullPointerException ex) {
								throw new PropertyException(getRootClass(), this.nestedPath + propertyName,
										"Cannot set element with index " + index + " in List of size " + list.size()
												+ ", accessed using property path '" + propertyName
												+ "': List does not support filling up gaps with null elements");
							}
						}
						list.add(convertedValue);
					}
				} catch (IllegalArgumentException ex) {
					PropertyChangeEvent pce = new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName,
							oldValue, newValue);
					throw new TypeMismatchException(pce, requiredType, ex);
				}
			} else if (propValue instanceof Map) {
				PropertyDescriptor pd = getPropertyDescriptorInternal(tokens.actualName);
				Class mapKeyType = null;
				Class mapValueType = null;
				if (JdkVersion.getJavaVersion() >= JdkVersion.JDK_1_5) {
					mapKeyType = GenericCollectionTypeResolver.getMapKeyReturnType(pd.getReadMethod());
					mapValueType = GenericCollectionTypeResolver.getMapValueReturnType(pd.getReadMethod());
				}
				Map map = (Map) propValue;
				Object oldValue = null;
				Object convertedMapKey = null;
				Object convertedMapValue = null;
				try {
					// IMPORTANT: Do not pass full property name in here - property editors
					// must not kick in for map keys but rather only for map values.
					convertedMapKey = this.typeConverterDelegate.convertIfNecessary(null, null, key, mapKeyType);
				} catch (IllegalArgumentException ex) {
					PropertyChangeEvent pce = new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName,
							oldValue, newValue);
					throw new TypeMismatchException(pce, mapKeyType, ex);
				}
				try {
					// Pass full property name and old value in here, since we want full
					// conversion ability for map values.
					convertedMapValue = this.typeConverterDelegate.convertIfNecessary(propertyName, oldValue, newValue,
							mapValueType);
				} catch (IllegalArgumentException ex) {
					PropertyChangeEvent pce = new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName,
							oldValue, newValue);
					throw new TypeMismatchException(pce, mapValueType, ex);
				}
				map.put(convertedMapKey, convertedMapValue);
			} else {
				throw new PropertyException(getRootClass(), this.nestedPath + propertyName,
						"Property referenced in indexed property path '" + propertyName
								+ "' is neither an array nor a List nor a Map; returned value was [" + newValue + "]");
			}
		}

		else {
			PropertyDescriptor pd = getPropertyDescriptorInternal(propertyName);
			if (pd == null || pd.getWriteMethod() == null) {
				PropertyMatches matches = PropertyMatches.forProperty(propertyName, getRootClass());
				throw new NotWritablePropertyException(getRootClass(), this.nestedPath + propertyName,
						matches.buildErrorMessage(), matches.getPossibleMatches());
			}

			Method readMethod = pd.getReadMethod();
			Method writeMethod = pd.getWriteMethod();
			Object oldValue = null;

			if (readMethod != null) {
				if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
					readMethod.setAccessible(true);
				}
				try {
					oldValue = readMethod.invoke(this.object, new Object[0]);
				} catch (Exception ex) {
					if (logger.isDebugEnabled()) {
						logger.debug("Could not read previous value of property '" + this.nestedPath + propertyName + "'",
								ex);
					}
				}
			}

			try {
				boolean isProx = false;
				if (newValue != null && newValue.getClass().getSimpleName().indexOf("$$Enhancer") > 0)
					isProx = true;
				// 增加对CGLIB增强的判断，凡是增强后的类型都不进行延迟加载
				Object convertedValue = isProx ? newValue : this.typeConverterDelegate.convertIfNecessary(oldValue,
						newValue, pd);
				if (pd.getPropertyType().isPrimitive() && (convertedValue == null || "".equals(convertedValue))) {
					throw new IllegalArgumentException("Invalid value [" + newValue + "] for property '" + pd.getName()
							+ "' of primitive type [" + pd.getPropertyType() + "]");
				}

				if (logger.isDebugEnabled()) {
					logger.debug("About to invoke write method [" + writeMethod + "] on object of class ["
							+ this.object.getClass().getName() + "]");
				}
				if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
					writeMethod.setAccessible(true);
				}
				writeMethod.invoke(this.object, new Object[] { convertedValue });
				if (logger.isDebugEnabled()) {
					logger.debug("Invoked write method [" + writeMethod + "] with value of type ["
							+ pd.getPropertyType().getName() + "]");
				}
			} catch (InvocationTargetException ex) {
				PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(this.rootObject, this.nestedPath
						+ propertyName, oldValue, newValue);
				if (ex.getTargetException() instanceof ClassCastException) {
					throw new TypeMismatchException(propertyChangeEvent, pd.getPropertyType(), ex.getTargetException());
				} else {
					throw new MethodInvocationException(propertyChangeEvent, ex.getTargetException());
				}
			} catch (IllegalArgumentException ex) {
				PropertyChangeEvent pce = new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName, oldValue,
						newValue);
				throw new TypeMismatchException(pce, pd.getPropertyType(), ex);
			} catch (IllegalAccessException ex) {
				PropertyChangeEvent pce = new PropertyChangeEvent(this.rootObject, this.nestedPath + propertyName, oldValue,
						newValue);
				throw new MethodInvocationException(pce, ex);
			}
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("BeanWrapperImpl: wrapping class [");
		sb.append(getWrappedClass().getName()).append("]");
		return sb.toString();
	}

	// ---------------------------------------------------------------------
	// Inner class for internal use
	// ---------------------------------------------------------------------

	private static class PropertyTokenHolder {

		private String canonicalName;

		private String actualName;

		private String[] keys;
	}

}
