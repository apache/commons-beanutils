/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.beanutils.bugs;

import org.apache.commons.beanutils.BeanUtils;

import junit.framework.TestCase;

/**
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-345">https://issues.apache.org/jira/browse/BEANUTILS-345</a>
 */
public class Jira345Test extends TestCase {

    /** Example Bean */
    public static class MyBean {

        private String[][] matr = {{"1","2"},{"3","4"}};

        private String[][][] matr3D = {
                {{"11","12"}, {"13","14"}},
                {{"21","22"}, {"23","24"}},
        };

        public String[][] getMatr() {
            return matr;
        }
        public String[][][] getMatr3D() {
            return matr3D;
        }
        public void setMatr(final String[][] matr) {
            this.matr = matr;
        }
        public void setMatr3D(final String[][][] matr3D) {
            this.matr3D = matr3D;
        }
    }
    /**
     * Create a test case with the specified name.
     *
     * @param name The name of the test
     */
    public Jira345Test(final String name) {
        super(name);
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
     * Test {@link BeanUtils} setProperty() with 2D array.
     */
    public void testBeanUtilsSetProperty_2DArray() throws Exception{
        final MyBean myBean = new MyBean();
        BeanUtils.setProperty(myBean, "matr[0][0]","Sample");
        assertEquals("Sample", myBean.getMatr()[0][0]);
    }

    /**
     * Test {@link BeanUtils} setProperty() with 3D array.
     */
    public void testBeanUtilsSetProperty_3DArray() throws Exception{
        final MyBean myBean = new MyBean();
        BeanUtils.setProperty(myBean, "matr3D[0][0][0]","Sample");
        assertEquals("Sample", myBean.getMatr3D()[0][0][0]);
    }
}
