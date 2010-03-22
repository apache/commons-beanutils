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
import org.apache.commons.beanutils.bugs.other.Jira273BeanFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Public methods overriden in anonymous or private subclasses
 * are not recognized by PropertyUtils - see issue# BEANUTILS-273.
 * <p />
 * See https://issues.apache.org/jira/browse/BEANUTILS-273
 * <p />
 *
 * @version $Revision$ $Date$
 */
public class Jira273TestCase extends TestCase {

    private Log log = LogFactory.getLog(Jira273TestCase.class);

    /**
     * Create a test case with the specified name.
     *
     * @param name The name of the test
     */
    public Jira273TestCase(String name) {
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
        return (new TestSuite(Jira273TestCase.class));
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
     * Test with an annonymous class that overrides a public method
     * of a public class.
     */
    public void testIssue_BEANUTILS_273_AnnonymousOverriden() {
        Object bean = Jira273BeanFactory.createAnnonymousOverriden();
        Object result = null;
        try {
            result = PropertyUtils.getProperty(bean, "beanValue");
        } catch (Throwable t) {
            log.error("AnnonymousOverriden: " + t.getMessage(), t);
            fail("AnnonymousOverriden Threw exception: " + t);
        }
        assertEquals("AnnonymousOverriden", result);
    }

    /**
     * Test with an annonymous class that inherits a public method
     * of a public class.
     */
    public void testIssue_BEANUTILS_273_AnnonymousNotOverriden() {
        Object bean = Jira273BeanFactory.createAnnonymousNotOverriden();
        Object result = null;
        try {
            result = PropertyUtils.getProperty(bean, "beanValue");
        } catch (Throwable t) {
            log.error("AnnonymousNotOverriden: " + t.getMessage(), t);
            fail("AnnonymousNotOverriden Threw exception: " + t);
        }
        assertEquals("PublicBeanWithMethod", result);
    }

    /**
     * Test with an private class that inherits a public method
     * of a public class.
     */
    public void testIssue_BEANUTILS_273_PrivatePublicNotOverriden() {
        Object bean = Jira273BeanFactory.createPrivatePublicNotOverriden();
        Object result = null;
        try {
            result = PropertyUtils.getProperty(bean, "beanValue");
        } catch (Throwable t) {
            log.error("PrivatePublicNotOverriden: " + t.getMessage(), t);
            fail("PrivatePublicNotOverriden Threw exception: " + t);
        }
        assertEquals("PublicBeanWithMethod", result);
    }

    /**
     * Test with an private class that overrides a public method
     * of a public class.
     */
    public void testIssue_BEANUTILS_273_PrivatePublicOverriden() {
        Object bean = Jira273BeanFactory.createPrivatePublicOverriden();
        Object result = null;
        try {
            result = PropertyUtils.getProperty(bean, "beanValue");
        } catch (Throwable t) {
            log.error("PrivatePublicOverriden: " + t.getMessage(), t);
            fail("PrivatePublicOverriden Threw exception: " + t);
        }
        assertEquals("PrivatePublicOverriden", result);
    }

    /**
     * Test with an private class that inherits a public method
     * of a "grand parent" public class.
     */
    public void testIssue_BEANUTILS_273_PrivatePrivatePublicNotOverriden() {
        Object bean = Jira273BeanFactory.createPrivatePrivatePublicNotOverriden();
        Object result = null;
        try {
            result = PropertyUtils.getProperty(bean, "beanValue");
        } catch (Throwable t) {
            log.error("PrivatePrivatePublicNotOverriden: " + t.getMessage(), t);
            fail("PrivatePrivatePublicNotOverriden Threw exception: " + t);
        }
        assertEquals("PublicBeanWithMethod", result);
    }

    /**
     * Test with an private class that overrides a public method
     * of a "grand parent" public class.
     */
    public void testIssue_BEANUTILS_273_PrivatePrivatePublicOverriden() {
        Object bean = Jira273BeanFactory.createPrivatePrivatePublicOverriden();
        Object result = null;
        try {
            result = PropertyUtils.getProperty(bean, "beanValue");
        } catch (Throwable t) {
            log.error("PrivatePrivatePublicOverriden: " + t.getMessage(), t);
            fail("PrivatePrivatePublicOverriden Threw exception: " + t);
        }
        assertEquals("PrivatePrivatePublicOverriden", result);
    }
}
