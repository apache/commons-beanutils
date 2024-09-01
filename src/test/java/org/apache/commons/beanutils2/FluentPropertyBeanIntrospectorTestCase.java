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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@code FluentPropertyBeanIntrospector}.
 */
public class FluentPropertyBeanIntrospectorTestCase {
    public static final class CapsBean {
        private URI mURI;

        public URI getURI() {
            return mURI;
        }

        public void setURI(final URI theURI) {
            mURI = theURI;
        }
    }

    /**
     * Puts all property descriptors into a map so that they can be accessed by property name.
     *
     * @param descs the array with descriptors
     * @return a map with property names as keys
     */
    private static Map<String, PropertyDescriptor> createDescriptorMap(final PropertyDescriptor[] descs) {
        final Map<String, PropertyDescriptor> map = new HashMap<>();
        for (final PropertyDescriptor pd : descs) {
            map.put(pd.getName(), pd);
        }
        return map;
    }

    /**
     * Convenience method for obtaining a specific property descriptor and checking whether it exists.
     *
     * @param props the map with property descriptors
     * @param name  the name of the desired descriptor
     * @return the descriptor from the map
     */
    private static PropertyDescriptor fetchDescriptor(final Map<String, PropertyDescriptor> props, final String name) {
        assertTrue(props.containsKey(name), "Property not found: " + name);
        return props.get(name);
    }

    /**
     * Tries to create an instance without a prefix for write methods.
     */
    @Test
    public void testInitNoPrefix() {
        assertThrows(NullPointerException.class, () -> new FluentPropertyBeanIntrospector(null));
    }

    /**
     * Tests whether correct property descriptors are detected.
     */
    @Test
    public void testIntrospection() throws IntrospectionException {
        final PropertyUtilsBean pu = new PropertyUtilsBean();
        final FluentPropertyBeanIntrospector introspector = new FluentPropertyBeanIntrospector();
        pu.addBeanIntrospector(introspector);
        final Map<String, PropertyDescriptor> props = createDescriptorMap(pu.getPropertyDescriptors(FluentIntrospectionTestBean.class));
        PropertyDescriptor pd = fetchDescriptor(props, "name");
        assertNotNull(pd.getReadMethod(), "No read method for name");
        assertNotNull(pd.getWriteMethod(), "No write method for name");
        fetchDescriptor(props, "stringProperty");
        pd = fetchDescriptor(props, "fluentProperty");
        assertNull(pd.getReadMethod(), "Read method for fluentProperty");
        assertNotNull(pd.getWriteMethod(), "No write method for fluentProperty");
        pd = fetchDescriptor(props, "fluentGetProperty");
        assertNotNull(pd.getReadMethod(), "No read method for fluentGetProperty");
        assertNotNull(pd.getWriteMethod(), "No write method for fluentGetProperty");
    }

    @Test
    public void testIntrospectionCaps() throws Exception {
        final PropertyUtilsBean pu = new PropertyUtilsBean();

        final FluentPropertyBeanIntrospector introspector = new FluentPropertyBeanIntrospector();

        pu.addBeanIntrospector(introspector);

        final Map<String, PropertyDescriptor> props = createDescriptorMap(pu.getPropertyDescriptors(CapsBean.class));

        final PropertyDescriptor aDescriptor = fetchDescriptor(props, "URI");

        assertNotNull(aDescriptor, "missing property");

        assertNotNull(aDescriptor.getReadMethod(), "No read method for uri");
        assertNotNull(aDescriptor.getWriteMethod(), "No write method for uri");

        assertNull(props.get("uRI"), "Should not find mis-capitalized property");
    }
}
