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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@code SuppressPropertiesBeanIntrospector}.
 */
public class SuppressPropertiesBeanIntrospectorTestCase {

    /**
     * A test implementation of IntrospectionContext which collects the properties which have been removed.
     */
    private static class IntrospectionContextTestImpl implements IntrospectionContext {
        /** Stores the names of properties which have been removed. */
        private final Set<String> removedProperties = new HashSet<>();

        @Override
        public void addPropertyDescriptor(final PropertyDescriptor desc) {
            throw new UnsupportedOperationException("Unexpected method call!");
        }

        @Override
        public void addPropertyDescriptors(final PropertyDescriptor[] descriptors) {
            throw new UnsupportedOperationException("Unexpected method call!");
        }

        @Override
        public PropertyDescriptor getPropertyDescriptor(final String name) {
            throw new UnsupportedOperationException("Unexpected method call!");
        }

        /**
         * Returns the names of properties which have been removed.
         *
         * @return the set with removed properties
         */
        public Set<String> getRemovedProperties() {
            return removedProperties;
        }

        @Override
        public Class<?> getTargetClass() {
            throw new UnsupportedOperationException("Unexpected method call!");
        }

        @Override
        public boolean hasProperty(final String name) {
            throw new UnsupportedOperationException("Unexpected method call!");
        }

        @Override
        public Set<String> propertyNames() {
            throw new UnsupportedOperationException("Unexpected method call!");
        }

        @Override
        public void removePropertyDescriptor(final String name) {
            removedProperties.add(name);
        }
    }

    /**
     * Tests that the set with properties to be removed cannot be modified.
     */
    @Test
    public void testGetSuppressedPropertiesModify() {
        final SuppressPropertiesBeanIntrospector introspector = new SuppressPropertiesBeanIntrospector(Arrays.asList("p1", "p2"));
        final Set<String> properties = introspector.getSuppressedProperties();
        assertThrows(UnsupportedOperationException.class, () -> properties.add("anotherProperty"));
    }

    /**
     * Tries to create an instance without properties.
     */
    @Test
    public void testInitNoPropertyNames() {
        assertThrows(NullPointerException.class, () -> new SuppressPropertiesBeanIntrospector(null));
    }

    /**
     * Tests that a defensive copy is created from the collection with properties to be removed.
     */
    @Test
    public void testPropertyNamesDefensiveCopy() throws IntrospectionException {
        final Collection<String> properties = new HashSet<>();
        properties.add("prop1");
        final SuppressPropertiesBeanIntrospector introspector = new SuppressPropertiesBeanIntrospector(properties);
        properties.add("prop2");
        final IntrospectionContextTestImpl context = new IntrospectionContextTestImpl();

        introspector.introspect(context);
        assertEquals(1, context.getRemovedProperties().size(), "Wrong number of removed properties");
        assertTrue(context.getRemovedProperties().contains("prop1"), "Wrong removed property");
    }

    /**
     * Tests whether the expected properties have been removed during introspection.
     */
    @Test
    public void testRemovePropertiesDuringIntrospection() throws IntrospectionException {
        final String[] properties = { "test", "other", "oneMore" };
        final SuppressPropertiesBeanIntrospector introspector = new SuppressPropertiesBeanIntrospector(Arrays.asList(properties));
        final IntrospectionContextTestImpl context = new IntrospectionContextTestImpl();

        introspector.introspect(context);
        assertEquals(properties.length, context.getRemovedProperties().size(), "Wrong number of removed properties");
        for (final String property : properties) {
            assertTrue(context.getRemovedProperties().contains(property), "Property not removed: " + property);
        }
    }
}
