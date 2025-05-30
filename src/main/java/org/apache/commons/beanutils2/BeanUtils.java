/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.beanutils2;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.collections4.map.ConcurrentReferenceHashMap;

/**
 * <p>
 * Utility methods for populating JavaBeans properties via reflection.
 * </p>
 *
 * <p>
 * The implementations are provided by {@link BeanUtilsBean}. These static utility methods use the default instance. More sophisticated behavior can be provided
 * by using a {@code BeanUtilsBean} instance.
 * </p>
 *
 * @see BeanUtilsBean
 */
public final class BeanUtils {

    /** An empty class array */
    static final Class<?>[] EMPTY_CLASS_ARRAY = {};

    /** An empty object array */
    static final Object[] EMPTY_OBJECT_ARRAY = {};

    /**
     * <p>
     * Clones a bean based on the available property getters and setters, even if the bean class itself does not implement Cloneable.
     * </p>
     *
     * <p>
     * For more details see {@code BeanUtilsBean}.
     * </p>
     *
     * @param bean Bean to be cloned
     * @return the cloned bean
     * @throws IllegalAccessException    if the caller does not have access to the property accessor method
     * @throws InstantiationException    if a new instance of the bean's class cannot be instantiated
     * @throws InvocationTargetException if the property accessor method throws an exception
     * @throws NoSuchMethodException     if an accessor method for this property cannot be found
     * @see BeanUtilsBean#cloneBean
     */
    public static Object cloneBean(final Object bean) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        return BeanUtilsBean.getInstance().cloneBean(bean);
    }

    /**
     * <p>
     * Copies property values from the origin bean to the destination bean for all cases where the property names are the same.
     * </p>
     *
     * <p>
     * For more details see {@code BeanUtilsBean}.
     * </p>
     *
     * @param dest Destination bean whose properties are modified
     * @param orig Origin bean whose properties are retrieved
     * @throws IllegalAccessException    if the caller does not have access to the property accessor method
     * @throws IllegalArgumentException  if the {@code dest} or {@code orig</code> argument is null or if the <code>dest} property type is different from the
     *                                   source type and the relevant converter has not been registered.
     * @throws InvocationTargetException if the property accessor method throws an exception
     * @see BeanUtilsBean#copyProperties
     */
    public static void copyProperties(final Object dest, final Object orig) throws IllegalAccessException, InvocationTargetException {
        BeanUtilsBean.getInstance().copyProperties(dest, orig);
    }

    /**
     * <p>
     * Copy the specified property value to the specified destination bean, performing any type conversion that is required.
     * </p>
     *
     * <p>
     * For more details see {@code BeanUtilsBean}.
     * </p>
     *
     * @param bean  Bean on which setting is to be performed
     * @param name  Property name (can be nested/indexed/mapped/combo)
     * @param value Value to be set
     * @throws IllegalAccessException    if the caller does not have access to the property accessor method
     * @throws InvocationTargetException if the property accessor method throws an exception
     * @see BeanUtilsBean#copyProperty
     */
    public static void copyProperty(final Object bean, final String name, final Object value) throws IllegalAccessException, InvocationTargetException {
        BeanUtilsBean.getInstance().copyProperty(bean, name, value);
    }

    /**
     * Creates a cache.
     *
     * @param <K> the key type of the cache
     * @param <V> the value type of the cache
     * @return a new cache
     * @since 1.8.0
     */
    public static <K, V> Map<K, V> createCache() {
        return ConcurrentReferenceHashMap.<K, V>builder().get();
        // return new ConcurrentHashMap<>();
    }

    /**
     * <p>
     * Return the entire set of properties for which the specified bean provides a read method.
     * </p>
     *
     * <p>
     * For more details see {@code BeanUtilsBean}.
     * </p>
     *
     * @param bean Bean whose properties are to be extracted
     * @return Map of property descriptors
     * @throws IllegalAccessException    if the caller does not have access to the property accessor method
     * @throws InvocationTargetException if the property accessor method throws an exception
     * @throws NoSuchMethodException     if an accessor method for this property cannot be found
     * @see BeanUtilsBean#describe
     */
    public static Map<String, String> describe(final Object bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtilsBean.getInstance().describe(bean);
    }

    /**
     * <p>
     * Return the value of the specified array property of the specified bean, as a String array.
     * </p>
     *
     * <p>
     * For more details see {@code BeanUtilsBean}.
     * </p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Name of the property to be extracted
     * @return The array property value
     * @throws IllegalAccessException    if the caller does not have access to the property accessor method
     * @throws InvocationTargetException if the property accessor method throws an exception
     * @throws NoSuchMethodException     if an accessor method for this property cannot be found
     * @see BeanUtilsBean#getArrayProperty
     */
    public static String[] getArrayProperty(final Object bean, final String name)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtilsBean.getInstance().getArrayProperty(bean, name);
    }

    /**
     * <p>
     * Return the value of the specified indexed property of the specified bean, as a String.
     * </p>
     *
     * <p>
     * For more details see {@code BeanUtilsBean}.
     * </p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name {@code propertyname[index]} of the property value to be extracted
     * @return The indexed property's value, converted to a String
     * @throws IllegalAccessException    if the caller does not have access to the property accessor method
     * @throws InvocationTargetException if the property accessor method throws an exception
     * @throws NoSuchMethodException     if an accessor method for this property cannot be found
     * @see BeanUtilsBean#getIndexedProperty(Object, String)
     */
    public static String getIndexedProperty(final Object bean, final String name)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtilsBean.getInstance().getIndexedProperty(bean, name);

    }

    /**
     * Gets the value of the specified indexed property of the specified bean, as a String. The index is specified as a method parameter and must *not* be
     * included in the property name expression
     *
     * <p>
     * For more details see {@code BeanUtilsBean}.
     * </p>
     *
     * @param bean  Bean whose property is to be extracted
     * @param name  Simple property name of the property value to be extracted
     * @param index Index of the property value to be extracted
     * @return The indexed property's value, converted to a String
     * @throws IllegalAccessException    if the caller does not have access to the property accessor method
     * @throws InvocationTargetException if the property accessor method throws an exception
     * @throws NoSuchMethodException     if an accessor method for this property cannot be found
     * @see BeanUtilsBean#getIndexedProperty(Object, String, int)
     */
    public static String getIndexedProperty(final Object bean, final String name, final int index)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtilsBean.getInstance().getIndexedProperty(bean, name, index);

    }

    /**
     * <p>
     * Return the value of the specified indexed property of the specified bean, as a String.
     * </p>
     *
     * <p>
     * For more details see {@code BeanUtilsBean}.
     * </p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name {@code propertyname(index)} of the property value to be extracted
     * @return The mapped property's value, converted to a String
     * @throws IllegalAccessException    if the caller does not have access to the property accessor method
     * @throws InvocationTargetException if the property accessor method throws an exception
     * @throws NoSuchMethodException     if an accessor method for this property cannot be found
     * @see BeanUtilsBean#getMappedProperty(Object, String)
     */
    public static String getMappedProperty(final Object bean, final String name)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtilsBean.getInstance().getMappedProperty(bean, name);

    }

    /**
     * <p>
     * Return the value of the specified mapped property of the specified bean, as a String.
     * </p>
     *
     * <p>
     * For more details see {@code BeanUtilsBean}.
     * </p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Simple property name of the property value to be extracted
     * @param key  Lookup key of the property value to be extracted
     * @return The mapped property's value, converted to a String
     * @throws IllegalAccessException    if the caller does not have access to the property accessor method
     * @throws InvocationTargetException if the property accessor method throws an exception
     * @throws NoSuchMethodException     if an accessor method for this property cannot be found
     * @see BeanUtilsBean#getMappedProperty(Object, String, String)
     */
    public static String getMappedProperty(final Object bean, final String name, final String key)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtilsBean.getInstance().getMappedProperty(bean, name, key);

    }

    /**
     * <p>
     * Return the value of the (possibly nested) property of the specified name, for the specified bean, as a String.
     * </p>
     *
     * <p>
     * For more details see {@code BeanUtilsBean}.
     * </p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly nested name of the property to be extracted
     * @return The nested property's value, converted to a String
     * @throws IllegalAccessException    if the caller does not have access to the property accessor method
     * @throws IllegalArgumentException  if a nested reference to a property returns null
     * @throws InvocationTargetException if the property accessor method throws an exception
     * @throws NoSuchMethodException     if an accessor method for this property cannot be found
     * @see BeanUtilsBean#getNestedProperty
     */
    public static String getNestedProperty(final Object bean, final String name)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtilsBean.getInstance().getNestedProperty(bean, name);

    }

    /**
     * <p>
     * Return the value of the specified property of the specified bean, no matter which property reference format is used, as a String.
     * </p>
     *
     * <p>
     * For more details see {@code BeanUtilsBean}.
     * </p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly indexed and/or nested name of the property to be extracted
     * @return The property's value, converted to a String
     * @throws IllegalAccessException    if the caller does not have access to the property accessor method
     * @throws InvocationTargetException if the property accessor method throws an exception
     * @throws NoSuchMethodException     if an accessor method for this property cannot be found
     * @see BeanUtilsBean#getProperty
     */
    public static String getProperty(final Object bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtilsBean.getInstance().getProperty(bean, name);

    }

    /**
     * <p>
     * Return the value of the specified simple property of the specified bean, converted to a String.
     * </p>
     *
     * <p>
     * For more details see {@code BeanUtilsBean}.
     * </p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Name of the property to be extracted
     * @return The property's value, converted to a String
     * @throws IllegalAccessException    if the caller does not have access to the property accessor method
     * @throws InvocationTargetException if the property accessor method throws an exception
     * @throws NoSuchMethodException     if an accessor method for this property cannot be found
     * @see BeanUtilsBean#getSimpleProperty
     */
    public static String getSimpleProperty(final Object bean, final String name)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtilsBean.getInstance().getSimpleProperty(bean, name);

    }

    /**
     * <p>
     * Populate the JavaBeans properties of the specified bean, based on the specified name/value pairs.
     * </p>
     *
     * <p>
     * For more details see {@code BeanUtilsBean}.
     * </p>
     *
     * @param bean       JavaBean whose properties are being populated
     * @param properties Map keyed by property name, with the corresponding (String or String[]) value(s) to be set
     * @throws IllegalAccessException    if the caller does not have access to the property accessor method
     * @throws InvocationTargetException if the property accessor method throws an exception
     * @see BeanUtilsBean#populate
     */
    public static void populate(final Object bean, final Map<String, ? extends Object> properties) throws IllegalAccessException, InvocationTargetException {
        BeanUtilsBean.getInstance().populate(bean, properties);
    }

    /**
     * <p>
     * Set the specified property value, performing type conversions as required to conform to the type of the destination property.
     * </p>
     *
     * <p>
     * For more details see {@code BeanUtilsBean}.
     * </p>
     *
     * @param bean  Bean on which setting is to be performed
     * @param name  Property name (can be nested/indexed/mapped/combo)
     * @param value Value to be set
     * @throws IllegalAccessException    if the caller does not have access to the property accessor method
     * @throws InvocationTargetException if the property accessor method throws an exception
     * @see BeanUtilsBean#setProperty
     */
    public static void setProperty(final Object bean, final String name, final Object value) throws IllegalAccessException, InvocationTargetException {
        BeanUtilsBean.getInstance().setProperty(bean, name, value);
    }

    private BeanUtils() {
        // empty
    }
}
