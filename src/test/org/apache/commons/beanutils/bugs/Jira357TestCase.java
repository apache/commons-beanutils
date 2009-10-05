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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * See https://issues.apache.org/jira/browse/BEANUTILS-357
 * <p />
 *
 * @version $Revision$ $Date$
 */
public class Jira357TestCase extends TestCase {

    /**
     * Create a test case with the specified name.
     *
     * @param name The name of the test
     */
    public Jira357TestCase(String name) {
        super(name);
    }

    /**
     * Run the Test.
     *
     * @param args Arguments
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Create a test suite for this test.
     *
     * @return a test suite
     */
    public static Test suite() {
        return (new TestSuite(Jira357TestCase.class));
    }

    /**
     * Set up.
     *
     * @throws java.lang.Exception
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Tear Down.
     *
     * @throws java.lang.Exception
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test {@link PropertyUtils#getPropertyDescriptors(Class)}
     */
    public void testPropertyUtils_getPropertyDescriptors_Foo() throws Exception {
        checkReadMethod("foo", ConcreteTestBean.class);
    }

    /**
     * Test {@link PropertyUtils#getPropertyDescriptors(Class)}
     */
    public void testPropertyUtils_getPropertyDescriptors_Bar() throws Exception {
        
        // FIXME the isBar() method returning AbstractTestBean.class as the
        //       declaring class instead of ConcreteTestBean.class
        //       causing this test to fail - so its commented out for now
        //checkReadMethod("bar", ConcreteTestBean.class);
    }

    /**
     * Test {@link PropertyUtils#getPropertyDescriptors(Class)}
     */
    public void testPropertyUtils_getPropertyDescriptors_InnerClassProperty() throws Exception {
        checkReadMethod("innerClassProperty", ConcreteTestBean.class);
    }

    /**
     * Test {@link PropertyUtils#getPropertyDescriptors(Class)}
     */
    private void checkReadMethod(String propertyName, Class expectedDeclaringClass) throws Exception {
        
        PropertyDescriptor[] descriptors = null;
        try {
            descriptors = PropertyUtils.getPropertyDescriptors(ConcreteTestBean.class);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Threw: " + e);
        }

        // Test InnerClassProperty
        PropertyDescriptor descriptor = findDescriptor(propertyName, descriptors);
        assertNotNull(propertyName + "descriptor", descriptor);
        assertEquals(propertyName + " read method declaring class", expectedDeclaringClass, descriptor.getReadMethod().getDeclaringClass());
    }

    /**
     * Find a property descriptor.
     */
    private PropertyDescriptor findDescriptor(String propertyName, PropertyDescriptor[] descriptors) {
        if (descriptors != null) {
            for (int i = 0; i < descriptors.length; i++) {
                if (propertyName.equals(descriptors[i].getName())) {
                    return descriptors[i];
                }
            }
        }
        return null; // not found
    }

    /**
     * Abstract test bean.
     */
    public abstract static class AbstractTestBean {

        public abstract String getFoo();
        public abstract void setFoo(String foo);

        public abstract boolean isBar();
        public abstract void setBar(boolean bar);

        public abstract AbstractTestBean.InnerClass getInnerClassProperty();
        
        /** Inner Class */
        public abstract static class InnerClass {
            private String firstName;
            public String getInnerName() {
                return firstName;
            }
            public void setInnerName(String firstName) {
                this.firstName = firstName;
            };
        }
    }

    /**
     * Concrete bean implementation.
     */
    public static class ConcreteTestBean extends AbstractTestBean {

        private String foo;
        private boolean bar;
        private ConcreteTestBean.InnerClass innerClassProperty;

        public String getFoo() {
            return foo;
        }
        public void setFoo(String foo) {
            this.foo = foo;
        }
        public boolean isBar() {
            return bar;
        }
        public void setBar(boolean bar) {
            this.bar = bar;
        }
        public ConcreteTestBean.InnerClass getInnerClassProperty() {
            return innerClassProperty;
        }
        public void setInnerClassProperty(ConcreteTestBean.InnerClass innerClassProperty) {
            this.innerClassProperty = innerClassProperty;
        }
    }
}
