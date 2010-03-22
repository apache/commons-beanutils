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


package org.apache.commons.beanutils;


import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.expression.Resolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <p>JavaBean property population methods.</p>
 *
 * <p>This class provides implementations for the utility methods in
 * {@link BeanUtils}.
 * Different instances can be used to isolate caches between classloaders
 * and to vary the value converters registered.</p>
 *
 * @author Craig R. McClanahan
 * @author Ralph Schaer
 * @author Chris Audley
 * @author Rey Francois
 * @author Gregor Rayman
 * @version $Revision$ $Date$
 * @see BeanUtils
 * @since 1.7
 */

public class BeanUtilsBean {


    // ------------------------------------------------------ Private Class Variables

    /** 
     * Contains <code>BeanUtilsBean</code> instances indexed by context classloader.
     */
    private static final ContextClassLoaderLocal 
            BEANS_BY_CLASSLOADER = new ContextClassLoaderLocal() {
                        // Creates the default instance used when the context classloader is unavailable
                        protected Object initialValue() {
                            return new BeanUtilsBean();
                        }
                    };
    
    /** 
     * Gets the instance which provides the functionality for {@link BeanUtils}.
     * This is a pseudo-singleton - an single instance is provided per (thread) context classloader.
     * This mechanism provides isolation for web apps deployed in the same container.
     *
     * @return The (pseudo-singleton) BeanUtils bean instance
     */
    public static BeanUtilsBean getInstance() {
        return (BeanUtilsBean) BEANS_BY_CLASSLOADER.get();
    }

    /** 
     * Sets the instance which provides the functionality for {@link BeanUtils}.
     * This is a pseudo-singleton - an single instance is provided per (thread) context classloader.
     * This mechanism provides isolation for web apps deployed in the same container.
     * 
     * @param newInstance The (pseudo-singleton) BeanUtils bean instance
     */
    public static void setInstance(BeanUtilsBean newInstance) {
        BEANS_BY_CLASSLOADER.set(newInstance);
    }

    // --------------------------------------------------------- Attributes

    /**
     * Logging for this instance
     */
    private Log log = LogFactory.getLog(BeanUtils.class);
    
    /** Used to perform conversions between object types when setting properties */
    private ConvertUtilsBean convertUtilsBean;
    
    /** Used to access properties*/
    private PropertyUtilsBean propertyUtilsBean;

    /** A reference to Throwable's initCause method, or null if it's not there in this JVM */
    private static final Method INIT_CAUSE_METHOD = getInitCauseMethod();

    // --------------------------------------------------------- Constuctors

    /** 
     * <p>Constructs an instance using new property 
     * and conversion instances.</p>
     */
    public BeanUtilsBean() {
        this(new ConvertUtilsBean(), new PropertyUtilsBean());
    }

    /** 
     * <p>Constructs an instance using given conversion instances
     * and new {@link PropertyUtilsBean} instance.</p>
     *
     * @param convertUtilsBean use this <code>ConvertUtilsBean</code> 
     * to perform conversions from one object to another
     *
     * @since 1.8.0
     */
    public BeanUtilsBean(ConvertUtilsBean convertUtilsBean) {
        this(convertUtilsBean, new PropertyUtilsBean());
    }

    /** 
     * <p>Constructs an instance using given property and conversion instances.</p>
     *
     * @param convertUtilsBean use this <code>ConvertUtilsBean</code> 
     * to perform conversions from one object to another
     * @param propertyUtilsBean use this <code>PropertyUtilsBean</code>
     * to access properties
     */
    public BeanUtilsBean(
                            ConvertUtilsBean convertUtilsBean, 
                            PropertyUtilsBean propertyUtilsBean) {
                            
        this.convertUtilsBean = convertUtilsBean;
        this.propertyUtilsBean = propertyUtilsBean;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * <p>Clone a bean based on the available property getters and setters,
     * even if the bean class itself does not implement Cloneable.</p>
     *
     * <p>
     * <strong>Note:</strong> this method creates a <strong>shallow</strong> clone.
     * In other words, any objects referred to by the bean are shared with the clone
     * rather than being cloned in turn.
     * </p>
     *
     * @param bean Bean to be cloned
     * @return the cloned bean
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InstantiationException if a new instance of the bean's
     *  class cannot be instantiated
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  property cannot be found
     */
    public Object cloneBean(Object bean)
            throws IllegalAccessException, InstantiationException,
            InvocationTargetException, NoSuchMethodException {

        if (log.isDebugEnabled()) {
            log.debug("Cloning bean: " + bean.getClass().getName());
        }
        Object newBean = null;
        if (bean instanceof DynaBean) {
            newBean = ((DynaBean) bean).getDynaClass().newInstance();
        } else {
            newBean = bean.getClass().newInstance();
        }
        getPropertyUtils().copyProperties(newBean, bean);
        return (newBean);

    }


    /**
     * <p>Copy property values from the origin bean to the destination bean
     * for all cases where the property names are the same.  For each
     * property, a conversion is attempted as necessary.  All combinations of
     * standard JavaBeans and DynaBeans as origin and destination are
     * supported.  Properties that exist in the origin bean, but do not exist
     * in the destination bean (or are read-only in the destination bean) are
     * silently ignored.</p>
     *
     * <p>If the origin "bean" is actually a <code>Map</code>, it is assumed
     * to contain String-valued <strong>simple</strong> property names as the keys, pointing at
     * the corresponding property values that will be converted (if necessary)
     * and set in the destination bean. <strong>Note</strong> that this method
     * is intended to perform a "shallow copy" of the properties and so complex
     * properties (for example, nested ones) will not be copied.</p>
     *
     * <p>This method differs from <code>populate()</code>, which
     * was primarily designed for populating JavaBeans from the map of request
     * parameters retrieved on an HTTP request, is that no scalar->indexed
     * or indexed->scalar manipulations are performed.  If the origin property
     * is indexed, the destination property must be also.</p>
     *
     * <p>If you know that no type conversions are required, the
     * <code>copyProperties()</code> method in {@link PropertyUtils} will
     * execute faster than this method.</p>
     *
     * <p><strong>FIXME</strong> - Indexed and mapped properties that do not
     * have getter and setter methods for the underlying array or Map are not
     * copied by this method.</p>
     *
     * @param dest Destination bean whose properties are modified
     * @param orig Origin bean whose properties are retrieved
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if the <code>dest</code> or
     *  <code>orig</code> argument is null or if the <code>dest</code> 
     *  property type is different from the source type and the relevant
     *  converter has not been registered.
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     */
    public void copyProperties(Object dest, Object orig)
        throws IllegalAccessException, InvocationTargetException {

        // Validate existence of the specified beans
        if (dest == null) {
            throw new IllegalArgumentException
                    ("No destination bean specified");
        }
        if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        }
        if (log.isDebugEnabled()) {
            log.debug("BeanUtils.copyProperties(" + dest + ", " +
                      orig + ")");
        }

        // Copy the properties, converting as necessary
        if (orig instanceof DynaBean) {
            DynaProperty[] origDescriptors =
                ((DynaBean) orig).getDynaClass().getDynaProperties();
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                // Need to check isReadable() for WrapDynaBean
                // (see Jira issue# BEANUTILS-61)
                if (getPropertyUtils().isReadable(orig, name) &&
                    getPropertyUtils().isWriteable(dest, name)) {
                    Object value = ((DynaBean) orig).get(name);
                    copyProperty(dest, name, value);
                }
            }
        } else if (orig instanceof Map) {
            Iterator entries = ((Map) orig).entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                String name = (String)entry.getKey();
                if (getPropertyUtils().isWriteable(dest, name)) {
                    copyProperty(dest, name, entry.getValue());
                }
            }
        } else /* if (orig is a standard JavaBean) */ {
            PropertyDescriptor[] origDescriptors =
                getPropertyUtils().getPropertyDescriptors(orig);
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                if ("class".equals(name)) {
                    continue; // No point in trying to set an object's class
                }
                if (getPropertyUtils().isReadable(orig, name) &&
                    getPropertyUtils().isWriteable(dest, name)) {
                    try {
                        Object value =
                            getPropertyUtils().getSimpleProperty(orig, name);
                        copyProperty(dest, name, value);
                    } catch (NoSuchMethodException e) {
                        // Should not happen
                    }
                }
            }
        }

    }


    /**
     * <p>Copy the specified property value to the specified destination bean,
     * performing any type conversion that is required.  If the specified
     * bean does not have a property of the specified name, or the property
     * is read only on the destination bean, return without
     * doing anything.  If you have custom destination property types, register
     * {@link Converter}s for them by calling the <code>register()</code>
     * method of {@link ConvertUtils}.</p>
     *
     * <p><strong>IMPLEMENTATION RESTRICTIONS</strong>:</p>
     * <ul>
     * <li>Does not support destination properties that are indexed,
     *     but only an indexed setter (as opposed to an array setter)
     *     is available.</li>
     * <li>Does not support destination properties that are mapped,
     *     but only a keyed setter (as opposed to a Map setter)
     *     is available.</li>
     * <li>The desired property type of a mapped setter cannot be
     *     determined (since Maps support any data type), so no conversion
     *     will be performed.</li>
     * </ul>
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
    public void copyProperty(Object bean, String name, Object value)
        throws IllegalAccessException, InvocationTargetException {

        // Trace logging (if enabled)
        if (log.isTraceEnabled()) {
            StringBuffer sb = new StringBuffer("  copyProperty(");
            sb.append(bean);
            sb.append(", ");
            sb.append(name);
            sb.append(", ");
            if (value == null) {
                sb.append("<NULL>");
            } else if (value instanceof String) {
                sb.append((String) value);
            } else if (value instanceof String[]) {
                String[] values = (String[]) value;
                sb.append('[');
                for (int i = 0; i < values.length; i++) {
                    if (i > 0) {
                        sb.append(',');
                    }
                    sb.append(values[i]);
                }
                sb.append(']');
            } else {
                sb.append(value.toString());
            }
            sb.append(')');
            log.trace(sb.toString());
        }

        // Resolve any nested expression to get the actual target bean
        Object target = bean;
        Resolver resolver = getPropertyUtils().getResolver();
        while (resolver.hasNested(name)) {
            try {
                target = getPropertyUtils().getProperty(target, resolver.next(name));
                name = resolver.remove(name);
            } catch (NoSuchMethodException e) {
                return; // Skip this property setter
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("    Target bean = " + target);
            log.trace("    Target name = " + name);
        }

        // Declare local variables we will require
        String propName = resolver.getProperty(name); // Simple name of target property
        Class type = null;                            // Java type of target property
        int index  = resolver.getIndex(name);         // Indexed subscript value (if any)
        String key = resolver.getKey(name);           // Mapped key value (if any)

        // Calculate the target property type
        if (target instanceof DynaBean) {
            DynaClass dynaClass = ((DynaBean) target).getDynaClass();
            DynaProperty dynaProperty = dynaClass.getDynaProperty(propName);
            if (dynaProperty == null) {
                return; // Skip this property setter
            }
            type = dynaProperty.getType();
        } else {
            PropertyDescriptor descriptor = null;
            try {
                descriptor =
                    getPropertyUtils().getPropertyDescriptor(target, name);
                if (descriptor == null) {
                    return; // Skip this property setter
                }
            } catch (NoSuchMethodException e) {
                return; // Skip this property setter
            }
            type = descriptor.getPropertyType();
            if (type == null) {
                // Most likely an indexed setter on a POJB only
                if (log.isTraceEnabled()) {
                    log.trace("    target type for property '" +
                              propName + "' is null, so skipping ths setter");
                }
                return;
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("    target propName=" + propName + ", type=" +
                      type + ", index=" + index + ", key=" + key);
        }

        // Convert the specified value to the required type and store it
        if (index >= 0) {                    // Destination must be indexed
            value = convert(value, type.getComponentType());
            try {
                getPropertyUtils().setIndexedProperty(target, propName,
                                                 index, value);
            } catch (NoSuchMethodException e) {
                throw new InvocationTargetException
                    (e, "Cannot set " + propName);
            }
        } else if (key != null) {            // Destination must be mapped
            // Maps do not know what the preferred data type is,
            // so perform no conversions at all
            // FIXME - should we create or support a TypedMap?
            try {
                getPropertyUtils().setMappedProperty(target, propName,
                                                key, value);
            } catch (NoSuchMethodException e) {
                throw new InvocationTargetException
                    (e, "Cannot set " + propName);
            }
        } else {                             // Destination must be simple
            value = convert(value, type);
            try {
                getPropertyUtils().setSimpleProperty(target, propName, value);
            } catch (NoSuchMethodException e) {
                throw new InvocationTargetException
                    (e, "Cannot set " + propName);
            }
        }

    }


    /**
     * <p>Return the entire set of properties for which the specified bean
     * provides a read method. This map contains the to <code>String</code>
     * converted property values for all properties for which a read method
     * is provided (i.e. where the getReadMethod() returns non-null).</p>
     *
     * <p>This map can be fed back to a call to
     * <code>BeanUtils.populate()</code> to reconsitute the same set of
     * properties, modulo differences for read-only and write-only
     * properties, but only if there are no indexed properties.</p>
     *
     * <p><strong>Warning:</strong> if any of the bean property implementations
     * contain (directly or indirectly) a call to this method then 
     * a stack overflow may result. For example:
     * <code><pre>
     * class MyBean
     * {
     *    public Map getParameterMap()
     *    {
     *         BeanUtils.describe(this);
     *    }
     * }
     * </pre></code>
     * will result in an infinite regression when <code>getParametersMap</code>
     * is called. It is recommended that such methods are given alternative
     * names (for example, <code>parametersMap</code>).
     * </p>
     * @param bean Bean whose properties are to be extracted
     * @return Map of property descriptors
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  property cannot be found
     */
    public Map describe(Object bean)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
        //            return (Collections.EMPTY_MAP);
            return (new java.util.HashMap());
        }
        
        if (log.isDebugEnabled()) {
            log.debug("Describing bean: " + bean.getClass().getName());
        }
            
        Map description = new HashMap();
        if (bean instanceof DynaBean) {
            DynaProperty[] descriptors =
                ((DynaBean) bean).getDynaClass().getDynaProperties();
            for (int i = 0; i < descriptors.length; i++) {
                String name = descriptors[i].getName();
                description.put(name, getProperty(bean, name));
            }
        } else {
            PropertyDescriptor[] descriptors =
                getPropertyUtils().getPropertyDescriptors(bean);
            Class clazz = bean.getClass();
            for (int i = 0; i < descriptors.length; i++) {
                String name = descriptors[i].getName();
                if (getPropertyUtils().getReadMethod(clazz, descriptors[i]) != null) {
                    description.put(name, getProperty(bean, name));
                }
            }
        }
        return (description);

    }


    /**
     * Return the value of the specified array property of the specified
     * bean, as a String array.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Name of the property to be extracted
     * @return The array property value
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  property cannot be found
     */
    public String[] getArrayProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        Object value = getPropertyUtils().getProperty(bean, name);
        if (value == null) {
            return (null);
        } else if (value instanceof Collection) {
            ArrayList values = new ArrayList();
            Iterator items = ((Collection) value).iterator();
            while (items.hasNext()) {
                Object item = items.next();
                if (item == null) {
                    values.add((String) null);
                } else {
                    // convert to string using convert utils
                    values.add(getConvertUtils().convert(item));
                }
            }
            return ((String[]) values.toArray(new String[values.size()]));
        } else if (value.getClass().isArray()) {
            int n = Array.getLength(value);
            String[] results = new String[n];
            for (int i = 0; i < n; i++) {
                Object item = Array.get(value, i);
                if (item == null) {
                    results[i] = null;
                } else {
                    // convert to string using convert utils
                    results[i] = getConvertUtils().convert(item);
                }
            }
            return (results);
        } else {
            String[] results = new String[1];
            results[0] = getConvertUtils().convert(value);
            return (results);
        }

    }


    /**
     * Return the value of the specified indexed property of the specified
     * bean, as a String.  The zero-relative index of the
     * required value must be included (in square brackets) as a suffix to
     * the property name, or <code>IllegalArgumentException</code> will be
     * thrown.
     *
     * @param bean Bean whose property is to be extracted
     * @param name <code>propertyname[index]</code> of the property value
     *  to be extracted
     * @return The indexed property's value, converted to a String
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  property cannot be found
     */
    public String getIndexedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        Object value = getPropertyUtils().getIndexedProperty(bean, name);
        return (getConvertUtils().convert(value));

    }


    /**
     * Return the value of the specified indexed property of the specified
     * bean, as a String.  The index is specified as a method parameter and
     * must *not* be included in the property name expression
     *
     * @param bean Bean whose property is to be extracted
     * @param name Simple property name of the property value to be extracted
     * @param index Index of the property value to be extracted
     * @return The indexed property's value, converted to a String
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  property cannot be found
     */
    public String getIndexedProperty(Object bean,
                                            String name, int index)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        Object value = getPropertyUtils().getIndexedProperty(bean, name, index);
        return (getConvertUtils().convert(value));

    }


    /**
     * Return the value of the specified indexed property of the specified
     * bean, as a String.  The String-valued key of the required value
     * must be included (in parentheses) as a suffix to
     * the property name, or <code>IllegalArgumentException</code> will be
     * thrown.
     *
     * @param bean Bean whose property is to be extracted
     * @param name <code>propertyname(index)</code> of the property value
     *  to be extracted
     * @return The mapped property's value, converted to a String
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  property cannot be found
     */
    public String getMappedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        Object value = getPropertyUtils().getMappedProperty(bean, name);
        return (getConvertUtils().convert(value));

    }


    /**
     * Return the value of the specified mapped property of the specified
     * bean, as a String.  The key is specified as a method parameter and
     * must *not* be included in the property name expression
     *
     * @param bean Bean whose property is to be extracted
     * @param name Simple property name of the property value to be extracted
     * @param key Lookup key of the property value to be extracted
     * @return The mapped property's value, converted to a String
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  property cannot be found
     */
    public String getMappedProperty(Object bean,
                                           String name, String key)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        Object value = getPropertyUtils().getMappedProperty(bean, name, key);
        return (getConvertUtils().convert(value));

    }


    /**
     * Return the value of the (possibly nested) property of the specified
     * name, for the specified bean, as a String.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly nested name of the property to be extracted
     * @return The nested property's value, converted to a String
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if a nested reference to a
     *  property returns null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  property cannot be found
     */
    public String getNestedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        Object value = getPropertyUtils().getNestedProperty(bean, name);
        return (getConvertUtils().convert(value));

    }


    /**
     * Return the value of the specified property of the specified bean,
     * no matter which property reference format is used, as a String.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly indexed and/or nested name of the property
     *  to be extracted
     * @return The property's value, converted to a String
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  property cannot be found
     */
    public String getProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return (getNestedProperty(bean, name));

    }


    /**
     * Return the value of the specified simple property of the specified
     * bean, converted to a String.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Name of the property to be extracted
     * @return The property's value, converted to a String
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  property cannot be found
     */
    public String getSimpleProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        Object value = getPropertyUtils().getSimpleProperty(bean, name);
        return (getConvertUtils().convert(value));

    }


    /**
     * <p>Populate the JavaBeans properties of the specified bean, based on
     * the specified name/value pairs.  This method uses Java reflection APIs
     * to identify corresponding "property setter" method names, and deals
     * with setter arguments of type <code>String</code>, <code>boolean</code>,
     * <code>int</code>, <code>long</code>, <code>float</code>, and
     * <code>double</code>.  In addition, array setters for these types (or the
     * corresponding primitive types) can also be identified.</p>
     * 
     * <p>The particular setter method to be called for each property is
     * determined using the usual JavaBeans introspection mechanisms.  Thus,
     * you may identify custom setter methods using a BeanInfo class that is
     * associated with the class of the bean itself.  If no such BeanInfo
     * class is available, the standard method name conversion ("set" plus
     * the capitalized name of the property in question) is used.</p>
     * 
     * <p><strong>NOTE</strong>:  It is contrary to the JavaBeans Specification
     * to have more than one setter method (with different argument
     * signatures) for the same property.</p>
     *
     * <p><strong>WARNING</strong> - The logic of this method is customized
     * for extracting String-based request parameters from an HTTP request.
     * It is probably not what you want for general property copying with
     * type conversion.  For that purpose, check out the
     * <code>copyProperties()</code> method instead.</p>
     *
     * @param bean JavaBean whose properties are being populated
     * @param properties Map keyed by property name, with the
     *  corresponding (String or String[]) value(s) to be set
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     */
    public void populate(Object bean, Map properties)
        throws IllegalAccessException, InvocationTargetException {

        // Do nothing unless both arguments have been specified
        if ((bean == null) || (properties == null)) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("BeanUtils.populate(" + bean + ", " +
                    properties + ")");
        }

        // Loop through the property name/value pairs to be set
        Iterator entries = properties.entrySet().iterator();
        while (entries.hasNext()) {

            // Identify the property name and value(s) to be assigned
            Map.Entry entry = (Map.Entry)entries.next();
            String name = (String) entry.getKey();
            if (name == null) {
                continue;
            }

            // Perform the assignment for this property
            setProperty(bean, name, entry.getValue());

        }

    }


    /**
     * <p>Set the specified property value, performing type conversions as
     * required to conform to the type of the destination property.</p>
     *
     * <p>If the property is read only then the method returns 
     * without throwing an exception.</p>
     *
     * <p>If <code>null</code> is passed into a property expecting a primitive value,
     * then this will be converted as if it were a <code>null</code> string.</p>
     *
     * <p><strong>WARNING</strong> - The logic of this method is customized
     * to meet the needs of <code>populate()</code>, and is probably not what
     * you want for general property copying with type conversion.  For that
     * purpose, check out the <code>copyProperty()</code> method instead.</p>
     *
     * <p><strong>WARNING</strong> - PLEASE do not modify the behavior of this
     * method without consulting with the Struts developer community.  There
     * are some subtleties to its functionality that are not documented in the
     * Javadoc description above, yet are vital to the way that Struts utilizes
     * this method.</p>
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
        throws IllegalAccessException, InvocationTargetException {

        // Trace logging (if enabled)
        if (log.isTraceEnabled()) {
            StringBuffer sb = new StringBuffer("  setProperty(");
            sb.append(bean);
            sb.append(", ");
            sb.append(name);
            sb.append(", ");
            if (value == null) {
                sb.append("<NULL>");
            } else if (value instanceof String) {
                sb.append((String) value);
            } else if (value instanceof String[]) {
                String[] values = (String[]) value;
                sb.append('[');
                for (int i = 0; i < values.length; i++) {
                    if (i > 0) {
                        sb.append(',');
                    }
                    sb.append(values[i]);
                }
                sb.append(']');
            } else {
                sb.append(value.toString());
            }
            sb.append(')');
            log.trace(sb.toString());
        }

        // Resolve any nested expression to get the actual target bean
        Object target = bean;
        Resolver resolver = getPropertyUtils().getResolver();
        while (resolver.hasNested(name)) {
            try {
                target = getPropertyUtils().getProperty(target, resolver.next(name));
                name = resolver.remove(name);
            } catch (NoSuchMethodException e) {
                return; // Skip this property setter
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("    Target bean = " + target);
            log.trace("    Target name = " + name);
        }

        // Declare local variables we will require
        String propName = resolver.getProperty(name); // Simple name of target property
        Class type = null;                            // Java type of target property
        int index  = resolver.getIndex(name);         // Indexed subscript value (if any)
        String key = resolver.getKey(name);           // Mapped key value (if any)

        // Calculate the property type
        if (target instanceof DynaBean) {
            DynaClass dynaClass = ((DynaBean) target).getDynaClass();
            DynaProperty dynaProperty = dynaClass.getDynaProperty(propName);
            if (dynaProperty == null) {
                return; // Skip this property setter
            }
            type = dynaProperty.getType();
        } else if (target instanceof Map) {
            type = Object.class;
        } else if (target != null && target.getClass().isArray() && index >= 0) {
            type = Array.get(target, index).getClass();
        } else {
            PropertyDescriptor descriptor = null;
            try {
                descriptor =
                    getPropertyUtils().getPropertyDescriptor(target, name);
                if (descriptor == null) {
                    return; // Skip this property setter
                }
            } catch (NoSuchMethodException e) {
                return; // Skip this property setter
            }
            if (descriptor instanceof MappedPropertyDescriptor) {
                if (((MappedPropertyDescriptor) descriptor).getMappedWriteMethod() == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Skipping read-only property");
                    }
                    return; // Read-only, skip this property setter
                }
                type = ((MappedPropertyDescriptor) descriptor).
                    getMappedPropertyType();
            } else if (index >= 0 && descriptor instanceof IndexedPropertyDescriptor) {
                if (((IndexedPropertyDescriptor) descriptor).getIndexedWriteMethod() == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Skipping read-only property");
                    }
                    return; // Read-only, skip this property setter
                }
                type = ((IndexedPropertyDescriptor) descriptor).
                    getIndexedPropertyType();
            } else if (key != null) {
                if (descriptor.getReadMethod() == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Skipping read-only property");
                    }
                    return; // Read-only, skip this property setter
                }
                type = (value == null) ? Object.class : value.getClass();
            } else {
                if (descriptor.getWriteMethod() == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Skipping read-only property");
                    }
                    return; // Read-only, skip this property setter
                }
                type = descriptor.getPropertyType();
            }
        }

        // Convert the specified value to the required type
        Object newValue = null;
        if (type.isArray() && (index < 0)) { // Scalar value into array
            if (value == null) {
                String[] values = new String[1];
                values[0] = null;
                newValue = getConvertUtils().convert(values, type);
            } else if (value instanceof String) {
                newValue = getConvertUtils().convert(value, type);
            } else if (value instanceof String[]) {
                newValue = getConvertUtils().convert((String[]) value, type);
            } else {
                newValue = convert(value, type);
            }
        } else if (type.isArray()) {         // Indexed value into array
            if (value instanceof String || value == null) {
                newValue = getConvertUtils().convert((String) value,
                                                type.getComponentType());
            } else if (value instanceof String[]) {
                newValue = getConvertUtils().convert(((String[]) value)[0],
                                                type.getComponentType());
            } else {
                newValue = convert(value, type.getComponentType());
            }
        } else {                             // Value into scalar
            if (value instanceof String) {
                newValue = getConvertUtils().convert((String) value, type);
            } else if (value instanceof String[]) {
                newValue = getConvertUtils().convert(((String[]) value)[0],
                                                type);
            } else {
                newValue = convert(value, type);
            }
        }

        // Invoke the setter method
        try {
          getPropertyUtils().setProperty(target, name, newValue);
        } catch (NoSuchMethodException e) {
            throw new InvocationTargetException
                (e, "Cannot set " + propName);
        }

    }
    
    /** 
     * Gets the <code>ConvertUtilsBean</code> instance used to perform the conversions.
     *
     * @return The ConvertUtils bean instance
     */
    public ConvertUtilsBean getConvertUtils() {
        return convertUtilsBean;
    }
    
    /**
     * Gets the <code>PropertyUtilsBean</code> instance used to access properties.
     *
     * @return The ConvertUtils bean instance
     */
    public PropertyUtilsBean getPropertyUtils() {
        return propertyUtilsBean;
    }

    /** 
     * If we're running on JDK 1.4 or later, initialize the cause for the given throwable.
     * 
     * @param  throwable The throwable.
     * @param  cause     The cause of the throwable.
     * @return  true if the cause was initialized, otherwise false.
     * @since 1.8.0
     */
    public boolean initCause(Throwable throwable, Throwable cause) {
        if (INIT_CAUSE_METHOD != null && cause != null) {
            try {
                INIT_CAUSE_METHOD.invoke(throwable, new Object[] { cause });
                return true;
            } catch (Throwable e) {
                return false; // can't initialize cause
            }
        }
        return false;
    }

    /**
     * <p>Convert the value to an object of the specified class (if
     * possible).</p>
     *
     * @param value Value to be converted (may be null)
     * @param type Class of the value to be converted to
     * @return The converted value
     *
     * @exception ConversionException if thrown by an underlying Converter
     * @since 1.8.0
     */
    protected Object convert(Object value, Class type) {
        Converter converter = getConvertUtils().lookup(type);
        if (converter != null) {
            log.trace("        USING CONVERTER " + converter);
            return converter.convert(type, value);
        } else {
            return value;
        }
    }

    /**
     * Returns a <code>Method<code> allowing access to
     * {@link Throwable#initCause(Throwable)} method of {@link Throwable},
     * or <code>null</code> if the method
     * does not exist.
     * 
     * @return A <code>Method<code> for <code>Throwable.initCause</code>, or
     * <code>null</code> if unavailable.
     */ 
    private static Method getInitCauseMethod() {
        try {
            Class[] paramsClasses = new Class[] { Throwable.class };
            return Throwable.class.getMethod("initCause", paramsClasses);
        } catch (NoSuchMethodException e) {
            Log log = LogFactory.getLog(BeanUtils.class);
            if (log.isWarnEnabled()) {
                log.warn("Throwable does not have initCause() method in JDK 1.3");
            }
            return null;
        } catch (Throwable e) {
            Log log = LogFactory.getLog(BeanUtils.class);
            if (log.isWarnEnabled()) {
                log.warn("Error getting the Throwable initCause() method", e);
            }
            return null;
        }
    }
}
