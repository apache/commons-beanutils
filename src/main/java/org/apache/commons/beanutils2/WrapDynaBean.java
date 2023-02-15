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

package org.apache.commons.beanutils2;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

/**
 * <p>Implementation of {@code DynaBean} that wraps a standard JavaBean
 * instance, so that DynaBean APIs can be used to access its properties.</p>
 *
 * <p>
 * The most common use cases for this class involve wrapping an existing java bean.
 * (This makes it different from the typical use cases for other {@code DynaBean}'s.)
 * For example:
 * </p>
 * <pre><code>
 *  Object aJavaBean = ...;
 *  ...
 *  DynaBean db = new WrapDynaBean(aJavaBean);
 *  ...
 * </code></pre>
 *
 * <p><strong>IMPLEMENTATION NOTE</strong> - This implementation does not
 * support the {@code contains()</code> and <code>remove()} methods.</p>
 *
 */

public class WrapDynaBean implements DynaBean, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The {@code DynaClass} "base class" that this DynaBean
     * is associated with.
     */
    protected transient WrapDynaClass dynaClass;

    /**
     * The JavaBean instance wrapped by this WrapDynaBean.
     */
    protected Object instance;

    /**
     * Constructs a new {@code DynaBean} associated with the specified
     * JavaBean instance.
     *
     * @param instance JavaBean instance to be wrapped
     */
    public WrapDynaBean(final Object instance) {
        this(instance, null);
    }

    /**
     * Creates a new instance of {@code WrapDynaBean}, associates it with the specified
     * JavaBean instance, and initializes the bean's {@code DynaClass}. Using this
     * constructor this {@code WrapDynaBean} instance can be assigned a class which has
     * been configured externally. If no {@code WrapDynaClass} is provided, a new one is
     * created using a standard mechanism.
     *
     * @param instance JavaBean instance to be wrapped
     * @param cls the optional {@code WrapDynaClass} to be used for this bean
     * @since 1.9
     */
    public WrapDynaBean(final Object instance, final WrapDynaClass cls) {
        this.instance = instance;
        this.dynaClass = cls != null ? cls : (WrapDynaClass) getDynaClass();
    }

    /**
     * Does the specified mapped property contain a value for the specified
     * key value?
     *
     * @param name Name of the property to check
     * @param key Name of the key to check
     * @return {@code true} if the mapped property contains a value for
     * the specified key, otherwise {@code false}
     *
     * @throws IllegalArgumentException if there is no property
     *  of the specified name
     */
    @Override
    public boolean contains(final String name, final String key) {
        throw new UnsupportedOperationException
                ("WrapDynaBean does not support contains()");
    }

    /**
     * Gets the value of a simple property with the specified name.
     *
     * @param name Name of the property whose value is to be retrieved
     * @return The property's value
     *
     * @throws IllegalArgumentException if there is no property
     *  of the specified name
     */
    @Override
    public Object get(final String name) {
        Object value = null;
        try {
            value = getPropertyUtils().getSimpleProperty(instance, name);
        } catch (final InvocationTargetException ite) {
            final Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException
                    ("Error reading property '" + name +
                              "' nested exception - " + cause);
        } catch (final Throwable t) {
            throw new IllegalArgumentException
                    ("Error reading property '" + name +
                              "', exception - " + t);
        }
        return value;
    }

    /**
     * Gets the value of an indexed property with the specified name.
     *
     * @param name Name of the property whose value is to be retrieved
     * @param index Index of the value to be retrieved
     * @return The indexed property's value
     *
     * @throws IllegalArgumentException if there is no property
     *  of the specified name
     * @throws IllegalArgumentException if the specified property
     *  exists, but is not indexed
     * @throws IndexOutOfBoundsException if the specified index
     *  is outside the range of the underlying property
     * @throws NullPointerException if no array or List has been
     *  initialized for this property
     */
    @Override
    public Object get(final String name, final int index) {
        Object value = null;
        try {
            value = getPropertyUtils().getIndexedProperty(instance, name, index);
        } catch (final IndexOutOfBoundsException e) {
            throw e;
        } catch (final InvocationTargetException ite) {
            final Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException
                    ("Error reading indexed property '" + name +
                              "' nested exception - " + cause);
        } catch (final Throwable t) {
            throw new IllegalArgumentException
                    ("Error reading indexed property '" + name +
                              "', exception - " + t);
        }
        return value;
    }

    /**
     * Gets the value of a mapped property with the specified name,
     * or {@code null} if there is no value for the specified key.
     *
     * @param name Name of the property whose value is to be retrieved
     * @param key Key of the value to be retrieved
     * @return The mapped property's value
     *
     * @throws IllegalArgumentException if there is no property
     *  of the specified name
     * @throws IllegalArgumentException if the specified property
     *  exists, but is not mapped
     */
    @Override
    public Object get(final String name, final String key) {
        Object value = null;
        try {
            value = getPropertyUtils().getMappedProperty(instance, name, key);
        } catch (final InvocationTargetException ite) {
            final Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException
                    ("Error reading mapped property '" + name +
                              "' nested exception - " + cause);
        } catch (final Throwable t) {
            throw new IllegalArgumentException
                    ("Error reading mapped property '" + name +
                              "', exception - " + t);
        }
        return value;
    }

    /**
     * Gets the {@code DynaClass} instance that describes the set of
     * properties available for this DynaBean.
     * @return The associated DynaClass
     */
    @Override
    public DynaClass getDynaClass() {
        if (dynaClass == null) {
            dynaClass = WrapDynaClass.createDynaClass(instance.getClass());
        }

        return this.dynaClass;
    }

    /**
     * Gets the property descriptor for the specified property name.
     *
     * @param name Name of the property for which to retrieve the descriptor
     * @return The descriptor for the specified property
     *
     * @throws IllegalArgumentException if this is not a valid property
     *  name for our DynaClass
     */
    protected DynaProperty getDynaProperty(final String name) {
        final DynaProperty descriptor = getDynaClass().getDynaProperty(name);
        if (descriptor == null) {
            throw new IllegalArgumentException
                    ("Invalid property name '" + name + "'");
        }
        return descriptor;
    }

    /**
     * Gets the bean instance wrapped by this DynaBean.
     * For most common use cases,
     * this object should already be known
     * and this method safely be ignored.
     * But some creators of frameworks using {@code DynaBean}'s may
     * find this useful.
     *
     * @return the java bean Object wrapped by this {@code DynaBean}
     */
    public Object getInstance() {
        return instance;
    }

    /**
     * Returns the {@code PropertyUtilsBean} instance to be used for accessing properties.
     * If available, this object is obtained from the associated {@code WrapDynaClass}.
     *
     * @return the associated {@code PropertyUtilsBean}
     */
    private PropertyUtilsBean getPropertyUtils() {
        PropertyUtilsBean propUtils = null;
        if (dynaClass != null) {
            propUtils = dynaClass.getPropertyUtilsBean();
        }
        return propUtils != null ? propUtils : PropertyUtilsBean.getInstance();
    }

    /**
     * Remove any existing value for the specified key on the
     * specified mapped property.
     *
     * @param name Name of the property for which a value is to
     *  be removed
     * @param key Key of the value to be removed
     *
     * @throws IllegalArgumentException if there is no property
     *  of the specified name
     */
    @Override
    public void remove(final String name, final String key) {
        throw new UnsupportedOperationException
                ("WrapDynaBean does not support remove()");
    }

    /**
     * Sets the value of an indexed property with the specified name.
     *
     * @param name Name of the property whose value is to be set
     * @param index Index of the property to be set
     * @param value Value to which this property is to be set
     *
     * @throws ConversionException if the specified value cannot be
     *  converted to the type required for this property
     * @throws IllegalArgumentException if there is no property
     *  of the specified name
     * @throws IllegalArgumentException if the specified property
     *  exists, but is not indexed
     * @throws IndexOutOfBoundsException if the specified index
     *  is outside the range of the underlying property
     */
    @Override
    public void set(final String name, final int index, final Object value) {
        try {
            getPropertyUtils().setIndexedProperty(instance, name, index, value);
        } catch (final IndexOutOfBoundsException e) {
            throw e;
        } catch (final InvocationTargetException ite) {
            final Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException
                    ("Error setting indexed property '" + name +
                              "' nested exception - " + cause);
        } catch (final Throwable t) {
            throw new IllegalArgumentException
                    ("Error setting indexed property '" + name +
                              "', exception - " + t);
        }
    }

    /**
     * Sets the value of a simple property with the specified name.
     *
     * @param name Name of the property whose value is to be set
     * @param value Value to which this property is to be set
     *
     * @throws ConversionException if the specified value cannot be
     *  converted to the type required for this property
     * @throws IllegalArgumentException if there is no property
     *  of the specified name
     * @throws NullPointerException if an attempt is made to set a
     *  primitive property to null
     */
    @Override
    public void set(final String name, final Object value) {
        try {
            getPropertyUtils().setSimpleProperty(instance, name, value);
        } catch (final InvocationTargetException ite) {
            final Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException
                    ("Error setting property '" + name +
                              "' nested exception -" + cause);
        } catch (final Throwable t) {
            throw new IllegalArgumentException
                    ("Error setting property '" + name +
                              "', exception - " + t);
        }
    }

    /**
     * Sets the value of a mapped property with the specified name.
     *
     * @param name Name of the property whose value is to be set
     * @param key Key of the property to be set
     * @param value Value to which this property is to be set
     *
     * @throws ConversionException if the specified value cannot be
     *  converted to the type required for this property
     * @throws IllegalArgumentException if there is no property
     *  of the specified name
     * @throws IllegalArgumentException if the specified property
     *  exists, but is not mapped
     */
    @Override
    public void set(final String name, final String key, final Object value) {
        try {
            getPropertyUtils().setMappedProperty(instance, name, key, value);
        } catch (final InvocationTargetException ite) {
            final Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException
                    ("Error setting mapped property '" + name +
                              "' nested exception - " + cause);
        } catch (final Throwable t) {
            throw new IllegalArgumentException
                    ("Error setting mapped property '" + name +
                              "', exception - " + t);
        }
    }
}
