/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
 
package org.apache.commons.beanutils.locale;


import org.apache.commons.beanutils.*;
import org.apache.commons.beanutils.ContextClassLoaderLocal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;


/**
 * <p>Utility methods for populating JavaBeans properties
 * via reflection in a locale-dependent manner.</p>
 *
 * @author Craig R. McClanahan
 * @author Ralph Schaer
 * @author Chris Audley
 * @author Rey François
 * @author Gregor Raýman
 * @author Yauheny Mikulski
 * @since 1.7
 */

public class LocaleBeanUtilsBean extends BeanUtilsBean {

	/** 
	 * Contains <code>LocaleBeanUtilsBean</code> instances indexed by context classloader.
	 */
    private static final ContextClassLoaderLocal 
            localeBeansByClassLoader = new ContextClassLoaderLocal() {
                        // Creates the default instance used when the context classloader is unavailable
                        protected Object initialValue() {
                            return new LocaleBeanUtilsBean();
                        }
                    };
     
     /** Gets singleton instance */
     public synchronized static LocaleBeanUtilsBean getLocaleBeanUtilsInstance() {
        return (LocaleBeanUtilsBean)localeBeansByClassLoader.get();
     }
 
	/** 
	 * Sets the instance which provides the functionality for {@link LocaleBeanUtils}.
	 * This is a pseudo-singleton - an single instance is provided per (thread) context classloader.
	 * This mechanism provides isolation for web apps deployed in the same container. 
	 */
	public synchronized static void setInstance(LocaleBeanUtilsBean newInstance) {
        localeBeansByClassLoader.set(newInstance);
	}

    /** All logging goes through this logger */
    private static Log log = LogFactory.getLog(LocaleBeanUtilsBean.class);

    // ----------------------------------------------------- Instance Variables
        
    /** Convertor used by this class */
    private LocaleConvertUtilsBean localeConvertUtils;
    
    // --------------------------------------------------------- Constructors
    
    /** Construct instance with standard conversion bean */
    public LocaleBeanUtilsBean() {
        this.localeConvertUtils = new LocaleConvertUtilsBean();
    }
    
    /** 
     * Construct instance that uses given locale conversion
     *
     * @param localeConvertUtils use this <code>localeConvertUtils</code> to perform
     * conversions
     * @param convertUtilsBean use this for standard conversions
     * @param propertyUtilsBean use this for property conversions
     */
    public LocaleBeanUtilsBean(
                            LocaleConvertUtilsBean localeConvertUtils,
                            ConvertUtilsBean convertUtilsBean,
                            PropertyUtilsBean propertyUtilsBean) {
        super(convertUtilsBean, propertyUtilsBean);
        this.localeConvertUtils = localeConvertUtils;
    }
    
    /** 
     * Construct instance that uses given locale conversion
     *
     * @param localeConvertUtils use this <code>localeConvertUtils</code> to perform
     * conversions
     */
    public LocaleBeanUtilsBean(LocaleConvertUtilsBean localeConvertUtils) {
        this.localeConvertUtils = localeConvertUtils;
    }
    
    // --------------------------------------------------------- Public Methods
    
    /** Gets the bean instance used for conversions */
    public LocaleConvertUtilsBean getLocaleConvertUtils() {
        return localeConvertUtils;
    }
    
    /**
     * Gets the default Locale
     */
    public Locale getDefaultLocale() {

        return getLocaleConvertUtils().getDefaultLocale();
    }


    /**
     * Sets the default Locale
     */
    public void setDefaultLocale(Locale locale) {

        getLocaleConvertUtils().setDefaultLocale(locale);
    }

    /**
     * Is the pattern to be applied localized
     * (Indicate whether the pattern is localized or not)
     */
    public boolean getApplyLocalized() {

        return getLocaleConvertUtils().getApplyLocalized();
    }

    /**
     * Sets whether the pattern is applied localized
     * (Indicate whether the pattern is localized or not)
     */
    public void setApplyLocalized(boolean newApplyLocalized) {

        getLocaleConvertUtils().setApplyLocalized(newApplyLocalized);
    }


    // --------------------------------------------------------- Public Methods

    /**
     * Return the value of the specified locale-sensitive indexed property
     * of the specified bean, as a String. The zero-relative index of the
     * required value must be included (in square brackets) as a suffix to
     * the property name, or <code>IllegalArgumentException</code> will be
     * thrown.
     *
     * @param bean Bean whose property is to be extracted
     * @param name <code>propertyname[index]</code> of the property value
     *  to be extracted
     * @param pattern The convertion pattern
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public String getIndexedProperty(
                                    Object bean, 
                                    String name, 
                                    String pattern)
                                        throws 
                                            IllegalAccessException, 
                                            InvocationTargetException,
                                            NoSuchMethodException {

        Object value = getPropertyUtils().getIndexedProperty(bean, name);
        return getLocaleConvertUtils().convert(value, pattern);
    }

    /**
     * Return the value of the specified locale-sensitive indexed property
     * of the specified bean, as a String using the default convertion pattern of
     * the corresponding {@link LocaleConverter}. The zero-relative index
     * of the required value must be included (in square brackets) as a suffix
     * to the property name, or <code>IllegalArgumentException</code> will be thrown.
     *
     * @param bean Bean whose property is to be extracted
     * @param name <code>propertyname[index]</code> of the property value
     *  to be extracted
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public String getIndexedProperty(
                                    Object bean, 
                                    String name)
                                        throws 
                                            IllegalAccessException, 
                                            InvocationTargetException,
                                            NoSuchMethodException {

        return getIndexedProperty(bean, name, null);
    }

    /**
     * Return the value of the specified locale-sensetive indexed property
     * of the specified bean, as a String using the specified convertion pattern.
     * The index is specified as a method parameter and
     * must *not* be included in the property name expression
     *
     * @param bean Bean whose property is to be extracted
     * @param name Simple property name of the property value to be extracted
     * @param index Index of the property value to be extracted
     * @param pattern The convertion pattern
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public String getIndexedProperty(Object bean,
                                            String name, int index, String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        Object value = getPropertyUtils().getIndexedProperty(bean, name, index);
        return getLocaleConvertUtils().convert(value, pattern);
    }

    /**
     * Return the value of the specified locale-sensetive indexed property
     * of the specified bean, as a String using the default convertion pattern of
     * the corresponding {@link LocaleConverter}.
     * The index is specified as a method parameter and
     * must *not* be included in the property name expression
     *
     * @param bean Bean whose property is to be extracted
     * @param name Simple property name of the property value to be extracted
     * @param index Index of the property value to be extracted
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public String getIndexedProperty(Object bean,
                                            String name, int index)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return getIndexedProperty(bean, name, index, null);
    }

    /**
     * Return the value of the specified simple locale-sensitive property
     * of the specified bean, converted to a String using the specified
     * convertion pattern.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Name of the property to be extracted
     * @param pattern The convertion pattern
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public String getSimpleProperty(Object bean, String name, String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        Object value = getPropertyUtils().getSimpleProperty(bean, name);
        return getLocaleConvertUtils().convert(value, pattern);
    }

    /**
     * Return the value of the specified simple locale-sensitive property
     * of the specified bean, converted to a String using the default
     * convertion pattern of the corresponding {@link LocaleConverter}.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Name of the property to be extracted
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public String getSimpleProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return getSimpleProperty(bean, name, null);
    }

    /**
     * Return the value of the specified mapped locale-sensitive property
     * of the specified bean, as a String using the specified convertion pattern.
     * The key is specified as a method parameter and must *not* be included in
     * the property name expression.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Simple property name of the property value to be extracted
     * @param key Lookup key of the property value to be extracted
     * @param pattern The convertion pattern
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public String getMappedProperty(
                                    Object bean,
                                    String name, 
                                    String key, 
                                    String pattern)
                                        throws 
                                            IllegalAccessException, 
                                            InvocationTargetException,
                                            NoSuchMethodException {

        Object value = getPropertyUtils().getMappedProperty(bean, name, key);
        return getLocaleConvertUtils().convert(value, pattern);
    }

    /**
     * Return the value of the specified mapped locale-sensitive property
     * of the specified bean, as a String
     * The key is specified as a method parameter and must *not* be included
     * in the property name expression
     *
     * @param bean Bean whose property is to be extracted
     * @param name Simple property name of the property value to be extracted
     * @param key Lookup key of the property value to be extracted
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public String getMappedProperty(Object bean,
                                           String name, String key)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return getMappedProperty(bean, name, key, null);
    }


    /**
     * Return the value of the specified locale-sensitive mapped property
     * of the specified bean, as a String using the specified pattern.
     * The String-valued key of the required value
     * must be included (in parentheses) as a suffix to
     * the property name, or <code>IllegalArgumentException</code> will be
     * thrown.
     *
     * @param bean Bean whose property is to be extracted
     * @param name <code>propertyname(index)</code> of the property value
     *  to be extracted
     * @param pattern The convertion pattern
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public String getMappedPropertyLocale(
                                        Object bean, 
                                        String name, 
                                        String pattern)
                                            throws 
                                                IllegalAccessException, 
                                                InvocationTargetException,
                                                NoSuchMethodException {

        Object value = getPropertyUtils().getMappedProperty(bean, name);
        return getLocaleConvertUtils().convert(value, pattern);
    }


    /**
     * Return the value of the specified locale-sensitive mapped property
     * of the specified bean, as a String using the default
     * convertion pattern of the corresponding {@link LocaleConverter}.
     * The String-valued key of the required value
     * must be included (in parentheses) as a suffix to
     * the property name, or <code>IllegalArgumentException</code> will be
     * thrown.
     *
     * @param bean Bean whose property is to be extracted
     * @param name <code>propertyname(index)</code> of the property value
     *  to be extracted
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public String getMappedProperty(Object bean, String name)
                                    throws 
                                        IllegalAccessException, 
                                        InvocationTargetException,
                                        NoSuchMethodException {

        return getMappedPropertyLocale(bean, name, null);
    }

    /**
     * Return the value of the (possibly nested) locale-sensitive property
     * of the specified name, for the specified bean,
     * as a String using the specified pattern.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly nested name of the property to be extracted
     * @param pattern The convertion pattern
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if a nested reference to a
     *  property returns null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public String getNestedProperty(    
                                    Object bean, 
                                    String name, 
                                    String pattern)
                                        throws 
                                            IllegalAccessException, 
                                            InvocationTargetException,
                                            NoSuchMethodException {

        Object value = getPropertyUtils().getNestedProperty(bean, name);
        return getLocaleConvertUtils().convert(value, pattern);
    }

    /**
     * Return the value of the (possibly nested) locale-sensitive property
     * of the specified name, for the specified bean, as a String using the default
     * convertion pattern of the corresponding {@link LocaleConverter}.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly nested name of the property to be extracted
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if a nested reference to a
     *  property returns null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public String getNestedProperty(Object bean, String name)
                                    throws 
                                        IllegalAccessException, 
                                        InvocationTargetException,
                                        NoSuchMethodException {

        return getNestedProperty(bean, name, null);
    }

    /**
     * Return the value of the specified locale-sensitive property
     * of the specified bean, no matter which property reference
     * format is used, as a String using the specified convertion pattern.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly indexed and/or nested name of the property
     *  to be extracted
     * @param pattern The convertion pattern
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public String getProperty(Object bean, String name, String pattern)
                                throws 
                                    IllegalAccessException, 
                                    InvocationTargetException,
                                    NoSuchMethodException {

        return getNestedProperty(bean, name, pattern);
    }

    /**
     * Return the value of the specified locale-sensitive property
     * of the specified bean, no matter which property reference
     * format is used, as a String using the default
     * convertion pattern of the corresponding {@link LocaleConverter}.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly indexed and/or nested name of the property
     *  to be extracted
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public String getProperty(Object bean, String name)
                                throws 
                                    IllegalAccessException, 
                                    InvocationTargetException,
                                    NoSuchMethodException {

        return getNestedProperty(bean, name);
    }

    /**
     * Set the specified locale-sensitive property value, performing type
     * conversions as required to conform to the type of the destination property
     * using the default convertion pattern of the corresponding {@link LocaleConverter}.
     *
     * @param bean Bean on which setting is to be performed
     * @param name Property name (can be nested/indexed/mapped/combo)
     * @param value Value to be set
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     */
    public void setProperty(Object bean, String name, Object value)
                                throws 
                                    IllegalAccessException, 
                                    InvocationTargetException {

        setProperty(bean, name, value, null);
    }

    /**
     * Set the specified locale-sensitive property value, performing type
     * conversions as required to conform to the type of the destination
     * property using the specified convertion pattern.
     *
     * @param bean Bean on which setting is to be performed
     * @param name Property name (can be nested/indexed/mapped/combo)
     * @param value Value to be set
     * @param pattern The convertion pattern
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     */
    public void setProperty(
                            Object bean, 
                            String name, 
                            Object value, 
                            String pattern)
                                throws 
                                    IllegalAccessException, 
                                    InvocationTargetException {

        // Trace logging (if enabled)
        if (log.isTraceEnabled()) {
            StringBuffer sb = new StringBuffer("  setProperty(");
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
                String values[] = (String[]) value;
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
            log.trace(sb.toString());
        }

        Descriptor propInfo = calculate(bean, name);

        if (propInfo != null) {
            Class type = definePropertyType(propInfo.getTarget(), name, propInfo.getPropName());

            if (type != null) {

                Object newValue = convert(type, propInfo.getIndex(), value, pattern);
                invokeSetter(propInfo.getTarget(), propInfo.getPropName(),
                        propInfo.getKey(), propInfo.getIndex(), newValue);
            }
        }
    }

    /**
     * Calculate the property type.
     *
     * @param target The bean
     * @param name The property name
     * @param propName The Simple name of target property
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     */
    protected Class definePropertyType(Object target, String name, String propName)
            throws IllegalAccessException, InvocationTargetException {

        Class type = null;               // Java type of target property

        if (target instanceof DynaBean) {
            DynaClass dynaClass = ((DynaBean) target).getDynaClass();
            DynaProperty dynaProperty = dynaClass.getDynaProperty(propName);
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
            catch (NoSuchMethodException e) {
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
     * Convert the specified value to the required type using the
     * specified convertion pattern.
     *
     * @param type The Java type of target property
     * @param index The indexed subscript value (if any)
     * @param value The value to be converted
     * @param pattern The convertion pattern
     */
    protected Object convert(Class type, int index, Object value, String pattern) {
		
		if (log.isTraceEnabled()) {
			log.trace("Converting value '" + value + "' to type:" + type);
		}
		
        Object newValue = null;

        if (type.isArray() && (index < 0)) { // Scalar value into array
            if (value instanceof String) {
                String values[] = new String[1];
                values[0] = (String) value;
                newValue = getLocaleConvertUtils().convert((String[]) values, type, pattern);
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
        }
        else {                             // Value into scalar
            if (value instanceof String) {
                newValue = getLocaleConvertUtils().convert((String) value, type, pattern);
            }
            else if (value instanceof String[]) {
                newValue = getLocaleConvertUtils().convert(((String[]) value)[0],
                        type, pattern);
            }
            else {
                newValue = value;
            }
        }
        return newValue;
    }

    /**
     *  Convert the specified value to the required type.
     *
     * @param type The Java type of target property
     * @param index The indexed subscript value (if any)
     * @param value The value to be converted
     */
    protected Object convert(Class type, int index, Object value) {

        Object newValue = null;

        if (type.isArray() && (index < 0)) { // Scalar value into array
            if (value instanceof String) {
                String values[] = new String[1];
                values[0] = (String) value;
                newValue = ConvertUtils.convert((String[]) values, type);
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
        }
        else {                             // Value into scalar
            if (value instanceof String) {
                newValue = ConvertUtils.convert((String) value, type);
            }
            else if (value instanceof String[]) {
                newValue = ConvertUtils.convert(((String[]) value)[0],
                        type);
            }
            else {
                newValue = value;
            }
        }
        return newValue;
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
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     */
    protected void invokeSetter(Object target, String propName, String key, int index, Object newValue)
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
        catch (NoSuchMethodException e) {
            throw new InvocationTargetException
                    (e, "Cannot set " + propName);
        }
    }

    /**
     * Resolve any nested expression to get the actual target bean.
     *
     * @param bean The bean
     * @param name The property name
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     */
    protected Descriptor calculate(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException {

        String propName = null;          // Simple name of target property
        int index = -1;                  // Indexed subscript value (if any)
        String key = null;               // Mapped key value (if any)

        Object target = bean;
        int delim = name.lastIndexOf(PropertyUtils.NESTED_DELIM);
        if (delim >= 0) {
            try {
                target =
                        getPropertyUtils().getProperty(bean, name.substring(0, delim));
            }
            catch (NoSuchMethodException e) {
                return null; // Skip this property setter
            }
            name = name.substring(delim + 1);
            if (log.isTraceEnabled()) {
                log.trace("    Target bean = " + target);
                log.trace("    Target name = " + name);
            }
        }

        // Calculate the property name, index, and key values
        propName = name;
        int i = propName.indexOf(PropertyUtils.INDEXED_DELIM);
        if (i >= 0) {
            int k = propName.indexOf(PropertyUtils.INDEXED_DELIM2);
            try {
                index =
                        Integer.parseInt(propName.substring(i + 1, k));
            }
            catch (NumberFormatException e) {
                ;
            }
            propName = propName.substring(0, i);
        }
        int j = propName.indexOf(PropertyUtils.MAPPED_DELIM);
        if (j >= 0) {
            int k = propName.indexOf(PropertyUtils.MAPPED_DELIM2);
            try {
                key = propName.substring(j + 1, k);
            }
            catch (IndexOutOfBoundsException e) {
                ;
            }
            propName = propName.substring(0, j);
        }
        return new Descriptor(target, name, propName, key, index);
    }

    protected class Descriptor {

        private int index = -1;    // Indexed subscript value (if any)
        private String name;
        private String propName;   // Simple name of target property
        private String key;        // Mapped key value (if any)
        private Object target;

        public Descriptor(Object target, String name, String propName, String key, int index) {

            setTarget(target);
            setName(name);
            setPropName(propName);
            setKey(key);
            setIndex(index);
        }

        public Object getTarget() {
            return target;
        }

        public void setTarget(Object target) {
            this.target = target;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPropName() {
            return propName;
        }

        public void setPropName(String propName) {
            this.propName = propName;
        }
    }
}


