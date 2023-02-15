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

import java.util.Arrays;

/**
 * <p>DynaClass which implements the {@code MutableDynaClass} interface.</p>
 *
 * <p>A {@code MutableDynaClass</code> is a specialized extension to <code>DynaClass}
 *    that allows properties to be added or removed dynamically.</p>
 *
 * <p>This implementation has one slightly unusual default behavior - calling
 *    the {@code getDynaProperty(name)} method for a property which doesn't
 *    exist returns a {@code DynaProperty</code> rather than <code>null}. The
 *    reason for this is that {@code BeanUtils} calls this method to check if
 *    a property exists before trying to set the value. This would defeat the object
 *    of the {@code LazyDynaBean} which automatically adds missing properties
 *    when any of its {@code set()} methods are called. For this reason the
 *    {@code isDynaProperty(name)} method has been added to this implementation
 *    in order to determine if a property actually exists. If the more <i>normal</i>
 *    behavior of returning {@code null} is required, then this can be achieved
 *    by calling the {@code setReturnNull(true)}.</p>
 *
 * <p>The {@code add(name, type, readable, writable)} method is not implemented
 *    and always throws an {@code UnsupportedOperationException}. I believe
 *    this attributes need to be added to the {@code DynaProperty} class
 *    in order to control read/write facilities.</p>
 *
 * @see LazyDynaBean
 */
public class LazyDynaClass extends BasicDynaClass implements MutableDynaClass  {

    private static final long serialVersionUID = 1L;

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
     * Constructs a new LazyDynaClass with default parameters.
     */
    public LazyDynaClass() {
        this(null, (DynaProperty[])null);
    }

    /**
     * Constructs a new LazyDynaClass with the specified name.
     *
     * @param name Name of this DynaBean class
     */
    public LazyDynaClass(final String name) {
        this(name, (DynaProperty[])null);
    }

    /**
     * Constructs a new LazyDynaClass with the specified name and DynaBean class.
     *
     * @param name Name of this DynaBean class
     * @param dynaBeanClass The implementation class for new instances
     */
    public LazyDynaClass(final String name, final Class<?> dynaBeanClass) {
        this(name, dynaBeanClass, null);
    }

    /**
     * Constructs a new LazyDynaClass with the specified name, DynaBean class and properties.
     *
     * @param name Name of this DynaBean class
     * @param dynaBeanClass The implementation class for new instances
     * @param properties Property descriptors for the supported properties
     */
    public LazyDynaClass(final String name, final Class<?> dynaBeanClass, final DynaProperty[] properties) {
        super(name, dynaBeanClass, properties);
    }

    /**
     * Constructs a new LazyDynaClass with the specified name and properties.
     *
     * @param name Name of this DynaBean class
     * @param properties Property descriptors for the supported properties
     */
    public LazyDynaClass(final String name, final DynaProperty[] properties) {
        this(name, LazyDynaBean.class, properties);
    }

    /**
     * Add a new dynamic property.
     *
     * @param property Property the new dynamic property to add.
     *
     * @throws IllegalArgumentException if name is null
     * @throws IllegalStateException if this DynaClass is currently
     *  restricted, so no new properties can be added
     */
    protected void add(final DynaProperty property) {
        if (property.getName() == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }

        if (isRestricted()) {
            throw new IllegalStateException("DynaClass is currently restricted. No new properties can be added.");
        }

        // Check if property already exists
        if (propertiesMap.get(property.getName()) != null) {
            return;
        }

        // Create a new property array with the specified property
        final DynaProperty[] oldProperties = getDynaProperties();
        final DynaProperty[] newProperties = Arrays.copyOf(oldProperties, oldProperties.length + 1);
        newProperties[oldProperties.length] = property;

        // Update the properties
        setProperties(newProperties);
    }

    /**
     * Add a new dynamic property with no restrictions on data type,
     * readability, or writeability.
     *
     * @param name Name of the new dynamic property
     *
     * @throws IllegalArgumentException if name is null
     * @throws IllegalStateException if this DynaClass is currently
     *  restricted, so no new properties can be added
     */
    @Override
    public void add(final String name) {
        add(new DynaProperty(name));
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
        if (type == null) {
            add(name);
        } else {
            add(new DynaProperty(name, type));
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
     *    before trying to set it - since these <i>Lazy</i> implementations automatically
     *    add any new properties when they are set, returning {@code null} from
     *    this method would defeat their purpose.</p>
     *
     * @param name Name of the dynamic property for which a descriptor
     *  is requested
     * @return The dyna property for the specified name
     *
     * @throws IllegalArgumentException if no property name is specified
     */
    @Override
    public DynaProperty getDynaProperty(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }

        DynaProperty dynaProperty = propertiesMap.get(name);

        // If it doesn't exist and returnNull is false
        // create a new DynaProperty
        if (dynaProperty == null && !isReturnNull() && !isRestricted()) {
            dynaProperty = new DynaProperty(name);
        }

        return dynaProperty;
    }

    /**
     * <p>Indicate whether a property actually exists.</p>
     *
     * <p><strong>N.B.</strong> Using {@code getDynaProperty(name) == null}
     * doesn't work in this implementation because that method might
     * return a DynaProperty if it doesn't exist (depending on the
     * {@code returnNull} indicator).</p>
     *
     * @param name The name of the property to check
     * @return {@code true} if there is a property of the
     * specified name, otherwise {@code false}
     * @throws IllegalArgumentException if no property name is specified
     */
    public boolean isDynaProperty(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }

        return propertiesMap.get(name) != null;
    }

    /**
     * <p>Is this DynaClass currently restricted.</p>
     * <p>If restricted, no changes to the existing registration of
     *  property names, data types, readability, or writeability are allowed.</p>
     * @return {@code true} if this {@link MutableDynaClass} cannot be changed
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

        // Ignore if property doesn't exist
        if (propertiesMap.get(name) == null) {
            return;
        }

        // Create a new property array of without the specified property
        final DynaProperty[] oldProperties = getDynaProperties();
        final DynaProperty[] newProperties = new DynaProperty[oldProperties.length-1];
        int j = 0;
        for (final DynaProperty oldProperty : oldProperties) {
            if (!name.equals(oldProperty.getName())) {
                newProperties[j] = oldProperty;
                j++;
            }
        }

        // Update the properties
        setProperties(newProperties);
    }

    /**
     * <p>Set whether this DynaClass is currently restricted.</p>
     * <p>If restricted, no changes to the existing registration of
     *  property names, data types, readability, or writeability are allowed.</p>
     * @param restricted {@code true} if this {@link MutableDynaClass} cannot
     * be changed otherwise {@code false}
     */
    @Override
    public void setRestricted(final boolean restricted) {
        this.restricted = restricted;
    }

    /**
     * Sets whether this DynaClass should return a {@code null} from
     * the {@code getDynaProperty(name)} method if the property
     * doesn't exist.
     * @param returnNull {@code true</code> if a <code>null} {@link DynaProperty}
     * should be returned if the property doesn't exist, otherwise
     * {@code false} if a new {@link DynaProperty} should be created.
     */
    public void setReturnNull(final boolean returnNull) {
        this.returnNull = returnNull;
    }

}