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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;


/**
 * <p>Implementation of <code>DynaBean</code> that wraps a standard JavaBean
 * instance, so that DynaBean APIs can be used to access its properties.</p>
 *
 * <p>
 * The most common use cases for this class involve wrapping an existing java bean.
 * (This makes it different from the typical use cases for other <code>DynaBean</code>'s.) 
 * For example:
 * </p>
 * <code><pre>
 *  Object aJavaBean = ...;
 *  ...
 *  DynaBean db = new WrapDynaBean(aJavaBean);
 *  ...
 * </pre></code>
 *
 * <p><strong>IMPLEMENTATION NOTE</strong> - This implementation does not
 * support the <code>contains()</code> and <code>remove()</code> methods.</p>
 *
 * @author Craig McClanahan
 * @version $Revision$ $Date$
 */

public class WrapDynaBean implements DynaBean, Serializable {


    // ---------------------------------------------------------- Constructors


    /**
     * Construct a new <code>DynaBean</code> associated with the specified
     * JavaBean instance.
     *
     * @param instance JavaBean instance to be wrapped
     */
    public WrapDynaBean(Object instance) {

        super();
        this.instance = instance;
        this.dynaClass = (WrapDynaClass)getDynaClass();

    }


    // ---------------------------------------------------- Instance Variables


    /**
     * The <code>DynaClass</code> "base class" that this DynaBean
     * is associated with.
     */
    protected transient WrapDynaClass dynaClass = null;


    /**
     * The JavaBean instance wrapped by this WrapDynaBean.
     */
    protected Object instance = null;


    // ------------------------------------------------------ DynaBean Methods


    /**
     * Does the specified mapped property contain a value for the specified
     * key value?
     *
     * @param name Name of the property to check
     * @param key Name of the key to check
     * @return <code>true<code> if the mapped property contains a value for
     * the specified key, otherwise <code>false</code>
     *
     * @exception IllegalArgumentException if there is no property
     *  of the specified name
     */
    public boolean contains(String name, String key) {

        throw new UnsupportedOperationException
                ("WrapDynaBean does not support contains()");

    }


    /**
     * Return the value of a simple property with the specified name.
     *
     * @param name Name of the property whose value is to be retrieved
     * @return The property's value
     *
     * @exception IllegalArgumentException if there is no property
     *  of the specified name
     */
    public Object get(String name) {

        Object value = null;
        try {
            value = PropertyUtils.getSimpleProperty(instance, name);
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException
                    ("Error reading property '" + name +
                              "' nested exception - " + cause);
        } catch (Throwable t) {
            throw new IllegalArgumentException
                    ("Error reading property '" + name +
                              "', exception - " + t);
        }
        return (value);

    }


    /**
     * Return the value of an indexed property with the specified name.
     *
     * @param name Name of the property whose value is to be retrieved
     * @param index Index of the value to be retrieved
     * @return The indexed property's value
     *
     * @exception IllegalArgumentException if there is no property
     *  of the specified name
     * @exception IllegalArgumentException if the specified property
     *  exists, but is not indexed
     * @exception IndexOutOfBoundsException if the specified index
     *  is outside the range of the underlying property
     * @exception NullPointerException if no array or List has been
     *  initialized for this property
     */
    public Object get(String name, int index) {

        Object value = null;
        try {
            value = PropertyUtils.getIndexedProperty(instance, name, index);
        } catch (IndexOutOfBoundsException e) {
            throw e;
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException
                    ("Error reading indexed property '" + name +
                              "' nested exception - " + cause);
        } catch (Throwable t) {
            throw new IllegalArgumentException
                    ("Error reading indexed property '" + name +
                              "', exception - " + t);
        }
        return (value);

    }


    /**
     * Return the value of a mapped property with the specified name,
     * or <code>null</code> if there is no value for the specified key.
     *
     * @param name Name of the property whose value is to be retrieved
     * @param key Key of the value to be retrieved
     * @return The mapped property's value
     *
     * @exception IllegalArgumentException if there is no property
     *  of the specified name
     * @exception IllegalArgumentException if the specified property
     *  exists, but is not mapped
     */
    public Object get(String name, String key) {

        Object value = null;
        try {
            value = PropertyUtils.getMappedProperty(instance, name, key);
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException
                    ("Error reading mapped property '" + name +
                              "' nested exception - " + cause);
        } catch (Throwable t) {
            throw new IllegalArgumentException
                    ("Error reading mapped property '" + name +
                              "', exception - " + t);
        }
        return (value);

    }


    /**
     * Return the <code>DynaClass</code> instance that describes the set of
     * properties available for this DynaBean.
     * @return The associated DynaClass
     */
    public DynaClass getDynaClass() {

        if (dynaClass == null) {
            dynaClass = WrapDynaClass.createDynaClass(instance.getClass());
        }

        return (this.dynaClass);

    }


    /**
     * Remove any existing value for the specified key on the
     * specified mapped property.
     *
     * @param name Name of the property for which a value is to
     *  be removed
     * @param key Key of the value to be removed
     *
     * @exception IllegalArgumentException if there is no property
     *  of the specified name
     */
    public void remove(String name, String key) {


        throw new UnsupportedOperationException
                ("WrapDynaBean does not support remove()");

    }


    /**
     * Set the value of a simple property with the specified name.
     *
     * @param name Name of the property whose value is to be set
     * @param value Value to which this property is to be set
     *
     * @exception ConversionException if the specified value cannot be
     *  converted to the type required for this property
     * @exception IllegalArgumentException if there is no property
     *  of the specified name
     * @exception NullPointerException if an attempt is made to set a
     *  primitive property to null
     */
    public void set(String name, Object value) {

        try {
            PropertyUtils.setSimpleProperty(instance, name, value);
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException
                    ("Error setting property '" + name +
                              "' nested exception -" + cause);
        } catch (Throwable t) {
            throw new IllegalArgumentException
                    ("Error setting property '" + name +
                              "', exception - " + t);
        }

    }


    /**
     * Set the value of an indexed property with the specified name.
     *
     * @param name Name of the property whose value is to be set
     * @param index Index of the property to be set
     * @param value Value to which this property is to be set
     *
     * @exception ConversionException if the specified value cannot be
     *  converted to the type required for this property
     * @exception IllegalArgumentException if there is no property
     *  of the specified name
     * @exception IllegalArgumentException if the specified property
     *  exists, but is not indexed
     * @exception IndexOutOfBoundsException if the specified index
     *  is outside the range of the underlying property
     */
    public void set(String name, int index, Object value) {

        try {
            PropertyUtils.setIndexedProperty(instance, name, index, value);
        } catch (IndexOutOfBoundsException e) {
            throw e;
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException
                    ("Error setting indexed property '" + name +
                              "' nested exception - " + cause);
        } catch (Throwable t) {
            throw new IllegalArgumentException
                    ("Error setting indexed property '" + name +
                              "', exception - " + t);
        }

    }


    /**
     * Set the value of a mapped property with the specified name.
     *
     * @param name Name of the property whose value is to be set
     * @param key Key of the property to be set
     * @param value Value to which this property is to be set
     *
     * @exception ConversionException if the specified value cannot be
     *  converted to the type required for this property
     * @exception IllegalArgumentException if there is no property
     *  of the specified name
     * @exception IllegalArgumentException if the specified property
     *  exists, but is not mapped
     */
    public void set(String name, String key, Object value) {

        try {
            PropertyUtils.setMappedProperty(instance, name, key, value);
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException
                    ("Error setting mapped property '" + name +
                              "' nested exception - " + cause);
        } catch (Throwable t) {
            throw new IllegalArgumentException
                    ("Error setting mapped property '" + name +
                              "', exception - " + t);
        }

    }

    /** 
     * Gets the bean instance wrapped by this DynaBean.
     * For most common use cases, 
     * this object should already be known 
     * and this method safely be ignored.
     * But some creators of frameworks using <code>DynaBean</code>'s may 
     * find this useful.
     *
     * @return the java bean Object wrapped by this <code>DynaBean</code>
     */
    public Object getInstance() {
        return instance;
    }


    // ------------------------------------------------------ Protected Methods


    /**
     * Return the property descriptor for the specified property name.
     *
     * @param name Name of the property for which to retrieve the descriptor
     * @return The descriptor for the specified property
     *
     * @exception IllegalArgumentException if this is not a valid property
     *  name for our DynaClass
     */
    protected DynaProperty getDynaProperty(String name) {

        DynaProperty descriptor = getDynaClass().getDynaProperty(name);
        if (descriptor == null) {
            throw new IllegalArgumentException
                    ("Invalid property name '" + name + "'");
        }
        return (descriptor);

    }


}
