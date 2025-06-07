/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.beanutils2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils2.bugs.other.Jira492IndexedListsSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Test Case for the Indexed Properties.
 * </p>
 */

public class IndexedPropertyTest {

    /**
     * The test bean for each test.
     */
    private IndexedTestBean bean;
    private BeanUtilsBean beanUtilsBean;
    private PropertyUtilsBean propertyUtilsBean;
    private String[] testArray;
    private String[] newArray;
    private List<String> testList;
    private List<Object> newList;
    private ArrayList<Object> arrayList;

    /**
     * Sets up instance variables required by this test case.
     */
    @BeforeEach
    public void setUp() {

        // BeanUtils
        beanUtilsBean = new BeanUtilsBean();
        propertyUtilsBean = beanUtilsBean.getPropertyUtils();

        // initialize Arrays and Lists
        testArray = new String[] { "array-0", "array-1", "array-2" };
        newArray = new String[] { "newArray-0", "newArray-1", "newArray-2" };

        testList = new ArrayList<>();
        testList.add("list-0");
        testList.add("list-1");
        testList.add("list-2");

        newList = new ArrayList<>();
        newList.add("newList-0");
        newList.add("newList-1");
        newList.add("newList-2");

        arrayList = new ArrayList<>();
        arrayList.add("arrayList-0");
        arrayList.add("arrayList-1");
        arrayList.add("arrayList-2");

        // initialize Test Bean properties
        bean = new IndexedTestBean();
        bean.setStringArray(testArray);
        bean.setStringList(testList);
        bean.setArrayList(arrayList);
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @AfterEach
    public void tearDown() {
        bean = null;
    }

    /**
     * Test IndexedPropertyDescriptor for an Array
     */
    @Test
    void testArrayIndexedPropertyDescriptor() throws Exception {
        final PropertyDescriptor descriptor = propertyUtilsBean.getPropertyDescriptor(bean, "stringArray");
        assertNotNull(descriptor, "No Array Descriptor");
        assertEquals(IndexedPropertyDescriptor.class, descriptor.getClass(), "Not IndexedPropertyDescriptor");
        assertEquals(testArray.getClass(), descriptor.getPropertyType(), "PropertyDescriptor Type invalid");
    }

    /**
     * Test Indexed Read Method for an Array
     */
    @Test
    void testArrayIndexedReadMethod() throws Exception {
        final IndexedPropertyDescriptor descriptor = (IndexedPropertyDescriptor) propertyUtilsBean.getPropertyDescriptor(bean, "stringArray");
        assertNotNull(descriptor.getIndexedReadMethod(), "No Array Indexed Read Method");
    }

    /**
     * Test Indexed Write Method for an Array
     */
    @Test
    void testArrayIndexedWriteMethod() throws Exception {
        final IndexedPropertyDescriptor descriptor = (IndexedPropertyDescriptor) propertyUtilsBean.getPropertyDescriptor(bean, "stringArray");
        assertNotNull(descriptor.getIndexedWriteMethod(), "No Array Indexed Write Method");
    }

    /**
     * Test IndexedPropertyDescriptor for an ArrayList
     */
    @Test
    void testArrayListIndexedPropertyDescriptor() throws Exception {
        final PropertyDescriptor descriptor = propertyUtilsBean.getPropertyDescriptor(bean, "arrayList");
        assertNotNull(descriptor, "No ArrayList Descriptor");
        if (Jira492IndexedListsSupport.supportsIndexedLists()) {
            assertEquals(IndexedPropertyDescriptor.class, descriptor.getClass(), "Not IndexedPropertyDescriptor");
        }
        assertEquals(ArrayList.class, descriptor.getPropertyType(), "PropertyDescriptor Type invalid");
    }

    /**
     * Test Read Method for an ArrayList
     */
    @Test
    void testArrayListReadMethod() throws Exception {
        final PropertyDescriptor descriptor = propertyUtilsBean.getPropertyDescriptor(bean, "arrayList");
        assertNotNull(descriptor.getReadMethod(), "No ArrayList Read Method");
    }

    /**
     * Test Write Method for an ArrayList
     */
    @Test
    void testArrayListWriteMethod() throws Exception {
        final PropertyDescriptor descriptor = propertyUtilsBean.getPropertyDescriptor(bean, "arrayList");
        assertNotNull(descriptor.getWriteMethod(), "No ArrayList Write Method");
    }

    /**
     * Test Read Method for an Array
     */
    @Test
    void testArrayReadMethod() throws Exception {
        final PropertyDescriptor descriptor = propertyUtilsBean.getPropertyDescriptor(bean, "stringArray");
        assertNotNull(descriptor.getReadMethod(), "No Array Read Method");
    }

    /**
     * Test Write Method for an Array
     */
    @Test
    void testArrayWriteMethod() throws Exception {
        final PropertyDescriptor descriptor = propertyUtilsBean.getPropertyDescriptor(bean, "stringArray");
        assertNotNull(descriptor.getWriteMethod(), "No Array Write Method");
    }

    /**
     * Test getting an array property
     */
    @Test
    void testGetArray() throws Exception {
        assertEquals(testArray, propertyUtilsBean.getProperty(bean, "stringArray"));
    }

    /**
     * Test getting an array property as a String
     *
     * NOTE: Why does retrieving array just return the first element in the array, whereas retrieving a List returns a comma separated list of all the elements?
     */
    @Test
    void testGetArrayAsString() throws Exception {
        assertEquals("array-0", beanUtilsBean.getProperty(bean, "stringArray"));
    }

    /**
     * Test getting an indexed item of an Array using getProperty("name[x]")
     */
    @Test
    void testGetArrayItemA() throws Exception {
        assertEquals("array-1", beanUtilsBean.getProperty(bean, "stringArray[1]"));
    }

    /**
     * Test getting an indexed item of an Array using getIndexedProperty("name")
     */
    @Test
    void testGetArrayItemB() throws Exception {
        assertEquals("array-1", beanUtilsBean.getIndexedProperty(bean, "stringArray", 1));
    }

    /**
     * Test getting an ArrayList
     */
    @Test
    void testGetArrayList() throws Exception {
        assertEquals(arrayList, propertyUtilsBean.getProperty(bean, "arrayList"));
    }

    /**
     * Test getting a List
     *
     * JDK 1.3.1_04: Test Passes JDK 1.4.2_05: Test Fails - fails NoSuchMethodException, i.e. reason as testListReadMethod() failed.
     */
    @Test
    void testGetList() throws Exception {
        assertEquals(testList, propertyUtilsBean.getProperty(bean, "stringList"));
    }

    /**
     * Test getting a List property as a String
     *
     * JDK 1.3.1_04: Test Passes JDK 1.4.2_05: Test Fails - fails NoSuchMethodException, i.e. reason as testListReadMethod() failed.
     */
    @Test
    void testGetListAsString() throws Exception {
        assertEquals("list-0", beanUtilsBean.getProperty(bean, "stringList"));
    }

    /**
     * Test getting an indexed item of a List using getProperty("name[x]")
     */
    @Test
    void testGetListItemA() throws Exception {
        assertEquals("list-1", beanUtilsBean.getProperty(bean, "stringList[1]"));
    }

    /**
     * Test getting an indexed item of a List using getIndexedProperty("name")
     */
    @Test
    void testGetListItemB() throws Exception {
        assertEquals("list-1", beanUtilsBean.getIndexedProperty(bean, "stringList", 1));
    }

    /**
     * Test IndexedPropertyDescriptor for a List
     */
    @Test
    void testListIndexedPropertyDescriptor() throws Exception {
        final PropertyDescriptor descriptor = propertyUtilsBean.getPropertyDescriptor(bean, "stringList");
        assertNotNull(descriptor, "No List Descriptor");
        if (Jira492IndexedListsSupport.supportsIndexedLists()) {
            // BEANUTILS-492 - can't assume lists are handled as arrays in Java 8+
            assertEquals(IndexedPropertyDescriptor.class, descriptor.getClass(), "Not IndexedPropertyDescriptor");
        }
        assertEquals(List.class, descriptor.getPropertyType(), "PropertyDescriptor Type invalid");
    }

    /**
     * Test Indexed Read Method for a List
     */
    @Test
    void testListIndexedReadMethod() throws Exception {
        final PropertyDescriptor descriptor = propertyUtilsBean.getPropertyDescriptor(bean, "stringList");
        assertNotNull(descriptor, "stringList descriptor not found");
        assumeTrue(Jira492IndexedListsSupport.supportsIndexedLists(), "JDK does not support index bean properties on java.util.List");
        assertNotNull(((IndexedPropertyDescriptor) descriptor).getIndexedReadMethod(), "No List Indexed Read Method");
    }

    /**
     * Test Indexed Write Method for a List
     */
    @Test
    void testListIndexedWriteMethod() throws Exception {
        final PropertyDescriptor descriptor = propertyUtilsBean.getPropertyDescriptor(bean, "stringList");
        assertNotNull(descriptor, "stringList descriptor not found");
        assumeTrue(Jira492IndexedListsSupport.supportsIndexedLists(), "JDK does not support index bean properties on java.util.List");
        assertNotNull(((IndexedPropertyDescriptor) descriptor).getIndexedWriteMethod(), "No List Indexed Write Method");
    }

    /**
     * Test Read Method for a List
     *
     * JDK 1.3.1_04: Test Passes JDK 1.4.2_05: Test Fails - getter which returns java.util.List not returned by IndexedPropertyDescriptor.getReadMethod();
     */
    @Test
    void testListReadMethod() throws Exception {
        final PropertyDescriptor descriptor = propertyUtilsBean.getPropertyDescriptor(bean, "stringList");
        assertNotNull(descriptor.getReadMethod(), "No List Read Method");
    }

    /**
     * Test Write Method for a List
     *
     * JDK 1.3.1_04: Test Passes JDK 1.4.2_05: Test Fails - setter which java.util.List argument not returned by IndexedPropertyDescriptor.getWriteMethod();
     */
    @Test
    void testListWriteMethod() throws Exception {
        final PropertyDescriptor descriptor = propertyUtilsBean.getPropertyDescriptor(bean, "stringList");
        assertNotNull(descriptor.getWriteMethod(), "No List Write Method");
    }

    /**
     * Test setting an Array property
     *
     * JDK 1.3.1_04 and 1.4.2_05: Test Fails - IllegalArgumentException can't invoke setter, argument type mismatch
     *
     * Fails because of a bug in BeanUtilsBean.setProperty() method. Value is always converted to the array's component type which in this case is a String.
     * Then it calls the setStringArray(String[]) passing a String rather than String[] causing this exception. If there isn't an "index" value then the
     * PropertyType (rather than IndexedPropertyType) should be used.
     */
    @Test
    void testSetArray() throws Exception {
        beanUtilsBean.setProperty(bean, "stringArray", newArray);
        final Object value = bean.getStringArray();
        assertEquals(newArray.getClass(), value.getClass(), "Type is different");
        final String[] array = (String[]) value;
        assertEquals(newArray.length, array.length, "Array Length is different");
        for (int i = 0; i < array.length; i++) {
            assertEquals(newArray[i], array[i], "Element " + i + " is different");
        }
    }

    /**
     * Test setting an indexed item of an Array using setProperty("name[x]", value)
     */
    @Test
    void testSetArrayItemA() throws Exception {
        beanUtilsBean.setProperty(bean, "stringArray[1]", "modified-1");
        assertEquals("modified-1", bean.getStringArray(1));
    }

    /**
     * Test setting an indexed item of an Array using setIndexedProperty("name", value)
     */
    @Test
    void testSetArrayItemB() throws Exception {
        propertyUtilsBean.setIndexedProperty(bean, "stringArray", 1, "modified-1");
        assertEquals("modified-1", bean.getStringArray(1));
    }

    /**
     * Test setting an ArrayList property
     */
    @Test
    void testSetArrayList() throws Exception {
        beanUtilsBean.setProperty(bean, "arrayList", newList);
        final Object value = bean.getArrayList();
        assertEquals(newList.getClass(), value.getClass(), "Type is different");
        final List<?> list = (List<?>) value;
        assertEquals(newList.size(), list.size(), "List size is different");
        for (int i = 0; i < list.size(); i++) {
            assertEquals(newList.get(i), list.get(i), "Element " + i + " is different");
        }
    }

    /**
     * Test setting a List property
     *
     * JDK 1.3.1_04: Test Passes JDK 1.4.2_05: Test Fails - setter which returns java.util.List not returned by IndexedPropertyDescriptor.getWriteMethod() -
     * therefore setProperty does nothing and values remain unchanged.
     */
    @Test
    void testSetList() throws Exception {
        beanUtilsBean.setProperty(bean, "stringList", newList);
        final Object value = bean.getStringList();
        assertEquals(newList.getClass(), value.getClass(), "Type is different");
        final List<?> list = (List<?>) value;
        assertEquals(newList.size(), list.size(), "List size is different");
        for (int i = 0; i < list.size(); i++) {
            assertEquals(newList.get(i), list.get(i), "Element " + i + " is different");
        }
    }

    /**
     * Test setting an indexed item of a List using setProperty("name[x]", value)
     */
    @Test
    void testSetListItemA() throws Exception {
        beanUtilsBean.setProperty(bean, "stringList[1]", "modified-1");
        assertEquals("modified-1", bean.getStringList(1));
    }

    /**
     * Test setting an indexed item of a List using setIndexedProperty("name", value)
     */
    @Test
    void testSetListItemB() throws Exception {
        propertyUtilsBean.setIndexedProperty(bean, "stringList", 1, "modified-1");
        assertEquals("modified-1", bean.getStringList(1));
    }

}
