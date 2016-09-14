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
package org.apache.commons.beanutils.bugs.other;

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 * Test if BeanInfo supports index properties for java.util.List
 * <p>
 * This was supported by Java until Java 8 (BEANUTILS-492).
 *
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-492">BEANUTILS-492</a>
 */
public class Jira492IndexedListsSupport {
    public static class IndexedBean {
        private List<String> someList = new ArrayList<String>();
        public List<String> getSomeList() {
            return someList;
        }
        public void setSomeList(List<String> someList) {
            this.someList = someList;
        }
        public void setSomeList(int i, String value) {
            someList.set(i, value);
        }
        public String getSomeList(int i) {
            return someList.get(i);
        }
    }

    public static boolean supportsIndexedLists() throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(IndexedBean.class);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (pd.getName().equals("someList")) {
                return pd instanceof IndexedPropertyDescriptor;
            }
        }
        throw new IllegalStateException("Could not find PropertyDescriptor for 'file'");
    }

}
