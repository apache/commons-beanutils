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

import org.apache.commons.beanutils.BeanUtils;

/**
 * @version $Id$
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-369">https://issues.apache.org/jira/browse/BEANUTILS-369</a>
 */
public class Jira369TestCase extends TestCase {

    /**
     * Create a test case with the specified name.
     *
     * @param name The name of the test
     */
    public Jira369TestCase(final String name) {
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
        return (new TestSuite(Jira369TestCase.class));
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
     * Test {@link BeanUtils} getProperty() for property "aRatedCd".
     */
    public void testBeanUtilsGetProperty_aRatedCd() throws Exception {
        final TestBean bean = new TestBean();
        bean.setARatedCd("foo");

        try {
            assertEquals("foo", BeanUtils.getProperty(bean, "aRatedCd"));
            fail("Expected NoSuchMethodException");
        } catch (final NoSuchMethodException e) {
            // expected result
        } catch (final Exception e) {
            fail("Threw " + e);
        }
    }

    /**
     * Test {@link BeanUtils} getProperty() for property "ARatedCd".
     */
    public void testBeanUtilsGetProperty_ARatedCd() throws Exception {
        final TestBean bean = new TestBean();
        bean.setARatedCd("foo");
        try {
            assertEquals("foo", BeanUtils.getProperty(bean, "ARatedCd"));
        } catch (final Exception e) {
            fail("Threw " + e);
        }
    }

    /**
     * Test {@link BeanUtils} getProperty() for property "bRatedCd".
     */
    public void testBeanUtilsGetProperty_bRatedCd() throws Exception {
        final TestBean bean = new TestBean();
        bean.setbRatedCd("foo");
        try {
            assertEquals("foo", BeanUtils.getProperty(bean, "bRatedCd"));
        } catch (final Exception e) {
            fail("Threw " + e);
        }
    }

    /**
     * Test Bean
     */
    public static class TestBean {
        private String aproperty;
        private String bproperty;

        public String getARatedCd() {
            return aproperty;
        }

        public void setARatedCd(final String aproperty) {
            this.aproperty = aproperty;
        }

        public String getbRatedCd() {
            return bproperty;
        }

        public void setbRatedCd(final String bproperty) {
            this.bproperty = bproperty;
        }
    }
}
