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
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.beanutils.expression.DefaultResolver;
import org.apache.commons.beanutils.expression.Resolver;
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
 * property value of a bean are defined, with the <i>default</i> layout of an
 * identifying String in parentheses. However the notation for these formats
 * and how they are resolved is now (since BeanUtils 1.8.0) controlled by
 * the configured {@link Resolver} implementation:
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
 * @version $Id$
 * @see Resolver
 * @see PropertyUtils
 * @since 1.7
 */

public class PropertyUtilsBean {

    private Resolver resolver = new DefaultResolver();

    // --------------------------------------------------------- Class Methods

    /**
     * Return the PropertyUtils bean instance.
     * @return The PropertyUtils bean instance
     */
    protected static PropertyUtilsBean getInstance() {
        return BeanUtilsBean.getInstance().getPropertyUtils();
    }

    // --------------------------------------------------------- Variables

    /**
     * The cache of PropertyDescriptor arrays for beans we have already
     * introspected, keyed by the java.lang.Class of this object.
     */
    private WeakFastHashMap<Class<?>, BeanIntrospectionData> descriptorsCache = null;
    private WeakFastHashMap<Class<?>, FastHashMap> mappedDescriptorsCache = null;

    /** An empty object array */
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    /** Log instance */
    private final Log log = LogFactory.getLog(PropertyUtils.class);

    /** The list with BeanIntrospector objects. */
    private final List<BeanIntrospector> introspectors;

    // ---------------------------------------------------------- Constructors

    /** Base constructor */
    public PropertyUtilsBean() {
        descriptorsCache = new WeakFastHashMap<Class<?>, BeanIntrospectionData>();
        descriptorsCache.setFast(true);
        mappedDescriptorsCache = new WeakFastHashMap<Class<?>, FastHashMap>();
        mappedDescriptorsCache.setFast(true);
        introspectors = new CopyOnWriteArrayList<BeanIntrospector>();
        resetBeanIntrospectors();
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Return the configured {@link Resolver} implementation used by BeanUtils.
     * <p>
     * The {@link Resolver} handles the <i>property name</i>
     * expressions and the implementation in use effectively
     * controls the dialect of the <i>expression language</i>
     * that BeanUtils recongnises.
     * <p>
     * {@link DefaultResolver} is the default implementation used.
     *
     * @return resolver The property expression resolver.
     * @since 1.8.0
     */
    public Resolver getResolver() {
        return resolver;
    }

    /**
     * Configure the {@link Resolver} implementation used by BeanUtils.
     * <p>
     * The {@link Resolver} handles the <i>property name</i>
     * expressions and the implementation in use effectively
     * controls the dialect of the <i>expression language</i>
     * that BeanUtils recongnises.
     * <p>
     * {@link DefaultResolver} is the default implementation used.
     *
     * @param resolver The property expression resolver.
     * @since 1.8.0
     */
    public void setResolver(final Resolver resolver) {
        if (resolver == null) {
            this.resolver = new DefaultResolver();
        } else {
            this.resolver = resolver;
        }
    }

    /**
     * Resets the {@link BeanIntrospector} objects registered at this instance. After this
     * method was called, only the default {@code BeanIntrospector} is registered.
     *
     * @since 1.9
     */
    public final void resetBeanIntrospectors() {
        introspectors.clear();
        introspectors.add(DefaultBeanIntrospector.INSTANCE);
        introspectors.add(SuppressPropertiesBeanIntrospector.SUPPRESS_CLASS);
    }

    /**
     * Adds a <code>BeanIntrospector</code>. This object is invoked when the
     * property descriptors of a class need to be obtained.
     *
     * @param introspector the <code>BeanIntrospector</code> to be added (must
     *        not be <b>null</b>
     * @throws IllegalArgumentException if the argument is <b>null</b>
     * @since 1.9
     */
    public void addBeanIntrospector(final BeanIntrospector introspector) {
        if (introspector == null) {
            throw new IllegalArgumentException(
                    "BeanIntrospector must not be null!");
        }
        introspectors.add(introspector);
    }

    /**
     * Removes the specified <code>BeanIntrospector</code>.
     *
     * @param introspector the <code>BeanIntrospector</code> to be removed
     * @return <b>true</b> if the <code>BeanIntrospector</code> existed and
     *         could be removed, <b>false</b> otherwise
     * @since 1.9
     */
    public boolean removeBeanIntrospector(final BeanIntrospector introspector) {
        return introspectors.remove(introspector);
    }

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
     * <p>Note, that this method will not copy a List to a List, or an Object[]
     * to an Object[]. It's specifically for copying JavaBean properties. </p>
     *
     * @param dest Destination bean whose properties are modified
     * @param orig Origin bean whose properties are retrieved
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws IllegalArgumentException if the <code>dest</code> or
     *  <code>orig</code> argument is null
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public void copyProperties(final Object dest, final Object orig)
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
            final DynaProperty[] origDescriptors =
                ((DynaBean) orig).getDynaClass().getDynaProperties();
            for (DynaProperty origDescriptor : origDescriptors) {
                final String name = origDescriptor.getName();
                if (isReadable(orig, name) && isWriteable(dest, name)) {
                    try {
                        final Object value = ((DynaBean) orig).get(name);
                        if (dest instanceof DynaBean) {
                            ((DynaBean) dest).set(name, value);
                        } else {
                                setSimpleProperty(dest, name, value);
                        }
                    } catch (final NoSuchMethodException e) {
                        if (log.isDebugEnabled()) {
                            log.debug("Error writing to '" + name + "' on class '" + dest.getClass() + "'", e);
                        }
                    }
                }
            }
        } else if (orig instanceof Map) {
            final Iterator<?> entries = ((Map<?, ?>) orig).entrySet().iterator();
            while (entries.hasNext()) {
                final Map.Entry<?, ?> entry = (Entry<?, ?>) entries.next();
                final String name = (String)entry.getKey();
                if (isWriteable(dest, name)) {
                    try {
                        if (dest instanceof DynaBean) {
                            ((DynaBean) dest).set(name, entry.getValue());
                        } else {
                            setSimpleProperty(dest, name, entry.getValue());
                        }
                    } catch (final NoSuchMethodException e) {
                        if (log.isDebugEnabled()) {
                            log.debug("Error writing to '" + name + "' on class '" + dest.getClass() + "'", e);
                        }
                    }
                }
            }
        } else /* if (orig is a standard JavaBean) */ {
            final PropertyDescriptor[] origDescriptors =
                getPropertyDescriptors(orig);
            for (PropertyDescriptor origDescriptor : origDescriptors) {
                final String name = origDescriptor.getName();
                if (isReadable(orig, name) && isWriteable(dest, name)) {
                    try {
                        final Object value = getSimpleProperty(orig, name);
                        if (dest instanceof DynaBean) {
                            ((DynaBean) dest).set(name, value);
                        } else {
                                setSimpleProperty(dest, name, value);
                        }
                    } catch (final NoSuchMethodException e) {
                        if (log.isDebugEnabled()) {
                            log.debug("Error writing to '" + name + "' on class '" + dest.getClass() + "'", e);
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
     * @return The set of properties for the bean
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> is null
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public Map<String, Object> describe(final Object bean)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        final Map<String, Object> description = new HashMap<String, Object>();
        if (bean instanceof DynaBean) {
            final DynaProperty[] descriptors =
                ((DynaBean) bean).getDynaClass().getDynaProperties();
            for (DynaProperty descriptor : descriptors) {
                final String name = descriptor.getName();
                description.put(name, getProperty(bean, name));
            }
        } else {
            final PropertyDescriptor[] descriptors =
                getPropertyDescriptors(bean);
            for (PropertyDescriptor descriptor : descriptors) {
                final String name = descriptor.getName();
                if (descriptor.getReadMethod() != null) {
                    description.put(name, getProperty(bean, name));
                }
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
     * @return the indexed property value
     *
     * @throws IndexOutOfBoundsException if the specified index
     *  is outside the valid range for the underlying array or List
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public Object getIndexedProperty(final Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }

        // Identify the index of the requested individual property
        int index = -1;
        try {
            index = resolver.getIndex(name);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid indexed property '" +
                    name + "' on bean class '" + bean.getClass() + "' " +
                    e.getMessage());
        }
        if (index < 0) {
            throw new IllegalArgumentException("Invalid indexed property '" +
                    name + "' on bean class '" + bean.getClass() + "'");
        }

        // Isolate the name
        name = resolver.getProperty(name);

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
     * @return the indexed property value
     *
     * @throws IndexOutOfBoundsException if the specified index
     *  is outside the valid range for the underlying property
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public Object getIndexedProperty(final Object bean,
                                            final String name, final int index)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null || name.length() == 0) {
            if (bean.getClass().isArray()) {
                return Array.get(bean, index);
            } else if (bean instanceof List) {
                return ((List<?>)bean).get(index);
            }
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }

        // Handle DynaBean instances specially
        if (bean instanceof DynaBean) {
            final DynaProperty descriptor =
                    ((DynaBean) bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" +
                    name + "' on bean class '" + bean.getClass() + "'");
            }
            return (((DynaBean) bean).get(name, index));
        }

        // Retrieve the property descriptor for the specified property
        final PropertyDescriptor descriptor =
                getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" +
                    name + "' on bean class '" + bean.getClass() + "'");
        }

        // Call the indexed getter method if there is one
        if (descriptor instanceof IndexedPropertyDescriptor) {
            Method readMethod = ((IndexedPropertyDescriptor) descriptor).
                    getIndexedReadMethod();
            readMethod = MethodUtils.getAccessibleMethod(bean.getClass(), readMethod);
            if (readMethod != null) {
                final Object[] subscript = new Object[1];
                subscript[0] = new Integer(index);
                try {
                    return (invokeMethod(readMethod,bean, subscript));
                } catch (final InvocationTargetException e) {
                    if (e.getTargetException() instanceof
                            IndexOutOfBoundsException) {
                        throw (IndexOutOfBoundsException)
                                e.getTargetException();
                    } else {
                        throw e;
                    }
                }
            }
        }

        // Otherwise, the underlying property must be an array
        final Method readMethod = getReadMethod(bean.getClass(), descriptor);
        if (readMethod == null) {
            throw new NoSuchMethodException("Property '" + name + "' has no " +
                    "getter method on bean class '" + bean.getClass() + "'");
        }

        // Call the property getter and return the value
        final Object value = invokeMethod(readMethod, bean, EMPTY_OBJECT_ARRAY);
        if (!value.getClass().isArray()) {
            if (!(value instanceof java.util.List)) {
                throw new IllegalArgumentException("Property '" + name +
                        "' is not indexed on bean class '" + bean.getClass() + "'");
            } else {
                //get the List's value
                return ((java.util.List<?>) value).get(index);
            }
        } else {
            //get the array's value
            try {
                return (Array.get(value, index));
            } catch (final ArrayIndexOutOfBoundsException e) {
                throw new ArrayIndexOutOfBoundsException("Index: " +
                        index + ", Size: " + Array.getLength(value) +
                        " for property '" + name + "'");
            }
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
     * @return the mapped property value
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public Object getMappedProperty(final Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }

        // Identify the key of the requested individual property
        String key  = null;
        try {
            key = resolver.getKey(name);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException
                    ("Invalid mapped property '" + name +
                    "' on bean class '" + bean.getClass() + "' " + e.getMessage());
        }
        if (key == null) {
            throw new IllegalArgumentException("Invalid mapped property '" +
                    name + "' on bean class '" + bean.getClass() + "'");
        }

        // Isolate the name
        name = resolver.getProperty(name);

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
     * @return the mapped property value
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public Object getMappedProperty(final Object bean,
                                           final String name, final String key)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }
        if (key == null) {
            throw new IllegalArgumentException("No key specified for property '" +
                    name + "' on bean class " + bean.getClass() + "'");
        }

        // Handle DynaBean instances specially
        if (bean instanceof DynaBean) {
            final DynaProperty descriptor =
                    ((DynaBean) bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" +
                        name + "'+ on bean class '" + bean.getClass() + "'");
            }
            return (((DynaBean) bean).get(name, key));
        }

        Object result = null;

        // Retrieve the property descriptor for the specified property
        final PropertyDescriptor descriptor = getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" +
                    name + "'+ on bean class '" + bean.getClass() + "'");
        }

        if (descriptor instanceof MappedPropertyDescriptor) {
            // Call the keyed getter method if there is one
            Method readMethod = ((MappedPropertyDescriptor) descriptor).
                    getMappedReadMethod();
            readMethod = MethodUtils.getAccessibleMethod(bean.getClass(), readMethod);
            if (readMethod != null) {
                final Object[] keyArray = new Object[1];
                keyArray[0] = key;
                result = invokeMethod(readMethod, bean, keyArray);
            } else {
                throw new NoSuchMethodException("Property '" + name +
                        "' has no mapped getter method on bean class '" +
                        bean.getClass() + "'");
            }
        } else {
          /* means that the result has to be retrieved from a map */
          final Method readMethod = getReadMethod(bean.getClass(), descriptor);
          if (readMethod != null) {
            final Object invokeResult = invokeMethod(readMethod, bean, EMPTY_OBJECT_ARRAY);
            /* test and fetch from the map */
            if (invokeResult instanceof java.util.Map) {
              result = ((java.util.Map<?, ?>)invokeResult).get(key);
            }
          } else {
            throw new NoSuchMethodException("Property '" + name +
                    "' has no mapped getter method on bean class '" +
                    bean.getClass() + "'");
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
     * @return the mapped property descriptors
     * @deprecated This method should not be exposed
     */
    @Deprecated
    public FastHashMap getMappedPropertyDescriptors(final Class<?> beanClass) {

        if (beanClass == null) {
            return null;
        }

        // Look up any cached descriptors for this bean class
        return mappedDescriptorsCache.get(beanClass);

    }


    /**
     * <p>Return the mapped property descriptors for this bean.</p>
     *
     * <p><strong>FIXME</strong> - Does not work with DynaBeans.</p>
     *
     * @param bean Bean to be introspected
     * @return the mapped property descriptors
     * @deprecated This method should not be exposed
     */
    @Deprecated
    public FastHashMap getMappedPropertyDescriptors(final Object bean) {

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
     * @return the nested property value
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @throws NestedNullException if a nested reference to a
     *  property returns null
     * @throws InvocationTargetException
     * if the property accessor method throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public Object getNestedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }

        // Resolve nested references
        while (resolver.hasNested(name)) {
            final String next = resolver.next(name);
            Object nestedBean = null;
            if (bean instanceof Map) {
                nestedBean = getPropertyOfMapBean((Map<?, ?>) bean, next);
            } else if (resolver.isMapped(next)) {
                nestedBean = getMappedProperty(bean, next);
            } else if (resolver.isIndexed(next)) {
                nestedBean = getIndexedProperty(bean, next);
            } else {
                nestedBean = getSimpleProperty(bean, next);
            }
            if (nestedBean == null) {
                throw new NestedNullException
                        ("Null property value for '" + name +
                        "' on bean class '" + bean.getClass() + "'");
            }
            bean = nestedBean;
            name = resolver.remove(name);
        }

        if (bean instanceof Map) {
            bean = getPropertyOfMapBean((Map<?, ?>) bean, name);
        } else if (resolver.isMapped(name)) {
            bean = getMappedProperty(bean, name);
        } else if (resolver.isIndexed(name)) {
            bean = getIndexedProperty(bean, name);
        } else {
            bean = getSimpleProperty(bean, name);
        }
        return bean;

    }

    /**
     * This method is called by getNestedProperty and setNestedProperty to
     * define what it means to get a property from an object which implements
     * Map. See setPropertyOfMapBean for more information.
     *
     * @param bean Map bean
     * @param propertyName The property name
     * @return the property value
     *
     * @throws IllegalArgumentException when the propertyName is regarded as
     * being invalid.
     *
     * @throws IllegalAccessException just in case subclasses override this
     * method to try to access real getter methods and find permission is denied.
     *
     * @throws InvocationTargetException just in case subclasses override this
     * method to try to access real getter methods, and find it throws an
     * exception when invoked.
     *
     * @throws NoSuchMethodException just in case subclasses override this
     * method to try to access real getter methods, and want to fail if
     * no simple method is available.
     * @since 1.8.0
     */
    protected Object getPropertyOfMapBean(final Map<?, ?> bean, String propertyName)
        throws IllegalArgumentException, IllegalAccessException,
        InvocationTargetException, NoSuchMethodException {

        if (resolver.isMapped(propertyName)) {
            final String name = resolver.getProperty(propertyName);
            if (name == null || name.length() == 0) {
                propertyName = resolver.getKey(propertyName);
            }
        }

        if (resolver.isIndexed(propertyName) ||
            resolver.isMapped(propertyName)) {
            throw new IllegalArgumentException(
                    "Indexed or mapped properties are not supported on"
                    + " objects of type Map: " + propertyName);
        }

        return bean.get(propertyName);
    }



    /**
     * Return the value of the specified property of the specified bean,
     * no matter which property reference format is used, with no
     * type conversions.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly indexed and/or nested name of the property
     *  to be extracted
     * @return the property value
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public Object getProperty(final Object bean, final String name)
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
     * <p>Note that for Java 8 and above, this method no longer return
     * IndexedPropertyDescriptor for {@link List}-typed properties, only for
     * properties typed as native array. (BEANUTILS-492).
     *
     * @param bean Bean for which a property descriptor is requested
     * @param name Possibly indexed and/or nested name of the property for
     *  which a property descriptor is requested
     * @return the property descriptor
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @throws IllegalArgumentException if a nested reference to a
     *  property returns null
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
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
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }

        // Resolve nested references
        while (resolver.hasNested(name)) {
            final String next = resolver.next(name);
            final Object nestedBean = getProperty(bean, next);
            if (nestedBean == null) {
                throw new NestedNullException
                        ("Null property value for '" + next +
                        "' on bean class '" + bean.getClass() + "'");
            }
            bean = nestedBean;
            name = resolver.remove(name);
        }

        // Remove any subscript from the final name value
        name = resolver.getProperty(name);

        // Look up and return this property from our cache
        // creating and adding it to the cache if not found.
        if (name == null) {
            return (null);
        }

        final BeanIntrospectionData data = getIntrospectionData(bean.getClass());
        PropertyDescriptor result = data.getDescriptor(name);
        if (result != null) {
            return result;
        }

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
                result = new MappedPropertyDescriptor(name, bean.getClass());
            } catch (final IntrospectionException ie) {
                /* Swallow IntrospectionException
                 * TODO: Why?
                 */
            }
            if (result != null) {
                mappedDescriptors.put(name, result);
            }
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
     * @return the property descriptors
     *
     * @throws IllegalArgumentException if <code>beanClass</code> is null
     */
    public PropertyDescriptor[]
            getPropertyDescriptors(final Class<?> beanClass) {

        return getIntrospectionData(beanClass).getDescriptors();

    }

    /**
     * <p>Retrieve the property descriptors for the specified bean,
     * introspecting and caching them the first time a particular bean class
     * is encountered.</p>
     *
     * <p><strong>FIXME</strong> - Does not work with DynaBeans.</p>
     *
     * @param bean Bean for which property descriptors are requested
     * @return the property descriptors
     *
     * @throws IllegalArgumentException if <code>bean</code> is null
     */
    public PropertyDescriptor[] getPropertyDescriptors(final Object bean) {

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
     * @return the property editor class
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @throws IllegalArgumentException if a nested reference to a
     *  property returns null
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public Class<?> getPropertyEditorClass(final Object bean, final String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }

        final PropertyDescriptor descriptor =
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
     * <p>
     * If the property is an indexed property (e.g. <code>String[]</code>),
     * this method will return the type of the items within that array.
     * Note that from Java 8 and newer, this method do not support
     * such index types from items within an Collection, and will
     * instead return the collection type (e.g. java.util.List) from the
     * getter mtethod.
     *
     * @param bean Bean for which a property descriptor is requested
     * @param name Possibly indexed and/or nested name of the property for
     *  which a property descriptor is requested
     * @return The property type
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @throws IllegalArgumentException if a nested reference to a
     *  property returns null
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public Class<?> getPropertyType(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }

        // Resolve nested references
        while (resolver.hasNested(name)) {
            final String next = resolver.next(name);
            final Object nestedBean = getProperty(bean, next);
            if (nestedBean == null) {
                throw new NestedNullException
                        ("Null property value for '" + next +
                        "' on bean class '" + bean.getClass() + "'");
            }
            bean = nestedBean;
            name = resolver.remove(name);
        }

        // Remove any subscript from the final name value
        name = resolver.getProperty(name);

        // Special handling for DynaBeans
        if (bean instanceof DynaBean) {
            final DynaProperty descriptor =
                    ((DynaBean) bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                return (null);
            }
            final Class<?> type = descriptor.getType();
            if (type == null) {
                return (null);
            } else if (type.isArray()) {
                return (type.getComponentType());
            } else {
                return (type);
            }
        }

        final PropertyDescriptor descriptor =
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
     * @return The read method
     */
    public Method getReadMethod(final PropertyDescriptor descriptor) {

        return (MethodUtils.getAccessibleMethod(descriptor.getReadMethod()));

    }


    /**
     * <p>Return an accessible property getter method for this property,
     * if there is one; otherwise return <code>null</code>.</p>
     *
     * <p><strong>FIXME</strong> - Does not work with DynaBeans.</p>
     *
     * @param clazz The class of the read method will be invoked on
     * @param descriptor Property descriptor to return a getter for
     * @return The read method
     */
    Method getReadMethod(final Class<?> clazz, final PropertyDescriptor descriptor) {
        return (MethodUtils.getAccessibleMethod(clazz, descriptor.getReadMethod()));
    }


    /**
     * Return the value of the specified simple property of the specified
     * bean, with no type conversions.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Name of the property to be extracted
     * @return The property value
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @throws IllegalArgumentException if the property name
     *  is nested or indexed
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public Object getSimpleProperty(final Object bean, final String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }

        // Validate the syntax of the property name
        if (resolver.hasNested(name)) {
            throw new IllegalArgumentException
                    ("Nested property names are not allowed: Property '" +
                    name + "' on bean class '" + bean.getClass() + "'");
        } else if (resolver.isIndexed(name)) {
            throw new IllegalArgumentException
                    ("Indexed property names are not allowed: Property '" +
                    name + "' on bean class '" + bean.getClass() + "'");
        } else if (resolver.isMapped(name)) {
            throw new IllegalArgumentException
                    ("Mapped property names are not allowed: Property '" +
                    name + "' on bean class '" + bean.getClass() + "'");
        }

        // Handle DynaBean instances specially
        if (bean instanceof DynaBean) {
            final DynaProperty descriptor =
                    ((DynaBean) bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" +
                        name + "' on dynaclass '" +
                        ((DynaBean) bean).getDynaClass() + "'" );
            }
            return (((DynaBean) bean).get(name));
        }

        // Retrieve the property getter method for the specified property
        final PropertyDescriptor descriptor =
                getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" +
                    name + "' on class '" + bean.getClass() + "'" );
        }
        final Method readMethod = getReadMethod(bean.getClass(), descriptor);
        if (readMethod == null) {
            throw new NoSuchMethodException("Property '" + name +
                    "' has no getter method in class '" + bean.getClass() + "'");
        }

        // Call the property getter and return the value
        final Object value = invokeMethod(readMethod, bean, EMPTY_OBJECT_ARRAY);
        return (value);

    }


    /**
     * <p>Return an accessible property setter method for this property,
     * if there is one; otherwise return <code>null</code>.</p>
     *
     * <p><em>Note:</em> This method does not work correctly with custom bean
     * introspection under certain circumstances. It may return {@code null}
     * even if a write method is defined for the property in question. Use
     * {@link #getWriteMethod(Class, PropertyDescriptor)} to be sure that the
     * correct result is returned.</p>
     * <p><strong>FIXME</strong> - Does not work with DynaBeans.</p>
     *
     * @param descriptor Property descriptor to return a setter for
     * @return The write method
     */
    public Method getWriteMethod(final PropertyDescriptor descriptor) {

        return (MethodUtils.getAccessibleMethod(descriptor.getWriteMethod()));

    }


    /**
     * <p>Return an accessible property setter method for this property,
     * if there is one; otherwise return <code>null</code>.</p>
     *
     * <p><strong>FIXME</strong> - Does not work with DynaBeans.</p>
     *
     * @param clazz The class of the read method will be invoked on
     * @param descriptor Property descriptor to return a setter for
     * @return The write method
     * @since 1.9.1
     */
    public Method getWriteMethod(final Class<?> clazz, final PropertyDescriptor descriptor) {
        final BeanIntrospectionData data = getIntrospectionData(clazz);
        return (MethodUtils.getAccessibleMethod(clazz,
                data.getWriteMethod(clazz, descriptor)));
    }


    /**
     * <p>Return <code>true</code> if the specified property name identifies
     * a readable property on the specified bean; otherwise, return
     * <code>false</code>.
     *
     * @param bean Bean to be examined (may be a {@link DynaBean}
     * @param name Property name to be evaluated
     * @return <code>true</code> if the property is readable,
     * otherwise <code>false</code>
     *
     * @throws IllegalArgumentException if <code>bean</code>
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
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }

        // Resolve nested references
        while (resolver.hasNested(name)) {
            final String next = resolver.next(name);
            Object nestedBean = null;
            try {
                nestedBean = getProperty(bean, next);
            } catch (final IllegalAccessException e) {
                return false;
            } catch (final InvocationTargetException e) {
                return false;
            } catch (final NoSuchMethodException e) {
                return false;
            }
            if (nestedBean == null) {
                throw new NestedNullException
                        ("Null property value for '" + next +
                        "' on bean class '" + bean.getClass() + "'");
            }
            bean = nestedBean;
            name = resolver.remove(name);
        }

        // Remove any subscript from the final name value
        name = resolver.getProperty(name);

        // Treat WrapDynaBean as special case - may be a write-only property
        // (see Jira issue# BEANUTILS-61)
        if (bean instanceof WrapDynaBean) {
            bean = ((WrapDynaBean)bean).getInstance();
        }

        // Return the requested result
        if (bean instanceof DynaBean) {
            // All DynaBean properties are readable
            return (((DynaBean) bean).getDynaClass().getDynaProperty(name) != null);
        } else {
            try {
                final PropertyDescriptor desc =
                    getPropertyDescriptor(bean, name);
                if (desc != null) {
                    Method readMethod = getReadMethod(bean.getClass(), desc);
                    if (readMethod == null) {
                        if (desc instanceof IndexedPropertyDescriptor) {
                            readMethod = ((IndexedPropertyDescriptor) desc).getIndexedReadMethod();
                        } else if (desc instanceof MappedPropertyDescriptor) {
                            readMethod = ((MappedPropertyDescriptor) desc).getMappedReadMethod();
                        }
                        readMethod = MethodUtils.getAccessibleMethod(bean.getClass(), readMethod);
                    }
                    return (readMethod != null);
                } else {
                    return (false);
                }
            } catch (final IllegalAccessException e) {
                return (false);
            } catch (final InvocationTargetException e) {
                return (false);
            } catch (final NoSuchMethodException e) {
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
     * @return <code>true</code> if the property is writeable,
     * otherwise <code>false</code>
     *
     * @throws IllegalArgumentException if <code>bean</code>
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
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }

        // Resolve nested references
        while (resolver.hasNested(name)) {
            final String next = resolver.next(name);
            Object nestedBean = null;
            try {
                nestedBean = getProperty(bean, next);
            } catch (final IllegalAccessException e) {
                return false;
            } catch (final InvocationTargetException e) {
                return false;
            } catch (final NoSuchMethodException e) {
                return false;
            }
            if (nestedBean == null) {
                throw new NestedNullException
                        ("Null property value for '" + next +
                        "' on bean class '" + bean.getClass() + "'");
            }
            bean = nestedBean;
            name = resolver.remove(name);
        }

        // Remove any subscript from the final name value
        name = resolver.getProperty(name);

        // Treat WrapDynaBean as special case - may be a read-only property
        // (see Jira issue# BEANUTILS-61)
        if (bean instanceof WrapDynaBean) {
            bean = ((WrapDynaBean)bean).getInstance();
        }

        // Return the requested result
        if (bean instanceof DynaBean) {
            // All DynaBean properties are writeable
            return (((DynaBean) bean).getDynaClass().getDynaProperty(name) != null);
        } else {
            try {
                final PropertyDescriptor desc =
                    getPropertyDescriptor(bean, name);
                if (desc != null) {
                    Method writeMethod = getWriteMethod(bean.getClass(), desc);
                    if (writeMethod == null) {
                        if (desc instanceof IndexedPropertyDescriptor) {
                            writeMethod = ((IndexedPropertyDescriptor) desc).getIndexedWriteMethod();
                        } else if (desc instanceof MappedPropertyDescriptor) {
                            writeMethod = ((MappedPropertyDescriptor) desc).getMappedWriteMethod();
                        }
                        writeMethod = MethodUtils.getAccessibleMethod(bean.getClass(), writeMethod);
                    }
                    return (writeMethod != null);
                } else {
                    return (false);
                }
            } catch (final IllegalAccessException e) {
                return (false);
            } catch (final InvocationTargetException e) {
                return (false);
            } catch (final NoSuchMethodException e) {
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
     * @throws IndexOutOfBoundsException if the specified index
     *  is outside the valid range for the underlying property
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public void setIndexedProperty(final Object bean, String name,
                                          final Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }

        // Identify the index of the requested individual property
        int index = -1;
        try {
            index = resolver.getIndex(name);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid indexed property '" +
                    name + "' on bean class '" + bean.getClass() + "'");
        }
        if (index < 0) {
            throw new IllegalArgumentException("Invalid indexed property '" +
                    name + "' on bean class '" + bean.getClass() + "'");
        }

        // Isolate the name
        name = resolver.getProperty(name);

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
     * @throws IndexOutOfBoundsException if the specified index
     *  is outside the valid range for the underlying property
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public void setIndexedProperty(final Object bean, final String name,
                                          final int index, final Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null || name.length() == 0) {
            if (bean.getClass().isArray()) {
                Array.set(bean, index, value);
                return;
            } else if (bean instanceof List) {
                final List<Object> list = toObjectList(bean);
                list.set(index, value);
                return;
            }
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }

        // Handle DynaBean instances specially
        if (bean instanceof DynaBean) {
            final DynaProperty descriptor =
                    ((DynaBean) bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" +
                        name + "' on bean class '" + bean.getClass() + "'");
            }
            ((DynaBean) bean).set(name, index, value);
            return;
        }

        // Retrieve the property descriptor for the specified property
        final PropertyDescriptor descriptor =
                getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" +
                    name + "' on bean class '" + bean.getClass() + "'");
        }

        // Call the indexed setter method if there is one
        if (descriptor instanceof IndexedPropertyDescriptor) {
            Method writeMethod = ((IndexedPropertyDescriptor) descriptor).
                    getIndexedWriteMethod();
            writeMethod = MethodUtils.getAccessibleMethod(bean.getClass(), writeMethod);
            if (writeMethod != null) {
                final Object[] subscript = new Object[2];
                subscript[0] = new Integer(index);
                subscript[1] = value;
                try {
                    if (log.isTraceEnabled()) {
                        final String valueClassName =
                            value == null ? "<null>"
                                          : value.getClass().getName();
                        log.trace("setSimpleProperty: Invoking method "
                                  + writeMethod +" with index=" + index
                                  + ", value=" + value
                                  + " (class " + valueClassName+ ")");
                    }
                    invokeMethod(writeMethod, bean, subscript);
                } catch (final InvocationTargetException e) {
                    if (e.getTargetException() instanceof
                            IndexOutOfBoundsException) {
                        throw (IndexOutOfBoundsException)
                                e.getTargetException();
                    } else {
                        throw e;
                    }
                }
                return;
            }
        }

        // Otherwise, the underlying property must be an array or a list
        final Method readMethod = getReadMethod(bean.getClass(), descriptor);
        if (readMethod == null) {
            throw new NoSuchMethodException("Property '" + name +
                    "' has no getter method on bean class '" + bean.getClass() + "'");
        }

        // Call the property getter to get the array or list
        final Object array = invokeMethod(readMethod, bean, EMPTY_OBJECT_ARRAY);
        if (!array.getClass().isArray()) {
            if (array instanceof List) {
                // Modify the specified value in the List
                final List<Object> list = toObjectList(array);
                list.set(index, value);
            } else {
                throw new IllegalArgumentException("Property '" + name +
                        "' is not indexed on bean class '" + bean.getClass() + "'");
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
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public void setMappedProperty(final Object bean, String name,
                                         final Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }

        // Identify the key of the requested individual property
        String key  = null;
        try {
            key = resolver.getKey(name);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException
                    ("Invalid mapped property '" + name +
                    "' on bean class '" + bean.getClass() + "'");
        }
        if (key == null) {
            throw new IllegalArgumentException
                    ("Invalid mapped property '" + name +
                    "' on bean class '" + bean.getClass() + "'");
        }

        // Isolate the name
        name = resolver.getProperty(name);

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
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public void setMappedProperty(final Object bean, final String name,
                                         final String key, final Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }
        if (key == null) {
            throw new IllegalArgumentException("No key specified for property '" +
                    name + "' on bean class '" + bean.getClass() + "'");
        }

        // Handle DynaBean instances specially
        if (bean instanceof DynaBean) {
            final DynaProperty descriptor =
                    ((DynaBean) bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" +
                        name + "' on bean class '" + bean.getClass() + "'");
            }
            ((DynaBean) bean).set(name, key, value);
            return;
        }

        // Retrieve the property descriptor for the specified property
        final PropertyDescriptor descriptor =
                getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" +
                    name + "' on bean class '" + bean.getClass() + "'");
        }

        if (descriptor instanceof MappedPropertyDescriptor) {
            // Call the keyed setter method if there is one
            Method mappedWriteMethod =
                    ((MappedPropertyDescriptor) descriptor).
                    getMappedWriteMethod();
            mappedWriteMethod = MethodUtils.getAccessibleMethod(bean.getClass(), mappedWriteMethod);
            if (mappedWriteMethod != null) {
                final Object[] params = new Object[2];
                params[0] = key;
                params[1] = value;
                if (log.isTraceEnabled()) {
                    final String valueClassName =
                        value == null ? "<null>" : value.getClass().getName();
                    log.trace("setSimpleProperty: Invoking method "
                              + mappedWriteMethod + " with key=" + key
                              + ", value=" + value
                              + " (class " + valueClassName +")");
                }
                invokeMethod(mappedWriteMethod, bean, params);
            } else {
                throw new NoSuchMethodException
                    ("Property '" + name + "' has no mapped setter method" +
                     "on bean class '" + bean.getClass() + "'");
            }
        } else {
          /* means that the result has to be retrieved from a map */
          final Method readMethod = getReadMethod(bean.getClass(), descriptor);
          if (readMethod != null) {
            final Object invokeResult = invokeMethod(readMethod, bean, EMPTY_OBJECT_ARRAY);
            /* test and fetch from the map */
            if (invokeResult instanceof java.util.Map) {
              final java.util.Map<String, Object> map = toPropertyMap(invokeResult);
              map.put(key, value);
            }
          } else {
            throw new NoSuchMethodException("Property '" + name +
                    "' has no mapped getter method on bean class '" +
                    bean.getClass() + "'");
          }
        }

    }


    /**
     * Set the value of the (possibly nested) property of the specified
     * name, for the specified bean, with no type conversions.
     * <p>
     * Example values for parameter "name" are:
     * <ul>
     * <li> "a" -- sets the value of property a of the specified bean </li>
     * <li> "a.b" -- gets the value of property a of the specified bean,
     * then on that object sets the value of property b.</li>
     * <li> "a(key)" -- sets a value of mapped-property a on the specified
     * bean. This effectively means bean.setA("key").</li>
     * <li> "a[3]" -- sets a value of indexed-property a on the specified
     * bean. This effectively means bean.setA(3).</li>
     * </ul>
     *
     * @param bean Bean whose property is to be modified
     * @param name Possibly nested name of the property to be modified
     * @param value Value to which the property is to be set
     *
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @throws IllegalArgumentException if a nested reference to a
     *  property returns null
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public void setNestedProperty(Object bean,
                                         String name, final Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }

        // Resolve nested references
        while (resolver.hasNested(name)) {
            final String next = resolver.next(name);
            Object nestedBean = null;
            if (bean instanceof Map) {
                nestedBean = getPropertyOfMapBean((Map<?, ?>)bean, next);
            } else if (resolver.isMapped(next)) {
                nestedBean = getMappedProperty(bean, next);
            } else if (resolver.isIndexed(next)) {
                nestedBean = getIndexedProperty(bean, next);
            } else {
                nestedBean = getSimpleProperty(bean, next);
            }
            if (nestedBean == null) {
                throw new NestedNullException
                        ("Null property value for '" + name +
                         "' on bean class '" + bean.getClass() + "'");
            }
            bean = nestedBean;
            name = resolver.remove(name);
        }

        if (bean instanceof Map) {
            setPropertyOfMapBean(toPropertyMap(bean), name, value);
        } else if (resolver.isMapped(name)) {
            setMappedProperty(bean, name, value);
        } else if (resolver.isIndexed(name)) {
            setIndexedProperty(bean, name, value);
        } else {
            setSimpleProperty(bean, name, value);
        }

    }

    /**
     * This method is called by method setNestedProperty when the current bean
     * is found to be a Map object, and defines how to deal with setting
     * a property on a Map.
     * <p>
     * The standard implementation here is to:
     * <ul>
     * <li>call bean.set(propertyName) for all propertyName values.</li>
     * <li>throw an IllegalArgumentException if the property specifier
     * contains MAPPED_DELIM or INDEXED_DELIM, as Map entries are essentially
     * simple properties; mapping and indexing operations do not make sense
     * when accessing a map (even thought the returned object may be a Map
     * or an Array).</li>
     * </ul>
     * <p>
     * The default behaviour of beanutils 1.7.1 or later is for assigning to
     * "a.b" to mean a.put(b, obj) always. However the behaviour of beanutils
     * version 1.6.0, 1.6.1, 1.7.0 was for "a.b" to mean a.setB(obj) if such
     * a method existed, and a.put(b, obj) otherwise. In version 1.5 it meant
     * a.put(b, obj) always (ie the same as the behaviour in the current version).
     * In versions prior to 1.5 it meant a.setB(obj) always. [yes, this is
     * all <i>very</i> unfortunate]
     * <p>
     * Users who would like to customise the meaning of "a.b" in method
     * setNestedProperty when a is a Map can create a custom subclass of
     * this class and override this method to implement the behaviour of
     * their choice, such as restoring the pre-1.4 behaviour of this class
     * if they wish. When overriding this method, do not forget to deal
     * with MAPPED_DELIM and INDEXED_DELIM characters in the propertyName.
     * <p>
     * Note, however, that the recommended solution for objects that
     * implement Map but want their simple properties to come first is
     * for <i>those</i> objects to override their get/put methods to implement
     * that behaviour, and <i>not</i> to solve the problem by modifying the
     * default behaviour of the PropertyUtilsBean class by overriding this
     * method.
     *
     * @param bean Map bean
     * @param propertyName The property name
     * @param value the property value
     *
     * @throws IllegalArgumentException when the propertyName is regarded as
     * being invalid.
     *
     * @throws IllegalAccessException just in case subclasses override this
     * method to try to access real setter methods and find permission is denied.
     *
     * @throws InvocationTargetException just in case subclasses override this
     * method to try to access real setter methods, and find it throws an
     * exception when invoked.
     *
     * @throws NoSuchMethodException just in case subclasses override this
     * method to try to access real setter methods, and want to fail if
     * no simple method is available.
     * @since 1.8.0
     */
    protected void setPropertyOfMapBean(final Map<String, Object> bean, String propertyName, final Object value)
        throws IllegalArgumentException, IllegalAccessException,
        InvocationTargetException, NoSuchMethodException {

        if (resolver.isMapped(propertyName)) {
            final String name = resolver.getProperty(propertyName);
            if (name == null || name.length() == 0) {
                propertyName = resolver.getKey(propertyName);
            }
        }

        if (resolver.isIndexed(propertyName) ||
            resolver.isMapped(propertyName)) {
            throw new IllegalArgumentException(
                    "Indexed or mapped properties are not supported on"
                    + " objects of type Map: " + propertyName);
        }

        bean.put(propertyName, value);
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
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public void setProperty(final Object bean, final String name, final Object value)
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
     * @throws IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @throws IllegalArgumentException if <code>bean</code> or
     *  <code>name</code> is null
     * @throws IllegalArgumentException if the property name is
     *  nested or indexed
     * @throws InvocationTargetException if the property accessor method
     *  throws an exception
     * @throws NoSuchMethodException if an accessor method for this
     *  propety cannot be found
     */
    public void setSimpleProperty(final Object bean,
                                         final String name, final Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                    bean.getClass() + "'");
        }

        // Validate the syntax of the property name
        if (resolver.hasNested(name)) {
            throw new IllegalArgumentException
                    ("Nested property names are not allowed: Property '" +
                    name + "' on bean class '" + bean.getClass() + "'");
        } else if (resolver.isIndexed(name)) {
            throw new IllegalArgumentException
                    ("Indexed property names are not allowed: Property '" +
                    name + "' on bean class '" + bean.getClass() + "'");
        } else if (resolver.isMapped(name)) {
            throw new IllegalArgumentException
                    ("Mapped property names are not allowed: Property '" +
                    name + "' on bean class '" + bean.getClass() + "'");
        }

        // Handle DynaBean instances specially
        if (bean instanceof DynaBean) {
            final DynaProperty descriptor =
                    ((DynaBean) bean).getDynaClass().getDynaProperty(name);
            if (descriptor == null) {
                throw new NoSuchMethodException("Unknown property '" +
                        name + "' on dynaclass '" +
                        ((DynaBean) bean).getDynaClass() + "'" );
            }
            ((DynaBean) bean).set(name, value);
            return;
        }

        // Retrieve the property setter method for the specified property
        final PropertyDescriptor descriptor =
                getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" +
                    name + "' on class '" + bean.getClass() + "'" );
        }
        final Method writeMethod = getWriteMethod(bean.getClass(), descriptor);
        if (writeMethod == null) {
            throw new NoSuchMethodException("Property '" + name +
                    "' has no setter method in class '" + bean.getClass() + "'");
        }

        // Call the property setter method
        final Object[] values = new Object[1];
        values[0] = value;
        if (log.isTraceEnabled()) {
            final String valueClassName =
                value == null ? "<null>" : value.getClass().getName();
            log.trace("setSimpleProperty: Invoking method " + writeMethod
                      + " with value " + value + " (class " + valueClassName + ")");
        }
        invokeMethod(writeMethod, bean, values);

    }

    /** This just catches and wraps IllegalArgumentException. */
    private Object invokeMethod(
                        final Method method,
                        final Object bean,
                        final Object[] values)
                            throws
                                IllegalAccessException,
                                InvocationTargetException {
        if(bean == null) {
            throw new IllegalArgumentException("No bean specified " +
                "- this should have been checked before reaching this method");
        }

        try {

            return method.invoke(bean, values);

        } catch (final NullPointerException cause) {
            // JDK 1.3 and JDK 1.4 throw NullPointerException if an argument is
            // null for a primitive value (JDK 1.5+ throw IllegalArgumentException)
            String valueString = "";
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    if (i>0) {
                        valueString += ", " ;
                    }
                    if (values[i] == null) {
                        valueString += "<null>";
                    } else {
                        valueString += (values[i]).getClass().getName();
                    }
                }
            }
            String expectedString = "";
            final Class<?>[] parTypes = method.getParameterTypes();
            if (parTypes != null) {
                for (int i = 0; i < parTypes.length; i++) {
                    if (i > 0) {
                        expectedString += ", ";
                    }
                    expectedString += parTypes[i].getName();
                }
            }
            final IllegalArgumentException e = new IllegalArgumentException(
                "Cannot invoke " + method.getDeclaringClass().getName() + "."
                + method.getName() + " on bean class '" + bean.getClass() +
                "' - " + cause.getMessage()
                // as per https://issues.apache.org/jira/browse/BEANUTILS-224
                + " - had objects of type \"" + valueString
                + "\" but expected signature \""
                +   expectedString + "\""
                );
            if (!BeanUtils.initCause(e, cause)) {
                log.error("Method invocation failed", cause);
            }
            throw e;
        } catch (final IllegalArgumentException cause) {
            String valueString = "";
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    if (i>0) {
                        valueString += ", " ;
                    }
                    if (values[i] == null) {
                        valueString += "<null>";
                    } else {
                        valueString += (values[i]).getClass().getName();
                    }
                }
            }
            String expectedString = "";
            final Class<?>[] parTypes = method.getParameterTypes();
            if (parTypes != null) {
                for (int i = 0; i < parTypes.length; i++) {
                    if (i > 0) {
                        expectedString += ", ";
                    }
                    expectedString += parTypes[i].getName();
                }
            }
            final IllegalArgumentException e = new IllegalArgumentException(
                "Cannot invoke " + method.getDeclaringClass().getName() + "."
                + method.getName() + " on bean class '" + bean.getClass() +
                "' - " + cause.getMessage()
                // as per https://issues.apache.org/jira/browse/BEANUTILS-224
                + " - had objects of type \"" + valueString
                + "\" but expected signature \""
                +   expectedString + "\""
                );
            if (!BeanUtils.initCause(e, cause)) {
                log.error("Method invocation failed", cause);
            }
            throw e;

        }
    }

    /**
     * Obtains the {@code BeanIntrospectionData} object describing the specified bean
     * class. This object is looked up in the internal cache. If necessary, introspection
     * is performed now on the affected bean class, and the results object is created.
     *
     * @param beanClass the bean class in question
     * @return the {@code BeanIntrospectionData} object for this class
     * @throws IllegalArgumentException if the bean class is <b>null</b>
     */
    private BeanIntrospectionData getIntrospectionData(final Class<?> beanClass) {
        if (beanClass == null) {
            throw new IllegalArgumentException("No bean class specified");
        }

        // Look up any cached information for this bean class
        BeanIntrospectionData data = descriptorsCache.get(beanClass);
        if (data == null) {
            data = fetchIntrospectionData(beanClass);
            descriptorsCache.put(beanClass, data);
        }

        return data;
    }

    /**
     * Performs introspection on the specified class. This method invokes all {@code BeanIntrospector} objects that were
     * added to this instance.
     *
     * @param beanClass the class to be inspected
     * @return a data object with the results of introspection
     */
    private BeanIntrospectionData fetchIntrospectionData(final Class<?> beanClass) {
        final DefaultIntrospectionContext ictx = new DefaultIntrospectionContext(beanClass);

        for (final BeanIntrospector bi : introspectors) {
            try {
                bi.introspect(ictx);
            } catch (final IntrospectionException iex) {
                log.error("Exception during introspection", iex);
            }
        }

        return new BeanIntrospectionData(ictx.getPropertyDescriptors());
    }

    /**
     * Converts an object to a list of objects. This method is used when dealing
     * with indexed properties. It assumes that indexed properties are stored as
     * lists of objects.
     *
     * @param obj the object to be converted
     * @return the resulting list of objects
     */
    private static List<Object> toObjectList(final Object obj) {
        @SuppressWarnings("unchecked")
        final
        // indexed properties are stored in lists of objects
        List<Object> list = (List<Object>) obj;
        return list;
    }

    /**
     * Converts an object to a map with property values. This method is used
     * when dealing with mapped properties. It assumes that mapped properties
     * are stored in a Map&lt;String, Object&gt;.
     *
     * @param obj the object to be converted
     * @return the resulting properties map
     */
    private static Map<String, Object> toPropertyMap(final Object obj) {
        @SuppressWarnings("unchecked")
        final
        // mapped properties are stores in maps of type <String, Object>
        Map<String, Object> map = (Map<String, Object>) obj;
        return map;
    }
}
