/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/BeanUtils.java,v 1.23 2002/06/05 20:46:38 rdonkin Exp $
 * $Revision: 1.23 $
 * $Date: 2002/06/05 20:46:38 $
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
 * @version $Revision: 1.23 $ $Date: 2002/06/05 20:46:38 $
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
     * Deprecated: use the log variable
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
     * Return the entire set of properties for which the specified bean
     * provides a read method.  This map can be fed back to a call to
     * <code>BeanUtils.populate()</code> to reconsitute the same set of
     * properties, modulo differences for read-only and write-only
     * properties.
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

        if (log.isDebugEnabled()) {
            log.debug("Describing bean: " + bean.getClass().getName());
        }
        if (bean == null) {
        //            return (Collections.EMPTY_MAP);
            return (new java.util.HashMap());
        }
        PropertyDescriptor descriptors[] =
                PropertyUtils.getPropertyDescriptors(bean);
        Map description = new HashMap(descriptors.length);
        for (int i = 0; i < descriptors.length; i++) {
            String name = descriptors[i].getName();
            if (descriptors[i].getReadMethod() != null)
                description.put(name, getProperty(bean, name));
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
     * Populate the JavaBeans properties of the specified bean, based on
     * the specified name/value pairs.  This method uses Java reflection APIs
     * to identify corresponding "property setter" method names, and deals
     * with setter arguments of type <code>String</code>, <code>boolean</code>,
     * <code>int</code>, <code>long</code>, <code>float</code>, and
     * <code>double</code>.  In addition, array setters for these types (or the
     * corresponding primitive types) can also be identified.
     * <p>
     * The particular setter method to be called for each property is
     * determined using the usual JavaBeans introspection mechanisms.  Thus,
     * you may identify custom setter methods using a BeanInfo class that is
     * associated with the class of the bean itself.  If no such BeanInfo
     * class is available, the standard method name conversion ("set" plus
     * the capitalized name of the property in question) is used.
     * <p>
     * <strong>NOTE</strong>:  It is contrary to the JavaBeans Specification
     * to have more than one setter method (with different argument
     * signatures) for the same property.
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
