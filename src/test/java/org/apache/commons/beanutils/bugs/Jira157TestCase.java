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

import java.io.Serializable;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.SuppressPropertiesBeanIntrospector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Beanutils's describe() method cannot determine reader methods for anonymous
 * class - see Jira issue# BEANUTILS-157.
 *
 * @version $Id$
 * @see <a
 *      href="https://issues.apache.org/jira/browse/BEANUTILS-157">https://issues.apache.org/jira/browse/BEANUTILS-157<a/>
 */
public class Jira157TestCase extends TestCase {

    private final Log log = LogFactory.getLog(Jira157TestCase.class);

    /**
     * Create a test case with the specified name.
     *
     * @param name The name of the test
     */
    public Jira157TestCase(final String name) {
        super(name);
    }

    /**
     * Run the Test.
     *
     * @param args Arguments
     */
    public static void main(final String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Create a test suite for this test.
     *
     * @return a test suite
     */
    public static Test suite() {
        return (new TestSuite(Jira157TestCase.class));
    }

    /**
     * Set up.
     *
     * @throws java.lang.Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        BeanUtilsBean custom = new BeanUtilsBean();
        custom.getPropertyUtils().removeBeanIntrospector(SuppressPropertiesBeanIntrospector.SUPPRESS_CLASS);
        BeanUtilsBean.setInstance(custom);
    }

    /**
     * Tear Down.
     *
     * @throws java.lang.Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test with an private class that overrides a public method
     * of a "grand parent" public class.
     * <p />
     * See Jira issue# BEANUTILS-157.
     */
    public void testIssue_BEANUTILS_157_BeanUtils_Describe_Serializable() {
        final Object bean = new Serializable() {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unused")
            public String getX() {
                return "x-value";
            }
            @SuppressWarnings("unused")
            public String getY() {
                return "y-value";
            }
        };
        Map<String, String> result = null;
        try {
            result = BeanUtils.describe(bean);
        } catch (final Throwable t) {
            log.error("Describe Serializable: " + t.getMessage(), t);
            fail("Describe Serializable Threw exception: " + t);
        }
        assertEquals("Check Size", 1, result.size());
        assertTrue("Class", result.containsKey("class"));
    }

    /**
     * Test with an private class that overrides a public method
     * of a "grand parent" public class.
     * <p />
     * See Jira issue# BEANUTILS-157.
     */
    public void testIssue_BEANUTILS_157_BeanUtils_Describe_Interface() {
        final Object bean = new XY() {
            public String getX() {
                return "x-value";
            }
            public String getY() {
                return "y-value";
            }
        };
        Map<String, String> result = null;
        try {
            result = BeanUtils.describe(bean);
        } catch (final Throwable t) {
            log.error("Describe Interface: " + t.getMessage(), t);
            fail("Describe Interface Threw exception: " + t);
        }
        assertEquals("Check Size", 3, result.size());
        assertTrue("Class", result.containsKey("class"));
        assertTrue("X Key", result.containsKey("x"));
        assertTrue("Y Key", result.containsKey("y"));
        assertEquals("X Value", "x-value", result.get("x"));
        assertEquals("Y Value", "y-value", result.get("y"));
    }

    /**
     * Test with an private class that overrides a public method
     * of a "grand parent" public class.
     * <p />
     * See Jira issue# BEANUTILS-157.
     */
    public void testIssue_BEANUTILS_157_BeanUtils_Describe_Bean() {
        final Object bean = new FooBar();
        Map<String, String> result = null;
        try {
            result = BeanUtils.describe(bean);
        } catch (final Throwable t) {
            log.error("Describe Bean: " + t.getMessage(), t);
            fail("Describe Bean Threw exception: " + t);
        }
        assertEquals("Check Size", 2, result.size());
        assertTrue("Class", result.containsKey("class"));
        assertTrue("publicFoo Key", result.containsKey("publicFoo"));
        assertEquals("publicFoo Value", "PublicFoo Value", result.get("publicFoo"));
    }

    public static interface XY {
        String getX();
        String getY();
    }

    public static class FooBar {
        String getPackageFoo() {
            return "Package Value";
        }
        @SuppressWarnings("unused")
        private String getPrivateFoo() {
            return "PrivateFoo Value";
        }
        protected String getProtectedFoo() {
            return "ProtectedFoo Value";
        }
        public String getPublicFoo() {
            return "PublicFoo Value";
        }
    }
}
