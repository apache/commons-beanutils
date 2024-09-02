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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@code IntrospectionContext}.
 */
public class DefaultIntrospectionContextTestCase {

    /** Constant for the name of a property. */
    private static final String PROP = "foo";

    /**
     * Creates a property descriptor object for a property with the given name.
     *
     * @param propName the property name
     * @return the descriptor for this property
     */
    private static PropertyDescriptor createDescriptor(final String propName) {
        try {
            return new PropertyDescriptor(propName, DefaultIntrospectionContextTestCase.class, null, null);
        } catch (final IntrospectionException e) {
            throw new IllegalStateException("Unexpected exception: " + e);
        }
    }

    /** The context to be tested. */
    private DefaultIntrospectionContext context;

    @BeforeEach
    protected void setUp() throws Exception {
        context = new DefaultIntrospectionContext(getClass());
    }

    /**
     * Tests whether a property descriptor can be added.
     */
    @Test
    public void testAddPropertyDescriptor() {
        final PropertyDescriptor desc = createDescriptor(PROP);
        context.addPropertyDescriptor(desc);
        assertTrue(context.hasProperty(PROP), "Property not found");
        assertSame(desc, context.getPropertyDescriptor(PROP), "Wrong descriptor");
    }

    /**
     * Tries to add a null descriptor.
     */
    @Test
    public void testAddPropertyDescriptorNull() {
        assertThrows(NullPointerException.class, () -> context.addPropertyDescriptor(null));
    }

    /**
     * Tests whether multiple descriptors can be added.
     */
    @Test
    public void testAddPropertyDescriptors() {
        final int count = 4;
        final PropertyDescriptor[] descs = new PropertyDescriptor[count];
        final Set<PropertyDescriptor> descSet = new HashSet<>();
        for (int i = 0; i < count; i++) {
            descs[i] = createDescriptor(PROP + i);
            descSet.add(descs[i]);
        }
        context.addPropertyDescriptors(descs);
        final PropertyDescriptor d = createDescriptor(PROP);
        context.addPropertyDescriptor(d);
        descSet.add(d);
        final Set<String> names = context.propertyNames();
        assertEquals(count + 1, names.size(), "Wrong number of property names");
        assertTrue(names.contains(PROP), "Property not found: " + PROP);
        for (int i = 0; i < count; i++) {
            assertTrue(names.contains(PROP + i), "Property not found: " + PROP + i);
        }
        final PropertyDescriptor[] addedDescs = context.getPropertyDescriptors();
        assertEquals(count + 1, addedDescs.length, "Wrong number of added descriptors");
        for (final PropertyDescriptor pd : addedDescs) {
            assertTrue(descSet.remove(pd), "Unexpected descriptor: " + pd);
        }
    }

    /**
     * Tries to add a null array with property descriptors.
     */
    @Test
    public void testAddPropertyDescriptorsNull() {
        assertThrows(NullPointerException.class, () -> context.addPropertyDescriptors(null));
    }

    /**
     * Tests getPropertyDescriptor() if the property name is unknown.
     */
    @Test
    public void testGetPropertyDescriptorUnknown() {
        assertNull(context.getPropertyDescriptor(PROP), "Got a property (1)");
        context.addPropertyDescriptor(createDescriptor(PROP));
        assertNull(context.getPropertyDescriptor("other"), "Got a property (2)");
    }

    /**
     * Tests hasProperty() if the expected result is false.
     */
    @Test
    public void testHasPropertyFalse() {
        assertFalse(context.hasProperty(PROP), "Wrong result (1)");
        context.addPropertyDescriptor(createDescriptor(PROP));
        assertFalse(context.hasProperty("other"), "Wrong result (2)");
    }

    /**
     * Tests a newly created instance.
     */
    @Test
    public void testInit() {
        assertEquals(getClass(), context.getTargetClass(), "Wrong current class");
        assertTrue(context.propertyNames().isEmpty(), "Got property names");
    }

    /**
     * Tests that the set with property names cannot be changed.
     */
    @Test
    public void testPropertyNamesModify() {
        final Set<String> names = context.propertyNames();
        assertThrows(UnsupportedOperationException.class, () -> names.add(PROP));
    }

    /**
     * Tests whether a descriptor can be removed.
     */
    @Test
    public void testRemovePropertyDescriptor() {
        context.addPropertyDescriptor(createDescriptor(PROP));
        context.removePropertyDescriptor(PROP);
        assertTrue(context.propertyNames().isEmpty(), "Got property names");
        assertEquals(0, context.getPropertyDescriptors().length, "Got descriptors");
    }
}
