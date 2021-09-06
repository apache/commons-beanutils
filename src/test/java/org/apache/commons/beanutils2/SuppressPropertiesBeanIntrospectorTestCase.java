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
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import org.mockito.Mockito;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;
import org.apache.commons.beanutils2.IntrospectionContext;

/**
 * Test class for {@code SuppressPropertiesBeanIntrospector}.
 *
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
        // Create variables for tracking behaviors of mock object
		Set<String> contextRemovedProperties = new HashSet<>();
		// Construct mock object
		final IntrospectionContext context = mock(IntrospectionContext.class);
		// Method Stubs
		doThrow(new UnsupportedOperationException("Unexpected method call!")).when(context)
				.addPropertyDescriptors(any(PropertyDescriptor[].class));
		doThrow(new UnsupportedOperationException("Unexpected method call!")).when(context)
				.addPropertyDescriptor(any(PropertyDescriptor.class));
		doAnswer((stubInvo) -> {
			String name = stubInvo.getArgument(0);
			contextRemovedProperties.add(name);
			return null;
		}).when(context).removePropertyDescriptor(any(String.class));
		when(context.getTargetClass()).thenAnswer((stubInvo) -> {
			throw new UnsupportedOperationException("Unexpected method call!");
		});
		when(context.hasProperty(any(String.class)))
				.thenThrow(new UnsupportedOperationException("Unexpected method call!"));
		when(context.getPropertyDescriptor(any(String.class)))
				.thenThrow(new UnsupportedOperationException("Unexpected method call!"));
		when(context.propertyNames()).thenAnswer((stubInvo) -> {
			throw new UnsupportedOperationException("Unexpected method call!");
		});

        introspector.introspect(context);
        assertEquals("Wrong number of removed properties", properties.length, contextRemovedProperties.size());
        for (final String property : properties) {
            assertTrue("Property not removed: " + property, contextRemovedProperties.contains(property));
        }
    }

    /**
     * Tests that a defensive copy is created from the collection with properties to be
     * removed.
     */
    public void testPropertyNamesDefensiveCopy() throws IntrospectionException {
        final Collection<String> properties = new HashSet<>();
        properties.add("prop1");
        final SuppressPropertiesBeanIntrospector introspector = new SuppressPropertiesBeanIntrospector(
                properties);
        properties.add("prop2");
        // Create variables for tracking behaviors of mock object
		Set<String> contextRemovedProperties = new HashSet<>();
		// Construct mock object
		final IntrospectionContext context = mock(IntrospectionContext.class);
		// Method Stubs
		doThrow(new UnsupportedOperationException("Unexpected method call!")).when(context)
				.addPropertyDescriptors(any(PropertyDescriptor[].class));
		doThrow(new UnsupportedOperationException("Unexpected method call!")).when(context)
				.addPropertyDescriptor(any(PropertyDescriptor.class));
		doAnswer((stubInvo) -> {
			String name = stubInvo.getArgument(0);
			contextRemovedProperties.add(name);
			return null;
		}).when(context).removePropertyDescriptor(any(String.class));
		when(context.getTargetClass()).thenAnswer((stubInvo) -> {
			throw new UnsupportedOperationException("Unexpected method call!");
		});
		when(context.hasProperty(any(String.class)))
				.thenThrow(new UnsupportedOperationException("Unexpected method call!"));
		when(context.getPropertyDescriptor(any(String.class)))
				.thenThrow(new UnsupportedOperationException("Unexpected method call!"));
		when(context.propertyNames()).thenAnswer((stubInvo) -> {
			throw new UnsupportedOperationException("Unexpected method call!");
		});

        introspector.introspect(context);
        assertEquals("Wrong number of removed properties", 1, contextRemovedProperties.size());
        assertTrue("Wrong removed property",
                contextRemovedProperties.contains("prop1"));
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
}
