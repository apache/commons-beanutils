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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.bugs.other.Jira87BeanFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test case for Jiar issue# BEANUTILS-87.
 *
 * <p>
 * In BeanUtils 1.7.0 a "package friendly" implementation
 * of a public interface with defined a "mapped property"
 * caused an {@link IllegalAccessException} to be thrown by
 * PropertyUtils's getMappedProperty method.
 *
 * <p>
 * This test case demonstrates the issue.
 *
 * @version $Id$
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-87">https://issues.apache.org/jira/browse/BEANUTILS-87</a>
 */
public class Jira87TestCase extends TestCase {

    private final Log log = LogFactory.getLog(Jira87TestCase.class);

    /**
     * Create a test case with the specified name.
     *
     * @param name The name of the test
     */
    public Jira87TestCase(final String name) {
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
        return (new TestSuite(Jira87TestCase.class));
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
     * Interface definition with a mapped property
     */
    public void testJira87() {

        final Jira87BeanFactory.PublicMappedInterface bean = Jira87BeanFactory.createMappedPropertyBean();
        try {
            // N.B. The test impl. returns the key value
            assertEquals("foo", PropertyUtils.getMappedProperty(bean, "value(foo)"));
        } catch (final Throwable t) {
            log.error("ERROR " + t, t);
            fail("Threw exception: " + t);
        }
    }
}
