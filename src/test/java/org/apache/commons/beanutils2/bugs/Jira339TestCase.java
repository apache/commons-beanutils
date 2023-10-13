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

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils2.BeanUtils;
import org.apache.commons.beanutils2.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-339">https://issues.apache.org/jira/browse/BEANUTILS-339</a>
 */
public class Jira339TestCase extends TestCase {

    /**
     * Test Bean.
     */
    public static class TestBean {
        private Comparator<?> comparator;

        /**
         * Gets the comparator.
         *
         * @return the comparator
         */
        public Comparator<?> getComparator() {
            return comparator;
        }

        /**
         * Sets the comparator.
         *
         * @param comparator the comparator
         */
        public void setComparator(final Comparator<?> comparator) {
            this.comparator = comparator;
        }

    }

    private static final Logger LOG = LoggerFactory.getLogger(Jira339TestCase.class);

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
        return new TestSuite(Jira339TestCase.class);
    }

    /**
     * Create a test case with the specified name.
     *
     * @param name The name of the test
     */
    public Jira339TestCase(final String name) {
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
     * Test {@link BeanUtils#populate(Object, Map)}
     */
    public void testIssue_BEANUTILS_331_BeanUtilsBean_populate() {

        final TestBean bean = new TestBean();
        try {
            final Map<String, Object> properties = new HashMap<>();
            properties.put("comparator", null);
            BeanUtils.populate(bean, properties);
        } catch (final Throwable t) {
            LOG.error("Failed: " + t.getMessage(), t);
            fail("Threw exception: " + t);
        }
        assertNull("TestBean comparator should be null", bean.getComparator());
    }

    /**
     * Test {@link PropertyUtils#setProperty(Object, String, Object)}
     */
    public void testIssue_BEANUTILS_339_BeanUtilsBean_setProperty() {

        final TestBean bean = new TestBean();
        try {
            BeanUtils.setProperty(bean, "comparator", null);
        } catch (final Throwable t) {
            LOG.error("Failed: " + t.getMessage(), t);
            fail("Threw exception: " + t);
        }
        assertNull("TestBean comparator should be null", bean.getComparator());
    }
}
