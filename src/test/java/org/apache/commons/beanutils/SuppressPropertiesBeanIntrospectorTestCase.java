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
        } catch (IllegalArgumentException iaex) {
            // ok
        }
    }

    /**
     * Tests whether the expected properties have been removed during introspection.
     */
    public void testRemovePropertiesDuringIntrospection() throws IntrospectionException {
        String[] properties = { "test", "other", "oneMore" };
        SuppressPropertiesBeanIntrospector introspector = new SuppressPropertiesBeanIntrospector(
                Arrays.asList(properties));
        IntrospectionContextTestImpl context = new IntrospectionContextTestImpl();

        introspector.introspect(context);
        assertEquals("Wrong number of removed properties", properties.length, context
                .getRemovedProperties().size());
        for (String property : properties) {
            assertTrue("Property not removed: " + property, context
                    .getRemovedProperties().contains(property));
        }
    }

    /**
     * Tests that a defensive copy is created from the collection with properties to be
     * removed.
     */
    public void testPropertyNamesDefensiveCopy() throws IntrospectionException {
        Collection<String> properties = new HashSet<String>();
        properties.add("prop1");
        SuppressPropertiesBeanIntrospector introspector = new SuppressPropertiesBeanIntrospector(
                properties);
        properties.add("prop2");
        IntrospectionContextTestImpl context = new IntrospectionContextTestImpl();

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
        SuppressPropertiesBeanIntrospector introspector = new SuppressPropertiesBeanIntrospector(
                Arrays.asList("p1", "p2"));
        Set<String> properties = introspector.getSuppressedProperties();
        try {
            properties.add("anotherProperty");
            fail("Could modify properties");
        } catch (UnsupportedOperationException uoex) {
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

        public void addPropertyDescriptor(PropertyDescriptor desc) {
            throw new UnsupportedOperationException("Unexpected method call!");
        }

        public void addPropertyDescriptors(PropertyDescriptor[] descriptors) {
            throw new UnsupportedOperationException("Unexpected method call!");
        }

        public boolean hasProperty(String name) {
            throw new UnsupportedOperationException("Unexpected method call!");
        }

        public PropertyDescriptor getPropertyDescriptor(String name) {
            throw new UnsupportedOperationException("Unexpected method call!");
        }

        public void removePropertyDescriptor(String name) {
            removedProperties.add(name);
        }

        public Set<String> propertyNames() {
            throw new UnsupportedOperationException("Unexpected method call!");
        }
    }
}
