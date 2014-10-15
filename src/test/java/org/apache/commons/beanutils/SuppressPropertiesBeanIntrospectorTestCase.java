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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

/**
 * Test class for {@code SuppressPropertiesBeanIntrospector}.
 *
 * @version $Id$
 */
public class SuppressPropertiesBeanIntrospectorTestCase extends TestCase {
    /**
     * Tries to create an instance without properties.
     */
    public void testInitNoPropertyNames() {
        try {
            new SuppressPropertiesBeanIntrospector(null);
            fail("Missing properties not detected!");
        } catch (final IllegalArgumentException iaex) {
            // ok
        }
    }

    /**
     * Tests whether the expected properties have been removed during introspection.
     */
    public void testRemovePropertiesDuringIntrospection() throws IntrospectionException {
        final String[] properties = { "test", "other", "oneMore" };
        final SuppressPropertiesBeanIntrospector introspector = new SuppressPropertiesBeanIntrospector(
                Arrays.asList(properties));
        final IntrospectionContextTestImpl context = new IntrospectionContextTestImpl();

        introspector.introspect(context);
        assertEquals("Wrong number of removed properties", properties.length, context
                .getRemovedProperties().size());
        for (final String property : properties) {
            assertTrue("Property not removed: " + property, context
                    .getRemovedProperties().contains(property));
        }
    }

    /**
     * Tests that a defensive copy is created from the collection with properties to be
     * removed.
     */
    public void testPropertyNamesDefensiveCopy() throws IntrospectionException {
        final Collection<String> properties = new HashSet<String>();
        properties.add("prop1");
        final SuppressPropertiesBeanIntrospector introspector = new SuppressPropertiesBeanIntrospector(
                properties);
        properties.add("prop2");
        final IntrospectionContextTestImpl context = new IntrospectionContextTestImpl();

        introspector.introspect(context);
        assertEquals("Wrong number of removed properties", 1, context
                .getRemovedProperties().size());
        assertTrue("Wrong removed property",
                context.getRemovedProperties().contains("prop1"));
    }

    /**
     * Tests that the set with properties to be removed cannot be modified.
     */
    public void testGetSuppressedPropertiesModify() {
        final SuppressPropertiesBeanIntrospector introspector = new SuppressPropertiesBeanIntrospector(
                Arrays.asList("p1", "p2"));
        final Set<String> properties = introspector.getSuppressedProperties();
        try {
            properties.add("anotherProperty");
            fail("Could modify properties");
        } catch (final UnsupportedOperationException uoex) {
            // ok
        }
    }

    /**
     * A test implementation of IntrospectionContext which collects the properties which
     * have been removed.
     */
    private static class IntrospectionContextTestImpl implements IntrospectionContext {
        /** Stores the names of properties which have been removed. */
        private final Set<String> removedProperties = new HashSet<String>();

        /**
         * Returns the names of properties which have been removed.
         *
         * @return the set with removed properties
         */
        public Set<String> getRemovedProperties() {
            return removedProperties;
        }

        public Class<?> getTargetClass() {
            throw new UnsupportedOperationException("Unexpected method call!");
        }

        public void addPropertyDescriptor(final PropertyDescriptor desc) {
            throw new UnsupportedOperationException("Unexpected method call!");
        }

        public void addPropertyDescriptors(final PropertyDescriptor[] descriptors) {
            throw new UnsupportedOperationException("Unexpected method call!");
        }

        public boolean hasProperty(final String name) {
            throw new UnsupportedOperationException("Unexpected method call!");
        }

        public PropertyDescriptor getPropertyDescriptor(final String name) {
            throw new UnsupportedOperationException("Unexpected method call!");
        }

        public void removePropertyDescriptor(final String name) {
            removedProperties.add(name);
        }

        public Set<String> propertyNames() {
            throw new UnsupportedOperationException("Unexpected method call!");
        }
    }
}
