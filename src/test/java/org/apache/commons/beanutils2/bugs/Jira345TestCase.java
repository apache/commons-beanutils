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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.beanutils2.BeanUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-345">https://issues.apache.org/jira/browse/BEANUTILS-345</a>
 */
public class Jira345TestCase {

    /** Example Bean */
    public static class MyBean {

        private String[][] matr = { { "1", "2" }, { "3", "4" } };

        private String[][][] matr3D = { { { "11", "12" }, { "13", "14" } }, { { "21", "22" }, { "23", "24" } }, };

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
     * Sets up.
     *
     * @throws Exception
     */
    @BeforeEach
    protected void setUp() throws Exception {
    }

    /**
     * Tear Down.
     *
     * @throws Exception
     */
    @AfterEach
    protected void tearDown() throws Exception {
    }

    /**
     * Test {@link BeanUtils} setProperty() with 2D array.
     */
    @Test
    public void testBeanUtilsSetProperty_2DArray() throws Exception {
        final MyBean myBean = new MyBean();
        BeanUtils.setProperty(myBean, "matr[0][0]", "Sample");
        assertEquals("Sample", myBean.getMatr()[0][0]);
    }

    /**
     * Test {@link BeanUtils} setProperty() with 3D array.
     */
    @Test
    public void testBeanUtilsSetProperty_3DArray() throws Exception {
        final MyBean myBean = new MyBean();
        BeanUtils.setProperty(myBean, "matr3D[0][0][0]", "Sample");
        assertEquals("Sample", myBean.getMatr3D()[0][0][0]);
    }
}
