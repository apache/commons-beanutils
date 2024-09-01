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
package org.apache.commons.beanutils2.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.PropertyDescriptor;

import org.apache.commons.beanutils2.FluentIntrospectionTestBean;
import org.apache.commons.beanutils2.FluentPropertyBeanIntrospector;
import org.apache.commons.beanutils2.PropertyUtilsBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Write methods for PropertyDescriptors created during custom introspection are lost. See <a href="https://issues.apache.org/jira/browse/BEANUTILS-456">JIRA
 * issue BEANUTILS-456</a>.
 */
public class Jira456TestCase {
    /** Constant for the name of the test property. */
    private static final String TEST_PROP = "fluentGetProperty";

    /** The PropertyUtilsBean used by the tests. */
    private PropertyUtilsBean pub;

    /**
     * Clears the reference to the write method in the property descriptor of the test property. This simulates that the write method reference is freed by the
     * GC.
     *
     * @return the bean instance used for testing
     * @throws Exception if an error occurs
     */
    private FluentIntrospectionTestBean clearWriteMethodRef() throws Exception {
        final FluentIntrospectionTestBean bean = new FluentIntrospectionTestBean();
        final PropertyDescriptor pd = pub.getPropertyDescriptor(bean, TEST_PROP);

        // simulate that the write method reference is freed
        pd.setWriteMethod(null);
        return bean;
    }

    @BeforeEach
    protected void setUp() throws Exception {
        pub = new PropertyUtilsBean();
        pub.addBeanIntrospector(new FluentPropertyBeanIntrospector());
    }

    /**
     * Tests whether a property is recognized as writable even if the reference to its write method was freed.
     */
    @Test
    public void testPropertyIsWritable() throws Exception {
        final FluentIntrospectionTestBean bean = clearWriteMethodRef();
        assertTrue(pub.isWriteable(bean, TEST_PROP), "Not writable");
    }

    /**
     * Tests whether a lost write method is automatically recovered and can be invoked.
     */
    @Test
    public void testWriteMethodRecover() throws Exception {
        final FluentIntrospectionTestBean bean = clearWriteMethodRef();
        final String value = "Test value";
        pub.setProperty(bean, TEST_PROP, value);
        assertEquals(value, bean.getFluentGetProperty(), "Property not set");
    }
}
