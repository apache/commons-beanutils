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

import org.apache.commons.beanutils2.PropertyUtils;
import org.apache.commons.beanutils2.bugs.other.Jira273BeanFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Public methods overridden in anonymous or private subclasses
 * are not recognized by PropertyUtils - see issue# BEANUTILS-273.
 *
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-273">https://issues.apache.org/jira/browse/BEANUTILS-273</a>
 */
public class Jira273TestCase extends TestCase {

    private final Log log = LogFactory.getLog(Jira273TestCase.class);

    /**
     * Create a test case with the specified name.
     *
     * @param name The name of the test
     */
    public Jira273TestCase(final String name) {
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
        return new TestSuite(Jira273TestCase.class);
    }

    /**
     * Set up.
     *
     * @throws java.lang.Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
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
     * Test with an anonymous class that overrides a public method
     * of a public class.
     */
    public void testIssue_BEANUTILS_273_AnonymousOverridden() {
        final Object bean = Jira273BeanFactory.createAnonymousOverridden();
        Object result = null;
        try {
            result = PropertyUtils.getProperty(bean, "beanValue");
        } catch (final Throwable t) {
            log.error("AnonymousOverridden: " + t.getMessage(), t);
            fail("AnonymousOverridden Threw exception: " + t);
        }
        assertEquals("AnonymousOverridden", result);
    }

    /**
     * Test with an anonymous class that inherits a public method
     * of a public class.
     */
    public void testIssue_BEANUTILS_273_AnonymousNotOverridden() {
        final Object bean = Jira273BeanFactory.createAnonymousNotOverridden();
        Object result = null;
        try {
            result = PropertyUtils.getProperty(bean, "beanValue");
        } catch (final Throwable t) {
            log.error("AnonymousNotOverridden: " + t.getMessage(), t);
            fail("AnonymousNotOverridden Threw exception: " + t);
        }
        assertEquals("PublicBeanWithMethod", result);
    }

    /**
     * Test with an private class that inherits a public method
     * of a public class.
     */
    public void testIssue_BEANUTILS_273_PrivatePublicNotOverridden() {
        final Object bean = Jira273BeanFactory.createPrivatePublicNotOverridden();
        Object result = null;
        try {
            result = PropertyUtils.getProperty(bean, "beanValue");
        } catch (final Throwable t) {
            log.error("PrivatePublicNotOverridden: " + t.getMessage(), t);
            fail("PrivatePublicNotOverridden Threw exception: " + t);
        }
        assertEquals("PublicBeanWithMethod", result);
    }

    /**
     * Test with an private class that overrides a public method
     * of a public class.
     */
    public void testIssue_BEANUTILS_273_PrivatePublicOverridden() {
        final Object bean = Jira273BeanFactory.createPrivatePublicOverridden();
        Object result = null;
        try {
            result = PropertyUtils.getProperty(bean, "beanValue");
        } catch (final Throwable t) {
            log.error("PrivatePublicOverridden: " + t.getMessage(), t);
            fail("PrivatePublicOverridden Threw exception: " + t);
        }
        assertEquals("PrivatePublicOverridden", result);
    }

    /**
     * Test with an private class that inherits a public method
     * of a "grand parent" public class.
     */
    public void testIssue_BEANUTILS_273_PrivatePrivatePublicNotOverridden() {
        final Object bean = Jira273BeanFactory.createPrivatePrivatePublicNotOverridden();
        Object result = null;
        try {
            result = PropertyUtils.getProperty(bean, "beanValue");
        } catch (final Throwable t) {
            log.error("PrivatePrivatePublicNotOverridden: " + t.getMessage(), t);
            fail("PrivatePrivatePublicNotOverridden Threw exception: " + t);
        }
        assertEquals("PublicBeanWithMethod", result);
    }

    /**
     * Test with an private class that overrides a public method
     * of a "grand parent" public class.
     */
    public void testIssue_BEANUTILS_273_PrivatePrivatePublicOverridden() {
        final Object bean = Jira273BeanFactory.createPrivatePrivatePublicOverridden();
        Object result = null;
        try {
            result = PropertyUtils.getProperty(bean, "beanValue");
        } catch (final Throwable t) {
            log.error("PrivatePrivatePublicOverridden: " + t.getMessage(), t);
            fail("PrivatePrivatePublicOverridden Threw exception: " + t);
        }
        assertEquals("PrivatePrivatePublicOverridden", result);
    }
}
