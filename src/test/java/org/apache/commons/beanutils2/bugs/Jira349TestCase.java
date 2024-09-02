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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-349">https://issues.apache.org/jira/browse/BEANUTILS-349</a>
 */
public class Jira349TestCase {

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

    private static final Log LOG = LogFactory.getLog(Jira349TestCase.class);

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
     * Test {@link PropertyUtils#copyProperties(Object, Object)}
     */
    @Test
    public void testIssue_BEANUTILS_349_PropertyUtils_copyProperties() {
        final PrimitiveBean dest = new PrimitiveBean();
        final ObjectBean origin = new ObjectBean();
        assertThrows(IllegalArgumentException.class, () -> PropertyUtils.copyProperties(dest, origin));
    }
}
