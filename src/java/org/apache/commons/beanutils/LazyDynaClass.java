/*
 * Copyright 2004 The Apache Software Foundation.
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

/**
 * <p>DynaClass which implements the <code>MutableDynaClass</code> interface.</p>
 *
 * <p>A <code>MutableDynaClass</code> is a specialized extension to <code>DynaClass</code>
 *    that allows properties to be added or removed dynamically.</p>
 *
 * <p>This implementation has one slightly unusual default behaviour - calling
 *    the <code>getDynaProperty(name)</code> method for a property which doesn't
 *    exist returns a <code>DynaProperty</code> rather than <code>null</code>. The
 *    reason for this is that <code>BeanUtils</code> calls this method to check if
 *    a property exists before trying to set the value. This would defeat the object
 *    of the <code>LazyDynaBean</code> which automatically adds missing properties
 *    when any of its <code>set()</code> methods are called. For this reason the
 *    <code>isDynaProperty(name)</code> method has been added to this implementation
 *    in order to determine if a property actually exists. If the more <i>normal</i>
 *    behaviour of returning <code>null</code> is required, then this can be achieved
 *    by calling the <code>setReturnNull(true)</code>.</p>
 *
 * <p>The <code>add(name, type, readable, writable)</code> method is not implemented
 *    and always throws an <code>UnsupportedOperationException</code>. I believe
 *    this attributes need to be added to the <code>DynaProperty</code> class
 *    in order to control read/write facilities.</p>
 *
 * @see LazyDynaBean
 * @author Niall Pemberton
 */
public class LazyDynaClass extends BasicDynaClass implements MutableDynaClass  {

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

    /**
     * Construct a new LazyDynaClass with default parameters.
     */
    public LazyDynaClass() {
        this(null, (DynaProperty[])null);
    }

    /**
     * Construct a new LazyDynaClass with the specified name.
     *
     * @param name Name of this DynaBean class
     */
    public LazyDynaClass(String name) {
        this(name, (DynaProperty[])null);
    }

    /**
     * Construct a new LazyDynaClass with the specified name and DynaBean class.
     *
     * @param name Name of this DynaBean class
     * @param dynaBeanClass The implementation class for new instances
     */
    public LazyDynaClass(String name, Class dynaBeanClass) {
        this(name, dynaBeanClass, null);
    }

    /**
     * Construct a new LazyDynaClass with the specified name and properties.
     *
     * @param name Name of this DynaBean class
     * @param properties Property descriptors for the supported properties
     */
    public LazyDynaClass(String name, DynaProperty[] properties) {
        this(name, LazyDynaBean.class, properties);
    }

    /**
     * Construct a new LazyDynaClass with the specified name, DynaBean class and properties.
     *
     * @param name Name of this DynaBean class
     * @param dynaBeanClass The implementation class for new intances
     * @param properties Property descriptors for the supported properties
     */
    public LazyDynaClass(String name, Class dynaBeanClass, DynaProperty properties[]) {
        super(name, dynaBeanClass, properties);
    }

    /**
     * <p>Is this DynaClass currently restricted.</p>
     * <p>If restricted, no changes to the existing registration of
     *  property names, data types, readability, or writeability are allowed.</p>
     */
    public boolean isRestricted() {
        return restricted;
    }

    /**
     * <p>Set whether this DynaClass is currently restricted.</p>
     * <p>If restricted, no changes to the existing registration of
     *  property names, data types, readability, or writeability are allowed.</p>
     */
    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    /**
     * Should this DynaClass return a <code>null</code> from
     * the <code>getDynaProperty(name)</code> method if the property
     * doesn't exist.
     */
    public boolean isReturnNull() {
        return returnNull;
    }

    /**
     * Set whether this DynaClass should return a <code>null</code> from
     * the <code>getDynaProperty(name)</code> method if the property
     * doesn't exist.
     */
    public void setReturnNull(boolean returnNull) {
        this.returnNull = returnNull;
    }

    /**
     * Add a new dynamic property with no restrictions on data type,
     * readability, or writeability.
     *
     * @param name Name of the new dynamic property
     *
     * @exception IllegalArgumentException if name is null
     * @exception IllegalStateException if this DynaClass is currently
     *  restricted, so no new properties can be added
     */
    public void add(String name) {
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
     * @exception IllegalArgumentException if name is null
     * @exception IllegalStateException if this DynaClass is currently
     *  restricted, so no new properties can be added
     */
    public void add(String name, Class type) {
        add(new DynaProperty(name, type));
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
     * @exception IllegalStateException if this DynaClass is currently
     *  restricted, so no new properties can be added
     */
    protected void add(DynaProperty property) {

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
        DynaProperty[] oldProperties = getDynaProperties();
        DynaProperty[] newProperties = new DynaProperty[oldProperties.length+1];
        System.arraycopy(oldProperties, 0, newProperties, 0, oldProperties.length);
        newProperties[oldProperties.length] = property;

       // Update the properties
       setProperties(newProperties);

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

        // Ignore if property doesn't exist
        if (propertiesMap.get(name) == null) {
            return;
        }


        // Create a new property array of without the specified property
        DynaProperty[] oldProperties = getDynaProperties();
        DynaProperty[] newProperties = new DynaProperty[oldProperties.length-1];
        int j = 0;
        for (int i = 0; i < oldProperties.length; i++) {
            if (!(name.equals(oldProperties[i].getName()))) {
                newProperties[j] = oldProperties[i];
                j++;
            }
        }

        // Update the properties
        setProperties(newProperties);

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
     *    before trying to set it - since these <i>Lazy</i> implementations automatically
     *    add any new properties when they are set, returning <code>null</code> from
     *    this method would defeat their purpose.</p>
     *
     * @param name Name of the dynamic property for which a descriptor
     *  is requested
     *
     * @exception IllegalArgumentException if no property name is specified
     */
    public DynaProperty getDynaProperty(String name) {

        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }

        DynaProperty dynaProperty = (DynaProperty)propertiesMap.get(name);

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
     * <p><strong>N.B.</strong> Using <code>getDynaProperty(name) == null</code>
     * doesn't work in this implementation because that method might
     * return a DynaProperty if it doesn't exist (depending on the
     * <code>returnNull</code> indicator).</p>
     *
     * @exception IllegalArgumentException if no property name is specified
     */
    public boolean isDynaProperty(String name) {

        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }

        return propertiesMap.get(name) ==  null ? false : true;

    }

}