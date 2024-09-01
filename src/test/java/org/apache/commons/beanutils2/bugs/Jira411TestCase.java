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

import org.apache.commons.beanutils2.BeanUtilsBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * BeanUtilsBean.setProperty throws IllegalArgumentException if getter of nested property returns null
 *
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-411">https://issues.apache.org/jira/browse/BEANUTILS-411</a>
 */
public class Jira411TestCase {

    public class DummyBean {

        private String imgLink;

        public String getImgLink() {
            return imgLink;
        }

        public void setImgLink(final String imgLink) {
            this.imgLink = imgLink;
        }
    }
    private BeanUtilsBean instance;

    private DummyBean testBean;

    @BeforeEach
    protected void setUp() throws Exception {
        instance = new BeanUtilsBean();
        testBean = new DummyBean();
    }

    @Test
    public void testSetProperty() throws Exception {
        instance.setProperty(testBean, "imgLink.x", "1");
    }
}
