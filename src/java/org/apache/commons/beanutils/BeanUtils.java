/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/BeanUtils.java,v 1.1 2001/04/14 18:29:56 craigmcc Exp $
 * $Revision: 1.1 $
 * $Date: 2001/04/14 18:29:56 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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


import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;


/**
 * Utility methods for populating JavaBeans properties via reflection.
 *
 * @author Craig R. McClanahan
 * @author Ralph Schaer
 * @author Chris Audley
 * @version $Revision: 1.1 $ $Date: 2001/04/14 18:29:56 $
 */

public class BeanUtils {


    // ------------------------------------------------------ Private Variables


    /**
     * The debugging detail level for this component.
     */
    private static int debug = 0;

    public static int getDebug() {
        return (debug);
    }

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

        Class clazz = bean.getClass();
        Object newBean = clazz.newInstance();
        PropertyUtils.copyProperties(newBean, bean);
        return (newBean);

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
                    values.add(Array.get(value, i).toString());
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

        StringBuffer sb = new StringBuffer(name);
        sb.append(PropertyUtils.INDEXED_DELIM);
        sb.append(index);
        sb.append(PropertyUtils.INDEXED_DELIM2);
        Object value = PropertyUtils.getIndexedProperty(bean, name);
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

        if ((bean == null) || (properties == null))
            return;

        /*
        if (debug >= 1)
            System.out.println("BeanUtils.populate(" + bean + ", " +
                               properties + ")");
        */

        // Loop through the property name/value pairs to be set
        Iterator names = properties.keySet().iterator();
        while (names.hasNext()) {

            // Identify the property name and value(s) to be assigned
            String name = (String) names.next();
            if (name == null)
                continue;
            Object value = properties.get(name);	// String or String[]

            /*
            if (debug >= 1)
                System.out.println("  name='" + name + "', value.class='" +
                                   (value == null ? "NONE" :
                                   value.getClass().getName()) + "'");
            */

            // Get the property descriptor of the requested property (if any)
            PropertyDescriptor descriptor = null;
            try {
                descriptor = PropertyUtils.getPropertyDescriptor(bean, name);
            } catch (Throwable t) {
                /*
                if (debug >= 1)
                    System.out.println("    getPropertyDescriptor: " + t);
                */
                descriptor = null;
            }
            if (descriptor == null) {
                /*
                if (debug >= 1)
                    System.out.println("    No such property, skipping");
                */
                continue;
            }
            /*
            if (debug >= 1)
                System.out.println("    Property descriptor is '" +
                                   descriptor + "'");
            */

            // Identify the relevant setter method (if there is one)
            Method setter = null;
            if (descriptor instanceof IndexedPropertyDescriptor)
                setter = ((IndexedPropertyDescriptor) descriptor).
                    getIndexedWriteMethod();
            if (setter == null)
                setter = descriptor.getWriteMethod();
            if (setter == null) {
                /*
                if (debug >= 1)
                    System.out.println("    No setter method, skipping");
                */
                continue;
            }
            Class parameterTypes[] = setter.getParameterTypes();
            /*
            if (debug >= 1)
                System.out.println("    Setter method is '" +
                                   setter.getName() + "(" +
                                   parameterTypes[0].getName() +
                                   (parameterTypes.length > 1 ?
                                    ", " + parameterTypes[1].getName() : "" )
                                   + ")'");
            */
            Class parameterType = parameterTypes[0];
            if (parameterTypes.length > 1)
                parameterType = parameterTypes[1];      // Indexed setter

            // Convert the parameter value as required for this setter method
            Object parameters[] = new Object[1];
            if (parameterTypes[0].isArray()) {
                if (value instanceof String) {
                    String values[] = new String[1];
                    values[0] = (String) value;
                    parameters[0] = ConvertUtils.convert((String[]) values,
                    parameterType);
                } else if (value instanceof String[]) {
                    parameters[0] = ConvertUtils.convert((String[]) value,
                    parameterType);
                } else {
                    parameters[0] = value;
                }
            } else {
                if (value instanceof String) {
                    parameters[0] = ConvertUtils.convert((String) value,
                    parameterType);
                } else if (value instanceof String[]) {
                    parameters[0] = ConvertUtils.convert(((String[]) value)[0],
                    parameterType);
                } else {
                    parameters[0] = value;
                }
            }

            // Invoke the setter method
            /*
            if (debug >= 1)
                System.out.println("    Setting to " +
                                   (parameters[0] == null ? "NULL" :
                                    "'" + parameters[0] + "'"));
            */
            try {
                PropertyUtils.setProperty(bean, name, parameters[0]);
            } catch (NoSuchMethodException e) {
                /*
                if (debug >= 1) {
                    System.out.println("    CANNOT HAPPEN: " + e);
                    e.printStackTrace(System.out);
                }
                */
            }

        }

        /*
        if (debug >= 1)
            System.out.println("============================================");
        */

    }


}
