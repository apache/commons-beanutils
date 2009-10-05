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
 * See https://issues.apache.org/jira/browse/BEANUTILS-360
 * <p />
 *
 * @version $Revision$ $Date$
 */
public class Jira360TestCase extends TestCase {

    /**
     * Create a test case with the specified name.
     *
     * @param name The name of the test
     */
    public Jira360TestCase(String name) {
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
        return (new TestSuite(Jira360TestCase.class));
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
     * Test {@link BeanUtils#copyProperties(Object, Object)}
     */
    public void testBeanUtils_copyProperties() throws Exception {
        
        try {

            //Create beans & set activated property
            GRPInfo orig = new GRPInfo();
            orig.setActivated(1);
            GRPInfo dest = new GRPInfo();
            dest.setActivated(0);

            // Check different before copy
            assertNotSame(orig.getActivated(), dest.getActivated());

            // Copy
            BeanUtils.copyProperties(dest, orig);

            // Check same after copy
            assertEquals(orig.getActivated(), dest.getActivated());

        } catch (IndexOutOfBoundsException e) {
            // expected result
        }
    }

    /** Interface */
    public interface GroupInterface {

        public void setActivated(Integer value);

        public Integer getActivated();

    }

    /** Implementation */
    public class GRPInfo implements GroupInterface {

        private Integer activated;

        public void setActivated(int value) {
            setActivated(new Integer(value));
        }

        public void setActivated(Integer value) {
            this.activated = value;
        }

        public Integer getActivated() {
            return activated;
        } 

    }
}
