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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.FastHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


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
 * @version $Revision: 1.14.2.1 $ $Date: 2004/07/27 21:31:00 $
 * @see PropertyUtils
 * @since 1.7
 */

public class PropertyUtilsBean {

    // --------------------------------------------------------- Class Methods
    
    protected static PropertyUtilsBean getInstance() {
        return BeanUtilsBean.getInstance().getPropertyUtils();
    }	

    // --------------------------------------------------------- Variables

    /**
     * The cache of PropertyDescriptor arrays for beans we have already
     * introspected, keyed by the java.lang.Class of this object.
     */
    private FastHashMap descriptorsCache = null;
    private FastHashMap mappedDescriptorsCache = null;
    
    /** Log instance */
    private Log log = LogFactory.getLog(PropertyUtils.class);
    
    // ---------------------------------------------------------- Constructors
    
    /** Base constructor */
    public PropertyUtilsBean() {
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
    public void clearDescriptors() {

        descriptorsCache.clear();
        mappedDescriptorsCache.clear();
        Introspector.flushCaches();

    }


    /**
     * <p>Copy property values from the "origin" bean to the "destination" bean
     * for all cases where the property names are the same (even though the
     * actual getter and setter methods might have been customized via
     * <code>BeanInfo</code> classes).  No conversions are performed on the
     * actual property values -- it is assumed that the values retrieved from
     * the origin bean are assignment-compatible with the types expected by
     * the destination bean.</p>
     *
     * <p>If the origin "bean" is actually a <code>Map</code>, it is assumed
     * to contain String-valued <strong>simple</strong> property names as the keys, pointing
     * at the corresponding property values that will be set in the destination
     * bean.<strong>Note</strong> that this method is intended to perform 
     * a "shallow copy" of the properties and so complex properties 
     * (for example, nested ones) will not be copied.</p>
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
    public void copyProperties(Object dest, Object orig)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (dest == null) {
            throw new IllegalArgumentException
                    ("No destination bean specified");
        }
        if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        }

        if (orig instanceof DynaBean) {
            DynaProperty origDescriptors[] =
                ((DynaBean) orig).getDynaClass().getDynaProperties();
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                if (dest instanceof DynaBean) {
                    if (isWriteable(dest, name)) {
                        Object value = ((DynaBean) orig).get(name);
                        ((DynaBean) dest).set(name, value);
                    }
                } else /* if (dest is a standard JavaBean) */ {
                    if (isWriteable(dest, name)) {
                        Object value = ((DynaBean) orig).get(name);
                        setSimpleProperty(dest, name, value);
                    }
                }
            }
        } else if (orig instanceof Map) {
            Iterator names = ((Map) orig).keySet().iterator();
            while (names.hasNext()) {
                String name = (String) names.next();
                if (dest instanceof DynaBean) {
                    if (isWriteable(dest, name)) {
                        Object value = ((Map) orig).get(name);
                        ((DynaBean) dest).set(name, value);
                    }
                } else /* if (dest is a standard JavaBean) */ {
                    if (isWriteable(dest, name)) {
                        Object value = ((Map) orig).get(name);
                        setSimpleProperty(dest, name, value);
                    }
                }
            }
        } else /* if (orig is a standard JavaBean) */ {
            PropertyDescriptor origDescriptors[] =
                getPropertyDescriptors(orig);
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                if (isReadable(orig, name)) {
                    if (dest instanceof DynaBean) {
                        if (isWriteable(dest, name)) {
                            Object value = getSimpleProperty(orig, name);
                            ((DynaBean) dest).set(name, value);
                        }
                    } else /* if (dest is a standard JavaBean) */ {
                        if (isWriteable(dest, name)) {
                            Object value = getSimpleProperty(orig, name);
                            setSimpleProperty(dest, name, value);
                        }
                    }
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
    public Map describe(Object bean)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
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
                getPropertyDescriptors(bean);
            for (int i = 0; i < descriptors.length; i++) {
                String name = descriptors[i].getName();
                if (descriptors[i].getReadMethod() != null)
                    description.put(name, getProperty(bean, name));
            }
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
    public Object getIndexedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }

        // Identify the index of the requested individual property
        int delim = name.indexOf(PropertyUtils.INDEXED_DELIM);
        int delim2 = name.indexOf(PropertyUtils.INDEXED_DELIM2);
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
    public Object getIndexedProperty(Object bean,
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
                    return (invokeMethod(readMethod,bean, subscript));
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
        Object value = invokeMethod(readMethod, bean, new Object[0]);
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
    public Object getMappedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }

        // Identify the index of the requested individual property
        int delim = name.indexOf(PropertyUtils.MAPPED_DELIM);
        int delim2 = name.indexOf(PropertyUtils.MAPPED_DELIM2);
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
    public Object getMappedProperty(Object bean,
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
                result = invokeMethod(readMethod, bean, keyArray);
            } else {
                throw new NoSuchMethodException("Property '" + name +
                        "' has no mapped getter method");
            }
        } else {
          /* means that the result has to be retrieved from a map */
          Method readMethod = descriptor.getReadMethod();
          if (readMethod != null) {
            Object invokeResult = invokeMethod(readMethod, bean, new Object[0]);
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
     * @deprecated This method should not be exposed
     */
    public FastHashMap getMappedPropertyDescriptors(Class beanClass) {

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
     * @deprecated This method should not be exposed
     */
    public FastHashMap getMappedPropertyDescriptors(Object bean) {

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
     * @exception NestedNullException if a nested reference to a
     *  property returns null
     * @exception InvocationTargetException 
     * if the property accessor method throws an exception
     * @exception NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public Object getNestedProperty(Object bean, String name)
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
        int indexOfMAPPED_DELIM2 = -1;
        int indexOfNESTED_DELIM = -1;
        while (true) {
            indexOfNESTED_DELIM  = name.indexOf(PropertyUtils.NESTED_DELIM);
            indexOfMAPPED_DELIM  = name.indexOf(PropertyUtils.MAPPED_DELIM);
            indexOfMAPPED_DELIM2 = name.indexOf(PropertyUtils.MAPPED_DELIM2);
            if (indexOfMAPPED_DELIM2 >= 0 && indexOfMAPPED_DELIM >=0 &&
                (indexOfNESTED_DELIM < 0 || indexOfNESTED_DELIM > indexOfMAPPED_DELIM)) {
                indexOfNESTED_DELIM =
                    name.indexOf(PropertyUtils.NESTED_DELIM, indexOfMAPPED_DELIM2);
            } else {
                indexOfNESTED_DELIM = name.indexOf(PropertyUtils.NESTED_DELIM);
            }
            if (indexOfNESTED_DELIM < 0) {
                break;
            }
            String next = name.substring(0, indexOfNESTED_DELIM);
            indexOfINDEXED_DELIM = next.indexOf(PropertyUtils.INDEXED_DELIM);
            indexOfMAPPED_DELIM = next.indexOf(PropertyUtils.MAPPED_DELIM);
            if (bean instanceof Map) {
                bean = ((Map) bean).get(next);
            } else if (indexOfMAPPED_DELIM >= 0) {
                bean = getMappedProperty(bean, next);
            } else if (indexOfINDEXED_DELIM >= 0) {
                bean = getIndexedProperty(bean, next);
            } else {
                bean = getSimpleProperty(bean, next);
            }
            if (bean == null) {
                throw new NestedNullException
                        ("Null property value for '" +
                        name.substring(0, indexOfNESTED_DELIM) + "'");
            }
            name = name.substring(indexOfNESTED_DELIM + 1);
        }

        indexOfINDEXED_DELIM = name.indexOf(PropertyUtils.INDEXED_DELIM);
        indexOfMAPPED_DELIM = name.indexOf(PropertyUtils.MAPPED_DELIM);

        if (bean instanceof Map) {
            bean = ((Map) bean).get(name);
        } else if (indexOfMAPPED_DELIM >= 0) {
            bean = getMappedProperty(bean, name);
        } else if (indexOfINDEXED_DELIM >= 0) {
            bean = getIndexedProperty(bean, name);
        } else {
            bean = getSimpleProperty(bean, name);
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
    public Object getProperty(Object bean, String name)
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
    public PropertyDescriptor getPropertyDescriptor(Object bean,
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
            int period = findNextNestedIndex(name);
            if (period < 0) {
                break;
            }
            String next = name.substring(0, period);
            int indexOfINDEXED_DELIM = next.indexOf(PropertyUtils.INDEXED_DELIM);
            int indexOfMAPPED_DELIM = next.indexOf(PropertyUtils.MAPPED_DELIM);
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
        int left = name.indexOf(PropertyUtils.INDEXED_DELIM);
        if (left >= 0) {
            name = name.substring(0, left);
        }
        left = name.indexOf(PropertyUtils.MAPPED_DELIM);
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
        if (mappedDescriptors == null) {
            mappedDescriptors = new FastHashMap();
            mappedDescriptors.setFast(true);
            mappedDescriptorsCache.put(bean.getClass(), mappedDescriptors);
        }
        result = (PropertyDescriptor) mappedDescriptors.get(name);
        if (result == null) {
            // not found, try to create it
            try {
                result =
                        new MappedPropertyDescriptor(name, bean.getClass());
            } catch (IntrospectionException ie) {
            }
            if (result != null) {
                mappedDescriptors.put(name, result);
            }
        }
        
        return result;

    }
    
    private int findNextNestedIndex(String expression)
    {
        // walk back from the end to the start 
        // and find the first index that 
        int bracketCount = 0;
        for (int i=0, size=expression.length(); i<size ; i++) {
            char at = expression.charAt(i);
            switch (at) {
                case PropertyUtils.NESTED_DELIM:
                    if (bracketCount < 1) {
                        return i;
                    }
                    break;
                    
                case PropertyUtils.MAPPED_DELIM:
                case PropertyUtils.INDEXED_DELIM:
                    // not bothered which
                    ++bracketCount;
                    break;
                
                case PropertyUtils.MAPPED_DELIM2:
                case PropertyUtils.INDEXED_DELIM2:
                    // not bothered which
                    --bracketCount;
                    break;            
            }
        }
        // can't find any
        return -1;
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
    public PropertyDescriptor[]
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
    public PropertyDescriptor[] getPropertyDescriptors(Object bean) {

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
    public Class getPropertyEditorClass(Object bean, String name)
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
    public Class getPropertyType(Object bean, String name)
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
        } else if (descriptor instanceof MappedPropertyDescriptor) {
            return (((MappedPropertyDescriptor) descriptor).
                    getMappedPropertyType());
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
    public Method getReadMethod(PropertyDescriptor descriptor) {

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
    public Object getSimpleProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }

        // Validate the syntax of the property name
        if (name.indexOf(PropertyUtils.NESTED_DELIM) >= 0) {
            throw new IllegalArgumentException
                    ("Nested property names are not allowed");
        } else if (name.indexOf(PropertyUtils.INDEXED_DELIM) >= 0) {
            throw new IllegalArgumentException
                    ("Indexed property names are not allowed");
        } else if (name.indexOf(PropertyUtils.MAPPED_DELIM) >= 0) {
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
        Object value = invokeMethod(readMethod, bean, new Object[0]);
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
    public Method getWriteMethod(PropertyDescriptor descriptor) {

        return (MethodUtils.getAccessibleMethod(descriptor.getWriteMethod()));

    }


    /**
     * <p>Return <code>true</code> if the specified property name identifies
     * a readable property on the specified bean; otherwise, return
     * <code>false</code>.
     *
     * @param bean Bean to be examined (may be a {@link DynaBean}
     * @param name Property name to be evaluated
     *
     * @exception IllegalArgumentException if <code>bean</code>
     *  or <code>name</code> is <code>null</code>
     *
     * @since BeanUtils 1.6
     */
    public boolean isReadable(Object bean, String name) {

        // Validate method parameters
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }

        // Return the requested result
        if (bean instanceof DynaBean) {
            // All DynaBean properties are readable
            return (((DynaBean) bean).getDynaClass().getDynaProperty(name) != null);
        } else {
            try {
                PropertyDescriptor desc =
                    getPropertyDescriptor(bean, name);
                if (desc != null) {
                    Method readMethod = desc.getReadMethod();
                    if ((readMethod == null) &&
                        (desc instanceof IndexedPropertyDescriptor)) {
                        readMethod = ((IndexedPropertyDescriptor) desc).getIndexedReadMethod();
                    }
                    return (readMethod != null);
                } else {
                    return (false);
                }
            } catch (IllegalAccessException e) {
                return (false);
            } catch (InvocationTargetException e) {
                return (false);
            } catch (NoSuchMethodException e) {
                return (false);
            }
        }

    }


    /**
     * <p>Return <code>true</code> if the specified property name identifies
     * a writeable property on the specified bean; otherwise, return
     * <code>false</code>.
     *
     * @param bean Bean to be examined (may be a {@link DynaBean}
     * @param name Property name to be evaluated
     *
     * @exception IllegalPointerException if <code>bean</code>
     *  or <code>name</code> is <code>null</code>
     *
     * @since BeanUtils 1.6
     */
    public boolean isWriteable(Object bean, String name) {

        // Validate method parameters
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified");
        }

        // Return the requested result
        if (bean instanceof DynaBean) {
            // All DynaBean properties are writeable
            return (((DynaBean) bean).getDynaClass().getDynaProperty(name) != null);
        } else {
            try {
                PropertyDescriptor desc =
                    getPropertyDescriptor(bean, name);
                if (desc != null) {
                    Method writeMethod = desc.getWriteMethod();
                    if ((writeMethod == null) &&
                        (desc instanceof IndexedPropertyDescriptor)) {
                        writeMethod = ((IndexedPropertyDescriptor) desc).getIndexedWriteMethod();
                    }
                    return (writeMethod != null);
                } else {
                    return (false);
                }
            } catch (IllegalAccessException e) {
                return (false);
            } catch (InvocationTargetException e) {
                return (false);
            } catch (NoSuchMethodException e) {
                return (false);
            }
        }

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
    public void setIndexedProperty(Object bean, String name,
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
        int delim = name.indexOf(PropertyUtils.INDEXED_DELIM);
        int delim2 = name.indexOf(PropertyUtils.INDEXED_DELIM2);
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
    public void setIndexedProperty(Object bean, String name,
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
                    if (log.isTraceEnabled()) {
                        String valueClassName =
                            value == null ? "<null>" 
                                          : value.getClass().getName();
                        log.trace("setSimpleProperty: Invoking method "
                                  + writeMethod +" with index=" + index
                                  + ", value=" + value
                                  + " (class " + valueClassName+ ")");
                    }
                    invokeMethod(writeMethod, bean, subscript);
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
        Object array = invokeMethod(readMethod, bean, new Object[0]);
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
    public void setMappedProperty(Object bean, String name,
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
        int delim = name.indexOf(PropertyUtils.MAPPED_DELIM);
        int delim2 = name.indexOf(PropertyUtils.MAPPED_DELIM2);
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
    public void setMappedProperty(Object bean, String name,
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
                if (log.isTraceEnabled()) {
                    String valueClassName =
                        value == null ? "<null>" : value.getClass().getName();
                    log.trace("setSimpleProperty: Invoking method "
                              + mappedWriteMethod + " with key=" + key
                              + ", value=" + value
                              + " (class " + valueClassName +")");
                }
                invokeMethod(mappedWriteMethod, bean, params);
            } else {
                throw new NoSuchMethodException
                        ("Property '" + name +
                        "' has no mapped setter method");
            }
        } else {
          /* means that the result has to be retrieved from a map */
          Method readMethod = descriptor.getReadMethod();
          if (readMethod != null) {
            Object invokeResult = invokeMethod(readMethod, bean, new Object[0]);
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
    public void setNestedProperty(Object bean,
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
            int delim = name.indexOf(PropertyUtils.NESTED_DELIM);
            if (delim < 0) {
                break;
            }
            String next = name.substring(0, delim);
            indexOfINDEXED_DELIM = next.indexOf(PropertyUtils.INDEXED_DELIM);
            indexOfMAPPED_DELIM = next.indexOf(PropertyUtils.MAPPED_DELIM);
            if (bean instanceof Map) {
                bean = ((Map) bean).get(next);
            } else if (indexOfMAPPED_DELIM >= 0) {
                bean = getMappedProperty(bean, next);
            } else if (indexOfINDEXED_DELIM >= 0) {
                bean = getIndexedProperty(bean, next);
            } else {
                bean = getSimpleProperty(bean, next);
            }
            if (bean == null) {
                throw new IllegalArgumentException
                        ("Null property value for '" +
                        name.substring(0, delim) + "'");
            }
            name = name.substring(delim + 1);
        }

        indexOfINDEXED_DELIM = name.indexOf(PropertyUtils.INDEXED_DELIM);
        indexOfMAPPED_DELIM = name.indexOf(PropertyUtils.MAPPED_DELIM);

        if (bean instanceof Map) {
            // check to see if the class has a standard property 
            PropertyDescriptor descriptor = 
                getPropertyDescriptor(bean, name);
            if (descriptor == null) {
                // no - then put the value into the map
                ((Map) bean).put(name, value);
            } else {
                // yes - use that instead
                setSimpleProperty(bean, name, value);
            }
        } else if (indexOfMAPPED_DELIM >= 0) {
            setMappedProperty(bean, name, value);
        } else if (indexOfINDEXED_DELIM >= 0) {
            setIndexedProperty(bean, name, value);
        } else {
            setSimpleProperty(bean, name, value);
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
    public void setProperty(Object bean, String name, Object value)
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
    public void setSimpleProperty(Object bean,
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
        if (name.indexOf(PropertyUtils.NESTED_DELIM) >= 0) {
            throw new IllegalArgumentException
                    ("Nested property names are not allowed");
        } else if (name.indexOf(PropertyUtils.INDEXED_DELIM) >= 0) {
            throw new IllegalArgumentException
                    ("Indexed property names are not allowed");
        } else if (name.indexOf(PropertyUtils.MAPPED_DELIM) >= 0) {
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
        if (log.isTraceEnabled()) {
            String valueClassName =
                value == null ? "<null>" : value.getClass().getName();
            log.trace("setSimpleProperty: Invoking method " + writeMethod
                      + " with value " + value + " (class " + valueClassName + ")");
        }
        invokeMethod(writeMethod, bean, values);

    }
    
    /** This just catches and wraps IllegalArgumentException. */
    private Object invokeMethod(
                        Method method, 
                        Object bean, 
                        Object[] values) 
                            throws
                                IllegalAccessException,
                                InvocationTargetException {
        try {
            
            return method.invoke(bean, values);
        
        } catch (IllegalArgumentException e) {
            
            log.error("Method invocation failed.", e);
            throw new IllegalArgumentException(
                "Cannot invoke " + method.getDeclaringClass().getName() + "." 
                + method.getName() + " - " + e.getMessage());
            
        }
    }
}
