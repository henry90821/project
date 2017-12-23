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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.collections.map.IdentityMap;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.collections.set.ListOrderedSet;
import org.apache.log4j.Logger;

/**
 * Factory for collections, being aware of JDK 1.4+ extended collections and Commons Collection 3.x's corresponding versions
 * for older JDKs. Mainly for internal use within the framework.
 * 
 * <p>
 * The goal of this class is to avoid runtime dependencies on JDK 1.4+ or Commons Collections 3.x, simply using the best
 * collection implementation that is available. Prefers JDK 1.4+ collection implementations to Commons Collections 3.x
 * versions, falling back to JDK 1.3 collections as worst case.
 * 
 * @author Juergen Hoeller
 * @since 1.1.1
 * @see #createLinkedSetIfPossible
 * @see #createLinkedMapIfPossible
 * @see #createLinkedCaseInsensitiveMapIfPossible
 * @see #createIdentityMapIfPossible
 */
public class CollectionFactory {

	private static final String COMMONS_COLLECTIONS_CLASS_NAME = "org.apache.commons.collections.map.LinkedMap";

	private static Logger logger = Logger.getLogger(CollectionFactory.class);

	private static boolean commonsCollections3xAvailable;

	static {
		// Check whether JDK 1.4+ collections and/or
		// Commons Collections 3.x are available.
		if (JdkVersion.getJavaVersion() >= JdkVersion.JDK_1_4) {
			logger.info("JDK 1.4+ collections available");
		}
		try {
			Class.forName(COMMONS_COLLECTIONS_CLASS_NAME);
			commonsCollections3xAvailable = true;
			logger.info("Commons Collections 3.x available");
		} catch (ClassNotFoundException ex) {
			commonsCollections3xAvailable = false;
		}
	}

	/**
	 * Create a linked set if possible: that is, if running on JDK >= 1.4 or if Commons Collections 3.x is available. Prefers
	 * a JDK 1.4+ LinkedHashSet to a Commons Collections 3.x ListOrderedSet.
	 * 
	 * @param initialCapacity
	 *            the initial capacity of the set
	 * @return the new set instance
	 * @see java.util.LinkedHashSet
	 * @see org.apache.commons.collections.set.ListOrderedSet
	 */
	public static Set createLinkedSetIfPossible(int initialCapacity) {
		if (JdkVersion.getJavaVersion() >= JdkVersion.JDK_1_4) {
			logger.debug("Creating [java.util.LinkedHashSet]");
			return Jdk14CollectionFactory.createLinkedHashSet(initialCapacity);
		} else if (commonsCollections3xAvailable) {
			logger.debug("Creating [org.apache.commons.collections.set.ListOrderedSet]");
			return CommonsCollectionFactory.createListOrderedSet(initialCapacity);
		} else {
			logger.debug("Falling back to [java.util.HashSet] for linked set");
			return new HashSet(initialCapacity);
		}
	}

	/**
	 * Create a linked map if possible: that is, if running on JDK >= 1.4 or if Commons Collections 3.x is available. Prefers
	 * a JDK 1.4+ LinkedHashMap to a Commons Collections 3.x LinkedMap.
	 * 
	 * @param initialCapacity
	 *            the initial capacity of the map
	 * @return the new map instance
	 * @see java.util.LinkedHashMap
	 * @see org.apache.commons.collections.map.LinkedMap
	 */
	public static Map createLinkedMapIfPossible(int initialCapacity) {
		if (JdkVersion.getJavaVersion() >= JdkVersion.JDK_1_4) {
			logger.debug("Creating [java.util.LinkedHashMap]");
			return Jdk14CollectionFactory.createLinkedHashMap(initialCapacity);
		} else if (commonsCollections3xAvailable) {
			logger.debug("Creating [org.apache.commons.collections.map.LinkedMap]");
			return CommonsCollectionFactory.createLinkedMap(initialCapacity);
		} else {
			logger.debug("Falling back to [java.util.HashMap] for linked map");
			return new HashMap(initialCapacity);
		}
	}

	/**
	 * Create a linked case-insensitive map if possible: if Commons Collections 3.x is available, a CaseInsensitiveMap with
	 * ListOrderedMap decorator will be created. Else, a JDK 1.4+ LinkedHashMap or plain HashMap will be used.
	 * 
	 * @param initialCapacity
	 *            the initial capacity of the map
	 * @return the new map instance
	 * @see org.apache.commons.collections.map.CaseInsensitiveMap
	 * @see org.apache.commons.collections.map.ListOrderedMap
	 */
	public static Map createLinkedCaseInsensitiveMapIfPossible(int initialCapacity) {
		if (commonsCollections3xAvailable) {
			logger.debug("Creating [org.apache.commons.collections.map.ListOrderedMap/CaseInsensitiveMap]");
			return CommonsCollectionFactory.createListOrderedCaseInsensitiveMap(initialCapacity);
		} else if (JdkVersion.getJavaVersion() >= JdkVersion.JDK_1_4) {
			logger.debug("Falling back to [java.util.LinkedHashMap] for linked case-insensitive map");
			return Jdk14CollectionFactory.createLinkedHashMap(initialCapacity);
		} else {
			logger.debug("Falling back to [java.util.HashMap] for linked case-insensitive map");
			return new HashMap(initialCapacity);
		}
	}

	/**
	 * Create an identity map if possible: that is, if running on JDK >= 1.4 or if Commons Collections 3.x is available.
	 * Prefers a JDK 1.4+ IdentityHashMap to a Commons Collections 3.x IdentityMap.
	 * 
	 * @param initialCapacity
	 *            the initial capacity of the map
	 * @return the new map instance
	 * @see java.util.IdentityHashMap
	 * @see org.apache.commons.collections.map.IdentityMap
	 */
	public static Map createIdentityMapIfPossible(int initialCapacity) {
		if (JdkVersion.getJavaVersion() >= JdkVersion.JDK_1_4) {
			logger.debug("Creating [java.util.IdentityHashMap]");
			return Jdk14CollectionFactory.createIdentityHashMap(initialCapacity);
		} else if (commonsCollections3xAvailable) {
			logger.debug("Creating [org.apache.commons.collections.map.IdentityMap]");
			return CommonsCollectionFactory.createIdentityMap(initialCapacity);
		} else {
			logger.debug("Falling back to [java.util.HashMap] for identity map");
			return new HashMap(initialCapacity);
		}
	}

	/**
	 * Create the most approximate collection for the given collection class.
	 * <p>
	 * Tries to create the given collection class. If that fails, an ArrayList, TreeSet or linked Set will be used as
	 * fallback for a List, SortedSet or Set, respectively.
	 * 
	 * @param collectionClass
	 *            the original collection class
	 * @param initialCapacity
	 *            the initial capacity
	 * @return the new collection instance
	 * @see java.util.ArrayList
	 * @see java.util.TreeSet
	 * @see #createLinkedSetIfPossible
	 */
	public static Collection createApproximateCollection(Class collectionClass, int initialCapacity) {
		Assert.notNull(collectionClass, "Collection class must not be null");

		if (!collectionClass.isInterface()) {
			try {
				return (Collection) collectionClass.newInstance();
			} catch (Exception ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Could not instantiate collection type [" + collectionClass.getName() + "]: "
							+ ex.getMessage());
				}
			}
		}
		if (List.class.isAssignableFrom(collectionClass)) {
			return new ArrayList(initialCapacity);
		} else if (SortedSet.class.isAssignableFrom(collectionClass)) {
			return new TreeSet();
		} else {

			return createLinkedSetIfPossible(initialCapacity);
		}
	}

	/**
	 * Create the most approximate map for the given map class.
	 * <p>
	 * Tries to create the given map class. If that fails, a TreeMap or linked Map will be used as fallback for a SortedMap
	 * or Map, respectively.
	 * 
	 * @param mapClass
	 *            the original map class
	 * @param initialCapacity
	 *            the initial capacity
	 * @return the new collection instance
	 * @see java.util.ArrayList
	 * @see java.util.TreeSet
	 * @see #createLinkedSetIfPossible
	 */
	public static Map createApproximateMap(Class mapClass, int initialCapacity) {
		Assert.notNull(mapClass, "Map class must not be null");
		if (!mapClass.isInterface()) {
			try {
				return (Map) mapClass.newInstance();
			} catch (Exception ex) {
				logger.debug("Could not instantiate map type [" + mapClass.getName() + "]: " + ex.getMessage());
			}
		}
		if (SortedMap.class.isAssignableFrom(mapClass)) {
			return new TreeMap();
		} else {
			return createLinkedMapIfPossible(initialCapacity);
		}
	}

	/**
	 * Actual creation of JDK 1.4+ Collections. In separate inner class to avoid runtime dependency on JDK 1.4+.
	 */
	private static abstract class Jdk14CollectionFactory {

		private static Set createLinkedHashSet(int initialCapacity) {
			return new LinkedHashSet(initialCapacity);
		}

		private static Map createLinkedHashMap(int initialCapacity) {
			return new LinkedHashMap(initialCapacity);
		}

		private static Map createIdentityHashMap(int initialCapacity) {
			return new IdentityHashMap(initialCapacity);
		}
	}

	/**
	 * Actual creation of Commons Collections. In separate inner class to avoid runtime dependency on Commons Collections
	 * 3.x.
	 */
	private static abstract class CommonsCollectionFactory {

		private static Set createListOrderedSet(int initialCapacity) {
			return ListOrderedSet.decorate(new HashSet(initialCapacity));
		}

		private static Map createLinkedMap(int initialCapacity) {
			// Commons Collections does not support initial capacity of 0.
			return new LinkedMap(initialCapacity == 0 ? 1 : initialCapacity);
		}

		private static Map createListOrderedCaseInsensitiveMap(int initialCapacity) {
			// Commons Collections does not support initial capacity of 0.
			return ListOrderedMap.decorate(new CaseInsensitiveMap(initialCapacity == 0 ? 1 : initialCapacity));
		}

		private static Map createIdentityMap(int initialCapacity) {
			// Commons Collections does not support initial capacity of 0.
			return new IdentityMap(initialCapacity == 0 ? 1 : initialCapacity);
		}
	}

}
