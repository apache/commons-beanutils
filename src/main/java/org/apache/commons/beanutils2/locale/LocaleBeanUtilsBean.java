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

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import org.apache.commons.beanutils2.BeanUtilsBean;
import org.apache.commons.beanutils2.ContextClassLoaderLocal;
import org.apache.commons.beanutils2.ConvertUtils;
import org.apache.commons.beanutils2.ConvertUtilsBean;
import org.apache.commons.beanutils2.DynaBean;
import org.apache.commons.beanutils2.DynaClass;
import org.apache.commons.beanutils2.DynaProperty;
import org.apache.commons.beanutils2.MappedPropertyDescriptor;
import org.apache.commons.beanutils2.PropertyUtilsBean;
import org.apache.commons.beanutils2.expression.Resolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Utility methods for populating JavaBeans properties
 * via reflection in a locale-dependent manner.</p>
 *
 * @since 1.7
 */
public class LocaleBeanUtilsBean extends BeanUtilsBean {

    /**
     * Contains {@code LocaleBeanUtilsBean} instances indexed by context classloader.
     */
    private static final ContextClassLoaderLocal<LocaleBeanUtilsBean>
            LOCALE_BEANS_BY_CLASSLOADER = new ContextClassLoaderLocal<LocaleBeanUtilsBean>() {
                        // Creates the default instance used when the context classloader is unavailable
                        @Override
                        protected LocaleBeanUtilsBean initialValue() {
                            return new LocaleBeanUtilsBean();
                        }
                    };

     /** All logging goes through this logger */
    private static final Log LOG = LogFactory.getLog(LocaleBeanUtilsBean.class);

    /**
      * Gets singleton instance
      *
      * @return the singleton instance
      */
     public static LocaleBeanUtilsBean getLocaleBeanUtilsInstance() {
        return LOCALE_BEANS_BY_CLASSLOADER.get();
     }

    /**
     * Sets the instance which provides the functionality for {@link LocaleBeanUtils}.
     * This is a pseudo-singleton - an single instance is provided per (thread) context classloader.
     * This mechanism provides isolation for web apps deployed in the same container.
     *
     * @param newInstance a new singleton instance
     */
    public static void setInstance(final LocaleBeanUtilsBean newInstance) {
        LOCALE_BEANS_BY_CLASSLOADER.set(newInstance);
    }

    /** Convertor used by this class */
    private final LocaleConvertUtilsBean localeConvertUtils;

    /** Construct instance with standard conversion bean */
    public LocaleBeanUtilsBean() {
        this.localeConvertUtils = new LocaleConvertUtilsBean();
    }

    /**
     * Construct instance that uses given locale conversion
     *
     * @param localeConvertUtils use this {@code localeConvertUtils} to perform
     * conversions
     */
    public LocaleBeanUtilsBean(final LocaleConvertUtilsBean localeConvertUtils) {
        this.localeConvertUtils = localeConvertUtils;
    }

    /**
     * Construct instance that uses given locale conversion
     *
     * @param localeConvertUtils use this {@code localeConvertUtils} to perform
     * conversions
     * @param convertUtilsBean use this for standard conversions
     * @param propertyUtilsBean use this for property conversions
     */
    public LocaleBeanUtilsBean(
                            final LocaleConvertUtilsBean localeConvertUtils,
                            final ConvertUtilsBean convertUtilsBean,
                            final PropertyUtilsBean propertyUtilsBean) {
        super(convertUtilsBean, propertyUtilsBean);
        this.localeConvertUtils = localeConvertUtils;
    }

    /**
     *  Convert the specified value to the required type.
     *
     * @param type The Java type of target property
     * @param index The indexed subscript value (if any)
     * @param value The value to be converted
     * @return The converted value
     */
    protected Object convert(final Class<?> type, final int index, final Object value) {
        Object newValue = null;

        if (type.isArray() && index < 0) { // Scalar value into array
            if (value instanceof String) {
                final String[] values = new String[1];
                values[0] = (String) value;
                newValue = ConvertUtils.convert(values, type);
            }
            else if (value instanceof String[]) {
                newValue = ConvertUtils.convert((String[]) value, type);
            }
            else {
                newValue = value;
            }
        }
        else if (type.isArray()) {         // Indexed value into array
            if (value instanceof String) {
                newValue = ConvertUtils.convert((String) value,
                        type.getComponentType());
            }
            else if (value instanceof String[]) {
                newValue = ConvertUtils.convert(((String[]) value)[0],
                        type.getComponentType());
            }
            else {
                newValue = value;
            }
        } else if (value instanceof String) {
            newValue = ConvertUtils.convert((String) value, type);
        }
        else if (value instanceof String[]) {
            newValue = ConvertUtils.convert(((String[]) value)[0],
                    type);
        }
        else {
            newValue = value;
        }
        return newValue;
    }

    /**
     * Convert the specified value to the required type using the
     * specified conversion pattern.
     *
     * @param type The Java type of target property
     * @param index The indexed subscript value (if any)
     * @param value The value to be converted
     * @param pattern The conversion pattern
     * @return The converted value
     */
    protected Object convert(final Class<?> type, final int index, final Object value, final String pattern) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Converting value '" + value + "' to type:" + type);
        }

        Object newValue = null;

        if (type.isArray() && index < 0) { // Scalar value into array
            if (value instanceof String) {
                final String[] values = new String[1];
                values[0] = (String) value;
                newValue = getLocaleConvertUtils().convert(values, type, pattern);
            }
            else if (value instanceof String[]) {
                newValue = getLocaleConvertUtils().convert((String[]) value, type, pattern);
            }
            else {
                newValue = value;
            }
        }
        else if (type.isArray()) {         // Indexed value into array
            if (value instanceof String) {
                newValue = getLocaleConvertUtils().convert((String) value,
                        type.getComponentType(), pattern);
            }
            else if (value instanceof String[]) {
                newValue = getLocaleConvertUtils().convert(((String[]) value)[0],
                        type.getComponentType(), pattern);
            }
            else {
                newValue = value;
            }
        } else if (value instanceof String) {
            newValue = getLocaleConvertUtils().convert((String) value, type, pattern);
        }
        else if (value instanceof String[]) {
            newValue = getLocaleConvertUtils().convert(((String[]) value)[0],
                    type, pattern);
        }
        else {
            newValue = value;
        }
        return newValue;
    }

    /**
     * Calculate the property type.
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
     */
    protected Class<?> definePropertyType(final Object target, final String name, final String propName)
            throws IllegalAccessException, InvocationTargetException {
        Class<?> type = null;               // Java type of target property

        if (target instanceof DynaBean) {
            final DynaClass dynaClass = ((DynaBean) target).getDynaClass();
            final DynaProperty dynaProperty = dynaClass.getDynaProperty(propName);
            if (dynaProperty == null) {
                return null; // Skip this property setter
            }
            type = dynaProperty.getType();
        }
        else {
            PropertyDescriptor descriptor = null;
            try {
                descriptor =
                        getPropertyUtils().getPropertyDescriptor(target, name);
                if (descriptor == null) {
                    return null; // Skip this property setter
                }
            }
            catch (final NoSuchMethodException e) {
                return null; // Skip this property setter
            }
            if (descriptor instanceof MappedPropertyDescriptor) {
                type = ((MappedPropertyDescriptor) descriptor).
                        getMappedPropertyType();
            }
            else if (descriptor instanceof IndexedPropertyDescriptor) {
                type = ((IndexedPropertyDescriptor) descriptor).
                        getIndexedPropertyType();
            }
            else {
                type = descriptor.getPropertyType();
            }
        }
        return type;
    }

    /**
     * Is the pattern to be applied localized
     * (Indicate whether the pattern is localized or not)
     *
     * @return {@code true} if pattern is localized,
     * otherwise {@code false}
     */
    public boolean getApplyLocalized() {
        return getLocaleConvertUtils().getApplyLocalized();
    }

    /**
     * Gets the default Locale
     * @return the default locale
     */
    public Locale getDefaultLocale() {
        return getLocaleConvertUtils().getDefaultLocale();
    }

    /**
     * Gets the value of the specified locale-sensitive indexed property
     * of the specified bean, as a String using the default conversion pattern of
     * the corresponding {@link LocaleConverter}. The zero-relative index
     * of the required value must be included (in square brackets) as a suffix
     * to the property name, or {@code IllegalArgumentException} will be thrown.
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
     */
    @Override
    public String getIndexedProperty(
                                    final Object bean,
                                    final String name)
                                        throws
                                            IllegalAccessException,
                                            InvocationTargetException,
                                            NoSuchMethodException {
        return getIndexedProperty(bean, name, null);
    }

    /**
     * Gets the value of the specified locale-sensetive indexed property
     * of the specified bean, as a String using the default conversion pattern of
     * the corresponding {@link LocaleConverter}.
     * The index is specified as a method parameter and
     * must *not* be included in the property name expression
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
     */
    @Override
    public String getIndexedProperty(final Object bean,
                                            final String name, final int index)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return getIndexedProperty(bean, name, index, null);
    }

    /**
     * Gets the value of the specified locale-sensetive indexed property
     * of the specified bean, as a String using the specified conversion pattern.
     * The index is specified as a method parameter and
     * must *not* be included in the property name expression
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
     */
    public String getIndexedProperty(final Object bean,
                                            final String name, final int index, final String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        final Object value = getPropertyUtils().getIndexedProperty(bean, name, index);
        return getLocaleConvertUtils().convert(value, pattern);
    }

    /**
     * Gets the value of the specified locale-sensitive indexed property
     * of the specified bean, as a String. The zero-relative index of the
     * required value must be included (in square brackets) as a suffix to
     * the property name, or {@code IllegalArgumentException} will be
     * thrown.
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
     */
    public String getIndexedProperty(
                                    final Object bean,
                                    final String name,
                                    final String pattern)
                                        throws
                                            IllegalAccessException,
                                            InvocationTargetException,
                                            NoSuchMethodException {
        final Object value = getPropertyUtils().getIndexedProperty(bean, name);
        return getLocaleConvertUtils().convert(value, pattern);
    }

    /**
     * Gets the bean instance used for conversions
     *
     * @return the locale converter bean instance
     */
    public LocaleConvertUtilsBean getLocaleConvertUtils() {
        return localeConvertUtils;
    }

    /**
     * Gets the value of the specified locale-sensitive mapped property
     * of the specified bean, as a String using the default
     * conversion pattern of the corresponding {@link LocaleConverter}.
     * The String-valued key of the required value
     * must be included (in parentheses) as a suffix to
     * the property name, or {@code IllegalArgumentException} will be
     * thrown.
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
     */
    @Override
    public String getMappedProperty(final Object bean, final String name)
                                    throws
                                        IllegalAccessException,
                                        InvocationTargetException,
                                        NoSuchMethodException {
        return getMappedPropertyLocale(bean, name, null);
    }

    /**
     * Gets the value of the specified mapped locale-sensitive property
     * of the specified bean, as a String
     * The key is specified as a method parameter and must *not* be included
     * in the property name expression
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
     */
    @Override
    public String getMappedProperty(final Object bean,
                                           final String name, final String key)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return getMappedProperty(bean, name, key, null);
    }

    /**
     * Gets the value of the specified mapped locale-sensitive property
     * of the specified bean, as a String using the specified conversion pattern.
     * The key is specified as a method parameter and must *not* be included in
     * the property name expression.
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
     */
    public String getMappedProperty(
                                    final Object bean,
                                    final String name,
                                    final String key,
                                    final String pattern)
                                        throws
                                            IllegalAccessException,
                                            InvocationTargetException,
                                            NoSuchMethodException {
        final Object value = getPropertyUtils().getMappedProperty(bean, name, key);
        return getLocaleConvertUtils().convert(value, pattern);
    }

    /**
     * Gets the value of the specified locale-sensitive mapped property
     * of the specified bean, as a String using the specified pattern.
     * The String-valued key of the required value
     * must be included (in parentheses) as a suffix to
     * the property name, or {@code IllegalArgumentException} will be
     * thrown.
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
     */
    public String getMappedPropertyLocale(
                                        final Object bean,
                                        final String name,
                                        final String pattern)
                                            throws
                                                IllegalAccessException,
                                                InvocationTargetException,
                                                NoSuchMethodException {
        final Object value = getPropertyUtils().getMappedProperty(bean, name);
        return getLocaleConvertUtils().convert(value, pattern);
    }

    /**
     * Gets the value of the (possibly nested) locale-sensitive property
     * of the specified name, for the specified bean, as a String using the default
     * conversion pattern of the corresponding {@link LocaleConverter}.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly nested name of the property to be extracted
     * @return The nested property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws IllegalArgumentException if a nested reference to a
     *  property returns null
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  property cannot be found
     */
    @Override
    public String getNestedProperty(final Object bean, final String name)
                                    throws
                                        IllegalAccessException,
                                        InvocationTargetException,
                                        NoSuchMethodException {
        return getNestedProperty(bean, name, null);
    }

    /**
     * Gets the value of the (possibly nested) locale-sensitive property
     * of the specified name, for the specified bean,
     * as a String using the specified pattern.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly nested name of the property to be extracted
     * @param pattern The conversion pattern
     * @return The nested property's value, converted to a String
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws IllegalArgumentException if a nested reference to a
     *  property returns null
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  property cannot be found
     */
    public String getNestedProperty(
                                    final Object bean,
                                    final String name,
                                    final String pattern)
                                        throws
                                            IllegalAccessException,
                                            InvocationTargetException,
                                            NoSuchMethodException {
        final Object value = getPropertyUtils().getNestedProperty(bean, name);
        return getLocaleConvertUtils().convert(value, pattern);
    }

    /**
     * Gets the value of the specified locale-sensitive property
     * of the specified bean, no matter which property reference
     * format is used, as a String using the default
     * conversion pattern of the corresponding {@link LocaleConverter}.
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
     */
    @Override
    public String getProperty(final Object bean, final String name)
                                throws
                                    IllegalAccessException,
                                    InvocationTargetException,
                                    NoSuchMethodException {
        return getNestedProperty(bean, name);
    }

    /**
     * Gets the value of the specified locale-sensitive property
     * of the specified bean, no matter which property reference
     * format is used, as a String using the specified conversion pattern.
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
     */
    public String getProperty(final Object bean, final String name, final String pattern)
                                throws
                                    IllegalAccessException,
                                    InvocationTargetException,
                                    NoSuchMethodException {
        return getNestedProperty(bean, name, pattern);
    }

    /**
     * Gets the value of the specified simple locale-sensitive property
     * of the specified bean, converted to a String using the default
     * conversion pattern of the corresponding {@link LocaleConverter}.
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
     */
    @Override
    public String getSimpleProperty(final Object bean, final String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return getSimpleProperty(bean, name, null);
    }

    /**
     * Gets the value of the specified simple locale-sensitive property
     * of the specified bean, converted to a String using the specified
     * conversion pattern.
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
     */
    public String getSimpleProperty(final Object bean, final String name, final String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        final Object value = getPropertyUtils().getSimpleProperty(bean, name);
        return getLocaleConvertUtils().convert(value, pattern);
    }

    /**
     * Invoke the setter method.
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
     */
    protected void invokeSetter(final Object target, final String propName, final String key, final int index,
            final Object newValue)
            throws IllegalAccessException, InvocationTargetException {

        try {
            if (index >= 0) {
                getPropertyUtils().setIndexedProperty(target, propName,
                        index, newValue);
            }
            else if (key != null) {
                getPropertyUtils().setMappedProperty(target, propName,
                        key, newValue);
            }
            else {
                getPropertyUtils().setProperty(target, propName, newValue);
            }
        }
        catch (final NoSuchMethodException e) {
            throw new InvocationTargetException
                    (e, "Cannot set " + propName);
        }
    }

    /**
     * Sets whether the pattern is applied localized
     * (Indicate whether the pattern is localized or not)
     *
     * @param newApplyLocalized {@code true} if pattern is localized,
     * otherwise {@code false}
     */
    public void setApplyLocalized(final boolean newApplyLocalized) {
        getLocaleConvertUtils().setApplyLocalized(newApplyLocalized);
    }

    /**
     * Sets the default Locale.
     *
     * @param locale the default locale
     */
    public void setDefaultLocale(final Locale locale) {
        getLocaleConvertUtils().setDefaultLocale(locale);
    }

    /**
     * Sets the specified locale-sensitive property value, performing type
     * conversions as required to conform to the type of the destination property
     * using the default conversion pattern of the corresponding {@link LocaleConverter}.
     *
     * @param bean Bean on which setting is to be performed
     * @param name Property name (can be nested/indexed/mapped/combo)
     * @param value Value to be set
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     */
    @Override
    public void setProperty(final Object bean, final String name, final Object value)
                                throws
                                    IllegalAccessException,
                                    InvocationTargetException {
        setProperty(bean, name, value, null);
    }

    /**
     * Sets the specified locale-sensitive property value, performing type
     * conversions as required to conform to the type of the destination
     * property using the specified conversion pattern.
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
     */
    public void setProperty(
                            final Object bean,
                            String name,
                            final Object value,
                            final String pattern)
                                throws
                                    IllegalAccessException,
                                    InvocationTargetException {
        // Trace logging (if enabled)
        if (LOG.isTraceEnabled()) {
            final StringBuilder sb = new StringBuilder("  setProperty(");
            sb.append(bean);
            sb.append(", ");
            sb.append(name);
            sb.append(", ");
            if (value == null) {
                sb.append("<NULL>");
            }
            else if (value instanceof String) {
                sb.append((String) value);
            }
            else if (value instanceof String[]) {
                final String[] values = (String[]) value;
                sb.append('[');
                for (int i = 0; i < values.length; i++) {
                    if (i > 0) {
                        sb.append(',');
                    }
                    sb.append(values[i]);
                }
                sb.append(']');
            }
            else {
                sb.append(value.toString());
            }
            sb.append(')');
            LOG.trace(sb.toString());
        }

        // Resolve any nested expression to get the actual target bean
        Object target = bean;
        final Resolver resolver = getPropertyUtils().getResolver();
        while (resolver.hasNested(name)) {
            try {
                target = getPropertyUtils().getProperty(target, resolver.next(name));
                name = resolver.remove(name);
            } catch (final NoSuchMethodException e) {
                return; // Skip this property setter
            }
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("    Target bean = " + target);
            LOG.trace("    Target name = " + name);
        }

        // Declare local variables we will require
        final String propName = resolver.getProperty(name); // Simple name of target property
        final int index  = resolver.getIndex(name);         // Indexed subscript value (if any)
        final String key = resolver.getKey(name);           // Mapped key value (if any)

        final Class<?> type = definePropertyType(target, name, propName);
        if (type != null) {
            final Object newValue = convert(type, index, value, pattern);
            invokeSetter(target, propName, key, index, newValue);
        }
    }
}

