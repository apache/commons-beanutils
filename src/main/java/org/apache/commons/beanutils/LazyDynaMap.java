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

import java.util.Map;
import java.util.Iterator;

/**
 * <p>Provides a <i>light weight</i> <code>DynaBean</code> facade to a <code>Map</code>
 *  with <i>lazy</i> map/list processing.</p>
 *
 * <p>Its a <i>light weight</i> <code>DynaBean</code> implementation because there is no
 *    actual <code>DynaClass</code> associated with this <code>DynaBean</code> - in fact
 *    it implements the <code>DynaClass</code> interface itself providing <i>pseudo</i> DynaClass
 *    behaviour from the actual values stored in the <code>Map</code>.</p>
 *
 * <p>As well providing rhe standard <code>DynaBean</code> access to the <code>Map</code>'s properties
 *    this class also provides the usual <i>Lazy</i> behaviour:</p>
 *    <ul>
 *       <li>Properties don't need to be pre-defined in a <code>DynaClass</code></li>
 *       <li>Indexed properties (<code>Lists</code> or <code>Arrays</code>) are automatically instantiated
 *           and <i>grown</i> so that they are large enough to cater for the index being set.</li>
 *       <li>Mapped properties are automatically instantiated.</li>
 *    </ul>
 *
 * <p><b><u><i>Restricted</i> DynaClass</u></b></p>
 *    <p>This class implements the <code>MutableDynaClass</code> interface.
 *       <code>MutableDynaClass</code> have a facility to <i>restrict</i> the <code>DynaClass</code>
 *       so that its properties cannot be modified. If the <code>MutableDynaClass</code> is
 *       restricted then calling any of the <code>set()</code> methods for a property which
 *       doesn't exist will result in a <code>IllegalArgumentException</code> being thrown.</p>
 *
 */
public class LazyDynaMap extends LazyDynaBean implements MutableDynaClass {

    /**
     * The name of this DynaClass (analogous to the
     * <code>getName()</code> method of <code>java.lang.Class</code>).
     */
    protected String name;

    /**
     * Controls whether changes to this DynaClass's properties are allowed.
     */
    protected boolean restricted;

    /**
     * <p>Controls whether the <code>getDynaProperty()</code> method returns
     * null if a property doesn't exist - or creates a new one.</p>
     *
     * <p>Default is <code>false</code>.
     */
    protected boolean returnNull = false;


    // ------------------- Constructors ----------------------------------

    /**
     * Default Constructor.
     */
    public LazyDynaMap() {
        this(null, (Map)null);
    }

    /**
     * Construct a new <code>LazyDynaMap</code> with the specified name.
     *
     * @param name Name of this DynaBean class
     */
    public LazyDynaMap(String name) {
        this(name, (Map)null);
    }

    /**
     * Construct a new <code>LazyDynaMap</code> with the specified <code>Map</code>.
     *
     * @param values The Map backing this <code>LazyDynaMap</code>
     */
    public LazyDynaMap(Map values) {
        this(null, values);
    }

    /**
     * Construct a new <code>LazyDynaMap</code> with the specified name and  <code>Map</code>.
     *
     * @param name Name of this DynaBean class
     * @param values The Map backing this <code>LazyDynaMap</code>
     */
    public LazyDynaMap(String name, Map values) {
        this.name      = name   == null ? "LazyDynaMap" : name;
        this.values    = values == null ? newMap()      : values;
        this.dynaClass = this;
    }

    /**
     * Construct a new <code>LazyDynaMap</code> with the specified properties.
     *
     * @param properties Property descriptors for the supported properties
     */
    public LazyDynaMap(DynaProperty[] properties) {
        this(null, properties);
    }

    /**
     * Construct a new <code>LazyDynaMap</code> with the specified name and properties.
     *
     * @param name Name of this DynaBean class
     * @param properties Property descriptors for the supported properties
     */
    public LazyDynaMap(String name, DynaProperty[] properties) {
        this(name, (Map)null);
        if (properties != null) {
            for (int i = 0; i < properties.length; i++) {
                add(properties[i]);
            }
        }
    }

    /**
     * Construct a new <code>LazyDynaMap</code> based on an exisiting DynaClass
     *
     * @param dynaClass DynaClass to copy the name and properties from
     */
    public LazyDynaMap(DynaClass dynaClass) {
        this(dynaClass.getName(), dynaClass.getDynaProperties());
    }

    // ------------------- Public Methods ----------------------------------

    /**
     * Set the Map backing this <code>DynaBean</code>
     *
     * @param values The new Map of values
     */
    public void setMap(Map values) {
        this.values = values;
    }

    /**
     * Return the underlying Map backing this <code>DynaBean</code>
     * @return the underlying Map
     * @since 1.8.0
     */
    public Map getMap() {
        return values;
    }

    // ------------------- DynaBean Methods ----------------------------------

    /**
     * Set the value of a simple property with the specified name.
     *
     * @param name Name of the property whose value is to be set
     * @param value Value to which this property is to be set
     */
    public void set(String name, Object value) {

        if (isRestricted() && !values.containsKey(name)) {
            throw new IllegalArgumentException
                    ("Invalid property name '" + name + "' (DynaClass is restricted)");
        }

        values.put(name, value);

    }

    // ------------------- DynaClass Methods ----------------------------------

    /**
     * Return the name of this DynaClass (analogous to the
     * <code>getName()</code> method of <code>java.lang.Class</code)
     *
     * @return the name of the DynaClass
     */
    public String getName() {
        return this.name;
    }

    /**
     * <p>Return a property descriptor for the specified property.</p>
     *
     * <p>If the property is not found and the <code>returnNull</code> indicator is
     *    <code>true</code>, this method always returns <code>null</code>.</p>
     *
     * <p>If the property is not found and the <code>returnNull</code> indicator is
     *    <code>false</code> a new property descriptor is created and returned (although
     *    its not actually added to the DynaClass's properties). This is the default
     *    beahviour.</p>
     *
     * <p>The reason for not returning a <code>null</code> property descriptor is that
     *    <code>BeanUtils</code> uses this method to check if a property exists
     *    before trying to set it - since these <i>Map</i> implementations automatically
     *    add any new properties when they are set, returning <code>null</code> from
     *    this method would defeat their purpose.</p>
     *
     * @param name Name of the dynamic property for which a descriptor
     *  is requested
     * @return The descriptor for the specified property
     *
     * @exception IllegalArgumentException if no property name is specified
     */
    public DynaProperty getDynaProperty(String name) {

        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }

        // If it doesn't exist and returnNull is false
        // create a new DynaProperty
        if (!values.containsKey(name) && isReturnNull()) {
            return null;
        }

        Object value = values.get(name);

        if (value == null) {
            return new DynaProperty(name);
        } else {
            return new DynaProperty(name, value.getClass());
        }

    }

    /**
     * <p>Return an array of <code>ProperyDescriptors</code> for the properties
     * currently defined in this DynaClass.  If no properties are defined, a
     * zero-length array will be returned.</p>
     *
     * <p><strong>FIXME</strong> - Should we really be implementing
     * <code>getBeanInfo()</code> instead, which returns property descriptors
     * and a bunch of other stuff?</p>
     * @return the set of properties for this DynaClass
     */
    public DynaProperty[] getDynaProperties() {

        int i = 0;
        DynaProperty[] properties = new DynaProperty[values.size()];
        Iterator iterator = values.keySet().iterator();

        while (iterator.hasNext()) {
            String name = (String)iterator.next();
            Object value = values.get(name);
            properties[i++] = new DynaProperty(name, value == null ? null : value.getClass());
        }

        return properties;

    }

    /**
     * Instantiate and return a new DynaBean instance, associated
     * with this DynaClass.
     * @return A new <code>DynaBean</code> instance
     */
    public DynaBean newInstance()  {

        // Create a new instance of the Map
        Map newMap = null;
        try {
            newMap = (Map)getMap().getClass().newInstance();
        } catch(Exception ex) {
            newMap = newMap();
        }

        // Crate new LazyDynaMap and initialize properties
        LazyDynaMap lazyMap = new LazyDynaMap(newMap);
        DynaProperty[] properties = this.getDynaProperties();
        if (properties != null) {
            for (int i = 0; i < properties.length; i++) {
                lazyMap.add(properties[i]);
            }
        }
        return lazyMap;
    }


    // ------------------- MutableDynaClass Methods ----------------------------------

    /**
     * <p>Is this DynaClass currently restricted.</p>
     * <p>If restricted, no changes to the existing registration of
     *  property names, data types, readability, or writeability are allowed.</p>
     *
     * @return <code>true</code> if this Mutable {@link DynaClass} is restricted,
     * otherwise <code>false</code>
     */
    public boolean isRestricted() {
        return restricted;
    }

    /**
     * <p>Set whether this DynaClass is currently restricted.</p>
     * <p>If restricted, no changes to the existing registration of
     *  property names, data types, readability, or writeability are allowed.</p>
     *
     * @param restricted The new restricted state
     */
    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    /**
     * Add a new dynamic property with no restrictions on data type,
     * readability, or writeability.
     *
     * @param name Name of the new dynamic property
     *
     * @exception IllegalArgumentException if name is null
     */
    public void add(String name) {
        add(name, null);
    }

    /**
     * Add a new dynamic property with the specified data type, but with
     * no restrictions on readability or writeability.
     *
     * @param name Name of the new dynamic property
     * @param type Data type of the new dynamic property (null for no
     *  restrictions)
     *
     * @exception IllegalArgumentException if name is null
     * @exception IllegalStateException if this DynaClass is currently
     *  restricted, so no new properties can be added
     */
    public void add(String name, Class type) {

        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }

        if (isRestricted()) {
            throw new IllegalStateException("DynaClass is currently restricted. No new properties can be added.");
        }

        Object value = values.get(name);

        // Check if the property already exists
        if (value == null) {
            values.put(name, type == null ? null : createProperty(name, type));
        }

    }

    /**
     * <p>Add a new dynamic property with the specified data type, readability,
     * and writeability.</p>
     *
     * <p><strong>N.B.</strong>Support for readable/writeable properties has not been implemented
     *    and this method always throws a <code>UnsupportedOperationException</code>.</p>
     *
     * <p>I'm not sure the intention of the original authors for this method, but it seems to
     *    me that readable/writable should be attributes of the <code>DynaProperty</code> class
     *    (which they are not) and is the reason this method has not been implemented.</p>
     *
     * @param name Name of the new dynamic property
     * @param type Data type of the new dynamic property (null for no
     *  restrictions)
     * @param readable Set to <code>true</code> if this property value
     *  should be readable
     * @param writeable Set to <code>true</code> if this property value
     *  should be writeable
     *
     * @exception UnsupportedOperationException anytime this method is called
     */
    public void add(String name, Class type, boolean readable, boolean writeable) {
        throw new java.lang.UnsupportedOperationException("readable/writable properties not supported");
    }

    /**
     * Add a new dynamic property.
     *
     * @param property Property the new dynamic property to add.
     *
     * @exception IllegalArgumentException if name is null
     */
    protected void add(DynaProperty property) {
        add(property.getName(), property.getType());
    }

    /**
     * Remove the specified dynamic property, and any associated data type,
     * readability, and writeability, from this dynamic class.
     * <strong>NOTE</strong> - This does <strong>NOT</strong> cause any
     * corresponding property values to be removed from DynaBean instances
     * associated with this DynaClass.
     *
     * @param name Name of the dynamic property to remove
     *
     * @exception IllegalArgumentException if name is null
     * @exception IllegalStateException if this DynaClass is currently
     *  restricted, so no properties can be removed
     */
    public void remove(String name) {

        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }

        if (isRestricted()) {
            throw new IllegalStateException("DynaClass is currently restricted. No properties can be removed.");
        }

        // Remove, if property doesn't exist
        if (values.containsKey(name)) {
            values.remove(name);
        }

    }


    // ------------------- Additional Public Methods ----------------------------------

    /**
     * Should this DynaClass return a <code>null</code> from
     * the <code>getDynaProperty(name)</code> method if the property
     * doesn't exist.
     *
     * @return <code>true<code> if a <code>null</code> {@link DynaProperty}
     * should be returned if the property doesn't exist, otherwise
     * <code>false</code> if a new {@link DynaProperty} should be created.
     */
    public boolean isReturnNull() {
        return returnNull;
    }

    /**
     * Set whether this DynaClass should return a <code>null</code> from
     * the <code>getDynaProperty(name)</code> method if the property
     * doesn't exist.
     *
     * @param returnNull <code>true<code> if a <code>null</code> {@link DynaProperty}
     * should be returned if the property doesn't exist, otherwise
     * <code>false</code> if a new {@link DynaProperty} should be created.
     */
    public void setReturnNull(boolean returnNull) {
        this.returnNull = returnNull;
    }


    // ------------------- Protected Methods ----------------------------------

   /**
     * <p>Indicate whether a property actually exists.</p>
     *
     * <p><strong>N.B.</strong> Using <code>getDynaProperty(name) == null</code>
     * doesn't work in this implementation because that method might
     * return a DynaProperty if it doesn't exist (depending on the
     * <code>returnNull</code> indicator).</p>
     *
     * @param name Name of the dynamic property
     * @return <code>true</code> if the property exists,
     * otherwise <code>false</code>
     * @exception IllegalArgumentException if no property name is specified
     */
    protected boolean isDynaProperty(String name) {

        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }

        return values.containsKey(name);

    }

}