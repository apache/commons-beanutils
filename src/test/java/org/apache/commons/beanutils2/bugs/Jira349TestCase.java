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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-349">https://issues.apache.org/jira/browse/BEANUTILS-349</a>
 */
public class Jira349TestCase extends TestCase {

    /**
     * Test Bean with a Boolean object property.
     */
    public static class ObjectBean {
        private Boolean testProperty;

        public Boolean getTestProperty() {
            return testProperty;
        }

        public void setTestProperty(final Boolean testProperty) {
            this.testProperty = testProperty;
        }
    }

    /**
     * Test Bean with a primitive boolean property.
     */
    public static class PrimitiveBean {
        private boolean testProperty;

        public boolean getTestProperty() {
            return testProperty;
        }

        public void setTestProperty(final boolean testProperty) {
            this.testProperty = testProperty;
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(Jira349TestCase.class);

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
        return new TestSuite(Jira349TestCase.class);
    }

    /**
     * Create a test case with the specified name.
     *
     * @param name The name of the test
     */
    public Jira349TestCase(final String name) {
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
     * Test {@link PropertyUtils#copyProperties(Object, Object)}
     */
    public void testIssue_BEANUTILS_349_PropertyUtils_copyProperties() {
        final PrimitiveBean dest = new PrimitiveBean();
        final ObjectBean origin = new ObjectBean();
        try {
            PropertyUtils.copyProperties(dest, origin);
        } catch (final NullPointerException e) {
            LOG.error("Failed", e);
            fail("Threw NullPointerException");
        } catch (final IllegalArgumentException e) {
            LOG.warn("Expected Result", e);
        } catch (final Throwable t) {
            LOG.error("Failed", t);
            fail("Threw exception: " + t);
        }
    }
}
