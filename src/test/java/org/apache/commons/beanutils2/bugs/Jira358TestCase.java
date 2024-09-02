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

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.commons.beanutils2.PropertyUtils;
import org.apache.commons.beanutils2.TestBean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-358">https://issues.apache.org/jira/browse/BEANUTILS-358</a>
 */
public class Jira358TestCase {

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
     * Test {@link PropertyUtils#getIndexedProperty(Object, String, int)}
     */
    @Test
    public void testPropertyUtils_getIndexedProperty_Array() throws Exception {

        final TestBean bean = new TestBean();
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> PropertyUtils.getIndexedProperty(bean, "intArray", bean.getIntArray().length));
    }

    /**
     * Test {@link PropertyUtils#getIndexedProperty(Object, String, int)}
     */
    @Test
    public void testPropertyUtils_getIndexedProperty_List() throws Exception {

        final TestBean bean = new TestBean();
        assertThrows(IndexOutOfBoundsException.class, () -> PropertyUtils.getIndexedProperty(bean, "listIndexed", bean.getListIndexed().size()));
    }
}
