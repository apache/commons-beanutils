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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@code BeanIntrospectionData}.
 */
public class BeanIntrospectionDataTestCase {
    /** Constant for the test bean class. */
    private static final Class<?> BEAN_CLASS = FluentIntrospectionTestBean.class;

    /** Constant for the name of the test property. */
    private static final String TEST_PROP = "fluentGetProperty";

    /**
     * Creates an array with property descriptors for the test bean class.
     *
     * @return the array with property descriptors
     */
    private static PropertyDescriptor[] fetchDescriptors() {
        final PropertyUtilsBean pub = new PropertyUtilsBean();
        pub.removeBeanIntrospector(SuppressPropertiesBeanIntrospector.SUPPRESS_CLASS);
        pub.addBeanIntrospector(new FluentPropertyBeanIntrospector());
        return pub.getPropertyDescriptors(BEAN_CLASS);
    }

    /**
     * Returns the property descriptor for the test property.
     *
     * @param bid the data object
     * @return the test property descriptor
     */
    private static PropertyDescriptor fetchTestDescriptor(final BeanIntrospectionData bid) {
        return bid.getDescriptor(TEST_PROP);
    }

    /**
     * Creates a test instance which is initialized with default property descriptors.
     *
     * @return the test instance
     */
    private static BeanIntrospectionData setUpData() {
        return new BeanIntrospectionData(fetchDescriptors());
    }

    /**
     * Tests whether a write method can be queried if it is defined in the descriptor.
     */
    @Test
    public void testGetWriteMethodDefined() {
        final BeanIntrospectionData data = setUpData();
        final PropertyDescriptor pd = fetchTestDescriptor(data);
        assertNotNull(pd.getWriteMethod(), "No write method");
        assertEquals(pd.getWriteMethod(), data.getWriteMethod(BEAN_CLASS, pd), "Wrong write method");
    }

    /**
     * Tests getWriteMethod() if the method cannot be resolved. (This is a corner case which should normally not happen in practice.)
     */
    @Test
    public void testGetWriteMethodNonExisting() throws Exception {
        final PropertyDescriptor pd = new PropertyDescriptor(TEST_PROP, BEAN_CLASS.getMethod("getFluentGetProperty"),
                BEAN_CLASS.getMethod("setFluentGetProperty", String.class));
        final Map<String, String> methods = new HashMap<>();
        methods.put(TEST_PROP, "hashCode");
        final BeanIntrospectionData data = new BeanIntrospectionData(new PropertyDescriptor[] { pd }, methods);
        pd.setWriteMethod(null);
        assertNull(data.getWriteMethod(BEAN_CLASS, pd), "Got a write method");
    }

    /**
     * Tests whether a write method can be queried that is currently not available in the property descriptor.
     */
    @Test
    public void testGetWriteMethodUndefined() throws Exception {
        final BeanIntrospectionData data = setUpData();
        final PropertyDescriptor pd = fetchTestDescriptor(data);
        final Method writeMethod = pd.getWriteMethod();
        pd.setWriteMethod(null);
        assertEquals(writeMethod, data.getWriteMethod(BEAN_CLASS, pd), "Wrong write method");
        assertEquals(writeMethod, pd.getWriteMethod(), "Method not set in descriptor");
    }

    /**
     * Tests getWriteMethod() for a property for which no write method is known.
     */
    @Test
    public void testGetWriteMethodUnknown() {
        final BeanIntrospectionData data = setUpData();
        final PropertyDescriptor pd = data.getDescriptor("class");
        assertNull(data.getWriteMethod(BEAN_CLASS, pd), "Got a write method");
    }
}
