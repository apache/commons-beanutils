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

import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils2.BeanUtils;
import org.apache.commons.beanutils2.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-339">https://issues.apache.org/jira/browse/BEANUTILS-339</a>
 */
public class Jira339TestCase {

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

    private static final Log LOG = LogFactory.getLog(Jira339TestCase.class);

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
     * Test {@link BeanUtils#populate(Object, Map)}
     */
    @Test
    public void testIssue_BEANUTILS_331_BeanUtilsBean_populate() throws Exception {
        final TestBean bean = new TestBean();
        final Map<String, Object> properties = new HashMap<>();
        properties.put("comparator", null);
        BeanUtils.populate(bean, properties);
        assertNull(bean.getComparator(), "TestBean comparator should be null");
    }

    /**
     * Test {@link PropertyUtils#setProperty(Object, String, Object)}
     */
    @Test
    public void testIssue_BEANUTILS_339_BeanUtilsBean_setProperty() throws Exception {
        final TestBean bean = new TestBean();
        BeanUtils.setProperty(bean, "comparator", null);
        assertNull(bean.getComparator(), "TestBean comparator should be null");
    }
}
