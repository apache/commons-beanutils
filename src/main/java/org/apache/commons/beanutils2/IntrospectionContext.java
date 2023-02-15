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

import java.beans.PropertyDescriptor;
import java.util.Set;

/**
 * <p>
 * A context interface used during introspection for querying and setting
 * property descriptors.
 * </p>
 * <p>
 * An implementation of this interface is passed to {@link BeanIntrospector}
 * objects during processing of a bean class. It allows the
 * {@code BeanIntrospector} to deliver descriptors for properties it has
 * detected. It is also possible to find out which properties have already been
 * found by another {@code BeanIntrospector}; this allows multiple
 * {@code BeanIntrospector} instances to collaborate.
 * </p>
 *
 * @since 1.9
 */
public interface IntrospectionContext {

    /**
     * Adds the given property descriptor to this context. This method is called
     * by a {@code BeanIntrospector} during introspection for each detected
     * property. If this context already contains a descriptor for the affected
     * property, it is overridden.
     *
     * @param desc the property descriptor
     */
    void addPropertyDescriptor(PropertyDescriptor desc);

    /**
     * Adds an array of property descriptors to this context. Using this method
     * multiple descriptors can be added at once.
     *
     * @param descriptors the array of descriptors to be added
     */
    void addPropertyDescriptors(PropertyDescriptor[] descriptors);

    /**
     * Returns the descriptor for the property with the given name or
     * <b>null</b> if this property is unknown.
     *
     * @param name the name of the property in question
     * @return the descriptor for this property or <b>null</b> if this property
     *         is unknown
     */
    PropertyDescriptor getPropertyDescriptor(String name);

    /**
     * Returns the class that is subject of introspection.
     *
     * @return the current class
     */
    Class<?> getTargetClass();

    /**
     * Tests whether a descriptor for the property with the given name is
     * already contained in this context. This method can be used for instance
     * to prevent that an already existing property descriptor is overridden.
     *
     * @param name the name of the property in question
     * @return <b>true</b> if a descriptor for this property has already been
     *         added, <b>false</b> otherwise
     */
    boolean hasProperty(String name);

    /**
     * Returns a set with the names of all properties known to this context.
     *
     * @return a set with the known property names
     */
    Set<String> propertyNames();

    /**
     * Removes the descriptor for the property with the given name.
     *
     * @param name the name of the affected property
     */
    void removePropertyDescriptor(String name);
}
