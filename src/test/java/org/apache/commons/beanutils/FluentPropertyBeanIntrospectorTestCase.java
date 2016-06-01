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
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Test class for {@code FluentPropertyBeanIntrospector}.
 *
 * @version $Id$
 */
public class FluentPropertyBeanIntrospectorTestCase extends TestCase {
    /**
     * Puts all property descriptors into a map so that they can be accessed by
     * property name.
     *
     * @param descs the array with descriptors
     * @return a map with property names as keys
     */
    private static Map<String, PropertyDescriptor> createDescriptorMap(
            final PropertyDescriptor[] descs) {
        final Map<String, PropertyDescriptor> map = new HashMap<String, PropertyDescriptor>();
        for (final PropertyDescriptor pd : descs) {
            map.put(pd.getName(), pd);
        }
        return map;
    }

    /**
     * Convenience method for obtaining a specific property descriptor and
     * checking whether it exists.
     *
     * @param props the map with property descriptors
     * @param name the name of the desired descriptor
     * @return the descriptor from the map
     */
    private static PropertyDescriptor fetchDescriptor(
            final Map<String, PropertyDescriptor> props, final String name) {
        assertTrue("Property not found: " + name, props.containsKey(name));
        return props.get(name);
    }

    /**
     * Tries to create an instance without a prefix for write methods.
     */
    public void testInitNoPrefix() {
        try {
            new FluentPropertyBeanIntrospector(null);
            fail("Missing prefix for write methods not detected!");
        } catch (final IllegalArgumentException iex) {
            // ok
        }
    }

    /**
     * Tests whether correct property descriptors are detected.
     */
    public void testIntrospection() throws IntrospectionException {
        final PropertyUtilsBean pu = new PropertyUtilsBean();
        final FluentPropertyBeanIntrospector introspector = new FluentPropertyBeanIntrospector();
        pu.addBeanIntrospector(introspector);
        final Map<String, PropertyDescriptor> props = createDescriptorMap(pu
                .getPropertyDescriptors(FluentIntrospectionTestBean.class));
        PropertyDescriptor pd = fetchDescriptor(props, "name");
        assertNotNull("No read method for name", pd.getReadMethod());
        assertNotNull("No write method for name", pd.getWriteMethod());
        fetchDescriptor(props, "stringProperty");
        pd = fetchDescriptor(props, "fluentProperty");
        assertNull("Read method for fluentProperty", pd.getReadMethod());
        assertNotNull("No write method for fluentProperty", pd.getWriteMethod());
        pd = fetchDescriptor(props, "fluentGetProperty");
        assertNotNull("No read method for fluentGetProperty",
                pd.getReadMethod());
        assertNotNull("No write method for fluentGetProperty",
                pd.getWriteMethod());
    }

    public void testIntrospectionCaps() throws Exception {
	    final PropertyUtilsBean pu = new PropertyUtilsBean();

        final FluentPropertyBeanIntrospector introspector = new FluentPropertyBeanIntrospector();

	    pu.addBeanIntrospector(introspector);

	    final Map<String, PropertyDescriptor> props = createDescriptorMap(
			pu.getPropertyDescriptors(CapsBean.class));

	    PropertyDescriptor aDescriptor = fetchDescriptor(props, "URI");

	    assertNotNull("missing property", aDescriptor);

	    assertNotNull("No read method for uri", aDescriptor.getReadMethod());
	    assertNotNull("No write method for uri", aDescriptor.getWriteMethod());

	    assertNull("Should not find mis-capitalized property", props.get("uRI"));
    }

	public static final class CapsBean {
		private URI mURI;

		public URI getURI() {
			return mURI;
		}

		public void setURI(final URI theURI) {
			mURI = theURI;
		}
	}
}
