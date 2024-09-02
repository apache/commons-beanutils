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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.commons.beanutils2.BeanUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-369">https://issues.apache.org/jira/browse/BEANUTILS-369</a>
 */
public class Jira369TestCase {

    /**
     * Test Bean
     */
    public static class TestBean {
        private String aproperty;
        private String bproperty;

        public String getARatedCd() {
            return aproperty;
        }

        public String getbRatedCd() {
            return bproperty;
        }

        public void setARatedCd(final String aproperty) {
            this.aproperty = aproperty;
        }

        public void setbRatedCd(final String bproperty) {
            this.bproperty = bproperty;
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
     * Test {@link BeanUtils} getProperty() for property "aRatedCd".
     */
    @Test
    public void testBeanUtilsGetProperty_aRatedCd() throws Exception {
        final TestBean bean = new TestBean();
        bean.setARatedCd("foo");
        assertThrows(NoSuchMethodException.class, () -> assertEquals("foo", BeanUtils.getProperty(bean, "aRatedCd")));
    }

    /**
     * Test {@link BeanUtils} getProperty() for property "ARatedCd".
     */
    @Test
    public void testBeanUtilsGetProperty_ARatedCd() throws Exception {
        final TestBean bean = new TestBean();
        bean.setARatedCd("foo");
        assertEquals("foo", BeanUtils.getProperty(bean, "ARatedCd"));
    }

    /**
     * Test {@link BeanUtils} getProperty() for property "bRatedCd".
     */
    @Test
    public void testBeanUtilsGetProperty_bRatedCd() throws Exception {
        final TestBean bean = new TestBean();
        bean.setbRatedCd("foo");
        assertEquals("foo", BeanUtils.getProperty(bean, "bRatedCd"));
    }
}
