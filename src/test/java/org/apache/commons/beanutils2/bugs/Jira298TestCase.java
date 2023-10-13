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

import java.lang.reflect.Method;

import org.apache.commons.beanutils2.MethodUtils;
import org.apache.commons.beanutils2.PropertyUtils;
import org.apache.commons.beanutils2.bugs.other.Jira298BeanFactory;
import org.apache.commons.beanutils2.bugs.other.Jira298BeanFactory.IX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-298">https://issues.apache.org/jira/browse/BEANUTILS-298</a>
 */
public class Jira298TestCase extends TestCase {

    private static final Logger LOG = LoggerFactory.getLogger(Jira298TestCase.class);

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
        return new TestSuite(Jira298TestCase.class);
    }

    /**
     * Create a test case with the specified name.
     *
     * @param name The name of the test
     */
    public Jira298TestCase(final String name) {
        super(name);
    }

    /**
     * Sets up.
     *
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Tear Down.
     *
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test {@link MethodUtils#getAccessibleMethod(Class, Method)}
     */
    public void testIssue_BEANUTILS_298_MethodUtils_getAccessibleMethod() {
        final Object bean = Jira298BeanFactory.createImplX();
        Object result = null;
        try {
            final Method m2 = MethodUtils.getAccessibleMethod(bean.getClass(), "getName", new Class[0]);
            result = m2.invoke(bean);
        } catch (final Throwable t) {
            LOG.error("Failed: " + t.getMessage(), t);
            fail("Threw exception: " + t);
        }
        assertEquals("BaseX name value", result);
    }

    /**
     * Test {@link PropertyUtils#getProperty(Object, String)}
     */
    public void testIssue_BEANUTILS_298_PropertyUtils_getProperty() {
        final Object bean = Jira298BeanFactory.createImplX();
        Object result = null;
        try {
            result = PropertyUtils.getProperty(bean, "name");
        } catch (final Throwable t) {
            LOG.error("Failed: " + t.getMessage(), t);
            fail("Threw exception: " + t);
        }
        assertEquals("BaseX name value", result);
    }

    /**
     * Test {@link PropertyUtils#setProperty(Object, String, Object)}
     */
    public void testIssue_BEANUTILS_298_PropertyUtils_setProperty() {
        final Object bean = Jira298BeanFactory.createImplX();
        assertEquals("BaseX name value", ((IX) bean).getName());
        try {
            PropertyUtils.setProperty(bean, "name", "new name");
        } catch (final Throwable t) {
            LOG.error("Failed: " + t.getMessage(), t);
            fail("Threw exception: " + t);
        }
        assertEquals("new name", ((IX) bean).getName());
    }
}
