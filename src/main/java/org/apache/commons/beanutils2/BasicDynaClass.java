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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Objects;

/**
 * <p>Minimal implementation of the {@code DynaClass} interface.  Can be
 * used as a convenience base class for more sophisticated implementations.</p>
 * <p><strong>IMPLEMENTATION NOTE</strong> - The {@code DynaBean}
 * implementation class supplied to our constructor MUST have a one-argument
 * constructor of its own that accepts a {@code DynaClass}.  This is
 * used to associate the DynaBean instance with this DynaClass.</p>
 */
public class BasicDynaClass implements DynaClass, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The method signature of the constructor we will use to create
     * new DynaBean instances.
     */
    private static final Class<?>[] CONSTRUCTOR_TYPES = { DynaClass.class };

    /**
     * The constructor of the {@code dynaBeanClass} that we will use
     * for creating new instances.
     */
    protected transient Constructor<?> constructor;

    /**
     * The argument values to be passed to the constructor we will use
     * to create new DynaBean instances.
     */
    protected Object[] constructorValues = { this };

    /**
     * The {@code DynaBean} implementation class we will use for
     * creating new instances.
     */
    protected Class<?> dynaBeanClass = BasicDynaBean.class;

    /**
     * The "name" of this DynaBean class.
     */
    protected String name = this.getClass().getName();

    /**
     * The set of dynamic properties that are part of this DynaClass.
     */
    protected DynaProperty[] properties = DynaProperty.EMPTY_ARRAY;

    /**
     * The set of dynamic properties that are part of this DynaClass,
     * keyed by the property name.  Individual descriptor instances will
     * be the same instances as those in the {@code properties} list.
     */
    protected HashMap<String, DynaProperty> propertiesMap = new HashMap<>();

    /**
     * Constructs a new BasicDynaClass with default parameters.
     */
    public BasicDynaClass() {
        this(null, null, null);
    }

    /**
     * Constructs a new BasicDynaClass with the specified parameters.
     *
     * @param name Name of this DynaBean class
     * @param dynaBeanClass The implementation class for new instances
     */
    public BasicDynaClass(final String name, final Class<?> dynaBeanClass) {
        this(name, dynaBeanClass, null);
    }

    /**
     * Constructs a new BasicDynaClass with the specified parameters.
     *
     * @param name Name of this DynaBean class
     * @param dynaBeanClass The implementation class for new instances
     * @param properties Property descriptors for the supported properties
     */
    public BasicDynaClass(final String name, Class<?> dynaBeanClass,
                          final DynaProperty[] properties) {
        if (name != null) {
            this.name = name;
        }
        if (dynaBeanClass == null) {
            dynaBeanClass = BasicDynaBean.class;
        }
        setDynaBeanClass(dynaBeanClass);
        if (properties != null) {
            setProperties(properties);
        }
    }

    /**
     * Gets the Class object we will use to create new instances in the
     * {@code newInstance()} method.  This Class <strong>MUST</strong>
     * implement the {@code DynaBean} interface.
     *
     * @return The class of the {@link DynaBean}
     */
    public Class<?> getDynaBeanClass() {
        return this.dynaBeanClass;
    }

    /**
     * <p>Return an array of {@code PropertyDescriptor} for the properties
     * currently defined in this DynaClass.  If no properties are defined, a
     * zero-length array will be returned.</p>
     *
     * <p><strong>FIXME</strong> - Should we really be implementing
     * {@code getBeanInfo()} instead, which returns property descriptors
     * and a bunch of other stuff?</p>
     *
     * @return the set of properties for this DynaClass
     */
    @Override
    public DynaProperty[] getDynaProperties() {
        return properties.clone();
    }

    /**
     * Gets a property descriptor for the specified property, if it exists;
     * otherwise, return {@code null}.
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
            throw new IllegalArgumentException
                    ("No property name specified");
        }
        return propertiesMap.get(name);
    }

    /**
     * Gets the name of this DynaClass (analogous to the
     * {@code getName()} method of {@code java.lang.Class}, which
     * allows the same {@code DynaClass} implementation class to support
     * different dynamic classes, with different sets of properties.
     *
     * @return the name of the DynaClass
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Instantiate and return a new DynaBean instance, associated
     * with this DynaClass.
     *
     * @return A new {@code DynaBean} instance
     * @throws IllegalAccessException if the Class or the appropriate
     *  constructor is not accessible
     * @throws InstantiationException if this Class represents an abstract
     *  class, an array class, a primitive type, or void; or if instantiation
     *  fails for some other reason
     */
    @Override
    public DynaBean newInstance()
            throws IllegalAccessException, InstantiationException {
        try {
            // Refind the constructor after a deserialization (if needed)
            if (constructor == null) {
                setDynaBeanClass(this.dynaBeanClass);
            }
            // Invoke the constructor to create a new bean instance
            return (DynaBean) constructor.newInstance(constructorValues);
        } catch (final InvocationTargetException e) {
            throw new InstantiationException
                    (e.getTargetException().getMessage());
        }
    }

    /**
     * Sets the Class object we will use to create new instances in the
     * {@code newInstance()} method.  This Class <strong>MUST</strong>
     * implement the {@code DynaBean} interface.
     *
     * @param dynaBeanClass The new Class object
     *
     * @throws IllegalArgumentException if the specified Class does not
     *  implement the {@code DynaBean} interface
     */
    protected void setDynaBeanClass(final Class<?> dynaBeanClass) {
        // Validate the argument type specified
        if (dynaBeanClass.isInterface()) {
            throw new IllegalArgumentException
                    ("Class " + dynaBeanClass.getName() +
                    " is an interface, not a class");
        }
        if (!DynaBean.class.isAssignableFrom(dynaBeanClass)) {
            throw new IllegalArgumentException
                    ("Class " + dynaBeanClass.getName() +
                    " does not implement DynaBean");
        }

        // Identify the Constructor we will use in newInstance()
        try {
            this.constructor = dynaBeanClass.getConstructor(CONSTRUCTOR_TYPES);
        } catch (final NoSuchMethodException e) {
            throw new IllegalArgumentException
                    ("Class " + dynaBeanClass.getName() +
                    " does not have an appropriate constructor");
        }
        this.dynaBeanClass = dynaBeanClass;
    }

    /**
     * Sets the list of dynamic properties supported by this DynaClass.
     *
     * @param properties List of dynamic properties to be supported
     */
    protected void setProperties(final DynaProperty[] properties) {
        this.properties = Objects.requireNonNull(properties, "properties");
        propertiesMap.clear();
        for (final DynaProperty property : properties) {
            propertiesMap.put(property.getName(), property);
        }
    }

}
