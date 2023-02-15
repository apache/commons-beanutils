/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.beanutils2.locale;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import org.apache.commons.beanutils2.BeanUtils;

/**
 * <p>Utility methods for populating JavaBeans properties
 * via reflection in a locale-dependent manner.</p>
 *
 * <p>The implementations for these methods are provided by {@code LocaleBeanUtilsBean}.
 * For more details see {@link LocaleBeanUtilsBean}.</p>
 */
public class LocaleBeanUtils extends BeanUtils {

    /**
     * <p>Converts the specified value to the required type.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @param type The Java type of target property
     * @param index The indexed subscript value (if any)
     * @param value The value to be converted
     * @return The converted value
     * @see LocaleBeanUtilsBean#convert(Class, int, Object)
     */
    protected static Object convert(final Class<?> type, final int index, final Object value) {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().convert(type, index, value);
    }

    /**
     * <p>Converts the specified value to the required type using the
     * specified conversion pattern.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @param type The Java type of target property
     * @param index The indexed subscript value (if any)
     * @param value The value to be converted
     * @param pattern The conversion pattern
     * @return The converted value
     * @see LocaleBeanUtilsBean#convert(Class, int, Object, String)
     */
    protected static Object convert(final Class<?> type, final int index, final Object value, final String pattern) {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().convert(type, index, value, pattern);
    }

    /**
     * <p>Calculate the property type.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @param target The bean
     * @param name The property name
     * @param propName The Simple name of target property
     * @return The property's type
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     *
     * @see LocaleBeanUtilsBean#definePropertyType(Object, String, String)
     */
    protected static Class<?> definePropertyType(final Object target, final String name, final String propName)
            throws IllegalAccessException, InvocationTargetException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().definePropertyType(target, name, propName);
    }

    /**
     * <p>Gets whether the pattern is localized or not.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @return {@code true} if pattern is localized,
     * otherwise {@code false}
     * @see LocaleBeanUtilsBean#getApplyLocalized()
     */
    public static boolean getApplyLocalized() {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getApplyLocalized();
    }

    /**
     * <p>Gets the locale used when no locale is passed.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @return the default locale
     * @see LocaleBeanUtilsBean#getDefaultLocale()
     */
    public static Locale getDefaultLocale() {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getDefaultLocale();
    }

    /**
     * Gets the value of the specified locale-sensitive indexed property
     * of the specified bean, as a String using the default conversion pattern of
     * the corresponding {@link LocaleConverter}.
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name {@code propertyname[index]} of the property value
     *  to be extracted
     * @return The indexed property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  property cannot be found
     *
     * @see LocaleBeanUtilsBean#getIndexedProperty(Object, String)
     */
    public static String getIndexedProperty(final Object bean, final String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name);
    }

    /**
     * <p>Return the value of the specified locale-sensitive indexed property
     * of the specified bean, as a String using the default conversion pattern of
     * the corresponding {@link LocaleConverter}.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Simple property name of the property value to be extracted
     * @param index Index of the property value to be extracted
     * @return The indexed property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  property cannot be found
     *
     * @see LocaleBeanUtilsBean#getIndexedProperty(Object, String, int)
     */
    public static String getIndexedProperty(final Object bean,
                                            final String name, final int index)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name, index);
    }

    /**
     * <p>Return the value of the specified locale-sensitive indexed property
     * of the specified bean, as a String using the specified conversion pattern.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Simple property name of the property value to be extracted
     * @param index Index of the property value to be extracted
     * @param pattern The conversion pattern
     * @return The indexed property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  property cannot be found
     *
     * @see LocaleBeanUtilsBean#getIndexedProperty(Object, String, int, String)
     */
    public static String getIndexedProperty(final Object bean,
                                            final String name, final int index, final String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name, index, pattern);
    }

    /**
     * <p>Return the value of the specified locale-sensitive indexed property
     * of the specified bean, as a String.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name {@code propertyname[index]} of the property value
     *  to be extracted
     * @param pattern The conversion pattern
     * @return The indexed property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  property cannot be found
     *
     * @see LocaleBeanUtilsBean#getIndexedProperty(Object, String, String)
     */
    public static String getIndexedProperty(final Object bean, final String name, final String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getIndexedProperty(bean, name, pattern);
    }

    /**
     * <p>Return the value of the specified locale-sensitive mapped property
     * of the specified bean, as a String using the default
     * conversion pattern of the corresponding {@link LocaleConverter}.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name {@code propertyname(index)} of the property value
     *  to be extracted
     * @return The mapped property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  property cannot be found
     *
     * @see LocaleBeanUtilsBean#getMappedProperty(Object, String)
     */
    public static String getMappedProperty(final Object bean, final String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getMappedProperty(bean, name);
    }

    /**
     * <p>Return the value of the specified mapped locale-sensitive property
     * of the specified bean, as a String
     * The key is specified as a method parameter and must *not* be included
     * in the property name expression.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Simple property name of the property value to be extracted
     * @param key Lookup key of the property value to be extracted
     * @return The mapped property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  property cannot be found
     *
     * @see LocaleBeanUtilsBean#getMappedProperty(Object, String, String)
     */
    public static String getMappedProperty(final Object bean,
                                           final String name, final String key)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getMappedProperty(bean, name, key);
    }

    /**
     * <p>Return the value of the specified mapped locale-sensitive property
     * of the specified bean, as a String using the specified conversion pattern.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Simple property name of the property value to be extracted
     * @param key Lookup key of the property value to be extracted
     * @param pattern The conversion pattern
     * @return The mapped property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  property cannot be found
     *
     * @see LocaleBeanUtilsBean#getMappedProperty(Object, String, String, String)
     */
    public static String getMappedProperty(final Object bean,
                                           final String name, final String key, final String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getMappedProperty(bean, name, key, pattern);
    }

    /**
     * <p>Return the value of the specified locale-sensitive mapped property
     * of the specified bean, as a String using the specified pattern.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name {@code propertyname(index)} of the property value
     *  to be extracted
     * @param pattern The conversion pattern
     * @return The mapped property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  property cannot be found
     *
     * @see LocaleBeanUtilsBean#getMappedPropertyLocale(Object, String, String)
     */
    public static String getMappedPropertyLocale(final Object bean, final String name, final String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getMappedPropertyLocale(bean, name, pattern);
    }

    /**
     * <p>Return the value of the (possibly nested) locale-sensitive property
     * of the specified name.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly nested name of the property to be extracted
     * @return The nested property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  property cannot be found
     *
     * @see LocaleBeanUtilsBean#getNestedProperty(Object, String)
     */
    public static String getNestedProperty(final Object bean, final String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getNestedProperty(bean, name);
    }

    /**
     * <p>Return the value of the (possibly nested) locale-sensitive property
     * of the specified name, for the specified bean,
     * as a String using the specified pattern.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly nested name of the property to be extracted
     * @param pattern The conversion pattern
     * @return The nested property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  property cannot be found
     *
     * @see LocaleBeanUtilsBean#getNestedProperty(Object, String, String)
     */
    public static String getNestedProperty(final Object bean, final String name, final String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getNestedProperty(bean, name, pattern);
    }

    /**
     * <p>Return the value of the specified locale-sensitive property
     * of the specified bean.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly indexed and/or nested name of the property
     *  to be extracted
     * @return The property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  property cannot be found
     *
     * @see LocaleBeanUtilsBean#getProperty(Object, String)
     */
    public static String getProperty(final Object bean, final String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getProperty(bean, name);
    }

    /**
     * <p>Return the value of the specified locale-sensitive property
     * of the specified bean.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly indexed and/or nested name of the property
     *  to be extracted
     * @param pattern The conversion pattern
     * @return The nested property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  property cannot be found
     *
     * @see LocaleBeanUtilsBean#getProperty(Object, String, String)
     */
    public static String getProperty(final Object bean, final String name, final String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getProperty(bean, name, pattern);
    }

    /**
     * <p>Return the value of the specified simple locale-sensitive property
     * of the specified bean, converted to a String using the default
     * conversion pattern of the corresponding {@link LocaleConverter}.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Name of the property to be extracted
     * @return The property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  property cannot be found
     *
     * @see LocaleBeanUtilsBean#getSimpleProperty(Object, String)
     */
    public static String getSimpleProperty(final Object bean, final String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getSimpleProperty(bean, name);
    }

    /**
     * <p>Return the value of the specified simple locale-sensitive property
     * of the specified bean, converted to a String using the specified
     * conversion pattern.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @param bean Bean whose property is to be extracted
     * @param name Name of the property to be extracted
     * @param pattern The conversion pattern
     * @return The property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  property cannot be found
     *
     * @see LocaleBeanUtilsBean#getSimpleProperty(Object, String, String)
     */
    public static String getSimpleProperty(final Object bean, final String name, final String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().getSimpleProperty(bean, name, pattern);
    }

    /**
     * <p>Invoke the setter method.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @param target The bean
     * @param propName The Simple name of target property
     * @param key The Mapped key value (if any)
     * @param index The indexed subscript value (if any)
     * @param newValue The value to be set
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     *
     * @see LocaleBeanUtilsBean#invokeSetter(Object, String, String, int, Object)
     */
    protected static void invokeSetter(final Object target, final String propName, final String key, final int index,
            final Object newValue)
            throws IllegalAccessException, InvocationTargetException {
       LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().invokeSetter(target, propName, key, index, newValue);
    }

    /**
     * <p>Sets whether the pattern is localized or not.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @param newApplyLocalized {@code true} if pattern is localized,
     * otherwise {@code false}
     * @see LocaleBeanUtilsBean#setApplyLocalized(boolean)
     */
    public static void setApplyLocalized(final boolean newApplyLocalized) {
        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().setApplyLocalized(newApplyLocalized);
    }

    /**
     * <p>Sets the locale used when no locale is passed.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @param locale the default locale
     * @see LocaleBeanUtilsBean#setDefaultLocale(Locale)
     */
    public static void setDefaultLocale(final Locale locale) {
        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().setDefaultLocale(locale);
    }

    /**
     * <p>Set the specified locale-sensitive property value, performing type
     * conversions as required to conform to the type of the destination property
     * using the default conversion pattern of the corresponding {@link LocaleConverter}.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @param bean Bean on which setting is to be performed
     * @param name Property name (can be nested/indexed/mapped/combo)
     * @param value Value to be set
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     *
     * @see LocaleBeanUtilsBean#setProperty(Object, String, Object)
     */
    public static void setProperty(final Object bean, final String name, final Object value)
            throws IllegalAccessException, InvocationTargetException {
        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().setProperty(bean, name, value);
    }

    /**
     * <p>Set the specified locale-sensitive property value, performing type
     * conversions as required to conform to the type of the destination
     * property using the specified conversion pattern.</p>
     *
     * <p>For more details see {@code LocaleBeanUtilsBean}</p>
     *
     * @param bean Bean on which setting is to be performed
     * @param name Property name (can be nested/indexed/mapped/combo)
     * @param value Value to be set
     * @param pattern The conversion pattern
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     *
     * @see LocaleBeanUtilsBean#setProperty(Object, String, Object, String)
     */
    public static void setProperty(final Object bean, final String name, final Object value, final String pattern)
            throws IllegalAccessException, InvocationTargetException {
        LocaleBeanUtilsBean.getLocaleBeanUtilsInstance().setProperty(bean, name, value, pattern);
     }
}

