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
package org.apache.commons.beanutils2.locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.commons.beanutils2.TestBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;

/**
 * Test Case for {@link LocaleBeanUtils}.
 *
 */
public class LocaleBeanUtilsTestCase {

    private static final Log LOG = LogFactory.getLog(LocaleBeanUtilsTestCase.class);

    /**
     * Test setting a nested indexed property
     */
    @Test
    public void testSetNestedPropertyIndexed() {
        final TestBean bean = new TestBean();
        bean.getNested().setIntIndexed(1, 51);
        assertEquals(51, bean.getNested().getIntIndexed(1), "Initial value[1] 51");
        try {
            LocaleBeanUtils.setProperty(bean, "nested.intIndexed[1]", "123", null);
        } catch (final Throwable t) {
            LOG.error(t);
            fail("Threw " + t);
        }
        assertEquals(123, bean.getNested().getIntIndexed(1), "Check Set Value");
    }

    /**
     * Test setting a nested simple property
     */
    @Test
    public void testSetNestedPropertySimple() {
        final TestBean bean = new TestBean();
        bean.getNested().setIntProperty(5);
        assertEquals(5, bean.getNested().getIntProperty(), "Initial value 5");
        try {
            LocaleBeanUtils.setProperty(bean, "nested.intProperty", "123", null);
        } catch (final Throwable t) {
            LOG.error(t);
            fail("Threw " + t);
        }
        assertEquals(123, bean.getNested().getIntProperty(), "Check Set Value");
    }
}
