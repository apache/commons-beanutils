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
package org.apache.commons.beanutils.bugs;

import static org.apache.commons.beanutils.bugs.other.Jira492IndexedListsSupport.supportsIndexedLists;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.bugs.other.Jira492IndexedListsSupport.IndexedBean;
import org.junit.Before;
import org.junit.Test;

/**
 * This test verifies that although BEANUTILS-492
 * means {@link IndexedPropertyDescriptor}s are not
 * returned for properties of type {@link List}, they
 * can still be accessed as positional items.
 *
 * @see <a href="https://issues.apache.org/jira/browse/BEANUTILS-492">BEANUTILS-492</a>
 */
public class Jira492TestCase {

    private final BeanUtilsBean beanUtils = new BeanUtilsBean();

    private final PropertyUtilsBean propertyUtils = new PropertyUtilsBean();

    private IndexedBean bean;

    @Before
    public void makeBean() {
        bean = new IndexedBean();
        bean.setSomeList(Arrays.asList("item0", "item1"));
    }

    @Test
    public void getIndexedProperty() throws Exception {
        assertEquals("item0", beanUtils.getIndexedProperty(bean, "someList", 0));
        assertEquals("item1", beanUtils.getIndexedProperty(bean, "someList[1]"));
    }

    @Test
    public void getPropertySubScript() throws Exception {
        assertEquals("item0", beanUtils.getProperty(bean, "someList[0]"));
        assertEquals("item1", beanUtils.getProperty(bean, "someList[1]"));
    }

    @Test
    public void setIndexedProperty() throws Exception {
        beanUtils.setProperty(bean, "someList[1]", "item1-modified");
        assertEquals("item1-modified", beanUtils.getIndexedProperty(bean, "someList", 1));
    }

    @Test
    public void getProperty() throws Exception {
        assertEquals("item0", beanUtils.getProperty(bean, "someList"));
    }

    @Test
    public void getPropertyUnconverted() throws Exception {
        Object someList = propertyUtils.getProperty(bean, "someList");
        assertTrue("Did not retrieve list", someList instanceof List);
    }

    public void getArrayProperty() throws Exception {
        String[] arr = beanUtils.getArrayProperty(bean, "someList");
        assertEquals(2, arr.length);
        assertEquals("item0", arr[0]);
        assertEquals("item1", arr[1]);
    }

    @Test
    public void describe() throws Exception {
        Map<String, String> described = beanUtils.describe(bean);
        // Only first element survives as a String
        assertEquals("item0", described.get("someList"));
    }

    @Test
    public void getPropertyType() throws Exception {
        if (supportsIndexedLists()) {
            // legacy behaviour (< Java 8)
            assertEquals(String.class, propertyUtils.getPropertyType(bean, "someList[0]"));
        } else {
            assertEquals(List.class, propertyUtils.getPropertyType(bean, "someList"));
        }
    }

    @Test
    public void getPropertyDescriptor() throws Exception {
        PropertyDescriptor propDesc = propertyUtils.getPropertyDescriptor(bean, "someList");
        if (supportsIndexedLists()) {
            // Java 7 or earlier? (BEANUTILS-492)
            IndexedPropertyDescriptor indexed = (IndexedPropertyDescriptor) propDesc;
            assertEquals(String.class, indexed.getIndexedReadMethod().getReturnType());
        }
    }


}
