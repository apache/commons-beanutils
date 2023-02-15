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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * An implementation of the {@code IntrospectionContext} interface used by
 * {@link PropertyUtilsBean} when doing introspection of a bean class.
 * </p>
 * <p>
 * This class implements the methods required by the
 * {@code IntrospectionContext} interface in a straight-forward manner
 * based on a map. It is used internally only. It is not thread-safe.
 * </p>
 *
 * @since 1.9
 */
class DefaultIntrospectionContext implements IntrospectionContext {
    /** The current class for introspection. */
    private final Class<?> currentClass;

    /** A map for storing the already added property descriptors. */
    private final Map<String, PropertyDescriptor> descriptors;

    /**
     *
     * Creates a new instance of {@code DefaultIntrospectionContext} and sets
     * the current class for introspection.
     *
     * @param cls the current class
     */
    public DefaultIntrospectionContext(final Class<?> cls) {
        currentClass = cls;
        descriptors = new HashMap<>();
    }

    @Override
    public void addPropertyDescriptor(final PropertyDescriptor desc) {
        if (desc == null) {
            throw new IllegalArgumentException(
                    "Property descriptor must not be null!");
        }
        descriptors.put(desc.getName(), desc);
    }

    @Override
    public void addPropertyDescriptors(final PropertyDescriptor[] descs) {
        if (descs == null) {
            throw new IllegalArgumentException(
                    "Array with descriptors must not be null!");
        }

        for (final PropertyDescriptor desc : descs) {
            addPropertyDescriptor(desc);
        }
    }

    @Override
    public PropertyDescriptor getPropertyDescriptor(final String name) {
        return descriptors.get(name);
    }

    /**
     * Returns an array with all descriptors added to this context. This method
     * is used to obtain the results of introspection.
     *
     * @return an array with all known property descriptors
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        return descriptors.values().toArray(PropertyDescriptors.EMPTY_ARRAY);
    }

    @Override
    public Class<?> getTargetClass() {
        return currentClass;
    }

    @Override
    public boolean hasProperty(final String name) {
        return descriptors.containsKey(name);
    }

    @Override
    public Set<String> propertyNames() {
        return descriptors.keySet();
    }

    @Override
    public void removePropertyDescriptor(final String name) {
        descriptors.remove(name);
    }
}
