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
package org.apache.commons.beanutils.bugs;

import java.beans.PropertyDescriptor;

import junit.framework.TestCase;

import org.apache.commons.beanutils.FluentIntrospectionTestBean;
import org.apache.commons.beanutils.FluentPropertyBeanIntrospector;
import org.apache.commons.beanutils.PropertyUtilsBean;

/**
 * Write methods for PropertyDescriptors created during custom introspection are lost. See
 * <a href="https://issues.apache.org/jira/browse/BEANUTILS-456">JIRA issue
 * BEANUTILS-456</a>.
 *
 * @version $Id$
 */
public class Jira456TestCase extends TestCase {
    /** Constant for the name of the test property. */
    private static final String TEST_PROP = "fluentGetProperty";

    /** The PropertyUtilsBean used by the tests. */
    private PropertyUtilsBean pub;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        pub = new PropertyUtilsBean();
        pub.addBeanIntrospector(new FluentPropertyBeanIntrospector());
    }

    /**
     * Clears the reference to the write method in the property descriptor of the test
     * property. This simulates that the write method reference is freed by the GC.
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

    /**
     * Tests whether a lost write method is automatically recovered and can be invoked.
     */
    public void testWriteMethodRecover() throws Exception {
        final FluentIntrospectionTestBean bean = clearWriteMethodRef();
        final String value = "Test value";
        pub.setProperty(bean, TEST_PROP, value);
        assertEquals("Property not set", value, bean.getFluentGetProperty());
    }

    /**
     * Tests whether a property is recognized as writable even if the reference to its
     * write method was freed.
     */
    public void testPropertyIsWritable() throws Exception {
        final FluentIntrospectionTestBean bean = clearWriteMethodRef();
        assertTrue("Not writable", pub.isWriteable(bean, TEST_PROP));
    }
}
