/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/BeanUtils.java,v 1.29 2002/12/16 00:21:02 craigmcc Exp $
 * $Revision: 1.29 $
 * $Date: 2002/12/16 00:21:02 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
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

import org.apache.commons.collections.FastHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Utility methods for populating JavaBeans properties via reflection.
 *
 * @author Craig R. McClanahan
 * @author Ralph Schaer
 * @author Chris Audley
 * @author Rey François
 * @author Gregor Raýman
 * @version $Revision: 1.29 $ $Date: 2002/12/16 00:21:02 $
 */

public class BeanUtils {


    // ------------------------------------------------------ Private Variables


    /**
     * Dummy collection from the Commons Collections API, to force a
     * ClassNotFoundException if commons-collections.jar is not present in the
     * runtime classpath, and this class is the first one referenced.
     * Otherwise, the ClassNotFoundException thrown by ConvertUtils or
     * PropertyUtils can get masked.
     */
    private static FastHashMap dummy = new FastHashMap();

    /**
     * All logging goes through this logger
     */
    private static Log log = LogFactory.getLog(BeanUtils.class);

    /**
     * The debugging detail level for this component.
     * @deprecated BeanUtils now uses commons-logging for all log messages.
     *             Use your favorite logging tool to configure logging for
     *             this class.
     */
    private static int debug = 0;

    /**
     * @deprecated BeanUtils now uses commons-logging for all log messages.
     *             Use your favorite logging tool to configure logging for
     *             this class.
     */
    public static int getDebug() {
        return (debug);
    }

    /**
     * @deprecated BeanUtils now uses commons-logging for all log messages.
     *             Use your favorite logging tool to configure logging for
     *             this class.
     */
    public static void setDebug(int newDebug) {
        debug = newDebug;
    }


    // --------------------------------------------------------- Public Classes


    /**
     * Clone a bean based on the available property getters and setters,
     * even if the bean class itself does not implement Cloneable.
     *
     * @param bean Bean to be cloned
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InstantiationException if a new instance of the bean's
     *  class cannot be instantiated
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static Object cloneBean(Object bean)
            throws IllegalAccessException, InstantiationException,
            InvocationTargetException, NoSuchMethodException {

        if (log.isDebugEnabled()) {
            log.debug("Cloning bean: " + bean.getClass().getName());
        }
        Class clazz = bean.getClass();
        Object newBean = clazz.newInstance();
        PropertyUtils.copyProperties(newBean, bean);
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
     *  <code>orig</code> argument is null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NullPointerException if <code>orig</code> or
     *  <code>dest</code> is <code>null</code>
     */
    public static void copyProperties(Object dest, Object orig)
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
            DynaProperty origDescriptors[] =
                ((DynaBean) orig).getDynaClass().getDynaProperties();
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                Object value = ((DynaBean) orig).get(name);
                copyProperty(dest, name, value);
            }
        } else if (orig instanceof Map) {
            Iterator names = ((Map) orig).keySet().iterator();
            while (names.hasNext()) {
                String name = (String) names.next();
                Object value = ((Map) orig).get(name);
                copyProperty(dest, name, value);
            }
        } else /* if (not a DynaBean or a Map) */ {
            PropertyDescriptor origDescriptors[] =
                PropertyUtils.getPropertyDescriptors(orig);
            for (int i = 0; i < origDescriptors.length; i++) {
                if (origDescriptors[i].getReadMethod() == null) {
                    if (log.isTraceEnabled()) {
                        log.trace("-->No getter on JavaBean for " +
                                  origDescriptors[i].getName() + ", skipping");
                    }
                    continue;
                }
                String name = origDescriptors[i].getName();
                if ("class".equals(name)) {
                    continue; // No point in trying to set an object's class
                }
                Object value = null;
                try {
                    value = PropertyUtils.getSimpleProperty(orig, name);
                } catch (NoSuchMethodException e) {
                    log.error("-->Should not have happened", e);
                    value = null; // Can not happen
                }
                copyProperty(dest, name, value);
            }
        }

    }


    /**
     * <p>Copy the specified property value to the specified destination bean,
     * performing any type conversion that is required.  If the specified
     * bean does not have a property of the specified name, return without
     * doing anything.  If you have custom destination property types, register
     * {@link Converter}s for them by calling the <code>register()</code>
     * method of {@link ConvertUtils}.</p>
     *
     * <p><strong>FIXME</strong> - Indexed and mapped properties that do not
     * have getter and setter methods for the underlying array or Map are not
     * copied by this method.</p>
     *
     * @param bean Bean on which setting is to be performed
     * @param name Simple property name of the property to be set
     * @param value Property value to be set
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     */
    public static void copyProperty(Object bean, String name, Object value)
        throws IllegalAccessException, InvocationTargetException {

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
                String values[] = (String[]) value;
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

        if (bean instanceof DynaBean) {
            DynaProperty propDescriptor =
                ((DynaBean) bean).getDynaClass().getDynaProperty(name);
            if (propDescriptor != null) {
                Converter converter =
                    ConvertUtils.lookup(propDescriptor.getType());
                if (converter != null) {
                    value = converter.convert(propDescriptor.getType(), value);
                }
                try {
                    PropertyUtils.setSimpleProperty(bean, name, value);
                } catch (NoSuchMethodException e) {
                    log.error("-->Should not have happened", e);
                    ; // Silently ignored
                }
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("-->No setter on 'to' DynaBean, skipping");
                }
            }
        } else /* if (!(bean instanceof DynaBean)) */ {
            PropertyDescriptor propDescriptor = null;
            try {
                propDescriptor =
                    PropertyUtils.getPropertyDescriptor(bean, name);
            } catch (NoSuchMethodException e) {
                propDescriptor = null;
            }
            if ((propDescriptor != null) &&
                (propDescriptor.getWriteMethod() == null)) {
                propDescriptor = null;
            }
            if (propDescriptor != null) {
                Converter converter =
                    ConvertUtils.lookup(propDescriptor.getPropertyType());
                if (converter != null) {
                    value = converter.convert
                        (propDescriptor.getPropertyType(), value);
                }
                try {
                    PropertyUtils.setSimpleProperty(bean, name, value);
                } catch (NoSuchMethodException e) {
                    log.error("-->Should not have happened", e);
                    ; // Silently ignored
                }
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("-->No setter on JavaBean, skipping");
                }
            }
        }

    }


    /**
     * Return the entire set of properties for which the specified bean
     * provides a read method.  This map can be fed back to a call to
     * <code>BeanUtils.populate()</code> to reconsitute the same set of
     * properties, modulo differences for read-only and write-only
     * properties, but only if there are no indexed properties.
     *
     * @param bean Bean whose properties are to be extracted
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static Map describe(Object bean)
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
            DynaProperty descriptors[] =
                ((DynaBean) bean).getDynaClass().getDynaProperties();
            for (int i = 0; i < descriptors.length; i++) {
                String name = descriptors[i].getName();
                description.put(name, getProperty(bean, name));
            }
        } else {
            PropertyDescriptor descriptors[] =
                PropertyUtils.getPropertyDescriptors(bean);
            for (int i = 0; i < descriptors.length; i++) {
                String name = descriptors[i].getName();
                if (descriptors[i].getReadMethod() != null)
                    description.put(name, getProperty(bean, name));
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
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static String[] getArrayProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        Object value = PropertyUtils.getProperty(bean, name);
        if (value == null) {
            return (null);
        } else if (value instanceof Collection) {
            ArrayList values = new ArrayList();
            Iterator items = ((Collection) value).iterator();
            while (items.hasNext()) {
                Object item = items.next();
                if (item == null)
                    values.add((String) null);
                else
                    values.add(item.toString());
            }
            return ((String[]) values.toArray(new String[values.size()]));
        } else if (value.getClass().isArray()) {
            ArrayList values = new ArrayList();
            try {
                int n = Array.getLength(value);
                for (int i = 0; i < n; i++) {
                    Object item = Array.get(value, i);
                    if (item == null)
                        values.add((String) null);
                    else
                        values.add(item.toString());
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                ;
            }
            return ((String[]) values.toArray(new String[values.size()]));
        } else {
            String results[] = new String[1];
            results[0] = value.toString();
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
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static String getIndexedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        Object value = PropertyUtils.getIndexedProperty(bean, name);
        return (ConvertUtils.convert(value));

    }


    /**
     * Return the value of the specified indexed property of the specified
     * bean, as a String.  The index is specified as a method parameter and
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
    public static String getIndexedProperty(Object bean,
                                            String name, int index)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        Object value = PropertyUtils.getIndexedProperty(bean, name, index);
        return (ConvertUtils.convert(value));

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
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static String getMappedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        Object value = PropertyUtils.getMappedProperty(bean, name);
        return (ConvertUtils.convert(value));

    }


    /**
     * Return the value of the specified mapped property of the specified
     * bean, as a String.  The key is specified as a method parameter and
     * must *not* be included in the property name expression
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
    public static String getMappedProperty(Object bean,
                                           String name, String key)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        Object value = PropertyUtils.getMappedProperty(bean, name, key);
        return (ConvertUtils.convert(value));

    }


    /**
     * Return the value of the (possibly nested) property of the specified
     * name, for the specified bean, as a String.
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
    public static String getNestedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        Object value = PropertyUtils.getNestedProperty(bean, name);
        return (ConvertUtils.convert(value));

    }


    /**
     * Return the value of the specified property of the specified bean,
     * no matter which property reference format is used, as a String.
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
    public static String getProperty(Object bean, String name)
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
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static String getSimpleProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        Object value = PropertyUtils.getSimpleProperty(bean, name);
        return (ConvertUtils.convert(value));

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
    public static void populate(Object bean, Map properties)
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
        Iterator names = properties.keySet().iterator();
        while (names.hasNext()) {

            // Identify the property name and value(s) to be assigned
            String name = (String) names.next();
            if (name == null) {
                continue;
            }
            Object value = properties.get(name);

            // Perform the assignment for this property
            setProperty(bean, name, value);

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
     * @param bean Bean on which setting is to be performed
     * @param name Property name (can be nested/indexed/mapped/combo)
     * @param value Value to be set
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     */
    public static void setProperty(Object bean, String name, Object value)
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
                String values[] = (String[]) value;
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
        int delim = name.lastIndexOf(PropertyUtils.NESTED_DELIM);
        if (delim >= 0) {
            try {
                target =
                    PropertyUtils.getProperty(bean, name.substring(0, delim));
            } catch (NoSuchMethodException e) {
                return; // Skip this property setter
            }
            name = name.substring(delim + 1);
            if (log.isTraceEnabled()) {
                log.trace("    Target bean = " + target);
                log.trace("    Target name = " + name);
            }
        }

        // Declare local variables we will require
        String propName = null;          // Simple name of target property
        Class type = null;               // Java type of target property
        int index = -1;                  // Indexed subscript value (if any)
        String key = null;               // Mapped key value (if any)

        // Calculate the property name, index, and key values
        propName = name;
        int i = propName.indexOf(PropertyUtils.INDEXED_DELIM);
        if (i >= 0) {
            int k = propName.indexOf(PropertyUtils.INDEXED_DELIM2);
            try {
                index =
                    Integer.parseInt(propName.substring(i + 1, k));
            } catch (NumberFormatException e) {
                ;
            }
            propName = propName.substring(0, i);
        }
        int j = propName.indexOf(PropertyUtils.MAPPED_DELIM);
        if (j >= 0) {
            int k = propName.indexOf(PropertyUtils.MAPPED_DELIM2);
            try {
                key = propName.substring(j + 1, k);
            } catch (IndexOutOfBoundsException e) {
                ;
            }
            propName = propName.substring(0, j);
        }

        // Calculate the property type
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
                    PropertyUtils.getPropertyDescriptor(target, name);
                if (descriptor == null) {
                    return; // Skip this property setter
                }
            } catch (NoSuchMethodException e) {
                return; // Skip this property setter
            }
            if (descriptor instanceof MappedPropertyDescriptor) {
                if (((MappedPropertyDescriptor) descriptor).getMappedWriteMethod() == null) {
                    log.debug("Skipping read-only property");
                    return; // Read-only, skip this property setter
                }
                type = ((MappedPropertyDescriptor) descriptor).
                    getMappedPropertyType();
            } else if (descriptor instanceof IndexedPropertyDescriptor) {
                if (((IndexedPropertyDescriptor) descriptor).getIndexedWriteMethod() == null) {
                    log.debug("Skipping read-only property");
                    return; // Read-only, skip this property setter
                }
                type = ((IndexedPropertyDescriptor) descriptor).
                    getIndexedPropertyType();
            } else {
                if (descriptor.getWriteMethod() == null) {
                    log.debug("Skipping read-only property");
                    return; // Read-only, skip this property setter
                }
                type = descriptor.getPropertyType();
            }
        }

        // Convert the specified value to the required type
        Object newValue = null;
        if (type.isArray() && (index < 0)) { // Scalar value into array
            if (value instanceof String) {
                String values[] = new String[1];
                values[0] = (String) value;
                newValue = ConvertUtils.convert((String[]) values, type);
            } else if (value instanceof String[]) {
                newValue = ConvertUtils.convert((String[]) value, type);
            } else {
                newValue = value;
            }
        } else if (type.isArray()) {         // Indexed value into array
            if (value instanceof String) {
                newValue = ConvertUtils.convert((String) value,
                                                type.getComponentType());
            } else if (value instanceof String[]) {
                newValue = ConvertUtils.convert(((String[]) value)[0],
                                                type.getComponentType());
            } else {
                newValue = value;
            }
        } else {                             // Value into scalar
            if (value instanceof String || (value == null && type.isPrimitive())) {
                newValue = ConvertUtils.convert((String) value, type);
            } else if (value instanceof String[]) {
                newValue = ConvertUtils.convert(((String[]) value)[0],
                                                type);
            } else {
                newValue = value;
            }
        }

        // Invoke the setter method
        try {
            if (index >= 0) {
                PropertyUtils.setIndexedProperty(target, propName,
                                                 index, newValue);
            } else if (key != null) {
                PropertyUtils.setMappedProperty(target, propName,
                                                key, newValue);
            } else {
                PropertyUtils.setProperty(target, propName, newValue);
            }
        } catch (NoSuchMethodException e) {
            throw new InvocationTargetException
                (e, "Cannot set " + propName);
        }

    }


}
