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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.beans.PropertyDescriptor;

import org.apache.commons.beanutils2.PropertyUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-357">https://issues.apache.org/jira/browse/BEANUTILS-357</a>
 */
public class Jira357TestCase {

    /**
     * Abstract test bean.
     */
    public abstract static class AbstractTestBean {

        /** Inner Class */
        public abstract static class InnerClass {
            private String firstName;

            public String getInnerName() {
                return firstName;
            }

            public void setInnerName(final String firstName) {
                this.firstName = firstName;
            }
        }

        public abstract String getFoo();

        public abstract AbstractTestBean.InnerClass getInnerClassProperty();

        public abstract boolean isBar();

        public abstract void setBar(boolean bar);

        public abstract void setFoo(String foo);
    }

    /**
     * Concrete bean implementation.
     */
    public static class ConcreteTestBean extends AbstractTestBean {

        private String foo;
        private boolean bar;
        private ConcreteTestBean.InnerClass innerClassProperty;

        @Override
        public String getFoo() {
            return foo;
        }

        @Override
        public ConcreteTestBean.InnerClass getInnerClassProperty() {
            return innerClassProperty;
        }

        @Override
        public boolean isBar() {
            return bar;
        }

        @Override
        public void setBar(final boolean bar) {
            this.bar = bar;
        }

        @Override
        public void setFoo(final String foo) {
            this.foo = foo;
        }

        public void setInnerClassProperty(final ConcreteTestBean.InnerClass innerClassProperty) {
            this.innerClassProperty = innerClassProperty;
        }
    }

    /**
     * Test {@link PropertyUtils#getPropertyDescriptors(Class)}
     */
    private void checkReadMethod(final String propertyName, final Class<?> expectedDeclaringClass) throws Exception {

        PropertyDescriptor[] descriptors = null;
        descriptors = PropertyUtils.getPropertyDescriptors(ConcreteTestBean.class);

        // Test InnerClassProperty
        final PropertyDescriptor descriptor = findDescriptor(propertyName, descriptors);
        assertNotNull(descriptor, propertyName + "descriptor");
        assertEquals(expectedDeclaringClass, descriptor.getReadMethod().getDeclaringClass(), propertyName + " read method declaring class");
    }

    /**
     * Find a property descriptor.
     */
    private PropertyDescriptor findDescriptor(final String propertyName, final PropertyDescriptor[] descriptors) {
        if (descriptors != null) {
            for (final PropertyDescriptor descriptor : descriptors) {
                if (propertyName.equals(descriptor.getName())) {
                    return descriptor;
                }
            }
        }
        return null; // not found
    }

    /**
     * Sets up.
     *
     * @throws Exception
     */
    @BeforeEach
    protected void setUp() throws Exception {
    }

    /**
     * Tear Down.
     *
     * @throws Exception
     */
    @AfterEach
    protected void tearDown() throws Exception {
    }

    /**
     * Test {@link PropertyUtils#getPropertyDescriptors(Class)}
     */
    @Test
    public void testPropertyUtils_getPropertyDescriptors_Bar() throws Exception {
        checkReadMethod("bar", ConcreteTestBean.class);
    }

    /**
     * Test {@link PropertyUtils#getPropertyDescriptors(Class)}
     */
    @Test
    public void testPropertyUtils_getPropertyDescriptors_Foo() throws Exception {
        checkReadMethod("foo", ConcreteTestBean.class);
    }

    /**
     * Test {@link PropertyUtils#getPropertyDescriptors(Class)}
     */
    @Test
    public void testPropertyUtils_getPropertyDescriptors_InnerClassProperty() throws Exception {
        checkReadMethod("innerClassProperty", ConcreteTestBean.class);
    }
}
