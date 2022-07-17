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

import java.beans.IntrospectionException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * A specialized {@code BeanIntrospector} implementation which suppresses some properties.
 * </p>
 * <p>
 * An instance of this class is passed a set with the names of the properties it should
 * process. During introspection of a bean class it removes all these properties from the
 * {@link IntrospectionContext}. So effectively, properties added by a different
 * {@code BeanIntrospector} are removed again.
 * </p>
 *
 * @since 1.9.2
 */
public class SuppressPropertiesBeanIntrospector implements BeanIntrospector {
    /**
     * A specialized instance which is configured to suppress the special {@code class}
     * properties of Java beans. Unintended access to the property {@code class} (which is
     * common to all Java objects) can be a security risk because it also allows access to
     * the class loader. Adding this instance as {@code BeanIntrospector} to an instance
     * of {@code PropertyUtilsBean} suppresses the {@code class} property; it can then no
     * longer be accessed.
     */
    public static final SuppressPropertiesBeanIntrospector SUPPRESS_CLASS =
            new SuppressPropertiesBeanIntrospector(Collections.singleton("class"));

    /** A set with the names of the properties to be suppressed. */
    private final Set<String> propertyNames;

    /**
     * Creates a new instance of {@code SuppressPropertiesBeanIntrospector} and sets the
     * names of the properties to be suppressed.
     *
     * @param propertiesToSuppress the names of the properties to be suppressed (must not
     * be <b>null</b>)
     * @throws IllegalArgumentException if the collection with property names is
     * <b>null</b>
     */
    public SuppressPropertiesBeanIntrospector(final Collection<String> propertiesToSuppress) {
        if (propertiesToSuppress == null) {
            throw new IllegalArgumentException("Property names must not be null!");
        }

        propertyNames = Collections.unmodifiableSet(new HashSet<>(
                propertiesToSuppress));
    }

    /**
     * Returns a (unmodifiable) set with the names of the properties which are suppressed
     * by this {@code BeanIntrospector}.
     *
     * @return a set with the names of the suppressed properties
     */
    public Set<String> getSuppressedProperties() {
        return propertyNames;
    }

    /**
     * {@inheritDoc} This implementation removes all properties from the given context it
     * is configured for.
     */
    @Override
    public void introspect(final IntrospectionContext icontext) throws IntrospectionException {
        getSuppressedProperties().forEach(icontext::removePropertyDescriptor);
    }
}
