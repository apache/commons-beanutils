/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//beanutils/src/java/org/apache/commons/beanutils/PropertyUtils.java,v 1.24 2002/04/28 01:16:48 craigmcc Exp $
 * $Revision: 1.24 $
 * $Date: 2002/04/28 01:16:48 $
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


import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.FastHashMap;


/**
 * Utility methods for using Java Reflection APIs to facilitate generic
 * property getter and setter operations on Java objects.  Much of this
 * code was originally included in <code>BeanUtils</code>, but has been
 * separated because of the volume of code involved.
 * <p>
 * In general, the objects that are examined and modified using these
 * methods are expected to conform to the property getter and setter method
 * naming conventions described in the JavaBeans Specification (Version 1.0.1).
 * No data type conversions are performed, and there are no usage of any
 * <code>PropertyEditor</code> classes that have been registered, although
 * a convenient way to access the registered classes themselves is included.
 * <p>
 * For the purposes of this class, five formats for referencing a particular
 * property value of a bean are defined, with the layout of an identifying
 * String in parentheses:
 * <ul>
 * <li><strong>Simple (<code>name</code>)</strong> - The specified
 *     <code>name</code> identifies an individual property of a particular
 *     JavaBean.  The name of the actual getter or setter method to be used
 *     is determined using standard JavaBeans instrospection, so that (unless
 *     overridden by a <code>BeanInfo</code> class, a property named "xyz"
 *     will have a getter method named <code>getXyz()</code> or (for boolean
 *     properties only) <code>isXyz()</code>, and a setter method named
 *     <code>setXyz()</code>.</li>
 * <li><strong>Nested (<code>name1.name2.name3</code>)</strong> The first
 *     name element is used to select a property getter, as for simple
 *     references above.  The object returned for this property is then
 *     consulted, using the same approach, for a property getter for a
 *     property named <code>name2</code>, and so on.  The property value that
 *     is ultimately retrieved or modified is the one identified by the
 *     last name element.</li>
 * <li><strong>Indexed (<code>name[index]</code>)</strong> - The underlying
 *     property value is assumed to be an array, or this JavaBean is assumed
 *     to have indexed property getter and setter methods.  The appropriate
 *     (zero-relative) entry in the array is selected.  <code>List</code>
 *     objects are now also supported for read/write.  You simply need to define
 *     a getter that returns the <code>List</code></li>
 * <li><strong>Mapped (<code>name(key)</code>)</strong> - The JavaBean
 *     is assumed to have an property getter and setter methods with an
 *     additional attribute of type <code>java.lang.String</code>.</li>
 * <li><strong>Combined (<code>name1.name2[index].name3(key)</code>)</strong> -
 *     Combining mapped, nested, and indexed references is also
 *     supported.</li>
 * </ul>
 *
 * @author Craig R. McClanahan
 * @author Ralph Schaer
 * @author Chris Audley
 * @author Rey François
 * @author Gregor Raýman
 * @author Jan Sorensen
 * @author Scott Sanders
 * @version $Revision: 1.24 $ $Date: 2002/04/28 01:16:48 $
 */

public class PropertyUtils {


    // ----------------------------------------------------- Manifest Constants


    /**
     * The delimiter that preceeds the zero-relative subscript for an
     * indexed reference.
     */
    public static final char INDEXED_DELIM = '[';


    /**
     * The delimiter that follows the zero-relative subscript for an
     * indexed reference.
     */
    public static final char INDEXED_DELIM2 = ']';


    /**
     * The delimiter that preceeds the key of a mapped property.
     */
    public static final char MAPPED_DELIM = '(';


    /**
     * The delimiter that follows the key of a mapped property.
     */
    public static final char MAPPED_DELIM2 = ')';


    /**
     * The delimiter that separates the components of a nested reference.
     */
    public static final char NESTED_DELIM = '.';


    // ------------------------------------------------------- Static Variables


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


    /**
     * The cache of PropertyDescriptor arrays for beans we have already
     * introspected, keyed by the java.lang.Class of this object.
     */
    private static FastHashMap descriptorsCache = null;
    private static FastHashMap mappedDescriptorsCache = null;

    static {
        descriptorsCache = new FastHashMap();
        descriptorsCache.setFast(true);
        mappedDescriptorsCache = new FastHashMap();
        mappedDescriptorsCache.setFast(true);
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Clear any cached property descriptors information for all classes
     * loaded by any class loaders.  This is useful in cases where class
     * loaders are thrown away to implement class reloading.
     */
    public static void clearDescriptors() {

        descriptorsCache.clear();
        mappedDescriptorsCache.clear();
        Introspector.flushCaches();

    }


    /**
     * Copy property values from the "origin" bean to the "destination" bean
     * for all cases where the property names are the same (even though the
     * actual getter and setter methods might have been customized via
     * <code>BeanInfo</code> classes).  No conversions are performed on the
     * actual property values -- it is assumed that the values retrieved from
     * the origin bean are assignment-compatible with the types expected by
     * the destination bean.
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
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static void copyProperties(Object dest, Object orig)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (dest == null) {
            throw new IllegalArgumentException
                    ("No destination bean specified");
        }
        if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        }

        PropertyDescriptor origDescriptors[] = getPropertyDescriptors(orig);
        for (int i = 0; i < origDescriptors.length; i++) {
            Method readMethod = origDescriptors[i].getReadMethod();
            if ((readMethod == null) &&
                (origDescriptors[i] instanceof IndexedPropertyDescriptor)) {
                readMethod =
                    ((IndexedPropertyDescriptor) origDescriptors[i]).
                    getIndexedReadMethod();
            }
            if (readMethod == null) {
                continue; // This is a write-only property
            }
            String name = origDescriptors[i].getName();
            if (getPropertyDescriptor(dest, name) != null) {
                Object value = getSimpleProperty(orig, name);
                try {
                    setSimpleProperty(dest, name, value);
                } catch (NoSuchMethodException e) {
                    ;   // Skip non-matching property
                }
            }
        }

    }


    /**
     * <p>Return the entire set of properties for which the specified bean
     * provides a read method.  This map contains the unconverted property
     * values for all properties for which a read method is provided
     * (i.e. where the <code>getReadMethod()</code> returns non-null).</p>
     *
     * <p><strong>FIXME</strong> - Does not account for mapped properties.</p>
     *
     * <p><strong>FIXME</strong> - Does not work with DynaBeans.</p>
     *
     * @param bean Bean whose properties are to be extracted
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> is null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static Map describe(Object bean)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
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
     * Return the value of the specified indexed property of the specified
     * bean, with no type conversions.  The zero-relative index of the
     * required value must be included (in square brackets) as a suffix to
     * the property name, or <code>IllegalArgumentException</code> will be
     * thrown.  In addition to supporting the JavaBeans specification, this
     * method has been extended to support <code>List</code> objects as well.
     *
     * @param bean Bean whose property is to be extracted
     * @param name <code>propertyname[index]</code> of the property value
     *  to be extracted
     *
     * @exception ArrayIndexOutOfBoundsException if the specified index
     *  is outside the valid range for the underlying array
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static Object getIndexedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }

        // Identify the index of the requested individual property
        int delim = name.indexOf(INDEXED_DELIM);
        int delim2 = name.indexOf(INDEXED_DELIM2);
        if ((delim < 0) || (delim2 <= delim)) {
            throw new IllegalArgumentException("Invalid indexed property '" +
                    name + "'");
        }
        int index = -1;
        try {
            String subscript = name.substring(delim + 1, delim2);
            index = Integer.parseInt(subscript);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid indexed property '" +
                    name + "'");
        }
        name = name.substring(0, delim);

        // Request the specified indexed property value
        return (getIndexedProperty(bean, name, index));

    }


    /**
     * Return the value of the specified indexed property of the specified
     * bean, with no type conversions.  In addition to supporting the JavaBeans
     * specification, this method has been extended to support
     * <code>List</code> objects as well.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Simple property name of the property value to be extracted
     * @param index Index of the property value to be extracted
     *
     * @exception ArrayIndexOutOfBoundsException if the specified index
     *  is outside the valid range for the underlying array
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static Object getIndexedProperty(Object bean,
                                            String name, int index)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }

        // Handle DynaBean instances specially
        if (bean instanceof DynaBean) {
            DynaProperty descriptor =
                    ((DynaBean) bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" +
                        name + "'");
            }
            return (((DynaBean) bean).get(name, index));
        }

        // Retrieve the property descriptor for the specified property
        PropertyDescriptor descriptor =
                getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" +
                    name + "'");
        }

        // Call the indexed getter method if there is one
        if (descriptor instanceof IndexedPropertyDescriptor) {
            Method readMethod = ((IndexedPropertyDescriptor) descriptor).
                    getIndexedReadMethod();
            if (readMethod != null) {
                Object subscript[] = new Object[1];
                subscript[0] = new Integer(index);
                try {
                    return (readMethod.invoke(bean, subscript));
                } catch (InvocationTargetException e) {
                    if (e.getTargetException() instanceof
                            ArrayIndexOutOfBoundsException) {
                        throw (ArrayIndexOutOfBoundsException)
                                e.getTargetException();
                    } else {
                        throw e;
                    }
                }
            }
        }

        // Otherwise, the underlying property must be an array
        Method readMethod = getReadMethod(descriptor);
        if (readMethod == null) {
            throw new NoSuchMethodException("Property '" + name +
                    "' has no getter method");
        }

        // Call the property getter and return the value
        Object value = readMethod.invoke(bean, new Object[0]);
        if (!value.getClass().isArray()) {
            if (!(value instanceof java.util.List)) {
                throw new IllegalArgumentException("Property '" + name
                        + "' is not indexed");
            } else {
                //get the List's value
                return ((java.util.List) value).get(index);
            }
        } else {
            //get the array's value
            return (Array.get(value, index));
        }

    }


    /**
     * Return the value of the specified mapped property of the
     * specified bean, with no type conversions.  The key of the
     * required value must be included (in brackets) as a suffix to
     * the property name, or <code>IllegalArgumentException</code> will be
     * thrown.
     *
     * @param bean Bean whose property is to be extracted
     * @param name <code>propertyname(key)</code> of the property value
     *  to be extracted
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static Object getMappedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }

        // Identify the index of the requested individual property
        int delim = name.indexOf(MAPPED_DELIM);
        int delim2 = name.indexOf(MAPPED_DELIM2);
        if ((delim < 0) || (delim2 <= delim)) {
            throw new IllegalArgumentException
                    ("Invalid mapped property '" + name + "'");
        }

        // Isolate the name and the key
        String key = name.substring(delim + 1, delim2);
        name = name.substring(0, delim);

        // Request the specified indexed property value
        return (getMappedProperty(bean, name, key));

    }


    /**
     * Return the value of the specified mapped property of the specified
     * bean, with no type conversions.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Mapped property name of the property value to be extracted
     * @param key Key of the property value to be extracted
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static Object getMappedProperty(Object bean,
                                           String name, String key)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }
        if (key == null) {
            throw new IllegalArgumentException("No key specified");
        }

        // Handle DynaBean instances specially
        if (bean instanceof DynaBean) {
            DynaProperty descriptor =
                    ((DynaBean) bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" +
                        name + "'");
            }
            return (((DynaBean) bean).get(name, key));
        }

        Object result = null;

        // Retrieve the property descriptor for the specified property
        PropertyDescriptor descriptor = getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" +
                    name + "'");
        }

        if (descriptor instanceof MappedPropertyDescriptor) {
            // Call the keyed getter method if there is one
            Method readMethod = ((MappedPropertyDescriptor) descriptor).
                    getMappedReadMethod();
            if (readMethod != null) {
                Object keyArray[] = new Object[1];
                keyArray[0] = key;
                result = readMethod.invoke(bean, keyArray);
            } else {
                throw new NoSuchMethodException("Property '" + name +
                        "' has no mapped getter method");
            }
        } else {
          /* means that the result has to be retrieved from a map */
          Method readMethod = descriptor.getReadMethod();
          if (readMethod != null) {
            Object invokeResult = readMethod.invoke(bean, new Object[0]);
            /* test and fetch from the map */
            if (invokeResult instanceof java.util.Map) {
              result = ((java.util.Map)invokeResult).get(key);
            }
          } else {
            throw new NoSuchMethodException("Property '" + name +
                    "' has no mapped getter method");
          }
        }
        return result;

    }


    /**
     * <p>Return the mapped property descriptors for this bean class.</p>
     *
     * <p><strong>FIXME</strong> - Does not work with DynaBeans.</p>
     *
     * @param beanClass Bean class to be introspected
     */
    public static FastHashMap getMappedPropertyDescriptors(Class beanClass) {

        if (beanClass == null) {
            return null;
        }

        // Look up any cached descriptors for this bean class
        return (FastHashMap) mappedDescriptorsCache.get(beanClass);

    }


    /**
     * <p>Return the mapped property descriptors for this bean.</p>
     *
     * <p><strong>FIXME</strong> - Does not work with DynaBeans.</p>
     *
     * @param bean Bean to be introspected
     */
    public static FastHashMap getMappedPropertyDescriptors(Object bean) {

        if (bean == null) {
            return null;
        }
        return (getMappedPropertyDescriptors(bean.getClass()));

    }


    /**
     * Return the value of the (possibly nested) property of the specified
     * name, for the specified bean, with no type conversions.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly nested name of the property to be extracted
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception IllegalArgumentException if a nested reference to a
     *  property returns null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static Object getNestedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }

        int indexOfINDEXED_DELIM = -1;
        int indexOfMAPPED_DELIM = -1;
        while (true) {
            int period = name.indexOf(NESTED_DELIM);
            if (period < 0) {
                break;
            }
            String next = name.substring(0, period);
            indexOfINDEXED_DELIM = next.indexOf(INDEXED_DELIM);
            indexOfMAPPED_DELIM = next.indexOf(MAPPED_DELIM);
            if (indexOfMAPPED_DELIM >= 0 &&
                    (indexOfINDEXED_DELIM < 0 ||
                    indexOfMAPPED_DELIM < indexOfINDEXED_DELIM)) {
                bean = getMappedProperty(bean, next);
            } else {
                if (indexOfINDEXED_DELIM >= 0) {
                    bean = getIndexedProperty(bean, next);
                } else {
                    bean = getSimpleProperty(bean, next);
                }
            }
            if (bean == null) {
                throw new IllegalArgumentException
                        ("Null property value for '" +
                        name.substring(0, period) + "'");
            }
            name = name.substring(period + 1);
        }

        indexOfINDEXED_DELIM = name.indexOf(INDEXED_DELIM);
        indexOfMAPPED_DELIM = name.indexOf(MAPPED_DELIM);

        if (indexOfMAPPED_DELIM >= 0 &&
                (indexOfINDEXED_DELIM < 0 ||
                indexOfMAPPED_DELIM < indexOfINDEXED_DELIM)) {
            bean = getMappedProperty(bean, name);
        } else {
            if (indexOfINDEXED_DELIM >= 0) {
                bean = getIndexedProperty(bean, name);
            } else {
                bean = getSimpleProperty(bean, name);
            }
        }
        return bean;

    }


    /**
     * Return the value of the specified property of the specified bean,
     * no matter which property reference format is used, with no
     * type conversions.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly indexed and/or nested name of the property
     *  to be extracted
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static Object getProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return (getNestedProperty(bean, name));

    }


    /**
     * <p>Retrieve the property descriptor for the specified property of the
     * specified bean, or return <code>null</code> if there is no such
     * descriptor.  This method resolves indexed and nested property
     * references in the same manner as other methods in this class, except
     * that if the last (or only) name element is indexed, the descriptor
     * for the last resolved property itself is returned.</p>
     *
     * <p><strong>FIXME</strong> - Does not work with DynaBeans.</p>
     *
     * @param bean Bean for which a property descriptor is requested
     * @param name Possibly indexed and/or nested name of the property for
     *  which a property descriptor is requested
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception IllegalArgumentException if a nested reference to a
     *  property returns null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static PropertyDescriptor getPropertyDescriptor(Object bean,
                                                           String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }

        // Resolve nested references
        while (true) {
            int period = name.indexOf(NESTED_DELIM);
            if (period < 0) {
                break;
            }
            String next = name.substring(0, period);
            int indexOfINDEXED_DELIM = next.indexOf(INDEXED_DELIM);
            int indexOfMAPPED_DELIM = next.indexOf(MAPPED_DELIM);
            if (indexOfMAPPED_DELIM >= 0 &&
                    (indexOfINDEXED_DELIM < 0 ||
                    indexOfMAPPED_DELIM < indexOfINDEXED_DELIM)) {
                bean = getMappedProperty(bean, next);
            } else {
                if (indexOfINDEXED_DELIM >= 0) {
                    bean = getIndexedProperty(bean, next);
                } else {
                    bean = getSimpleProperty(bean, next);
                }
            }
            if (bean == null) {
                throw new IllegalArgumentException
                        ("Null property value for '" +
                        name.substring(0, period) + "'");
            }
            name = name.substring(period + 1);
        }

        // Remove any subscript from the final name value
        int left = name.indexOf(INDEXED_DELIM);
        if (left >= 0) {
            name = name.substring(0, left);
        }
        left = name.indexOf(MAPPED_DELIM);
        if (left >= 0) {
            name = name.substring(0, left);
        }

        // Look up and return this property from our cache
        // creating and adding it to the cache if not found.
        if ((bean == null) || (name == null)) {
            return (null);
        }

        PropertyDescriptor descriptors[] = getPropertyDescriptors(bean);
        if (descriptors != null) {
            for (int i = 0; i < descriptors.length; i++) {
                if (name.equals(descriptors[i].getName()))
                    return (descriptors[i]);
            }
        }

        PropertyDescriptor result = null;
        FastHashMap mappedDescriptors =
                getMappedPropertyDescriptors(bean);
        if (mappedDescriptors != null) {
            result = (PropertyDescriptor) mappedDescriptors.get(name);
        }
        if (result == null) {
            // not found, try to create it
            try {
                result =
                        new MappedPropertyDescriptor(name, bean.getClass());
            } catch (IntrospectionException ie) {
            }
        }
        if (result != null) {
            mappedDescriptorsCache.put(name, result);
        }
        return result;

    }


    /**
     * <p>Retrieve the property descriptors for the specified class,
     * introspecting and caching them the first time a particular bean class
     * is encountered.</p>
     *
     * <p><strong>FIXME</strong> - Does not work with DynaBeans.</p>
     *
     * @param beanClass Bean class for which property descriptors are requested
     *
     * @exception IllegalArgumentException if <code>beanClass</code> is null
     */
    public static PropertyDescriptor[]
            getPropertyDescriptors(Class beanClass) {

        if (beanClass == null) {
            throw new IllegalArgumentException("No bean class specified");
        }

        // Look up any cached descriptors for this bean class
        PropertyDescriptor descriptors[] = null;
        descriptors =
                (PropertyDescriptor[]) descriptorsCache.get(beanClass);
        if (descriptors != null) {
            return (descriptors);
        }

        // Introspect the bean and cache the generated descriptors
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(beanClass);
        } catch (IntrospectionException e) {
            return (new PropertyDescriptor[0]);
        }
        descriptors = beanInfo.getPropertyDescriptors();
        if (descriptors == null) {
            descriptors = new PropertyDescriptor[0];
        }
        descriptorsCache.put(beanClass, descriptors);
        return (descriptors);

    }


    /**
     * <p>Retrieve the property descriptors for the specified bean,
     * introspecting and caching them the first time a particular bean class
     * is encountered.</p>
     *
     * <p><strong>FIXME</strong> - Does not work with DynaBeans.</p>
     *
     * @param bean Bean for which property descriptors are requested
     *
     * @exception IllegalArgumentException if <code>bean</code> is null
     */
    public static PropertyDescriptor[] getPropertyDescriptors(Object bean) {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        return (getPropertyDescriptors(bean.getClass()));

    }


    /**
     * <p>Return the Java Class repesenting the property editor class that has
     * been registered for this property (if any).  This method follows the
     * same name resolution rules used by <code>getPropertyDescriptor()</code>,
     * so if the last element of a name reference is indexed, the property
     * editor for the underlying property's class is returned.</p>
     * 
     * <p>Note that <code>null</code> will be returned if there is no property,
     * or if there is no registered property editor class.  Because this
     * return value is ambiguous, you should determine the existence of the
     * property itself by other means.</p>
     *
     * <p><strong>FIXME</strong> - Does not work with DynaBeans.</p>
     *
     * @param bean Bean for which a property descriptor is requested
     * @param name Possibly indexed and/or nested name of the property for
     *  which a property descriptor is requested
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception IllegalArgumentException if a nested reference to a
     *  property returns null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static Class getPropertyEditorClass(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }

        PropertyDescriptor descriptor =
                getPropertyDescriptor(bean, name);
        if (descriptor != null) {
            return (descriptor.getPropertyEditorClass());
        } else {
            return (null);
        }

    }


    /**
     * Return the Java Class representing the property type of the specified
     * property, or <code>null</code> if there is no such property for the
     * specified bean.  This method follows the same name resolution rules
     * used by <code>getPropertyDescriptor()</code>, so if the last element
     * of a name reference is indexed, the type of the property itself will
     * be returned.  If the last (or only) element has no property with the
     * specified name, <code>null</code> is returned.
     *
     * @param bean Bean for which a property descriptor is requested
     * @param name Possibly indexed and/or nested name of the property for
     *  which a property descriptor is requested
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception IllegalArgumentException if a nested reference to a
     *  property returns null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static Class getPropertyType(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }

        // Special handling for DynaBeans
        if (bean instanceof DynaBean) {
            DynaProperty descriptor =
                    ((DynaBean) bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                return (null);
            }
            Class type = descriptor.getType();
            if (type == null) {
                return (null);
            } else if (type.isArray()) {
                return (type.getComponentType());
            } else {
                return (type);
            }
        }

        PropertyDescriptor descriptor =
                getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            return (null);
        } else if (descriptor instanceof IndexedPropertyDescriptor) {
            return (((IndexedPropertyDescriptor) descriptor).
                    getIndexedPropertyType());
        } else {
            return (descriptor.getPropertyType());
        }

    }


    /**
     * <p>Return an accessible property getter method for this property,
     * if there is one; otherwise return <code>null</code>.</p>
     *
     * <p><strong>FIXME</strong> - Does not work with DynaBeans.</p>
     *
     * @param descriptor Property descriptor to return a getter for
     */
    public static Method getReadMethod(PropertyDescriptor descriptor) {

        return (MethodUtils.getAccessibleMethod(descriptor.getReadMethod()));

    }


    /**
     * Return the value of the specified simple property of the specified
     * bean, with no type conversions.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Name of the property to be extracted
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception IllegalArgumentException if the property name
     *  is nested or indexed
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static Object getSimpleProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }

        // Validate the syntax of the property name
        if (name.indexOf(NESTED_DELIM) >= 0) {
            throw new IllegalArgumentException
                    ("Nested property names are not allowed");
        } else if (name.indexOf(INDEXED_DELIM) >= 0) {
            throw new IllegalArgumentException
                    ("Indexed property names are not allowed");
        } else if (name.indexOf(MAPPED_DELIM) >= 0) {
            throw new IllegalArgumentException
                    ("Mapped property names are not allowed");
        }

        // Handle DynaBean instances specially
        if (bean instanceof DynaBean) {
            DynaProperty descriptor =
                    ((DynaBean) bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" +
                        name + "'");
            }
            return (((DynaBean) bean).get(name));
        }

        // Retrieve the property getter method for the specified property
        PropertyDescriptor descriptor =
                getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" +
                    name + "'");
        }
        Method readMethod = getReadMethod(descriptor);
        if (readMethod == null) {
            throw new NoSuchMethodException("Property '" + name +
                    "' has no getter method");
        }

        // Call the property getter and return the value
        Object value = readMethod.invoke(bean, new Object[0]);
        return (value);

    }


    /**
     * <p>Return an accessible property setter method for this property,
     * if there is one; otherwise return <code>null</code>.</p>
     *
     * <p><strong>FIXME</strong> - Does not work with DynaBeans.</p>
     *
     * @param descriptor Property descriptor to return a setter for
     */
    public static Method getWriteMethod(PropertyDescriptor descriptor) {

        return (MethodUtils.getAccessibleMethod(descriptor.getWriteMethod()));

    }


    /**
     * Set the value of the specified indexed property of the specified
     * bean, with no type conversions.  The zero-relative index of the
     * required value must be included (in square brackets) as a suffix to
     * the property name, or <code>IllegalArgumentException</code> will be
     * thrown.  In addition to supporting the JavaBeans specification, this
     * method has been extended to support <code>List</code> objects as well.
     *
     * @param bean Bean whose property is to be modified
     * @param name <code>propertyname[index]</code> of the property value
     *  to be modified
     * @param value Value to which the specified property element
     *  should be set
     *
     * @exception ArrayIndexOutOfBoundsException if the specified index
     *  is outside the valid range for the underlying array
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static void setIndexedProperty(Object bean, String name,
                                          Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }

        // Identify the index of the requested individual property
        int delim = name.indexOf(INDEXED_DELIM);
        int delim2 = name.indexOf(INDEXED_DELIM2);
        if ((delim < 0) || (delim2 <= delim)) {
            throw new IllegalArgumentException("Invalid indexed property '" +
                    name + "'");
        }
        int index = -1;
        try {
            String subscript = name.substring(delim + 1, delim2);
            index = Integer.parseInt(subscript);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid indexed property '" +
                    name + "'");
        }
        name = name.substring(0, delim);

        // Set the specified indexed property value
        setIndexedProperty(bean, name, index, value);

    }


    /**
     * Set the value of the specified indexed property of the specified
     * bean, with no type conversions.  In addition to supporting the JavaBeans
     * specification, this method has been extended to support
     * <code>List</code> objects as well.
     *
     * @param bean Bean whose property is to be set
     * @param name Simple property name of the property value to be set
     * @param index Index of the property value to be set
     * @param value Value to which the indexed property element is to be set
     *
     * @exception ArrayIndexOutOfBoundsException if the specified index
     *  is outside the valid range for the underlying array
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static void setIndexedProperty(Object bean, String name,
                                          int index, Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }

        // Handle DynaBean instances specially
        if (bean instanceof DynaBean) {
            DynaProperty descriptor =
                    ((DynaBean) bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" +
                        name + "'");
            }
            ((DynaBean) bean).set(name, index, value);
            return;
        }

        // Retrieve the property descriptor for the specified property
        PropertyDescriptor descriptor =
                getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" +
                    name + "'");
        }

        // Call the indexed setter method if there is one
        if (descriptor instanceof IndexedPropertyDescriptor) {
            Method writeMethod = ((IndexedPropertyDescriptor) descriptor).
                    getIndexedWriteMethod();
            if (writeMethod != null) {
                Object subscript[] = new Object[2];
                subscript[0] = new Integer(index);
                subscript[1] = value;
                try {
                    writeMethod.invoke(bean, subscript);
                } catch (InvocationTargetException e) {
                    if (e.getTargetException() instanceof
                            ArrayIndexOutOfBoundsException) {
                        throw (ArrayIndexOutOfBoundsException)
                                e.getTargetException();
                    } else {
                        throw e;
                    }
                }
                return;
            }
        }

        // Otherwise, the underlying property must be an array or a list
        Method readMethod = descriptor.getReadMethod();
        if (readMethod == null) {
            throw new NoSuchMethodException("Property '" + name +
                    "' has no getter method");
        }

        // Call the property getter to get the array or list
        Object array = readMethod.invoke(bean, new Object[0]);
        if (!array.getClass().isArray()) {
            if (array instanceof List) {
                // Modify the specified value in the List
                ((List) array).set(index, value);
            } else {
                throw new IllegalArgumentException("Property '" + name +
                        "' is not indexed");
            }
        } else {
            // Modify the specified value in the array
            Array.set(array, index, value);
        }

    }


    /**
     * Set the value of the specified mapped property of the
     * specified bean, with no type conversions.  The key of the
     * value to set must be included (in brackets) as a suffix to
     * the property name, or <code>IllegalArgumentException</code> will be
     * thrown.
     *
     * @param bean Bean whose property is to be set
     * @param name <code>propertyname(key)</code> of the property value
     *  to be set
     * @param value The property value to be set
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static void setMappedProperty(Object bean, String name,
                                         Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }

        // Identify the index of the requested individual property
        int delim = name.indexOf(MAPPED_DELIM);
        int delim2 = name.indexOf(MAPPED_DELIM2);
        if ((delim < 0) || (delim2 <= delim)) {
            throw new IllegalArgumentException
                    ("Invalid mapped property '" + name + "'");
        }

        // Isolate the name and the key
        String key = name.substring(delim + 1, delim2);
        name = name.substring(0, delim);

        // Request the specified indexed property value
        setMappedProperty(bean, name, key, value);

    }


    /**
     * Set the value of the specified mapped property of the specified
     * bean, with no type conversions.
     *
     * @param bean Bean whose property is to be set
     * @param name Mapped property name of the property value to be set
     * @param key Key of the property value to be set
     * @param value The property value to be set
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static void setMappedProperty(Object bean, String name,
                                         String key, Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }
        if (key == null) {
            throw new IllegalArgumentException("No key specified");
        }

        // Handle DynaBean instances specially
        if (bean instanceof DynaBean) {
            DynaProperty descriptor =
                    ((DynaBean) bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" +
                        name + "'");
            }
            ((DynaBean) bean).set(name, key, value);
            return;
        }

        // Retrieve the property descriptor for the specified property
        PropertyDescriptor descriptor =
                getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" +
                    name + "'");
        }

        if (descriptor instanceof MappedPropertyDescriptor) {
            // Call the keyed setter method if there is one
            Method mappedWriteMethod =
                    ((MappedPropertyDescriptor) descriptor).
                    getMappedWriteMethod();
            if (mappedWriteMethod != null) {
                Object params[] = new Object[2];
                params[0] = key;
                params[1] = value;
                mappedWriteMethod.invoke(bean, params);
            } else {
                throw new NoSuchMethodException
                        ("Property '" + name +
                        "' has no mapped setter method");
            }
        } else {
          /* means that the result has to be retrieved from a map */
          Method readMethod = descriptor.getReadMethod();
          if (readMethod != null) {
            Object invokeResult = readMethod.invoke(bean, new Object[0]);
            /* test and fetch from the map */
            if (invokeResult instanceof java.util.Map) {
              ((java.util.Map)invokeResult).put(key, value);
            }
          } else {
            throw new NoSuchMethodException("Property '" + name +
                    "' has no mapped getter method");
          }
        }

    }


    /**
     * Set the value of the (possibly nested) property of the specified
     * name, for the specified bean, with no type conversions.
     *
     * @param bean Bean whose property is to be modified
     * @param name Possibly nested name of the property to be modified
     * @param value Value to which the property is to be set
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception IllegalArgumentException if a nested reference to a
     *  property returns null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static void setNestedProperty(Object bean,
                                         String name, Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }

        int indexOfINDEXED_DELIM = -1;
        int indexOfMAPPED_DELIM = -1;
        while (true) {
            int delim = name.indexOf(NESTED_DELIM);
            if (delim < 0) {
                break;
            }
            String next = name.substring(0, delim);
            indexOfINDEXED_DELIM = next.indexOf(INDEXED_DELIM);
            indexOfMAPPED_DELIM = next.indexOf(MAPPED_DELIM);
            if (indexOfMAPPED_DELIM >= 0 &&
                    (indexOfINDEXED_DELIM < 0 ||
                    indexOfMAPPED_DELIM < indexOfINDEXED_DELIM)) {
                bean = getMappedProperty(bean, next);
            } else {
                if (indexOfINDEXED_DELIM >= 0) {
                    bean = getIndexedProperty(bean, next);
                } else {
                    bean = getSimpleProperty(bean, next);
                }
            }
            if (bean == null) {
                throw new IllegalArgumentException
                        ("Null property value for '" +
                        name.substring(0, delim) + "'");
            }
            name = name.substring(delim + 1);
        }

        indexOfINDEXED_DELIM = name.indexOf(INDEXED_DELIM);
        indexOfMAPPED_DELIM = name.indexOf(MAPPED_DELIM);

        if (indexOfMAPPED_DELIM >= 0 &&
                (indexOfINDEXED_DELIM < 0 ||
                indexOfMAPPED_DELIM < indexOfINDEXED_DELIM)) {
            setMappedProperty(bean, name, value);
        } else {
            if (indexOfINDEXED_DELIM >= 0) {
                setIndexedProperty(bean, name, value);
            } else {
                setSimpleProperty(bean, name, value);
            }
        }

    }


    /**
     * Set the value of the specified property of the specified bean,
     * no matter which property reference format is used, with no
     * type conversions.
     *
     * @param bean Bean whose property is to be modified
     * @param name Possibly indexed and/or nested name of the property
     *  to be modified
     * @param value Value to which this property is to be set
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static void setProperty(Object bean, String name, Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        setNestedProperty(bean, name, value);

    }


    /**
     * Set the value of the specified simple property of the specified bean,
     * with no type conversions.
     *
     * @param bean Bean whose property is to be modified
     * @param name Name of the property to be modified
     * @param value Value to which the property should be set
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @exception IllegalArgumentException if the property name is
     *  nested or indexed
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public static void setSimpleProperty(Object bean,
                                         String name, Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }

        // Validate the syntax of the property name
        if (name.indexOf(NESTED_DELIM) >= 0) {
            throw new IllegalArgumentException
                    ("Nested property names are not allowed");
        } else if (name.indexOf(INDEXED_DELIM) >= 0) {
            throw new IllegalArgumentException
                    ("Indexed property names are not allowed");
        } else if (name.indexOf(MAPPED_DELIM) >= 0) {
            throw new IllegalArgumentException
                    ("Mapped property names are not allowed");
        }

        // Handle DynaBean instances specially
        if (bean instanceof DynaBean) {
            DynaProperty descriptor =
                    ((DynaBean) bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" +
                        name + "'");
            }
            ((DynaBean) bean).set(name, value);
            return;
        }

        // Retrieve the property setter method for the specified property
        PropertyDescriptor descriptor =
                getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" +
                    name + "'");
        }
        Method writeMethod = getWriteMethod(descriptor);
        if (writeMethod == null) {
            throw new NoSuchMethodException("Property '" + name +
                    "' has no setter method");
        }

        // Call the property setter method
        Object values[] = new Object[1];
        values[0] = value;
        writeMethod.invoke(bean, values);

    }


}
