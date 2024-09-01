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

import java.util.Date;

import org.apache.commons.beanutils2.BeanUtils;
import org.junit.jupiter.api.Test;

/**
 * copyProperties() throws a ConversionException : No value specified for 'Date' when the field is a java.util.Date with a null value
 */
public class Jira454TestCase {
    public static class TestBean {
        private Date createdAt;

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(final Date createdAt) {
            this.createdAt = createdAt;
        }
    }

    @Test
    public void testCopyProperties() throws Exception {
        final TestBean bean = new TestBean();
        final TestBean b2 = new TestBean();
        BeanUtils.copyProperties(b2, bean);
        assertNull(b2.getCreatedAt(), "Got a creation date");
    }
}
