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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.bugs.other.Jira18BeanFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Test case for Jira issue# BEANUTILS-18.

 * <p>This test case demonstrates the issue.
 *
 * @version $Id$
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-18">https://issues.apache.org/jira/browse/BEANUTILS-18</a>
 */
public class Jira18TestCase extends TestCase {

    private final Log log = LogFactory.getLog(Jira18TestCase.class);
    private Object bean;

    /**
     * Create a test case with the specified name.
     *
     * @param name The name of the test
     */
    public Jira18TestCase(final String name) {
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
        return (new TestSuite(Jira18TestCase.class));
    }

    /**
     * Set up.
     *
     * @throws java.lang.Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        bean = Jira18BeanFactory.createBean();
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
     * Test {@link PropertyUtils#isReadable(Object, String)}
     * for simple properties.
     */
    public void testIssue_BEANUTILS_18_PropertyUtils_isReadable() {
        boolean result = false;
        try {
            result = PropertyUtils.isReadable(bean, "simple");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("Threw exception: " + t);
        }
        assertFalse("PropertyUtils.isReadable(bean, \"simple\") returned true", result);
    }

    /**
     * Test {@link PropertyUtils#isWriteable(Object, String)}
     * for simple properties.
     */
    public void testIssue_BEANUTILS_18_PropertyUtils_isWriteable() {
        boolean result = false;
        try {
            result = PropertyUtils.isWriteable(bean, "simple");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("Threw exception: " + t);
        }
        assertFalse("PropertyUtils.isWriteable(bean, \"simple\") returned true", result);
    }

    /**
     * Test {@link PropertyUtils#isReadable(Object, String)}
     * for indexed properties.
     */
    public void testIssue_BEANUTILS_18_PropertyUtils_isReadable_Indexed() {
        boolean result = false;
        try {
            result = PropertyUtils.isReadable(bean, "indexed");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("Threw exception: " + t);
        }
        assertFalse("PropertyUtils.isReadable(bean, \"indexed\") returned true", result);
    }

    /**
     * Test {@link PropertyUtils#isWriteable(Object, String)}
     * for indexed properties.
     */
    public void testIssue_BEANUTILS_18_PropertyUtils_isWriteable_Indexed() {
        boolean result = false;
        try {
            result = PropertyUtils.isWriteable(bean, "indexed");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("Threw exception: " + t);
        }
        assertFalse("PropertyUtils.isWriteable(bean, \"indexed\") returned true", result);
    }

    /**
     * Test {@link PropertyUtils#isReadable(Object, String)}
     * for Mapped properties.
     */
    public void testIssue_BEANUTILS_18_PropertyUtils_isReadable_Mapped() {
        boolean result = false;
        try {
            result = PropertyUtils.isReadable(bean, "mapped");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("Threw exception: " + t);
        }
        assertFalse("PropertyUtils.isReadable(bean, \"mapped\") returned true", result);
    }

    /**
     * Test {@link PropertyUtils#isWriteable(Object, String)}
     * for Mapped properties.
     */
    public void testIssue_BEANUTILS_18_PropertyUtils_isWriteable_Mapped() {
        boolean result = false;
        try {
            result = PropertyUtils.isWriteable(bean, "mapped");
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("Threw exception: " + t);
        }
        assertFalse("PropertyUtils.isWriteable(bean, \"mapped\") returned true", result);
    }

    /**
     * Test {@link PropertyUtils#getProperty(Object, String)}
     * for simple properties.
     */
    public void testIssue_BEANUTILS_18_PropertyUtils_getProperty() {
        boolean threwNoSuchMethodException = false;
        Object result = null;
        try {
            result = PropertyUtils.getProperty(bean, "simple");
        } catch (final NoSuchMethodException ex) {
            threwNoSuchMethodException = true; // expected result
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("Threw exception: " + t);
        }
        assertTrue("Expected NoSuchMethodException but returned '" + result + "'", threwNoSuchMethodException);
    }

    /**
     * Test {@link PropertyUtils#setProperty(Object, String, Object)}
     * for simple properties.
     */
    public void testIssue_BEANUTILS_18_PropertyUtils_setProperty() {
        boolean threwNoSuchMethodException = false;
        try {
            PropertyUtils.setProperty(bean, "simple", "BAR");
        } catch (final NoSuchMethodException ex) {
            threwNoSuchMethodException = true; // expected result
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("Threw exception: " + t);
        }
        assertTrue("Expected NoSuchMethodException", threwNoSuchMethodException);
    }

    /**
     * Test {@link PropertyUtils#getProperty(Object, String)}
     * for indexed properties.
     */
    public void testIssue_BEANUTILS_18_PropertyUtils_getProperty_Indexed() {
        boolean threwNoSuchMethodException = false;
        Object result = null;
        try {
            result = PropertyUtils.getProperty(bean, "indexed[0]");
        } catch (final NoSuchMethodException ex) {
            threwNoSuchMethodException = true; // expected result
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("Threw exception: " + t);
        }
        assertTrue("Expected NoSuchMethodException but returned '" + result + "'", threwNoSuchMethodException);
    }

    /**
     * Test {@link PropertyUtils#setProperty(Object, String, Object)}
     * for indexed properties.
     */
    public void testIssue_BEANUTILS_18_PropertyUtils_setProperty_Indexed() {
        boolean threwNoSuchMethodException = false;
        try {
            PropertyUtils.setProperty(bean, "indexed[0]", "BAR");
        } catch (final NoSuchMethodException ex) {
            threwNoSuchMethodException = true; // expected result
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("Threw exception: " + t);
        }
        assertTrue("Expected NoSuchMethodException", threwNoSuchMethodException);
    }

    /**
     * Test {@link PropertyUtils#getProperty(Object, String)}
     * for mapped properties.
     */
    public void testIssue_BEANUTILS_18_PropertyUtils_getProperty_Mapped() {
        boolean threwNoSuchMethodException = false;
        Object result = null;
        try {
            result = PropertyUtils.getProperty(bean, "mapped(foo-key)");
        } catch (final NoSuchMethodException ex) {
            threwNoSuchMethodException = true; // expected result
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("Threw exception: " + t);
        }
        assertTrue("Expected NoSuchMethodException but returned '" + result + "'", threwNoSuchMethodException);
    }

    /**
     * Test {@link PropertyUtils#setProperty(Object, String, Object)}
     * for mapped properties.
     */
    public void testIssue_BEANUTILS_18_PropertyUtils_setProperty_Mapped() {
        boolean threwNoSuchMethodException = false;
        try {
            PropertyUtils.setProperty(bean, "mapped(foo-key)", "BAR");
        } catch (final NoSuchMethodException ex) {
            threwNoSuchMethodException = true; // expected result
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("Threw exception: " + t);
        }
        assertTrue("Expected NoSuchMethodException", threwNoSuchMethodException);
    }
}
