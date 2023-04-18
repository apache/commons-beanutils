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

import java.util.Map;

/**
 * <p>Provides a <i>light weight</i> {@code DynaBean</code> facade to a <code>Map}
 *  with <i>lazy</i> map/list processing.</p>
 *
 * <p>Its a <i>light weight</i> {@code DynaBean} implementation because there is no
 *    actual {@code DynaClass</code> associated with this <code>DynaBean} - in fact
 *    it implements the {@code DynaClass} interface itself providing <i>pseudo</i> DynaClass
 *    behavior from the actual values stored in the {@code Map}.</p>
 *
 * <p>As well providing rhe standard {@code DynaBean</code> access to the <code>Map}'s properties
 *    this class also provides the usual <i>Lazy</i> behavior:</p>
 *    <ul>
 *       <li>Properties don't need to be pre-defined in a {@code DynaClass}</li>
 *       <li>Indexed properties ({@code Lists</code> or <code>Arrays}) are automatically instantiated
 *           and <i>grown</i> so that they are large enough to cater for the index being set.</li>
 *       <li>Mapped properties are automatically instantiated.</li>
 *    </ul>
 *
 * <p><b><u><i>Restricted</i> DynaClass</u></b></p>
 *    <p>This class implements the {@code MutableDynaClass} interface.
 *       {@code MutableDynaClass</code> have a facility to <i>restrict</i> the <code>DynaClass}
 *       so that its properties cannot be modified. If the {@code MutableDynaClass} is
 *       restricted then calling any of the {@code set()} methods for a property which
 *       doesn't exist will result in a {@code IllegalArgumentException} being thrown.</p>
 *
 */
public class LazyDynaMap extends LazyDynaBean implements MutableDynaClass {

    private static final long serialVersionUID = 1L;

    /**
     * The name of this DynaClass (analogous to the
     * {@code getName()</code> method of <code>java.lang.Class}).
     */
    protected String name;

    /**
     * Controls whether changes to this DynaClass's properties are allowed.
     */
    protected boolean restricted;

    /**
     * <p>Controls whether the {@code getDynaProperty()} method returns
     * null if a property doesn't exist - or creates a new one.</p>
     *
     * <p>Default is {@code false}.
     */
    protected boolean returnNull;

    /**
     * Default Constructor.
     */
    public LazyDynaMap() {
        this(null, (Map<String, Object>)null);
    }

    /**
     * Constructs a new {@code LazyDynaMap} based on an exisiting DynaClass
     *
     * @param dynaClass DynaClass to copy the name and properties from
     */
    public LazyDynaMap(final DynaClass dynaClass) {
        this(dynaClass.getName(), dynaClass.getDynaProperties());
    }

    /**
     * Constructs a new {@code LazyDynaMap} with the specified properties.
     *
     * @param properties Property descriptors for the supported properties
     */
    public LazyDynaMap(final DynaProperty[] properties) {
        this(null, properties);
    }

    /**
     * Constructs a new {@code LazyDynaMap</code> with the specified <code>Map}.
     *
     * @param values The Map backing this {@code LazyDynaMap}
     */
    public LazyDynaMap(final Map<String, Object> values) {
        this(null, values);
    }

    /**
     * Constructs a new {@code LazyDynaMap} with the specified name.
     *
     * @param name Name of this DynaBean class
     */
    public LazyDynaMap(final String name) {
        this(name, (Map<String, Object>)null);
    }

    /**
     * Constructs a new {@code LazyDynaMap} with the specified name and properties.
     *
     * @param name Name of this DynaBean class
     * @param properties Property descriptors for the supported properties
     */
    public LazyDynaMap(final String name, final DynaProperty[] properties) {
        this(name, (Map<String, Object>)null);
        if (properties != null) {
            for (final DynaProperty property : properties) {
                add(property);
            }
        }
    }

    /**
     * Constructs a new {@code LazyDynaMap</code> with the specified name and  <code>Map}.
     *
     * @param name Name of this DynaBean class
     * @param values The Map backing this {@code LazyDynaMap}
     */
    public LazyDynaMap(final String name, final Map<String, Object> values) {
        this.name      = name   == null ? "LazyDynaMap" : name;
        this.values    = values == null ? newMap()      : values;
        this.dynaClass = this;
    }



    /**
     * Add a new dynamic property.
     *
     * @param property Property the new dynamic property to add.
     *
     * @throws IllegalArgumentException if name is null
     */
    protected void add(final DynaProperty property) {
        add(property.getName(), property.getType());
    }

    /**
     * Add a new dynamic property with no restrictions on data type,
     * readability, or writeability.
     *
     * @param name Name of the new dynamic property
     *
     * @throws IllegalArgumentException if name is null
     */
    @Override
    public void add(final String name) {
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
     * @throws IllegalArgumentException if name is null
     * @throws IllegalStateException if this DynaClass is currently
     *  restricted, so no new properties can be added
     */
    @Override
    public void add(final String name, final Class<?> type) {
        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }

        if (isRestricted()) {
            throw new IllegalStateException("DynaClass is currently restricted. No new properties can be added.");
        }

        final Object value = values.get(name);

        // Check if the property already exists
        if (value == null) {
            values.put(name, type == null ? null : createProperty(name, type));
        }
    }



    /**
     * <p>Add a new dynamic property with the specified data type, readability,
     * and writeability.</p>
     *
     * <p><strong>N.B.</strong>Support for readable/writable properties has not been implemented
     *    and this method always throws a {@code UnsupportedOperationException}.</p>
     *
     * <p>I'm not sure the intention of the original authors for this method, but it seems to
     *    me that readable/writable should be attributes of the {@code DynaProperty} class
     *    (which they are not) and is the reason this method has not been implemented.</p>
     *
     * @param name Name of the new dynamic property
     * @param type Data type of the new dynamic property (null for no
     *  restrictions)
     * @param readable Set to {@code true} if this property value
     *  should be readable
     * @param writable Set to {@code true} if this property value
     *  should be writable
     *
     * @throws UnsupportedOperationException anytime this method is called
     */
    @Override
    public void add(final String name, final Class<?> type, final boolean readable, final boolean writable) {
        throw new java.lang.UnsupportedOperationException("readable/writable properties not supported");
    }

    /**
     * <p>Return an array of {@code PropertyDescriptor} for the properties
     * currently defined in this DynaClass.  If no properties are defined, a
     * zero-length array will be returned.</p>
     *
     * <p><strong>FIXME</strong> - Should we really be implementing
     * {@code getBeanInfo()} instead, which returns property descriptors
     * and a bunch of other stuff?</p>
     * @return the set of properties for this DynaClass
     */
    @Override
    public DynaProperty[] getDynaProperties() {
        int i = 0;
        final DynaProperty[] properties = new DynaProperty[values.size()];
        for (final Map.Entry<String, Object> e : values.entrySet()) {
            final String name = e.getKey();
            final Object value = values.get(name);
            properties[i++] = new DynaProperty(name, value == null ? null
                    : value.getClass());
        }

        return properties;
    }

    /**
     * <p>Return a property descriptor for the specified property.</p>
     *
     * <p>If the property is not found and the {@code returnNull} indicator is
     *    {@code true</code>, this method always returns <code>null}.</p>
     *
     * <p>If the property is not found and the {@code returnNull} indicator is
     *    {@code false} a new property descriptor is created and returned (although
     *    its not actually added to the DynaClass's properties). This is the default
     *    behavior.</p>
     *
     * <p>The reason for not returning a {@code null} property descriptor is that
     *    {@code BeanUtils} uses this method to check if a property exists
     *    before trying to set it - since these <i>Map</i> implementations automatically
     *    add any new properties when they are set, returning {@code null} from
     *    this method would defeat their purpose.</p>
     *
     * @param name Name of the dynamic property for which a descriptor
     *  is requested
     * @return The descriptor for the specified property
     *
     * @throws IllegalArgumentException if no property name is specified
     */
    @Override
    public DynaProperty getDynaProperty(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }

        final Object value = values.get(name);

        // If it doesn't exist and returnNull is false
        // create a new DynaProperty
        if (value == null && isReturnNull()) {
            return null;
        }

        if (value == null) {
            return new DynaProperty(name);
        }
        return new DynaProperty(name, value.getClass());
    }

    /**
     * Gets the underlying Map backing this {@code DynaBean}
     * @return the underlying Map
     * @since 1.8.0
     */
    @Override
    public Map<String, Object> getMap() {
        return values;
    }

    /**
     * Gets the name of this DynaClass (analogous to the
     * {@code getName()</code> method of <code>java.lang.Class})
     *
     * @return the name of the DynaClass
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
         * <p>Indicate whether a property actually exists.</p>
         *
         * <p><strong>N.B.</strong> Using {@code getDynaProperty(name) == null}
         * doesn't work in this implementation because that method might
         * return a DynaProperty if it doesn't exist (depending on the
         * {@code returnNull} indicator).</p>
         *
         * @param name Name of the dynamic property
         * @return {@code true} if the property exists,
         * otherwise {@code false}
         * @throws IllegalArgumentException if no property name is specified
         */
        @Override
        protected boolean isDynaProperty(final String name) {
            if (name == null) {
                throw new IllegalArgumentException("Property name is missing.");
            }

            return values.containsKey(name);
        }

    /**
     * <p>Is this DynaClass currently restricted.</p>
     * <p>If restricted, no changes to the existing registration of
     *  property names, data types, readability, or writeability are allowed.</p>
     *
     * @return {@code true} if this Mutable {@link DynaClass} is restricted,
     * otherwise {@code false}
     */
    @Override
    public boolean isRestricted() {
        return restricted;
    }

    /**
     * Should this DynaClass return a {@code null} from
     * the {@code getDynaProperty(name)} method if the property
     * doesn't exist.
     *
     * @return {@code true</code> if a <code>null} {@link DynaProperty}
     * should be returned if the property doesn't exist, otherwise
     * {@code false} if a new {@link DynaProperty} should be created.
     */
    public boolean isReturnNull() {
        return returnNull;
    }

    /**
     * Instantiate and return a new DynaBean instance, associated
     * with this DynaClass.
     * @return A new {@code DynaBean} instance
     */
    @Override
    public DynaBean newInstance()  {
        // Create a new instance of the Map
        Map<String, Object> newMap = null;
        try {
            final
            // The new map is used as properties map
            Map<String, Object> temp = getMap().getClass().newInstance();
            newMap = temp;
        } catch (final Exception ex) {
            newMap = newMap();
        }

        // Crate new LazyDynaMap and initialize properties
        final LazyDynaMap lazyMap = new LazyDynaMap(newMap);
        final DynaProperty[] properties = this.getDynaProperties();
        if (properties != null) {
            for (final DynaProperty property : properties) {
                lazyMap.add(property);
            }
        }
        return lazyMap;
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
     * @throws IllegalArgumentException if name is null
     * @throws IllegalStateException if this DynaClass is currently
     *  restricted, so no properties can be removed
     */
    @Override
    public void remove(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }

        if (isRestricted()) {
            throw new IllegalStateException("DynaClass is currently restricted. No properties can be removed.");
        }

        values.remove(name);
    }

    /**
     * Sets the value of a simple property with the specified name.
     *
     * @param name Name of the property whose value is to be set
     * @param value Value to which this property is to be set
     */
    @Override
    public void set(final String name, final Object value) {
        if (isRestricted() && !values.containsKey(name)) {
            throw new IllegalArgumentException
                    ("Invalid property name '" + name + "' (DynaClass is restricted)");
        }

        values.put(name, value);
    }

    /**
     * Sets the Map backing this {@code DynaBean}
     *
     * @param values The new Map of values
     */
    public void setMap(final Map<String, Object> values) {
        this.values = values;
    }

    /**
     * <p>Set whether this DynaClass is currently restricted.</p>
     * <p>If restricted, no changes to the existing registration of
     *  property names, data types, readability, or writeability are allowed.</p>
     *
     * @param restricted The new restricted state
     */
    @Override
    public void setRestricted(final boolean restricted) {
        this.restricted = restricted;
    }

   /**
 * Sets whether this DynaClass should return a {@code null} from
 * the {@code getDynaProperty(name)} method if the property
 * doesn't exist.
 *
 * @param returnNull {@code true</code> if a <code>null} {@link DynaProperty}
 * should be returned if the property doesn't exist, otherwise
 * {@code false} if a new {@link DynaProperty} should be created.
 */
public void setReturnNull(final boolean returnNull) {
    this.returnNull = returnNull;
}

}